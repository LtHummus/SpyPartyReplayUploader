package parsing.versions

case class ReplayVersion5DataExtractor(headerData: Array[Byte]) extends SpyPartyReplayDataExtractor
  with DisplayNameAware
  with GuestsAndDurationAware {

  override val versionNumber: Int = 5

  override val protocolVersionOffset: Int           = 0x08
  override val spyPartyVersionOffset: Int           = 0x0C
  override val durationOffset: Int                  = 0x14
  override val uuidOffset: Int                      = 0x18
  override val timestampOffset: Int                 = 0x28
  override val sequenceNumberOffset: Int            = 0x2C
  override val spyUsernameLengthOffset: Int         = 0x2E
  override val sniperUsernameLengthOffset: Int      = 0x2F
  override val gameResultOffset: Int                = 0x38
  override val gameTypeOffset: Int                  = 0x3C
  override val levelOffset: Int                     = 0x40
  override val playerNamesOffset: Int               = 0x60
  override val selectedMissionsOffset: Int          = 0x44
  override val pickedMissionsOffset: Int            = 0x48
  override val completedMissionsOffset: Int         = 0x4C

  override val spyDisplayNameLengthOffset: Int      = 0x30
  override val sniperDisplayNameLengthOffset: Int   = 0x31
  override val startDurationOffset: Int             = 0x50
  override val numGuestsOffset: Int                 = 0x54

}
