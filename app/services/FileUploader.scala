package services

import java.nio.file.Path

import com.amazonaws.services.s3.AmazonS3
import config.SpyPartyReplayUploaderConfig
import javax.inject.{Inject, Singleton}
import scalaz.\/

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FileUploader @Inject() (s3: AmazonS3, config: SpyPartyReplayUploaderConfig){

  def uploadFile(source: Path, key: String)(implicit ec: ExecutionContext): Future[Throwable \/ String] = Future {
    \/.fromTryCatchNonFatal{
      s3.putObject(config.ReplayBucketName, key, source.toFile)
      s"https://s3-us-west-2.amazonaws.com/${config.ReplayBucketName}/$key"
    }
  }

}
