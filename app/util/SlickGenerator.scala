package util

import slick.codegen.SourceCodeGenerator
import slick.dbio.DBIO
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api._
import slick.jdbc.meta.MTable
import slick.model.Model

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

/*
slick.dbs.default.profile = "slick.jdbc.MySQLProfile$"
slick.dbs.default.db.url = "jdbc:mysql://localhost/uploader?useSSL=false&nullCatalogMeansCurrent=true"
slick.dbs.default.db.user = "root"
slick.dbs.default.db.password = "root"
 */

object SlickGenerator extends App {

  trait WorkingMySQLProfile extends MySQLProfile{
    override def defaultTables(implicit ec: ExecutionContext): DBIO[Seq[MTable]] = MTable.getTables(None,None,None,None)
  }
  object WorkingMySQLProfile extends WorkingMySQLProfile

  val db = Database.forURL("jdbc:mysql://localhost/uploader?useSSL=false&nullCatalogMeansCurrent=true", "root", "root")

  val allSchemas = Await.result(db.run(
    WorkingMySQLProfile.createModel(None, ignoreInvalidDefaults = true)(ExecutionContext.global).withPinnedSession), Duration.Inf)

  val publicSchema = new Model(allSchemas.tables.filterNot(_.name.schema.isEmpty), allSchemas.options)

  new SourceCodeGenerator(publicSchema).writeToFile(MySQLProfile.getClass.getName, "app", "database")

}
