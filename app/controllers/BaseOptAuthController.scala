package controllers

import jp.t2v.lab.play2.auth.OptionalAuthElement
import models.document.{ DocumentAccessLevel, DocumentService }
import models.generated.tables.records.{ DocumentFilepartRecord, DocumentRecord, UserRecord }
import models.user.UserService
import play.api.Configuration
import play.api.mvc.{AnyContent, Request, Result}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Logger
import scala.concurrent.Future

abstract class BaseOptAuthController(
    config: Configuration,
    documents: DocumentService,
    users: UserService
  ) extends BaseController(config, users) with OptionalAuthElement {
  
  
  /** Helper that covers the boilerplate for all document views
    *
    * Just hand this method a function that produces an HTTP OK result for a document, while
    * the method handles ForbiddenPage/Not Found error cases.
    */
  protected def documentResponse(documentId: String, maybeUser: Option[UserRecord],
      response: (DocumentRecord, Seq[DocumentFilepartRecord], DocumentAccessLevel) => Future[Result]) = {

    documents.findByIdWithFileparts(documentId, maybeUser.map(_.getUsername)).flatMap(_ match {
      case Some((document, fileparts, accesslevel)) => response(document, fileparts, accesslevel)
      case None => Future.successful(NotFoundPage)
    }).recover { case t =>
      t.printStackTrace()
      InternalServerError(t.getMessage)    
    }
  }
  
  /** Helper that covers the boilerplate for document views requiring read access **/
  protected def documentReadResponse(documentId: String, maybeUser: Option[UserRecord],
      response: (DocumentRecord, Seq[DocumentFilepartRecord], DocumentAccessLevel) => Future[Result])(implicit request: Request[AnyContent])  = {
    
    documentResponse(documentId, maybeUser, { case (document, fileparts, accesslevel) =>
      if (accesslevel.canRead)
        response(document, fileparts, accesslevel)
      else if (maybeUser.isEmpty) // No read rights - but user is not logged in yet 
        authenticationFailed(request)        
      else
        Future.successful(ForbiddenPage)
    })
  }
  
  /** Helper that covers the boilerplate for all document part views **/
  protected def documentPartResponse(documentId: String, partNo: Int, maybeUser: Option[UserRecord],
      response: (DocumentRecord, Seq[DocumentFilepartRecord], DocumentFilepartRecord, DocumentAccessLevel) => Future[Result]) = {
    
    documentResponse(documentId, maybeUser, { case (document, fileparts, accesslevel) =>
      val selectedPart = fileparts.filter(_.getSequenceNo == partNo)
      if (selectedPart.isEmpty) {
        Future.successful(NotFoundPage)
      } else if (selectedPart.size == 1) {
        response(document, fileparts, selectedPart.head, accesslevel)
      } else {
        // More than one part with this sequence number - DB integrity broken!
        Logger.warn("Invalid document part:" + documentId + "/" + partNo) 
        Future.successful(InternalServerError)
      }
    })
  }
  
}