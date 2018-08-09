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
  lazy val schema: profile.SchemaDescription = Bouts.schema ++ Games.schema ++ Tournaments.schema
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

  /** Entity class storing rows of table Games
   *  @param id Database column id SqlType(INT), AutoInc, PrimaryKey
   *  @param bout Database column bout SqlType(INT)
   *  @param spy Database column spy SqlType(TINYTEXT), Length(255,true)
   *  @param sniper Database column sniper SqlType(TINYTEXT), Length(255,true)
   *  @param result Database column result SqlType(TINYINT)
   *  @param level Database column level SqlType(INT)
   *  @param loadout Database column loadout SqlType(TINYTEXT), Length(255,true)
   *  @param uuid Database column uuid SqlType(VARCHAR), Length(50,true)
   *  @param version Database column version SqlType(TINYINT)
   *  @param selectedMissions Database column selected_missions SqlType(SMALLINT)
   *  @param pickedMissions Database column picked_missions SqlType(SMALLINT)
   *  @param accomplishedMissions Database column accomplished_missions SqlType(SMALLINT)
   *  @param startDurationSeconds Database column start_duration_seconds SqlType(SMALLINT), Default(None)
   *  @param numGuests Database column num_guests SqlType(SMALLINT), Default(None) */
  case class GamesRow(id: Int, bout: Int, spy: String, sniper: String, result: Byte, level: Int, loadout: String, uuid: String, version: Byte, selectedMissions: Int, pickedMissions: Int, accomplishedMissions: Int, startDurationSeconds: Option[Int] = None, numGuests: Option[Int] = None)
  /** GetResult implicit for fetching GamesRow objects using plain SQL queries */
  implicit def GetResultGamesRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Byte], e3: GR[Option[Int]]): GR[GamesRow] = GR{
    prs => import prs._
    GamesRow.tupled((<<[Int], <<[Int], <<[String], <<[String], <<[Byte], <<[Int], <<[String], <<[String], <<[Byte], <<[Int], <<[Int], <<[Int], <<?[Int], <<?[Int]))
  }
  /** Table description of table games. Objects of this class serve as prototypes for rows in queries. */
  class Games(_tableTag: Tag) extends profile.api.Table[GamesRow](_tableTag, Some("uploader"), "games") {
    def * = (id, bout, spy, sniper, result, level, loadout, uuid, version, selectedMissions, pickedMissions, accomplishedMissions, startDurationSeconds, numGuests) <> (GamesRow.tupled, GamesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(bout), Rep.Some(spy), Rep.Some(sniper), Rep.Some(result), Rep.Some(level), Rep.Some(loadout), Rep.Some(uuid), Rep.Some(version), Rep.Some(selectedMissions), Rep.Some(pickedMissions), Rep.Some(accomplishedMissions), startDurationSeconds, numGuests).shaped.<>({r=>import r._; _1.map(_=> GamesRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13, _14)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(INT), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column bout SqlType(INT) */
    val bout: Rep[Int] = column[Int]("bout")
    /** Database column spy SqlType(TINYTEXT), Length(255,true) */
    val spy: Rep[String] = column[String]("spy", O.Length(255,varying=true))
    /** Database column sniper SqlType(TINYTEXT), Length(255,true) */
    val sniper: Rep[String] = column[String]("sniper", O.Length(255,varying=true))
    /** Database column result SqlType(TINYINT) */
    val result: Rep[Byte] = column[Byte]("result")
    /** Database column level SqlType(INT) */
    val level: Rep[Int] = column[Int]("level")
    /** Database column loadout SqlType(TINYTEXT), Length(255,true) */
    val loadout: Rep[String] = column[String]("loadout", O.Length(255,varying=true))
    /** Database column uuid SqlType(VARCHAR), Length(50,true) */
    val uuid: Rep[String] = column[String]("uuid", O.Length(50,varying=true))
    /** Database column version SqlType(TINYINT) */
    val version: Rep[Byte] = column[Byte]("version")
    /** Database column selected_missions SqlType(SMALLINT) */
    val selectedMissions: Rep[Int] = column[Int]("selected_missions")
    /** Database column picked_missions SqlType(SMALLINT) */
    val pickedMissions: Rep[Int] = column[Int]("picked_missions")
    /** Database column accomplished_missions SqlType(SMALLINT) */
    val accomplishedMissions: Rep[Int] = column[Int]("accomplished_missions")
    /** Database column start_duration_seconds SqlType(SMALLINT), Default(None) */
    val startDurationSeconds: Rep[Option[Int]] = column[Option[Int]]("start_duration_seconds", O.Default(None))
    /** Database column num_guests SqlType(SMALLINT), Default(None) */
    val numGuests: Rep[Option[Int]] = column[Option[Int]]("num_guests", O.Default(None))

    /** Foreign key referencing Bouts (database name fk_bout) */
    lazy val boutsFk = foreignKey("fk_bout", bout, Bouts)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Uniqueness Index over (uuid) (database name uiuid) */
    val index1 = index("uiuid", uuid, unique=true)
  }
  /** Collection-like TableQuery object for table Games */
  lazy val Games = new TableQuery(tag => new Games(tag))

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
