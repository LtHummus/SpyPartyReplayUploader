package modules

import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.google.inject.{AbstractModule, Provides}


class AwsModule extends AbstractModule {

  @Provides
  def amazonS3Client(): AmazonS3 = AmazonS3ClientBuilder.defaultClient()

  override def configure(): Unit = {
    //nop
  }
}
