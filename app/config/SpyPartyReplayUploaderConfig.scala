package config

import com.typesafe.config.Config
import javax.inject.{Inject, Singleton}

@Singleton
class SpyPartyReplayUploaderConfig @Inject() (config: Config) {

  lazy val ReplayBucketName: String = config.getString("s3.replayBucket")
  lazy val tournamentCreationPassword: String = config.getString("tournament.creationPassword")
}
