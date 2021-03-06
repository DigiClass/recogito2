package models.user

import models.generated.tables.records.{ UserRecord, UserRoleRecord }
import models.generated.tables.records.FeatureToggleRecord
import models.generated.tables.records.FeatureToggleRecord

case class User(val record: UserRecord, private val roleRecords: Seq[UserRoleRecord], private val featureToggleRecords: Seq[FeatureToggleRecord]) {
  
  val username = record.getUsername
  
  val email = record.getEmail
  
  val passwordHash = record.getPasswordHash
  
  val salt = record.getSalt
  
  val memberSince = record.getMemberSince
  
  val realName = record.getRealName
  
  val bio = record.getBio
  
  val website = record.getWebsite
  
  val quotaMb = record.getQuotaMb
    
  val featureToggles = featureToggleRecords.map(_.getHasToggle)
  
  def hasRole(role: Roles.Role): Boolean = roleRecords.exists(_.getHasRole == role.toString)

}

object Roles {

  sealed trait Role

  case object Admin extends Role { override lazy val toString = "ADMIN" }

  case object Normal extends Role { override lazy val toString = "NORMAL" }

}
