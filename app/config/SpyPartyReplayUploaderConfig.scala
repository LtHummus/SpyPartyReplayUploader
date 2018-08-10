package config

import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}

@Singleton
class SpyPartyReplayUploaderConfig @Inject() (config: Config) {

  lazy val ReplayBucketName: String           = config.getString("s3.replayBucket")
  lazy val TournamentCreationPassword: String = config.getString("tournament.creationPassword")
  lazy val DefaultStart: Int                  = config.getInt("pagination.defaultStart")
  lazy val DefaultCount: Int                  = config.getInt("pagination.defaultCount")
  lazy val MaxCount: Int                      = config.getInt("pagination.maxCount")
}
