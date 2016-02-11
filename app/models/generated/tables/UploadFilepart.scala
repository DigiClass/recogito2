/**
 * This class is generated by jOOQ
 */
package models.generated.tables


import java.lang.Class
import java.lang.Integer
import java.lang.String
import java.util.Arrays
import java.util.List

import javax.annotation.Generated

import models.generated.DefaultSchema
import models.generated.Keys
import models.generated.tables.records.UploadFilepartRecord

import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Table
import org.jooq.TableField
import org.jooq.UniqueKey
import org.jooq.impl.TableImpl

import scala.Array
import scala.Byte


object UploadFilepart {

	/**
	 * The reference instance of <code>upload_filepart</code>
	 */
	val UPLOAD_FILEPART = new UploadFilepart
}

/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = Array(
		"http://www.jooq.org",
		"jOOQ version:3.7.2"
	),
	comments = "This class is generated by jOOQ"
)
class UploadFilepart(alias : String, aliased : Table[UploadFilepartRecord], parameters : Array[ Field[_] ]) extends TableImpl[UploadFilepartRecord](alias, DefaultSchema.DEFAULT_SCHEMA, aliased, parameters, "") {

	/**
	 * The class holding records for this type
	 */
	override def getRecordType : Class[UploadFilepartRecord] = {
		classOf[UploadFilepartRecord]
	}

	/**
	 * The column <code>upload_filepart.id</code>.
	 */
	val ID : TableField[UploadFilepartRecord, Integer] = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), "")

	/**
	 * The column <code>upload_filepart.upload_id</code>.
	 */
	val UPLOAD_ID : TableField[UploadFilepartRecord, Integer] = createField("upload_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), "")

	/**
	 * The column <code>upload_filepart.title</code>.
	 */
	val TITLE : TableField[UploadFilepartRecord, String] = createField("title", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), "")

	/**
	 * The column <code>upload_filepart.content_type</code>.
	 */
	val CONTENT_TYPE : TableField[UploadFilepartRecord, String] = createField("content_type", org.jooq.impl.SQLDataType.VARCHAR.nullable(false), "")

	/**
	 * The column <code>upload_filepart.content</code>.
	 */
	val CONTENT : TableField[UploadFilepartRecord, Array[Byte]] = createField("content", org.jooq.impl.SQLDataType.BLOB.nullable(false), "")

	/**
	 * Create a <code>upload_filepart</code> table reference
	 */
	def this() = {
		this("upload_filepart", null, null)
	}

	/**
	 * Create an aliased <code>upload_filepart</code> table reference
	 */
	def this(alias : String) = {
		this(alias, models.generated.tables.UploadFilepart.UPLOAD_FILEPART, null)
	}

	private def this(alias : String, aliased : Table[UploadFilepartRecord]) = {
		this(alias, aliased, null)
	}

	override def getPrimaryKey : UniqueKey[UploadFilepartRecord] = {
		Keys.PK_UPLOAD_FILEPART
	}

	override def getKeys : List[ UniqueKey[UploadFilepartRecord] ] = {
		return Arrays.asList[ UniqueKey[UploadFilepartRecord] ](Keys.PK_UPLOAD_FILEPART)
	}

	override def getReferences : List[ ForeignKey[UploadFilepartRecord, _] ] = {
		return Arrays.asList[ ForeignKey[UploadFilepartRecord, _] ](Keys.FK_UPLOAD_FILEPART_UPLOAD_1)
	}

	override def as(alias : String) : UploadFilepart = {
		new UploadFilepart(alias, this)
	}

	/**
	 * Rename this table
	 */
	def rename(name : String) : UploadFilepart = {
		new UploadFilepart(name, null)
	}
}
