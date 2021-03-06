package controllers.document

import controllers.HasConfig
import java.io.{ File, FileInputStream, FileOutputStream, BufferedInputStream, ByteArrayInputStream, InputStream, PrintWriter }
import java.math.BigInteger
import java.security.{ MessageDigest, DigestInputStream }
import java.util.UUID
import java.util.zip.{ ZipEntry, ZipOutputStream }
import models.HasDate
import models.annotation.{ Annotation, AnnotationService }
import models.document.DocumentInfo
import models.generated.tables.records.{ DocumentRecord, DocumentFilepartRecord }
import play.api.libs.json.Json
import play.api.libs.Files.TemporaryFile
import scala.concurrent.{ ExecutionContext, Future }
import storage.Uploads
import java.security.DigestInputStream

trait BackupWriter extends HasBackupValidation { self: HasConfig =>
  
  private val TMP = System.getProperty("java.io.tmpdir")
  
  private val BUFFER_SIZE = 2048
  
  private def writeToZip(inputStream: InputStream, filename: String, zip: ZipOutputStream) = {
    zip.putNextEntry(new ZipEntry(filename))
     
    val md = MessageDigest.getInstance(ALGORITHM)    
    val in = new DigestInputStream(new BufferedInputStream(inputStream), md)

    var data= new Array[Byte](BUFFER_SIZE)
    var count: Int = 0

    while ({ count = in.read(data, 0, BUFFER_SIZE); count } > -1) {
      zip.write(data, 0, count)
    }

    in.close()
    zip.closeEntry()
    
    new BigInteger(1, md.digest()).toString(16)
  }
  
  def createBackup(doc: DocumentInfo)(implicit ctx: ExecutionContext, uploads: Uploads, annotations: AnnotationService): Future[File] = {
    
    def getFileAsStream(owner: String, documentId: String, filename: String) = {
      val dir = uploads.getDocumentDir(owner, documentId).get // Fail hard if the dir doesn't exist
      new FileInputStream(new File(dir, filename))
    }
    
    def getManifestAsStream() = {
      val manifest = "Recogito-Version: 2.0.1-alpha"
      new ByteArrayInputStream(manifest.getBytes)
    }
    
    def getMetadataAsStream(doc: DocumentInfo) = {
      
      // DocumentRecord JSON serialization
      import models.document.DocumentService._
      
      val json = Json.prettyPrint(Json.toJson((doc.document, doc.fileparts)))
      new ByteArrayInputStream(json.getBytes)
    }
    
    def getAnnotationsAsStream(docId: String, annotations: Seq[Annotation], parts: Seq[DocumentFilepartRecord]): InputStream = {
      val tmp = new TemporaryFile(new File(TMP, docId + "_annotations.jsonl"))
      val writer = new PrintWriter(tmp.file)
      annotations.foreach(a => writer.println(Json.stringify(Json.toJson(a))))
      writer.close()
      new FileInputStream(tmp.file)
    }
    
    Future {
      new TemporaryFile(new File(TMP, doc.id + ".zip"))
    } flatMap { zipFile =>
      val zipStream = new ZipOutputStream(new FileOutputStream(zipFile.file))

      writeToZip(getManifestAsStream(), "manifest", zipStream)
      val metadataHash = writeToZip(getMetadataAsStream(doc), "metadata.json", zipStream)

      val fileHashes = doc.fileparts.map { part =>
        writeToZip(getFileAsStream(doc.ownerName, doc.id, part.getFile), "parts" + File.separator + part.getFile, zipStream)
      }

      annotations.findByDocId(doc.id).map { annotations =>
        val annotationsHash = writeToZip(getAnnotationsAsStream(doc.id, annotations.map(_._1), doc.fileparts), "annotations.jsonl", zipStream)
        
        val signature = computeSignature(metadataHash, fileHashes, annotationsHash)
        writeToZip(new ByteArrayInputStream(signature.getBytes), "signature", zipStream)
        
        zipStream.close()
        zipFile.file
      }
    }
  }
  
}