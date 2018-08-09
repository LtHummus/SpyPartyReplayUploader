package database
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Bouts.schema ++ Tournaments.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Bouts
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param tournament Database column tournament SqlType(INT)
   *  @param player1 Database column player1 SqlType(TINYTEXT), Length(255,true)
   *  @param player2 Database column player2 SqlType(TINYTEXT), Length(255,true)
   *  @param url Database column url SqlType(TEXT)
   *  @param metadata Database column metadata SqlType(JSON), Length(1073741824,true) */
  case class BoutsRow(id: Int, tournament: Int, player1: String, player2: String, url: String, metadata: String)
  /** GetResult implicit for fetching BoutsRow objects using plain SQL queries */
  implicit def GetResultBoutsRow(implicit e0: GR[Int], e1: GR[String]): GR[BoutsRow] = GR{
    prs => import prs._
    BoutsRow.tupled((<<[Int], <<[Int], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table bouts. Objects of this class serve as prototypes for rows in queries. */
  class Bouts(_tableTag: Tag) extends profile.api.Table[BoutsRow](_tableTag, Some("uploader"), "bouts") {
    def * = (id, tournament, player1, player2, url, metadata) <> (BoutsRow.tupled, BoutsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(tournament), Rep.Some(player1), Rep.Some(player2), Rep.Some(url), Rep.Some(metadata)).shaped.<>({r=>import r._; _1.map(_=> BoutsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column tournament SqlType(INT) */
    val tournament: Rep[Int] = column[Int]("tournament")
    /** Database column player1 SqlType(TINYTEXT), Length(255,true) */
    val player1: Rep[String] = column[String]("player1", O.Length(255,varying=true))
    /** Database column player2 SqlType(TINYTEXT), Length(255,true) */
    val player2: Rep[String] = column[String]("player2", O.Length(255,varying=true))
    /** Database column url SqlType(TEXT) */
    val url: Rep[String] = column[String]("url")
    /** Database column metadata SqlType(JSON), Length(1073741824,true) */
    val metadata: Rep[String] = column[String]("metadata", O.Length(1073741824,varying=true))

    /** Foreign key referencing Tournaments (database name fk_tournament) */
    lazy val tournamentsFk = foreignKey("fk_tournament", tournament, Tournaments)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Bouts */
  lazy val Bouts = new TableQuery(tag => new Bouts(tag))

  /** Entity class storing rows of table Tournaments
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param name Database column name SqlType(TEXT)
   *  @param active Database column active SqlType(BIT), Default(true)
   *  @param formItems Database column form_items SqlType(JSON), Length(1073741824,true)
   *  @param configuration Database column configuration SqlType(JSON), Length(1073741824,true) */
  case class TournamentsRow(id: Int, name: String, active: Boolean = true, formItems: String, configuration: String)
  /** GetResult implicit for fetching TournamentsRow objects using plain SQL queries */
  implicit def GetResultTournamentsRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Boolean]): GR[TournamentsRow] = GR{
    prs => import prs._
    TournamentsRow.tupled((<<[Int], <<[String], <<[Boolean], <<[String], <<[String]))
  }
  /** Table description of table tournaments. Objects of this class serve as prototypes for rows in queries. */
  class Tournaments(_tableTag: Tag) extends profile.api.Table[TournamentsRow](_tableTag, Some("uploader"), "tournaments") {
    def * = (id, name, active, formItems, configuration) <> (TournamentsRow.tupled, TournamentsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(name), Rep.Some(active), Rep.Some(formItems), Rep.Some(configuration)).shaped.<>({r=>import r._; _1.map(_=> TournamentsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column name SqlType(TEXT) */
    val name: Rep[String] = column[String]("name")
    /** Database column active SqlType(BIT), Default(true) */
    val active: Rep[Boolean] = column[Boolean]("active", O.Default(true))
    /** Database column form_items SqlType(JSON), Length(1073741824,true) */
    val formItems: Rep[String] = column[String]("form_items", O.Length(1073741824,varying=true))
    /** Database column configuration SqlType(JSON), Length(1073741824,true) */
    val configuration: Rep[String] = column[String]("configuration", O.Length(1073741824,varying=true))
  }
  /** Collection-like TableQuery object for table Tournaments */
  lazy val Tournaments = new TableQuery(tag => new Tournaments(tag))
}
