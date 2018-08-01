package util

import java.io.{BufferedInputStream, FileInputStream}

import parsing.Replay

object ReplayTester extends App {


  val fileName = "C:\\Users\\Benjamin\\AppData\\Local\\SpyParty\\replays\\SCL 4\\Week 06\\B - dowsey v bitbandingpig\\SpyPartyReplay-20180616-14-18-02-dowsey-vs-bitbandingpig-2Irn_E6vT3-_4XgF6aisMA-v23.replay"

  val is = new BufferedInputStream(new FileInputStream(fileName))

  println(Replay.fromInputStream(is))

  is.close()

}
