package saisei.container.mkv.element

import naibu.io.memory.Memory
import saisei.io.format.ebml.EBMLIntegerType
import saisei.io.format.ebml.EbmlID
import saisei.io.format.ebml.element.ElementDeclaration
import saisei.io.format.ebml.readEBMLInteger_

/**
 * **Definition**
 * The Root Element that contains all other Top-Level Elements; see (#data-layout).
 */
object Segment : ElementDeclaration.MASTER.Actual(
    EbmlID("Segment",
    0x18, 0x53, 0x80, 0x67)
) {
  init {
    add(SeekHead)
    add(Info)
    add(Cluster)
    add(Tracks)
    add(Cues)
    add(Attachments)
    add(Chapters)
    add(Tags)
  }

  /**
   * **Definition**
   * Contains seeking information of Top-Level Elements; see (#data-layout).
   */
  object SeekHead : ElementDeclaration.MASTER.Actual(
      EbmlID("SeekHead",
      0x11, 0x4D, 0x9B, 0x74)
  ) {
    init {
      add(Seek)
    }

    /**
     * **Definition**
     * Contains a single seek entry to an EBML Element.
     */
    object Seek : ElementDeclaration.MASTER.Actual(
        EbmlID("Seek", 0x4D,
        0xBB)
    ) {
      /**
       * **Definition**
       * The binary EBML ID of a Top-Level Element.
       */
      val SeekID: ElementDeclaration.CUSTOM<EbmlID> =
          ElementDeclaration.CUSTOM.Actual<EbmlID>(
              EbmlID("SeekID",
          0x53, 0xAB), { EbmlID.Binary(it, "") })

      /**
       * **Definition**
       * The Segment Position ((#segment-position)) of a Top-Level Element.
       */
      val SeekPosition: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("SeekPosition",
          0x53, 0xAC), EBMLIntegerType.Unsigned)

      init {
        add(SeekID)
        add(SeekPosition)
      }
    }
  }

  /**
   * **Definition**
   * Contains general information about the Segment.
   */
  object Info : ElementDeclaration.MASTER.Actual(
      EbmlID("Info", 0x15,
      0x49, 0xA9, 0x66)
  ) {
    /**
     * **Definition**
     * A randomly generated unique ID to identify the Segment amongst many others (128 bits). It is
     * equivalent to a UUID v4 [@!RFC4122] with all bits randomly (or pseudo-randomly) chosen. An
     * actual UUID v4 value, where some bits are not random, **MAY** also be used.**UsageNotes**
     * If the Segment is a part of a Linked Segment, then this Element is **REQUIRED**. The value of
     * the unique ID **MUST** contain at least one bit set to 1.
     */
    val SegmentUUID: ElementDeclaration.BINARY =
        ElementDeclaration.BINARY.Actual(
            EbmlID("SegmentUUID",
        0x73, 0xA4)
        )

    /**
     * **Definition**
     * A filename corresponding to this Segment.
     */
    val SegmentFilename: ElementDeclaration.STRING =
        ElementDeclaration.STRING.Actual(
            EbmlID("SegmentFilename",
        0x73, 0x84), true)

    /**
     * **Definition**
     * An ID to identify the previous Segment of a Linked Segment.**UsageNotes**
     * If the Segment is a part of a Linked Segment that uses Hard Linking ((#hard-linking)), then
     * either the PrevUUID or the NextUUID Element is **REQUIRED**. If a Segment contains a PrevUUID
     * but not a NextUUID, then it **MAY** be considered as the last Segment of the Linked Segment. The
     * PrevUUID **MUST NOT** be equal to the SegmentUUID.
     */
    val PrevUUID: ElementDeclaration.BINARY =
        ElementDeclaration.BINARY.Actual(
            EbmlID("PrevUUID",
        0x3C, 0xB9, 0x23)
        )

    /**
     * **Definition**
     * A filename corresponding to the file of the previous Linked Segment.**UsageNotes**
     * Provision of the previous filename is for display convenience, but PrevUUID **SHOULD** be
     * considered authoritative for identifying the previous Segment in a Linked Segment.
     */
    val PrevFilename: ElementDeclaration.STRING =
        ElementDeclaration.STRING.Actual(
            EbmlID("PrevFilename",
        0x3C, 0x83, 0xAB), true)

    /**
     * **Definition**
     * An ID to identify the next Segment of a Linked Segment.**UsageNotes**
     * If the Segment is a part of a Linked Segment that uses Hard Linking ((#hard-linking)), then
     * either the PrevUUID or the NextUUID Element is **REQUIRED**. If a Segment contains a NextUUID
     * but not a PrevUUID, then it **MAY** be considered as the first Segment of the Linked Segment.
     * The NextUUID **MUST NOT** be equal to the SegmentUUID.
     */
    val NextUUID: ElementDeclaration.BINARY =
        ElementDeclaration.BINARY.Actual(
            EbmlID("NextUUID",
        0x3E, 0xB9, 0x23)
        )

    /**
     * **Definition**
     * A filename corresponding to the file of the next Linked Segment.**UsageNotes**
     * Provision of the next filename is for display convenience, but NextUUID **SHOULD** be
     * considered authoritative for identifying the Next Segment.
     */
    val NextFilename: ElementDeclaration.STRING =
        ElementDeclaration.STRING.Actual(
            EbmlID("NextFilename",
        0x3E, 0x83, 0xBB), true)

    /**
     * **Definition**
     * A unique ID that all Segments of a Linked Segment **MUST** share (128 bits). It is equivalent
     * to a UUID v4 [@!RFC4122] with all bits randomly (or pseudo-randomly) chosen. An actual UUID v4
     * value, where some bits are not random, **MAY** also be used.**UsageNotes**
     * If the Segment Info contains a `ChapterTranslate` element, this Element is **REQUIRED**.
     */
    val SegmentFamily: ElementDeclaration.BINARY =
        ElementDeclaration.BINARY.Actual(
            EbmlID("SegmentFamily",
        0x44, 0x44)
        )

    /**
     * **Definition**
     * Base unit for Segment Ticks and Track Ticks, in nanoseconds. A TimestampScale value of
     * 1000000 means scaled timestamps in the Segment are expressed in milliseconds; see (#timestamps)
     * on how to interpret timestamps.
     */
    val TimestampScale: ElementDeclaration.INTEGER =
        ElementDeclaration.INTEGER.Actual(
            EbmlID("TimestampScale",
        0x2A, 0xD7, 0xB1), EBMLIntegerType.Unsigned)

    /**
     * **Definition**
     * Duration of the Segment, expressed in Segment Ticks which is based on TimestampScale; see
     * (#timestamp-ticks).
     */
    val Duration: ElementDeclaration.FLOAT =
        ElementDeclaration.FLOAT.Actual(
            EbmlID("Duration",
        0x44, 0x89)
        )

    /**
     * **Definition**
     * The date and time that the Segment was created by the muxing application or library.
     */
    val DateUTC: ElementDeclaration.DATE =
        ElementDeclaration.DATE.Actual(
            EbmlID("DateUTC",
        0x44, 0x61)
        )

    /**
     * **Definition**
     * General name of the Segment.
     */
    val Title: ElementDeclaration.STRING =
        ElementDeclaration.STRING.Actual(
            EbmlID("Title",
        0x7B, 0xA9), true)

    /**
     * **Definition**
     * Muxing application or library (example: "libmatroska-0.4.3").**UsageNotes**
     * Include the full name of the application or library followed by the version number.
     */
    val MuxingApp: ElementDeclaration.STRING =
        ElementDeclaration.STRING.Actual(
            EbmlID("MuxingApp",
        0x4D, 0x80), true)

    /**
     * **Definition**
     * Writing application (example: "mkvmerge-0.3.3").**UsageNotes**
     * Include the full name of the application followed by the version number.
     */
    val WritingApp: ElementDeclaration.STRING =
        ElementDeclaration.STRING.Actual(
            EbmlID("WritingApp",
        0x57, 0x41), true)

    init {
      add(SegmentUUID)
      add(SegmentFilename)
      add(PrevUUID)
      add(PrevFilename)
      add(NextUUID)
      add(NextFilename)
      add(SegmentFamily)
      add(TimestampScale)
      add(Duration)
      add(DateUTC)
      add(Title)
      add(MuxingApp)
      add(WritingApp)
      add(ChapterTranslate)
    }

    /**
     * **Definition**
     * The mapping between this `Segment` and a segment value in the given Chapter
     * Codec.**Rationale**
     * Chapter Codec may need to address different segments, but they may not know of the way to
     * identify such segment when stored in Matroska. This element and its child elements add a way to
     * map the internal segments known to the Chapter Codec to the Segment IDs in Matroska. This allows
     * remuxing a file with Chapter Codec without changing the content of the codec data, just the
     * Segment mapping.
     */
    object ChapterTranslate :
        ElementDeclaration.MASTER.Actual(
            EbmlID("ChapterTranslate", 0x69,
        0x24)
        ) {
      /**
       * **Definition**
       * The binary value used to represent this Segment in the chapter codec data. The format
       * depends on the ChapProcessCodecID used; see (#chapprocesscodecid-element).
       */
      val ChapterTranslateID: ElementDeclaration.BINARY =
          ElementDeclaration.BINARY.Actual(
              EbmlID("ChapterTranslateID",
          0x69, 0xA5)
          )

      /**
       * **Definition**
       * Specify a chapter edition UID on which this `ChapterTranslate` applies.**UsageNotes**
       * When no `ChapterTranslateEditionUID` is specified in the `ChapterTranslate`, the
       * `ChapterTranslate` applies to all chapter editions found in the Segment using the given
       * `ChapterTranslateCodec`.
       */
      val ChapterTranslateEditionUID: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("ChapterTranslateEditionUID",
          0x69, 0xFC), EBMLIntegerType.Unsigned)

      init {
        add(ChapterTranslateID)
        add(ChapterTranslateCodec)
        add(ChapterTranslateEditionUID)
      }

      /**
       * **Definition**
       * This `ChapterTranslate` applies to this chapter codec of the given chapter edition(s); see
       * (#chapprocesscodecid-element).
       */
      enum class ChapterTranslateCodec(
        val code: Long,
      ) {
        /**
         * **Definition**
         * Chapter commands using the Matroska Script codec.
         */
        `Matroska Script`(0),
        /**
         * **Definition**
         * Chapter commands using the DVD-like codec.
         */
        `DVD-menu`(1),
        ;

        companion object Declaration : ElementDeclaration.CUSTOM<ChapterTranslateCodec> {
          override val id: EbmlID = EbmlID("ChapterTranslateCodec", 0x69,
              0xBF)

          override suspend fun process(`data`: Memory): ChapterTranslateCodec {
            val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
            return entries.first { it.code == code }
          }
        }
      }
    }
  }

  /**
   * **Definition**
   * The Top-Level Element containing the (monolithic) Block structure.
   */
  object Cluster : ElementDeclaration.MASTER.Actual(
      EbmlID("Cluster",
      0x1F, 0x43, 0xB6, 0x75)
  ) {
    /**
     * **Definition**
     * Absolute timestamp of the cluster, expressed in Segment Ticks which is based on
     * TimestampScale; see (#timestamp-ticks).**UsageNotes**
     * This element **SHOULD** be the first child element of the Cluster it belongs to, or the
     * second if that Cluster contains a CRC-32 element ((#crc-32)).
     */
    val Timestamp: ElementDeclaration.INTEGER =
        ElementDeclaration.INTEGER.Actual(
            EbmlID("Timestamp",
        0xE7), EBMLIntegerType.Unsigned)

    /**
     * **Definition**
     * The Segment Position of the Cluster in the Segment (0 in live streams). It might help to
     * resynchronise offset on damaged streams.
     */
    val Position: ElementDeclaration.INTEGER =
        ElementDeclaration.INTEGER.Actual(
            EbmlID("Position",
        0xA7), EBMLIntegerType.Unsigned)

    /**
     * **Definition**
     * Size of the previous Cluster, in octets. Can be useful for backward playing.
     */
    val PrevSize: ElementDeclaration.INTEGER =
        ElementDeclaration.INTEGER.Actual(
            EbmlID("PrevSize",
        0xAB), EBMLIntegerType.Unsigned)

    /**
     * **Definition**
     * Similar to Block, see (#block-structure), but without all the extra information, mostly used
     * to reduced overhead when no extra feature is needed; see (#simpleblock-structure) on SimpleBlock
     * Structure.
     */
    val SimpleBlock: ElementDeclaration.BINARY =
        ElementDeclaration.BINARY.Actual(
            EbmlID("SimpleBlock",
        0xA3)
        )

    /**
     * **Definition**
     * Similar to SimpleBlock, see (#simpleblock-structure), but the data inside the Block are
     * Transformed (encrypt and/or signed).
     */
    val EncryptedBlock: ElementDeclaration.BINARY =
        ElementDeclaration.BINARY.Actual(
            EbmlID("EncryptedBlock",
        0xAF)
        )

    init {
      add(Timestamp)
      add(Position)
      add(PrevSize)
      add(SimpleBlock)
      add(EncryptedBlock)
      add(SilentTracks)
      add(BlockGroup)
    }

    /**
     * **Definition**
     * The list of tracks that are not used in that part of the stream. It is useful when using
     * overlay tracks on seeking or to decide what track to use.
     */
    object SilentTracks :
        ElementDeclaration.MASTER.Actual(EbmlID("SilentTracks", 0x58, 0x54)) {
      /**
       * **Definition**
       * One of the track number that are not used from now on in the stream. It could change later
       * if not specified as silent in a further Cluster.
       */
      val SilentTrackNumber: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("SilentTrackNumber",
          0x58, 0xD7), EBMLIntegerType.Unsigned)

      init {
        add(SilentTrackNumber)
      }
    }

    /**
     * **Definition**
     * Basic container of information containing a single Block and information specific to that
     * Block.
     */
    object BlockGroup :
        ElementDeclaration.MASTER.Actual(EbmlID("BlockGroup", 0xA0)) {
      /**
       * **Definition**
       * Block containing the actual data to be rendered and a timestamp relative to the Cluster
       * Timestamp; see (#block-structure) on Block Structure.
       */
      val Block: ElementDeclaration.BINARY =
          ElementDeclaration.BINARY.Actual(
              EbmlID("Block",
          0xA1)
          )

      /**
       * **Definition**
       * A Block with no data. It must be stored in the stream at the place the real Block would be
       * in display order.
       */
      val BlockVirtual: ElementDeclaration.BINARY =
          ElementDeclaration.BINARY.Actual(
              EbmlID("BlockVirtual",
          0xA2)
          )

      /**
       * **Definition**
       * The duration of the Block, expressed in Track Ticks; see (#timestamp-ticks). The
       * BlockDuration Element can be useful at the end of a Track to define the duration of the last
       * frame (as there is no subsequent Block available), or when there is a break in a track like
       * for subtitle tracks.
       */
      val BlockDuration: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("BlockDuration",
          0x9B), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * This frame is referenced and has the specified cache priority. In cache only a frame of the
       * same or higher priority can replace this frame. A value of 0 means the frame is not
       * referenced.
       */
      val ReferencePriority: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("ReferencePriority",
          0xFA), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * A timestamp value, relative to the timestamp of the Block in this BlockGroup, expressed in
       * Track Ticks; see (#timestamp-ticks). This is used to reference other frames necessary to
       * decode this frame. The relative value **SHOULD** correspond to a valid `Block` this `Block`
       * depends on. Historically Matroska Writer didn't write the actual `Block(s)` this `Block`
       * depends on, but *some* `Block` in the past. The value "0" **MAY** also be used to signify this
       * `Block` cannot be decoded on its own, but without knownledge of which `Block` is necessary. In
       * this case, other `ReferenceBlock` **MUST NOT** be found in the same `BlockGroup`. If the
       * `BlockGroup` doesn't have any `ReferenceBlock` element, then the `Block` it contains can be
       * decoded without using any other `Block` data.
       */
      val ReferenceBlock: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("ReferenceBlock",
          0xFB), EBMLIntegerType.Signed)

      /**
       * **Definition**
       * The Segment Position of the data that would otherwise be in position of the virtual block.
       */
      val ReferenceVirtual: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("ReferenceVirtual",
          0xFD), EBMLIntegerType.Signed)

      /**
       * **Definition**
       * The new codec state to use. Data interpretation is private to the codec. This information
       * **SHOULD** always be referenced by a seek entry.
       */
      val CodecState: ElementDeclaration.BINARY =
          ElementDeclaration.BINARY.Actual(
              EbmlID("CodecState",
          0xA4)
          )

      /**
       * **Definition**
       * Duration of the silent data added to the Block, expressed in Matroska Ticks -- i.e., in
       * nanoseconds; see (#timestamp-ticks) (padding at the end of the Block for positive value, at
       * the beginning of the Block for negative value). The duration of DiscardPadding is not
       * calculated in the duration of the TrackEntry and **SHOULD** be discarded during playback.
       */
      val DiscardPadding: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("DiscardPadding",
          0x75, 0xA2), EBMLIntegerType.Signed)

      init {
        add(Block)
        add(BlockVirtual)
        add(BlockDuration)
        add(ReferencePriority)
        add(ReferenceBlock)
        add(ReferenceVirtual)
        add(CodecState)
        add(DiscardPadding)
        add(BlockAdditions)
        add(Slices)
        add(ReferenceFrame)
      }

      /**
       * **Definition**
       * Contain additional binary data to complete the main one; see Codec BlockAdditions section
       * of [@?MatroskaCodec] for more information. An EBML parser that has no knowledge of the Block
       * structure could still see and use/skip these data.
       */
      object BlockAdditions :
          ElementDeclaration.MASTER.Actual(
              EbmlID("BlockAdditions", 0x75,
          0xA1)
          ) {
        init {
          add(BlockMore)
        }

        /**
         * **Definition**
         * Contain the BlockAdditional and some parameters.
         */
        object BlockMore :
            ElementDeclaration.MASTER.Actual(EbmlID("BlockMore", 0xA6)) {
          /**
           * **Definition**
           * Interpreted by the codec as it wishes (using the BlockAddID).
           */
          val BlockAdditional: ElementDeclaration.BINARY =
              ElementDeclaration.BINARY.Actual(
                  EbmlID("BlockAdditional",
              0xA5)
              )

          /**
           * **Definition**
           * An ID to identify how to interpret the BlockAdditional data; see Codec BlockAdditions
           * section of [@?MatroskaCodec] for more information. A value of 1 indicates that the meaning
           * of the BlockAdditional data is defined by the codec. Any other value indicates the meaning
           * of the BlockAdditional data is found in the BlockAddIDType found in the
           * TrackEntry.**UsageNotes**
           * Each BlockAddID value **MUST** be unique between all BlockMore elements found in a
           * BlockAdditions.**UsageNotes**
           * To keep MaxBlockAdditionID as low as possible, small values **SHOULD** be used.
           */
          val BlockAddID: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("BlockAddID",
              0xEE), EBMLIntegerType.Unsigned)

          init {
            add(BlockAdditional)
            add(BlockAddID)
          }
        }
      }

      /**
       * **Definition**
       * Contains slices description.
       */
      object Slices : ElementDeclaration.MASTER.Actual(
          EbmlID("Slices",
          0x8E)
      ) {
        init {
          add(TimeSlice)
        }

        /**
         * **Definition**
         * Contains extra time information about the data contained in the Block. Being able to
         * interpret this Element is not required for playback.
         */
        object TimeSlice :
            ElementDeclaration.MASTER.Actual(EbmlID("TimeSlice", 0xE8)) {
          /**
           * **Definition**
           * The reverse number of the frame in the lace (0 is the last frame, 1 is the next to
           * last, etc.). Being able to interpret this Element is not required for playback.
           */
          val LaceNumber: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("LaceNumber",
              0xCC), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * The number of the frame to generate from this lace with this delay (allow you to
           * generate many frames from the same Block/Frame).
           */
          val FrameNumber: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("FrameNumber",
              0xCD), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * The ID of the BlockAdditional Element (0 is the main Block).
           */
          val BlockAdditionID: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("BlockAdditionID",
              0xCB), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * The delay to apply to the Element, expressed in Track Ticks; see (#timestamp-ticks).
           */
          val Delay: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("Delay",
              0xCE), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * The duration to apply to the Element, expressed in Track Ticks; see (#timestamp-ticks).
           */
          val SliceDuration: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("SliceDuration",
              0xCF), EBMLIntegerType.Unsigned)

          init {
            add(LaceNumber)
            add(FrameNumber)
            add(BlockAdditionID)
            add(Delay)
            add(SliceDuration)
          }
        }
      }

      /**
       * **Definition**
       * Contains information about the last reference frame. See [@?DivXTrickTrack].
       */
      object ReferenceFrame :
          ElementDeclaration.MASTER.Actual(EbmlID("ReferenceFrame", 0xC8)) {
        /**
         * **Definition**
         * The relative offset, in bytes, from the previous BlockGroup element for this Smooth FF/RW
         * video track to the containing BlockGroup element. See [@?DivXTrickTrack].
         */
        val ReferenceOffset: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("ReferenceOffset",
            0xC9), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * The timestamp of the BlockGroup pointed to by ReferenceOffset, expressed in Track Ticks;
         * see (#timestamp-ticks). See [@?DivXTrickTrack].
         */
        val ReferenceTimestamp: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("ReferenceTimestamp",
            0xCA), EBMLIntegerType.Unsigned)

        init {
          add(ReferenceOffset)
          add(ReferenceTimestamp)
        }
      }
    }
  }

  /**
   * **Definition**
   * A Top-Level Element of information with many tracks described.
   */
  object Tracks : ElementDeclaration.MASTER.Actual(
      EbmlID("Tracks",
      0x16, 0x54, 0xAE, 0x6B)
  ) {
    init {
      add(TrackEntry)
    }

    /**
     * **Definition**
     * Describes a track with all Elements.
     */
    object TrackEntry :
        ElementDeclaration.MASTER.Actual(EbmlID("TrackEntry", 0xAE)) {
      /**
       * **Definition**
       * The track number as used in the Block Header.
       */
      val TrackNumber: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("TrackNumber",
          0xD7), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * A unique ID to identify the Track.
       */
      val TrackUID: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("TrackUID",
          0x73, 0xC5), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Set to 1 if the track is usable. It is possible to turn a not usable track into a usable
       * track using chapter codecs or control tracks.
       */
      val FlagEnabled: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FlagEnabled",
          0xB9), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Set if that track (audio, video or subs) is eligible for automatic selection by the player;
       * see (#default-track-selection) for more details.
       */
      val FlagDefault: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FlagDefault",
          0x88), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Applies only to subtitles. Set if that track is eligible for automatic selection by the
       * player if it matches the user's language preference, even if the user's preferences would
       * normally not enable subtitles with the selected audio track; this can be used for tracks
       * containing only translations of foreign-language audio or onscreen text. See
       * (#default-track-selection) for more details.
       */
      val FlagForced: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FlagForced",
          0x55, 0xAA), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Set to 1 if and only if that track is suitable for users with hearing impairments.
       */
      val FlagHearingImpaired: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FlagHearingImpaired",
          0x55, 0xAB), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Set to 1 if and only if that track is suitable for users with visual impairments.
       */
      val FlagVisualImpaired: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FlagVisualImpaired",
          0x55, 0xAC), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Set to 1 if and only if that track contains textual descriptions of video content.
       */
      val FlagTextDescriptions: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FlagTextDescriptions",
          0x55, 0xAD), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Set to 1 if and only if that track is in the content's original language.
       */
      val FlagOriginal: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FlagOriginal",
          0x55, 0xAE), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Set to 1 if and only if that track contains commentary.
       */
      val FlagCommentary: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FlagCommentary",
          0x55, 0xAF), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Set to 1 if the track **MAY** contain blocks using lacing. When set to 0 all blocks
       * **MUST** have their lacing flags set to No lacing; see (#block-lacing) on Block Lacing.
       */
      val FlagLacing: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FlagLacing",
          0x9C), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * The minimum number of frames a player should be able to cache during playback. If set to 0,
       * the reference pseudo-cache system is not used.
       */
      val MinCache: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("MinCache",
          0x6D, 0xE7), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * The maximum cache size necessary to store referenced frames in and the current frame. 0
       * means no cache is needed.
       */
      val MaxCache: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("MaxCache",
          0x6D, 0xF8), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Number of nanoseconds per frame, expressed in Matroska Ticks -- i.e., in nanoseconds; see
       * (#timestamp-ticks) (frame in the Matroska sense -- one Element put into a (Simple)Block).
       */
      val DefaultDuration: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("DefaultDuration",
          0x23, 0xE3, 0x83), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * The period between two successive fields at the output of the decoding process, expressed
       * in Matroska Ticks -- i.e., in nanoseconds; see (#timestamp-ticks). see
       * (#defaultdecodedfieldduration) for more information
       */
      val DefaultDecodedFieldDuration: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("DefaultDecodedFieldDuration",
          0x23, 0x4E, 0x7A), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * The scale to apply on this track to work at normal speed in relation with other tracks
       * (mostly used to adjust video speed when the audio length differs).
       */
      val TrackTimestampScale: ElementDeclaration.FLOAT =
          ElementDeclaration.FLOAT.Actual(
              EbmlID("TrackTimestampScale",
          0x23, 0x31, 0x4F)
          )

      /**
       * **Definition**
       * A value to add to the Block's Timestamp, expressed in Matroska Ticks -- i.e., in
       * nanoseconds; see (#timestamp-ticks). This can be used to adjust the playback offset of a
       * track.
       */
      val TrackOffset: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("TrackOffset",
          0x53, 0x7F), EBMLIntegerType.Signed)

      /**
       * **Definition**
       * The maximum value of BlockAddID ((#blockaddid-element)). A value 0 means there is no
       * BlockAdditions ((#blockadditions-element)) for this track.
       */
      val MaxBlockAdditionID: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("MaxBlockAdditionID",
          0x55, 0xEE), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * A human-readable track name.
       */
      val Name: ElementDeclaration.STRING =
          ElementDeclaration.STRING.Actual(
              EbmlID("Name",
          0x53, 0x6E), true)

      /**
       * **Definition**
       * The language of the track, in the Matroska languages form; see (#language-codes) on
       * language codes. This Element **MUST** be ignored if the LanguageBCP47 Element is used in the
       * same TrackEntry.
       */
      val Language: ElementDeclaration.STRING =
          ElementDeclaration.STRING.Actual(
              EbmlID("Language",
          0x22, 0xB5, 0x9C), false)

      /**
       * **Definition**
       * The language of the track, in the [@!BCP47] form; see (#language-codes) on language codes.
       * If this Element is used, then any Language Elements used in the same TrackEntry **MUST** be
       * ignored.
       */
      val LanguageBCP47: ElementDeclaration.STRING =
          ElementDeclaration.STRING.Actual(
              EbmlID("LanguageBCP47",
          0x22, 0xB5, 0x9D), false)

      /**
       * **Definition**
       * An ID corresponding to the codec, see [@?MatroskaCodec] for more info.
       */
      val CodecID: ElementDeclaration.STRING =
          ElementDeclaration.STRING.Actual(
              EbmlID("CodecID",
          0x86), false)

      /**
       * **Definition**
       * Private data only known to the codec.
       */
      val CodecPrivate: ElementDeclaration.BINARY =
          ElementDeclaration.BINARY.Actual(
              EbmlID("CodecPrivate",
          0x63, 0xA2)
          )

      /**
       * **Definition**
       * A human-readable string specifying the codec.
       */
      val CodecName: ElementDeclaration.STRING =
          ElementDeclaration.STRING.Actual(
              EbmlID("CodecName",
          0x25, 0x86, 0x88), true)

      /**
       * **Definition**
       * The UID of an attachment that is used by this codec.**UsageNotes**
       * The value **MUST** match the `FileUID` value of an attachment found in this Segment.
       */
      val AttachmentLink: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("AttachmentLink",
          0x74, 0x46), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * A string describing the encoding setting used.
       */
      val CodecSettings: ElementDeclaration.STRING =
          ElementDeclaration.STRING.Actual(
              EbmlID("CodecSettings",
          0x3A, 0x96, 0x97), true)

      /**
       * **Definition**
       * A URL to find information about the codec used.
       */
      val CodecInfoURL: ElementDeclaration.STRING =
          ElementDeclaration.STRING.Actual(
              EbmlID("CodecInfoURL",
          0x3B, 0x40, 0x40), false)

      /**
       * **Definition**
       * A URL to download about the codec used.
       */
      val CodecDownloadURL: ElementDeclaration.STRING =
          ElementDeclaration.STRING.Actual(
              EbmlID("CodecDownloadURL",
          0x26, 0xB2, 0x40), false)

      /**
       * **Definition**
       * Set to 1 if the codec can decode potentially damaged data.
       */
      val CodecDecodeAll: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("CodecDecodeAll",
          0xAA), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Specify that this track is an overlay track for the Track specified (in the u-integer).
       * That means when this track has a gap on SilentTracks, the overlay track should be used
       * instead. The order of multiple TrackOverlay matters, the first one is the one that should be
       * used. If not found it should be the second, etc.
       */
      val TrackOverlay: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("TrackOverlay",
          0x6F, 0xAB), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * CodecDelay is The codec-built-in delay, expressed in Matroska Ticks -- i.e., in
       * nanoseconds; see (#timestamp-ticks). It represents the amount of codec samples that will be
       * discarded by the decoder during playback. This timestamp value **MUST** be subtracted from
       * each frame timestamp in order to get the timestamp that will be actually played. The value
       * **SHOULD** be small so the muxing of tracks with the same actual timestamp are in the same
       * Cluster.
       */
      val CodecDelay: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("CodecDelay",
          0x56, 0xAA), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * After a discontinuity, SeekPreRoll is the duration of the data the decoder **MUST** decode
       * before the decoded data is valid, expressed in Matroska Ticks -- i.e., in nanoseconds; see
       * (#timestamp-ticks).
       */
      val SeekPreRoll: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("SeekPreRoll",
          0x56, 0xBB), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * The TrackUID of the Smooth FF/RW video in the paired EBML structure corresponding to this
       * video track. See [@?DivXTrickTrack].
       */
      val TrickTrackUID: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("TrickTrackUID",
          0xC0), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * The SegmentUID of the Segment containing the track identified by TrickTrackUID. See
       * [@?DivXTrickTrack].
       */
      val TrickTrackSegmentUID: ElementDeclaration.BINARY =
          ElementDeclaration.BINARY.Actual(
              EbmlID("TrickTrackSegmentUID",
          0xC1)
          )

      /**
       * **Definition**
       * Set to 1 if this video track is a Smooth FF/RW track. If set to 1, MasterTrackUID and
       * MasterTrackSegUID should be present and BlockGroups for this track must contain ReferenceFrame
       * structures. Otherwise, TrickTrackUID and TrickTrackSegUID must be present if this track has a
       * corresponding Smooth FF/RW track. See [@?DivXTrickTrack].
       */
      val TrickTrackFlag: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("TrickTrackFlag",
          0xC6), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * The TrackUID of the video track in the paired EBML structure that corresponds to this
       * Smooth FF/RW track. See [@?DivXTrickTrack].
       */
      val TrickMasterTrackUID: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("TrickMasterTrackUID",
          0xC7), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * The SegmentUID of the Segment containing the track identified by MasterTrackUID. See
       * [@?DivXTrickTrack].
       */
      val TrickMasterTrackSegmentUID: ElementDeclaration.BINARY =
          ElementDeclaration.BINARY.Actual(
              EbmlID("TrickMasterTrackSegmentUID",
          0xC4)
          )

      init {
        add(TrackNumber)
        add(TrackUID)
        add(TrackType)
        add(FlagEnabled)
        add(FlagDefault)
        add(FlagForced)
        add(FlagHearingImpaired)
        add(FlagVisualImpaired)
        add(FlagTextDescriptions)
        add(FlagOriginal)
        add(FlagCommentary)
        add(FlagLacing)
        add(MinCache)
        add(MaxCache)
        add(DefaultDuration)
        add(DefaultDecodedFieldDuration)
        add(TrackTimestampScale)
        add(TrackOffset)
        add(MaxBlockAdditionID)
        add(Name)
        add(Language)
        add(LanguageBCP47)
        add(CodecID)
        add(CodecPrivate)
        add(CodecName)
        add(AttachmentLink)
        add(CodecSettings)
        add(CodecInfoURL)
        add(CodecDownloadURL)
        add(CodecDecodeAll)
        add(TrackOverlay)
        add(CodecDelay)
        add(SeekPreRoll)
        add(TrickTrackUID)
        add(TrickTrackSegmentUID)
        add(TrickTrackFlag)
        add(TrickMasterTrackUID)
        add(TrickMasterTrackSegmentUID)
        add(BlockAdditionMapping)
        add(TrackTranslate)
        add(Video)
        add(Audio)
        add(TrackOperation)
        add(ContentEncodings)
      }

      /**
       * **Definition**
       * The `TrackType` defines the type of each frame found in the Track. The value **SHOULD** be
       * stored on 1 octet.
       */
      enum class TrackType(
        val code: Long,
      ) {
        /**
         * **Definition**
         * An image.
         */
        video(1),
        /**
         * **Definition**
         * Audio samples.
         */
        audio(2),
        /**
         * **Definition**
         * A mix of different other TrackType. The codec needs to define how the `Matroska Player`
         * should interpret such data.
         */
        complex(3),
        /**
         * **Definition**
         * An image to be rendered over the video track(s).
         */
        logo(16),
        /**
         * **Definition**
         * Subtitle or closed caption data to be rendered over the video track(s).
         */
        subtitle(17),
        /**
         * **Definition**
         * Interactive button(s) to be rendered over the video track(s).
         */
        buttons(18),
        /**
         * **Definition**
         * Metadata used to control the player of the `Matroska Player`.
         */
        control(32),
        /**
         * **Definition**
         * Timed metadata that can be passed on to the `Matroska Player`.
         */
        metadata(33),
        ;

        companion object Declaration : ElementDeclaration.CUSTOM<TrackType> {
          override val id: EbmlID = EbmlID("TrackType", 0x83)

          override suspend fun process(`data`: Memory): TrackType {
            val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
            return entries.first { it.code == code }
          }
        }
      }

      /**
       * **Definition**
       * Contains elements that extend the track format, by adding content either to each frame,
       * with BlockAddID ((#blockaddid-element)), or to the track as a whole with BlockAddIDExtraData.
       */
      object BlockAdditionMapping :
          ElementDeclaration.MASTER.Actual(
              EbmlID("BlockAdditionMapping",
          0x41, 0xE4)
          ) {
        /**
         * **Definition**
         * If the track format extension needs content beside frames, the value refers to the
         * BlockAddID ((#blockaddid-element)), value being described.**UsageNotes**
         * To keep MaxBlockAdditionID as low as possible, small values **SHOULD** be used.
         */
        val BlockAddIDValue: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("BlockAddIDValue",
            0x41, 0xF0), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * A human-friendly name describing the type of BlockAdditional data, as defined by the
         * associated Block Additional Mapping.
         */
        val BlockAddIDName: ElementDeclaration.STRING =
            ElementDeclaration.STRING.Actual(
                EbmlID("BlockAddIDName",
            0x41, 0xA4), false)

        /**
         * **Definition**
         * Stores the registered identifier of the Block Additional Mapping to define how the
         * BlockAdditional data should be handled.**UsageNotes**
         * If BlockAddIDType is 0, the BlockAddIDValue and corresponding BlockAddID values **MUST**
         * be 1.
         */
        val BlockAddIDType: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("BlockAddIDType",
            0x41, 0xE7), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * Extra binary data that the BlockAddIDType can use to interpret the BlockAdditional data.
         * The interpretation of the binary data depends on the BlockAddIDType value and the
         * corresponding Block Additional Mapping.
         */
        val BlockAddIDExtraData: ElementDeclaration.BINARY =
            ElementDeclaration.BINARY.Actual(
                EbmlID("BlockAddIDExtraData",
            0x41, 0xED)
            )

        init {
          add(BlockAddIDValue)
          add(BlockAddIDName)
          add(BlockAddIDType)
          add(BlockAddIDExtraData)
        }
      }

      /**
       * **Definition**
       * The mapping between this `TrackEntry` and a track value in the given Chapter
       * Codec.**Rationale**
       * Chapter Codec may need to address content in specific track, but they may not know of the
       * way to identify tracks in Matroska. This element and its child elements add a way to map the
       * internal tracks known to the Chapter Codec to the track IDs in Matroska. This allows remuxing
       * a file with Chapter Codec without changing the content of the codec data, just the track
       * mapping.
       */
      object TrackTranslate :
          ElementDeclaration.MASTER.Actual(
              EbmlID("TrackTranslate", 0x66,
          0x24)
          ) {
        /**
         * **Definition**
         * The binary value used to represent this `TrackEntry` in the chapter codec data. The
         * format depends on the `ChapProcessCodecID` used; see (#chapprocesscodecid-element).
         */
        val TrackTranslateTrackID: ElementDeclaration.BINARY =
            ElementDeclaration.BINARY.Actual(
                EbmlID("TrackTranslateTrackID",
            0x66, 0xA5)
            )

        /**
         * **Definition**
         * Specify a chapter edition UID on which this `TrackTranslate` applies.**UsageNotes**
         * When no `TrackTranslateEditionUID` is specified in the `TrackTranslate`, the
         * `TrackTranslate` applies to all chapter editions found in the Segment using the given
         * `TrackTranslateCodec`.
         */
        val TrackTranslateEditionUID: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("TrackTranslateEditionUID",
            0x66, 0xFC), EBMLIntegerType.Unsigned)

        init {
          add(TrackTranslateTrackID)
          add(TrackTranslateCodec)
          add(TrackTranslateEditionUID)
        }

        /**
         * **Definition**
         * This `TrackTranslate` applies to this chapter codec of the given chapter edition(s); see
         * (#chapprocesscodecid-element).
         */
        enum class TrackTranslateCodec(
          val code: Long,
        ) {
          /**
           * **Definition**
           * Chapter commands using the Matroska Script codec.
           */
          `Matroska Script`(0),
          /**
           * **Definition**
           * Chapter commands using the DVD-like codec.
           */
          `DVD-menu`(1),
          ;

          companion object Declaration : ElementDeclaration.CUSTOM<TrackTranslateCodec> {
            override val id: EbmlID = EbmlID("TrackTranslateCodec", 0x66,
                0xBF)

            override suspend fun process(`data`: Memory): TrackTranslateCodec {
              val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
              return entries.first { it.code == code }
            }
          }
        }
      }

      /**
       * **Definition**
       * Video settings.
       */
      object Video : ElementDeclaration.MASTER.Actual(
          EbmlID("Video",
          0xE0)
      ) {
        /**
         * **Definition**
         * Width of the encoded video frames in pixels.
         */
        val PixelWidth: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("PixelWidth",
            0xB0), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * Height of the encoded video frames in pixels.
         */
        val PixelHeight: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("PixelHeight",
            0xBA), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * The number of video pixels to remove at the bottom of the image.
         */
        val PixelCropBottom: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("PixelCropBottom",
            0x54, 0xAA), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * The number of video pixels to remove at the top of the image.
         */
        val PixelCropTop: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("PixelCropTop",
            0x54, 0xBB), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * The number of video pixels to remove on the left of the image.
         */
        val PixelCropLeft: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("PixelCropLeft",
            0x54, 0xCC), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * The number of video pixels to remove on the right of the image.
         */
        val PixelCropRight: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("PixelCropRight",
            0x54, 0xDD), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * Width of the video frames to display. Applies to the video frame after cropping
         * (PixelCrop* Elements).
         */
        val DisplayWidth: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("DisplayWidth",
            0x54, 0xB0), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * Height of the video frames to display. Applies to the video frame after cropping
         * (PixelCrop* Elements).
         */
        val DisplayHeight: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("DisplayHeight",
            0x54, 0xBA), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * Specify the uncompressed pixel format used for the Track's data as a FourCC. This value
         * is similar in scope to the biCompression value of AVI's `BITMAPINFO` [@?AVIFormat]. There is
         * no definitive list of FourCC values, nor an official registry. Some common values for YUV
         * pixel formats can be found at [@?MSYUV8], [@?MSYUV16] and [@?FourCC-YUV]. Some common values
         * for uncompressed RGB pixel formats can be found at [@?MSRGB] and [@?FourCC-RGB].
         */
        val UncompressedFourCC: ElementDeclaration.BINARY =
            ElementDeclaration.BINARY.Actual(
                EbmlID("UncompressedFourCC",
            0x2E, 0xB5, 0x24)
            )

        /**
         * **Definition**
         * Gamma Value.
         */
        val GammaValue: ElementDeclaration.FLOAT =
            ElementDeclaration.FLOAT.Actual(
                EbmlID("GammaValue",
            0x2F, 0xB5, 0x23)
            )

        /**
         * **Definition**
         * Number of frames per second. This value is Informational only. It is intended for
         * constant frame rate streams, and should not be used for a variable frame rate TrackEntry.
         */
        val FrameRate: ElementDeclaration.FLOAT =
            ElementDeclaration.FLOAT.Actual(
                EbmlID("FrameRate",
            0x23, 0x83, 0xE3)
            )

        init {
          add(FlagInterlaced)
          add(FieldOrder)
          add(StereoMode)
          add(AlphaMode)
          add(OldStereoMode)
          add(PixelWidth)
          add(PixelHeight)
          add(PixelCropBottom)
          add(PixelCropTop)
          add(PixelCropLeft)
          add(PixelCropRight)
          add(DisplayWidth)
          add(DisplayHeight)
          add(DisplayUnit)
          add(AspectRatioType)
          add(UncompressedFourCC)
          add(GammaValue)
          add(FrameRate)
          add(Colour)
          add(Projection)
        }

        /**
         * **Definition**
         * Specify whether the video frames in this track are interlaced.
         */
        enum class FlagInterlaced(
          val code: Long,
        ) {
          /**
           * **Definition**
           * Unknown status.**UsageNotes**
           * This value **SHOULD** be avoided.
           */
          undetermined(0),
          /**
           * **Definition**
           * Interlaced frames.
           */
          interlaced(1),
          /**
           * **Definition**
           * No interlacing.
           */
          progressive(2),
          ;

          companion object Declaration : ElementDeclaration.CUSTOM<FlagInterlaced> {
            override val id: EbmlID = EbmlID("FlagInterlaced", 0x9A)

            override suspend fun process(`data`: Memory): FlagInterlaced {
              val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
              return entries.first { it.code == code }
            }
          }
        }

        /**
         * **Definition**
         * Specify the field ordering of video frames in this track.**UsageNotes**
         * If FlagInterlaced is not set to 1, this Element **MUST** be ignored.
         */
        enum class FieldOrder(
          val code: Long,
        ) {
          /**
           * **Definition**
           * Interlaced frames.**UsageNotes**
           * This value **SHOULD** be avoided, setting FlagInterlaced to 2 is sufficient.
           */
          progressive(0),
          /**
           * **Definition**
           * Top field displayed first. Top field stored first.
           */
          tff(1),
          /**
           * **Definition**
           * Unknown field order.**UsageNotes**
           * This value **SHOULD** be avoided.
           */
          undetermined(2),
          /**
           * **Definition**
           * Bottom field displayed first. Bottom field stored first.
           */
          bff(6),
          /**
           * `bff(swapped)`
           *
           * **Definition**
           * Top field displayed first. Fields are interleaved in storage with the top line of the
           * top field stored first.
           */
          `bff swapped`(9),
          /**
           * `tff(swapped)`
           *
           * **Definition**
           * Bottom field displayed first. Fields are interleaved in storage with the top line of
           * the top field stored first.
           */
          `tff swapped`(14),
          ;

          companion object Declaration : ElementDeclaration.CUSTOM<FieldOrder> {
            override val id: EbmlID = EbmlID("FieldOrder", 0x9D)

            override suspend fun process(`data`: Memory): FieldOrder {
              val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
              return entries.first { it.code == code }
            }
          }
        }

        /**
         * **Definition**
         * Stereo-3D video mode. There are some more details in (#multi-planar-and-3d-videos).
         */
        enum class StereoMode(
          val code: Long,
        ) {
          mono(0),
          /**
           * `side by side (left eye first)`
           */
          `side by side left eye first`(1),
          /**
           * `top - bottom (right eye is first)`
           */
          `top - bottom right eye is first`(2),
          /**
           * `top - bottom (left eye is first)`
           */
          `top - bottom left eye is first`(3),
          /**
           * `checkboard (right eye is first)`
           */
          `checkboard right eye is first`(4),
          /**
           * `checkboard (left eye is first)`
           */
          `checkboard left eye is first`(5),
          /**
           * `row interleaved (right eye is first)`
           */
          `row interleaved right eye is first`(6),
          /**
           * `row interleaved (left eye is first)`
           */
          `row interleaved left eye is first`(7),
          /**
           * `column interleaved (right eye is first)`
           */
          `column interleaved right eye is first`(8),
          /**
           * `column interleaved (left eye is first)`
           */
          `column interleaved left eye is first`(9),
          /**
           * `anaglyph (cyan/red)`
           */
          `anaglyph cyan red`(10),
          /**
           * `side by side (right eye first)`
           */
          `side by side right eye first`(11),
          /**
           * `anaglyph (green/magenta)`
           */
          `anaglyph green magenta`(12),
          /**
           * `both eyes laced in one Block (left eye is first)`
           */
          `both eyes laced in one Block left eye is first`(13),
          /**
           * `both eyes laced in one Block (right eye is first)`
           */
          `both eyes laced in one Block right eye is first`(14),
          ;

          companion object Declaration : ElementDeclaration.CUSTOM<StereoMode> {
            override val id: EbmlID = EbmlID("StereoMode", 0x53, 0xB8)

            override suspend fun process(`data`: Memory): StereoMode {
              val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
              return entries.first { it.code == code }
            }
          }
        }

        /**
         * **Definition**
         * Indicate whether the BlockAdditional Element with BlockAddID of "1" contains Alpha data,
         * as defined by to the Codec Mapping for the `CodecID`. Undefined values **SHOULD NOT** be
         * used as the behavior of known implementations is different (considered either as 0 or 1).
         */
        enum class AlphaMode(
          val code: Long,
        ) {
          /**
           * **Definition**
           * The BlockAdditional Element with BlockAddID of "1" does not exist or **SHOULD NOT** be
           * considered as containing such data.
           */
          none(0),
          /**
           * **Definition**
           * The BlockAdditional Element with BlockAddID of "1" contains alpha channel data.
           */
          present(1),
          ;

          companion object Declaration : ElementDeclaration.CUSTOM<AlphaMode> {
            override val id: EbmlID = EbmlID("AlphaMode", 0x53, 0xC0)

            override suspend fun process(`data`: Memory): AlphaMode {
              val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
              return entries.first { it.code == code }
            }
          }
        }

        /**
         * **Definition**
         * Bogus StereoMode value used in old versions of libmatroska.**UsageNotes**
         * This Element **MUST NOT** be used. It was an incorrect value used in libmatroska up to
         * 0.9.0.
         */
        enum class OldStereoMode(
          val code: Long,
        ) {
          mono(0),
          `right eye`(1),
          `left eye`(2),
          `both eyes`(3),
          ;

          companion object Declaration : ElementDeclaration.CUSTOM<OldStereoMode> {
            override val id: EbmlID = EbmlID("OldStereoMode", 0x53, 0xB9)

            override suspend fun process(`data`: Memory): OldStereoMode {
              val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
              return entries.first { it.code == code }
            }
          }
        }

        /**
         * **Definition**
         * How DisplayWidth & DisplayHeight are interpreted.
         */
        enum class DisplayUnit(
          val code: Long,
        ) {
          pixels(0),
          centimeters(1),
          inches(2),
          `display aspect ratio`(3),
          unknown(4),
          ;

          companion object Declaration : ElementDeclaration.CUSTOM<DisplayUnit> {
            override val id: EbmlID = EbmlID("DisplayUnit", 0x54, 0xB2)

            override suspend fun process(`data`: Memory): DisplayUnit {
              val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
              return entries.first { it.code == code }
            }
          }
        }

        /**
         * **Definition**
         * Specify the possible modifications to the aspect ratio.
         */
        enum class AspectRatioType(
          val code: Long,
        ) {
          `free resizing`(0),
          `keep aspect ratio`(1),
          fixed(2),
          ;

          companion object Declaration : ElementDeclaration.CUSTOM<AspectRatioType> {
            override val id: EbmlID = EbmlID("AspectRatioType", 0x54, 0xB3)

            override suspend fun process(`data`: Memory): AspectRatioType {
              val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
              return entries.first { it.code == code }
            }
          }
        }

        /**
         * **Definition**
         * Settings describing the colour format.
         */
        object Colour :
            ElementDeclaration.MASTER.Actual(EbmlID("Colour", 0x55, 0xB0)) {
          /**
           * **Definition**
           * Number of decoded bits per channel. A value of 0 indicates that the BitsPerChannel is
           * unspecified.
           */
          val BitsPerChannel: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("BitsPerChannel",
              0x55, 0xB2), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * The amount of pixels to remove in the Cr and Cb channels for every pixel not removed
           * horizontally. Example: For video with 4:2:0 chroma subsampling, the ChromaSubsamplingHorz
           * **SHOULD** be set to 1.
           */
          val ChromaSubsamplingHorz: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("ChromaSubsamplingHorz",
              0x55, 0xB3), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * The amount of pixels to remove in the Cr and Cb channels for every pixel not removed
           * vertically. Example: For video with 4:2:0 chroma subsampling, the ChromaSubsamplingVert
           * **SHOULD** be set to 1.
           */
          val ChromaSubsamplingVert: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("ChromaSubsamplingVert",
              0x55, 0xB4), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * The amount of pixels to remove in the Cb channel for every pixel not removed
           * horizontally. This is additive with ChromaSubsamplingHorz. Example: For video with 4:2:1
           * chroma subsampling, the ChromaSubsamplingHorz **SHOULD** be set to 1 and CbSubsamplingHorz
           * **SHOULD** be set to 1.
           */
          val CbSubsamplingHorz: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("CbSubsamplingHorz",
              0x55, 0xB5), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * The amount of pixels to remove in the Cb channel for every pixel not removed
           * vertically. This is additive with ChromaSubsamplingVert.
           */
          val CbSubsamplingVert: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("CbSubsamplingVert",
              0x55, 0xB6), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * Maximum brightness of a single pixel (Maximum Content Light Level) in candelas per
           * square meter (cd/m^2^).
           */
          val MaxCLL: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("MaxCLL",
              0x55, 0xBC), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * Maximum brightness of a single full frame (Maximum Frame-Average Light Level) in
           * candelas per square meter (cd/m^2^).
           */
          val MaxFALL: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("MaxFALL",
              0x55, 0xBD), EBMLIntegerType.Unsigned)

          init {
            add(MatrixCoefficients)
            add(BitsPerChannel)
            add(ChromaSubsamplingHorz)
            add(ChromaSubsamplingVert)
            add(CbSubsamplingHorz)
            add(CbSubsamplingVert)
            add(ChromaSitingHorz)
            add(ChromaSitingVert)
            add(Range)
            add(TransferCharacteristics)
            add(Primaries)
            add(MaxCLL)
            add(MaxFALL)
            add(MasteringMetadata)
          }

          /**
           * **Definition**
           * The Matrix Coefficients of the video used to derive luma and chroma values from red,
           * green, and blue color primaries. For clarity, the value and meanings for
           * MatrixCoefficients are adopted from Table 4 of [@!ITU-H.273].
           */
          enum class MatrixCoefficients(
            val code: Long,
          ) {
            Identity(0),
            /**
             * `ITU-R BT.709`
             */
            `ITU-R BT 709`(1),
            unspecified(2),
            reserved(3),
            /**
             * `US FCC 73.682`
             */
            `US FCC 73 682`(4),
            /**
             * `ITU-R BT.470BG`
             */
            `ITU-R BT 470BG`(5),
            `SMPTE 170M`(6),
            `SMPTE 240M`(7),
            YCoCg(8),
            `BT2020 Non-constant Luminance`(9),
            `BT2020 Constant Luminance`(10),
            `SMPTE ST 2085`(11),
            `Chroma-derived Non-constant Luminance`(12),
            `Chroma-derived Constant Luminance`(13),
            /**
             * `ITU-R BT.2100-0`
             */
            `ITU-R BT 2100-0`(14),
            ;

            companion object Declaration : ElementDeclaration.CUSTOM<MatrixCoefficients> {
              override val id: EbmlID = EbmlID("MatrixCoefficients", 0x55,
                  0xB1)

              override suspend fun process(`data`: Memory): MatrixCoefficients {
                val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                return entries.first { it.code == code }
              }
            }
          }

          /**
           * **Definition**
           * How chroma is subsampled horizontally.
           */
          enum class ChromaSitingHorz(
            val code: Long,
          ) {
            unspecified(0),
            `left collocated`(1),
            half(2),
            ;

            companion object Declaration : ElementDeclaration.CUSTOM<ChromaSitingHorz> {
              override val id: EbmlID = EbmlID("ChromaSitingHorz", 0x55, 0xB7)

              override suspend fun process(`data`: Memory): ChromaSitingHorz {
                val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                return entries.first { it.code == code }
              }
            }
          }

          /**
           * **Definition**
           * How chroma is subsampled vertically.
           */
          enum class ChromaSitingVert(
            val code: Long,
          ) {
            unspecified(0),
            `top collocated`(1),
            half(2),
            ;

            companion object Declaration : ElementDeclaration.CUSTOM<ChromaSitingVert> {
              override val id: EbmlID = EbmlID("ChromaSitingVert", 0x55, 0xB8)

              override suspend fun process(`data`: Memory): ChromaSitingVert {
                val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                return entries.first { it.code == code }
              }
            }
          }

          /**
           * **Definition**
           * Clipping of the color ranges.
           */
          enum class Range(
            val code: Long,
          ) {
            unspecified(0),
            `broadcast range`(1),
            /**
             * `full range (no clipping)`
             */
            `full range no clipping`(2),
            /**
             * `defined by MatrixCoefficients / TransferCharacteristics`
             */
            `defined by MatrixCoefficients TransferCharacteristics`(3),
            ;

            companion object Declaration : ElementDeclaration.CUSTOM<Range> {
              override val id: EbmlID = EbmlID("Range", 0x55, 0xB9)

              override suspend fun process(`data`: Memory): Range {
                val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                return entries.first { it.code == code }
              }
            }
          }

          /**
           * **Definition**
           * The transfer characteristics of the video. For clarity, the value and meanings for
           * TransferCharacteristics are adopted from Table 3 of [@!ITU-H.273].
           */
          enum class TransferCharacteristics(
            val code: Long,
          ) {
            reserved(0),
            /**
             * `ITU-R BT.709`
             */
            `ITU-R BT 709`(1),
            unspecified(2),
            reserved2(3),
            /**
             * `Gamma 2.2 curve - BT.470M`
             */
            `Gamma 2 2 curve - BT 470M`(4),
            /**
             * `Gamma 2.8 curve - BT.470BG`
             */
            `Gamma 2 8 curve - BT 470BG`(5),
            `SMPTE 170M`(6),
            `SMPTE 240M`(7),
            Linear(8),
            Log(9),
            `Log Sqrt`(10),
            `IEC 61966-2-4`(11),
            /**
             * `ITU-R BT.1361 Extended Colour Gamut`
             */
            `ITU-R BT 1361 Extended Colour Gamut`(12),
            `IEC 61966-2-1`(13),
            /**
             * `ITU-R BT.2020 10 bit`
             */
            `ITU-R BT 2020 10 bit`(14),
            /**
             * `ITU-R BT.2020 12 bit`
             */
            `ITU-R BT 2020 12 bit`(15),
            /**
             * `ITU-R BT.2100 Perceptual Quantization`
             */
            `ITU-R BT 2100 Perceptual Quantization`(16),
            `SMPTE ST 428-1`(17),
            /**
             * `ARIB STD-B67 (HLG)`
             */
            `ARIB STD-B67 HLG`(18),
            ;

            companion object Declaration : ElementDeclaration.CUSTOM<TransferCharacteristics>
                {
              override val id: EbmlID = EbmlID("TransferCharacteristics",
                  0x55, 0xBA)

              override suspend fun process(`data`: Memory): TransferCharacteristics {
                val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                return entries.first { it.code == code }
              }
            }
          }

          /**
           * **Definition**
           * The colour primaries of the video. For clarity, the value and meanings for Primaries
           * are adopted from Table 2 of [@!ITU-H.273].
           */
          enum class Primaries(
            val code: Long,
          ) {
            reserved(0),
            /**
             * `ITU-R BT.709`
             */
            `ITU-R BT 709`(1),
            unspecified(2),
            reserved2(3),
            /**
             * `ITU-R BT.470M`
             */
            `ITU-R BT 470M`(4),
            /**
             * `ITU-R BT.470BG - BT.601 625`
             */
            `ITU-R BT 470BG - BT 601 625`(5),
            /**
             * `ITU-R BT.601 525 - SMPTE 170M`
             */
            `ITU-R BT 601 525 - SMPTE 170M`(6),
            `SMPTE 240M`(7),
            FILM(8),
            /**
             * `ITU-R BT.2020`
             */
            `ITU-R BT 2020`(9),
            `SMPTE ST 428-1`(10),
            `SMPTE RP 432-2`(11),
            `SMPTE EG 432-2`(12),
            /**
             * `EBU Tech. 3213-E - JEDEC P22 phosphors`
             */
            `EBU Tech 3213-E - JEDEC P22 phosphors`(22),
            ;

            companion object Declaration : ElementDeclaration.CUSTOM<Primaries> {
              override val id: EbmlID = EbmlID("Primaries", 0x55, 0xBB)

              override suspend fun process(`data`: Memory): Primaries {
                val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                return entries.first { it.code == code }
              }
            }
          }

          /**
           * **Definition**
           * SMPTE 2086 mastering data.
           */
          object MasteringMetadata :
              ElementDeclaration.MASTER.Actual(
                  EbmlID("MasteringMetadata",
              0x55, 0xD0)
              ) {
            /**
             * **Definition**
             * Red X chromaticity coordinate, as defined by [@!CIE-1931].
             */
            val PrimaryRChromaticityX: ElementDeclaration.FLOAT =
                ElementDeclaration.FLOAT.Actual(
                    EbmlID("PrimaryRChromaticityX",
                0x55, 0xD1)
                )

            /**
             * **Definition**
             * Red Y chromaticity coordinate, as defined by [@!CIE-1931].
             */
            val PrimaryRChromaticityY: ElementDeclaration.FLOAT =
                ElementDeclaration.FLOAT.Actual(
                    EbmlID("PrimaryRChromaticityY",
                0x55, 0xD2)
                )

            /**
             * **Definition**
             * Green X chromaticity coordinate, as defined by [@!CIE-1931].
             */
            val PrimaryGChromaticityX: ElementDeclaration.FLOAT =
                ElementDeclaration.FLOAT.Actual(
                    EbmlID("PrimaryGChromaticityX",
                0x55, 0xD3)
                )

            /**
             * **Definition**
             * Green Y chromaticity coordinate, as defined by [@!CIE-1931].
             */
            val PrimaryGChromaticityY: ElementDeclaration.FLOAT =
                ElementDeclaration.FLOAT.Actual(
                    EbmlID("PrimaryGChromaticityY",
                0x55, 0xD4)
                )

            /**
             * **Definition**
             * Blue X chromaticity coordinate, as defined by [@!CIE-1931].
             */
            val PrimaryBChromaticityX: ElementDeclaration.FLOAT =
                ElementDeclaration.FLOAT.Actual(
                    EbmlID("PrimaryBChromaticityX",
                0x55, 0xD5)
                )

            /**
             * **Definition**
             * Blue Y chromaticity coordinate, as defined by [@!CIE-1931].
             */
            val PrimaryBChromaticityY: ElementDeclaration.FLOAT =
                ElementDeclaration.FLOAT.Actual(
                    EbmlID("PrimaryBChromaticityY",
                0x55, 0xD6)
                )

            /**
             * **Definition**
             * White X chromaticity coordinate, as defined by [@!CIE-1931].
             */
            val WhitePointChromaticityX: ElementDeclaration.FLOAT =
                ElementDeclaration.FLOAT.Actual(
                    EbmlID("WhitePointChromaticityX",
                0x55, 0xD7)
                )

            /**
             * **Definition**
             * White Y chromaticity coordinate, as defined by [@!CIE-1931].
             */
            val WhitePointChromaticityY: ElementDeclaration.FLOAT =
                ElementDeclaration.FLOAT.Actual(
                    EbmlID("WhitePointChromaticityY",
                0x55, 0xD8)
                )

            /**
             * **Definition**
             * Maximum luminance. Represented in candelas per square meter (cd/m^2^).
             */
            val LuminanceMax: ElementDeclaration.FLOAT =
                ElementDeclaration.FLOAT.Actual(
                    EbmlID("LuminanceMax",
                0x55, 0xD9)
                )

            /**
             * **Definition**
             * Minimum luminance. Represented in candelas per square meter (cd/m^2^).
             */
            val LuminanceMin: ElementDeclaration.FLOAT =
                ElementDeclaration.FLOAT.Actual(
                    EbmlID("LuminanceMin",
                0x55, 0xDA)
                )

            init {
              add(PrimaryRChromaticityX)
              add(PrimaryRChromaticityY)
              add(PrimaryGChromaticityX)
              add(PrimaryGChromaticityY)
              add(PrimaryBChromaticityX)
              add(PrimaryBChromaticityY)
              add(WhitePointChromaticityX)
              add(WhitePointChromaticityY)
              add(LuminanceMax)
              add(LuminanceMin)
            }
          }
        }

        /**
         * **Definition**
         * Describes the video projection details. Used to render spherical, VR videos or flipping
         * videos horizontally/vertically.
         */
        object Projection :
            ElementDeclaration.MASTER.Actual(EbmlID("Projection", 0x76, 0x70))
            {
          /**
           * **Definition**
           * Private data that only applies to a specific projection. * If `ProjectionType` equals 0
           * (Rectangular), then this element **MUST NOT** be present. * If `ProjectionType` equals 1
           * (Equirectangular), then this element **MUST** be present and contain the same binary data
           * that would be stored inside an ISOBMFF Equirectangular Projection Box ('equi'). * If
           * `ProjectionType` equals 2 (Cubemap), then this element **MUST** be present and contain the
           * same binary data that would be stored inside an ISOBMFF Cubemap Projection Box ('cbmp'). *
           * If `ProjectionType` equals 3 (Mesh), then this element **MUST** be present and contain the
           * same binary data that would be stored inside an ISOBMFF Mesh Projection Box
           * ('mshp').**UsageNotes**
           * ISOBMFF box size and fourcc fields are not included in the binary data, but the FullBox
           * version and flag fields are. This is to avoid redundant framing information while
           * preserving versioning and semantics between the two container formats.
           */
          val ProjectionPrivate: ElementDeclaration.BINARY =
              ElementDeclaration.BINARY.Actual(
                  EbmlID("ProjectionPrivate",
              0x76, 0x72)
              )

          /**
           * **Definition**
           * Specifies a yaw rotation to the projection. Value represents a clockwise rotation, in
           * degrees, around the up vector. This rotation must be applied before any
           * `ProjectionPosePitch` or `ProjectionPoseRoll` rotations. The value of this element
           * **MUST** be in the -180 to 180 degree range, both included. Setting `ProjectionPoseYaw` to
           * 180 or -180 degrees, with the `ProjectionPoseRoll` and `ProjectionPosePitch` set to 0
           * degrees flips the image horizontally.
           */
          val ProjectionPoseYaw: ElementDeclaration.FLOAT =
              ElementDeclaration.FLOAT.Actual(
                  EbmlID("ProjectionPoseYaw",
              0x76, 0x73)
              )

          /**
           * **Definition**
           * Specifies a pitch rotation to the projection. Value represents a counter-clockwise
           * rotation, in degrees, around the right vector. This rotation must be applied after the
           * `ProjectionPoseYaw` rotation and before the `ProjectionPoseRoll` rotation. The value of
           * this element **MUST** be in the -90 to 90 degree range, both included.
           */
          val ProjectionPosePitch: ElementDeclaration.FLOAT =
              ElementDeclaration.FLOAT.Actual(
                  EbmlID("ProjectionPosePitch",
              0x76, 0x74)
              )

          /**
           * **Definition**
           * Specifies a roll rotation to the projection. Value represents a counter-clockwise
           * rotation, in degrees, around the forward vector. This rotation must be applied after the
           * `ProjectionPoseYaw` and `ProjectionPosePitch` rotations. The value of this element
           * **MUST** be in the -180 to 180 degree range, both included. Setting `ProjectionPoseRoll`
           * to 180 or -180 degrees, the `ProjectionPoseYaw` to 180 or -180 degrees with
           * `ProjectionPosePitch` set to 0 degrees flips the image vertically. Setting
           * `ProjectionPoseRoll` to 180 or -180 degrees, with the `ProjectionPoseYaw` and
           * `ProjectionPosePitch` set to 0 degrees flips the image horizontally and vertically.
           */
          val ProjectionPoseRoll: ElementDeclaration.FLOAT =
              ElementDeclaration.FLOAT.Actual(
                  EbmlID("ProjectionPoseRoll",
              0x76, 0x75)
              )

          init {
            add(ProjectionType)
            add(ProjectionPrivate)
            add(ProjectionPoseYaw)
            add(ProjectionPosePitch)
            add(ProjectionPoseRoll)
          }

          /**
           * **Definition**
           * Describes the projection used for this video track.
           */
          enum class ProjectionType(
            val code: Long,
          ) {
            rectangular(0),
            equirectangular(1),
            cubemap(2),
            mesh(3),
            ;

            companion object Declaration : ElementDeclaration.CUSTOM<ProjectionType> {
              override val id: EbmlID = EbmlID("ProjectionType", 0x76, 0x71)

              override suspend fun process(`data`: Memory): ProjectionType {
                val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                return entries.first { it.code == code }
              }
            }
          }
        }
      }

      /**
       * **Definition**
       * Audio settings.
       */
      object Audio : ElementDeclaration.MASTER.Actual(
          EbmlID("Audio",
          0xE1)
      ) {
        /**
         * **Definition**
         * Sampling frequency in Hz.
         */
        val SamplingFrequency: ElementDeclaration.FLOAT =
            ElementDeclaration.FLOAT.Actual(
                EbmlID("SamplingFrequency",
            0xB5)
            )

        /**
         * **Definition**
         * Real output sampling frequency in Hz (used for SBR techniques).
         */
        val OutputSamplingFrequency: ElementDeclaration.FLOAT =
            ElementDeclaration.FLOAT.Actual(
                EbmlID("OutputSamplingFrequency",
            0x78, 0xB5)
            )

        /**
         * **Definition**
         * Numbers of channels in the track.
         */
        val Channels: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("Channels",
            0x9F), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * Table of horizontal angles for each successive channel.
         */
        val ChannelPositions: ElementDeclaration.BINARY =
            ElementDeclaration.BINARY.Actual(
                EbmlID("ChannelPositions",
            0x7D, 0x7B)
            )

        /**
         * **Definition**
         * Bits per sample, mostly used for PCM.
         */
        val BitDepth: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("BitDepth",
            0x62, 0x64), EBMLIntegerType.Unsigned)

        init {
          add(SamplingFrequency)
          add(OutputSamplingFrequency)
          add(Channels)
          add(ChannelPositions)
          add(BitDepth)
          add(Emphasis)
        }

        /**
         * **Definition**
         * Audio emphasis applied on audio samples. The player **MUST** apply the inverse emphasis
         * to get the proper audio samples.
         */
        enum class Emphasis(
          val code: Long,
        ) {
          `No emphasis`(0),
          /**
           * **Definition**
           * First order filter with zero point at 50 microseconds and a pole at 15 microseconds.
           * Also found on DVD Audio and MPEG audio.
           */
          `CD audio`(1),
          reserved(2),
          /**
           * `CCIT J.17`
           *
           * **Definition**
           * Defined in [@!ITU-J.17].
           */
          `CCIT J 17`(3),
          /**
           * **Definition**
           * FM Radio in Europe. RC Filter with a time constant of 50 microseconds.
           */
          `FM 50`(4),
          /**
           * **Definition**
           * FM Radio in the USA. RC Filter with a time constant of 75 microseconds.
           */
          `FM 75`(5),
          /**
           * **Definition**
           * Phono filter with time constants of t1=3180, t2=318 and t3=75 microseconds. [@!NAB1964]
           */
          `Phono RIAA`(10),
          /**
           * **Definition**
           * Phono filter with time constants of t1=3180, t2=450 and t3=50 microseconds.
           */
          `Phono IEC N78`(11),
          /**
           * **Definition**
           * Phono filter with time constants of t1=3180, t2=318 and t3=50 microseconds.
           */
          `Phono TELDEC`(12),
          /**
           * **Definition**
           * Phono filter with time constants of t1=2500, t2=500 and t3=70 microseconds.
           */
          `Phono EMI`(13),
          /**
           * **Definition**
           * Phono filter with time constants of t1=1590, t2=318 and t3=100 microseconds.
           */
          `Phono Columbia LP`(14),
          /**
           * **Definition**
           * Phono filter with time constants of t1=1590, t2=318 and t3=50 microseconds.
           */
          `Phono LONDON`(15),
          /**
           * **Definition**
           * Phono filter with time constants of t1=3180, t2=318 and t3=100 microseconds.
           */
          `Phono NARTB`(16),
          ;

          companion object Declaration : ElementDeclaration.CUSTOM<Emphasis> {
            override val id: EbmlID = EbmlID("Emphasis", 0x52, 0xF1)

            override suspend fun process(`data`: Memory): Emphasis {
              val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
              return entries.first { it.code == code }
            }
          }
        }
      }

      /**
       * **Definition**
       * Operation that needs to be applied on tracks to create this virtual track. For more details
       * look at (#track-operation).
       */
      object TrackOperation :
          ElementDeclaration.MASTER.Actual(EbmlID("TrackOperation", 0xE2)) {
        init {
          add(TrackCombinePlanes)
          add(TrackJoinBlocks)
        }

        /**
         * **Definition**
         * Contains the list of all video plane tracks that need to be combined to create this 3D
         * track
         */
        object TrackCombinePlanes :
            ElementDeclaration.MASTER.Actual(
                EbmlID("TrackCombinePlanes",
            0xE3)
            ) {
          init {
            add(TrackPlane)
          }

          /**
           * **Definition**
           * Contains a video plane track that need to be combined to create this 3D track
           */
          object TrackPlane :
              ElementDeclaration.MASTER.Actual(EbmlID("TrackPlane", 0xE4)) {
            /**
             * **Definition**
             * The trackUID number of the track representing the plane.
             */
            val TrackPlaneUID: ElementDeclaration.INTEGER =
                ElementDeclaration.INTEGER.Actual(
                    EbmlID("TrackPlaneUID",
                0xE5), EBMLIntegerType.Unsigned)

            init {
              add(TrackPlaneUID)
              add(TrackPlaneType)
            }

            /**
             * **Definition**
             * The kind of plane this track corresponds to.
             */
            enum class TrackPlaneType(
              val code: Long,
            ) {
              `left eye`(0),
              `right eye`(1),
              background(2),
              ;

              companion object Declaration : ElementDeclaration.CUSTOM<TrackPlaneType> {
                override val id: EbmlID = EbmlID("TrackPlaneType", 0xE6)

                override suspend fun process(`data`: Memory): TrackPlaneType {
                  val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                  return entries.first { it.code == code }
                }
              }
            }
          }
        }

        /**
         * **Definition**
         * Contains the list of all tracks whose Blocks need to be combined to create this virtual
         * track
         */
        object TrackJoinBlocks :
            ElementDeclaration.MASTER.Actual(EbmlID("TrackJoinBlocks", 0xE9))
            {
          /**
           * **Definition**
           * The trackUID number of a track whose blocks are used to create this virtual track.
           */
          val TrackJoinUID: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("TrackJoinUID",
              0xED), EBMLIntegerType.Unsigned)

          init {
            add(TrackJoinUID)
          }
        }
      }

      /**
       * **Definition**
       * Settings for several content encoding mechanisms like compression or encryption.
       */
      object ContentEncodings :
          ElementDeclaration.MASTER.Actual(
              EbmlID("ContentEncodings", 0x6D,
          0x80)
          ) {
        init {
          add(ContentEncoding)
        }

        /**
         * **Definition**
         * Settings for one content encoding like compression or encryption.
         */
        object ContentEncoding :
            ElementDeclaration.MASTER.Actual(
                EbmlID("ContentEncoding", 0x62,
            0x40)
            ) {
          /**
           * **Definition**
           * Tell in which order to apply each `ContentEncoding` of the `ContentEncodings`. The
           * decoder/demuxer **MUST** start with the `ContentEncoding` with the highest
           * `ContentEncodingOrder` and work its way down to the `ContentEncoding` with the lowest
           * `ContentEncodingOrder`. This value **MUST** be unique over for each `ContentEncoding`
           * found in the `ContentEncodings` of this `TrackEntry`.
           */
          val ContentEncodingOrder: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("ContentEncodingOrder",
              0x50, 0x31), EBMLIntegerType.Unsigned)

          init {
            add(ContentEncodingOrder)
            add(ContentEncodingScope)
            add(ContentEncodingType)
            add(ContentCompression)
            add(ContentEncryption)
          }

          /**
           * **Definition**
           * A bit field that describes which Elements have been modified in this way. Values
           * (big-endian) can be OR'ed.
           */
          enum class ContentEncodingScope(
            val code: Long,
          ) {
            /**
             * **Definition**
             * All frame contents, excluding lacing data.
             */
            Block(1),
            /**
             * **Definition**
             * The track's `CodecPrivate` data.
             */
            Private(2),
            /**
             * **Definition**
             * The next ContentEncoding (next `ContentEncodingOrder`. Either the data inside
             * `ContentCompression` and/or `ContentEncryption`).**UsageNotes**
             * This value **SHOULD NOT** be used as it's not supported by players.
             */
            Next(4),
            ;

            companion object Declaration : ElementDeclaration.CUSTOM<ContentEncodingScope> {
              override val id: EbmlID = EbmlID("ContentEncodingScope", 0x50,
                  0x32)

              override suspend fun process(`data`: Memory): ContentEncodingScope {
                val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                return entries.first { it.code == code }
              }
            }
          }

          /**
           * **Definition**
           * A value describing what kind of transformation is applied.
           */
          enum class ContentEncodingType(
            val code: Long,
          ) {
            Compression(0),
            Encryption(1),
            ;

            companion object Declaration : ElementDeclaration.CUSTOM<ContentEncodingType> {
              override val id: EbmlID = EbmlID("ContentEncodingType", 0x50,
                  0x33)

              override suspend fun process(`data`: Memory): ContentEncodingType {
                val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                return entries.first { it.code == code }
              }
            }
          }

          /**
           * **Definition**
           * Settings describing the compression used. This Element **MUST** be present if the value
           * of ContentEncodingType is 0 and absent otherwise. Each block **MUST** be decompressable
           * even if no previous block is available in order not to prevent seeking.
           */
          object ContentCompression :
              ElementDeclaration.MASTER.Actual(
                  EbmlID("ContentCompression",
              0x50, 0x34)
              ) {
            /**
             * **Definition**
             * Settings that might be needed by the decompressor. For Header Stripping
             * (`ContentCompAlgo`=3), the bytes that were removed from the beginning of each frames of
             * the track.
             */
            val ContentCompSettings: ElementDeclaration.BINARY =
                ElementDeclaration.BINARY.Actual(
                    EbmlID("ContentCompSettings",
                0x42, 0x55)
                )

            init {
              add(ContentCompAlgo)
              add(ContentCompSettings)
            }

            /**
             * **Definition**
             * The compression algorithm used.**UsageNotes**
             * Compression method "1" (bzlib) and "2" (lzo1x) are lacking proper documentation on
             * the format which limits implementation possibilities. Due to licensing conflicts on
             * commonly available libraries compression methods "2" (lzo1x) does not offer widespread
             * interoperability. A Matroska Writer **SHOULD NOT** use these compression methods by
             * default. A Matroska Reader **MAY** support methods "1" and "2" as possible, and
             * **SHOULD** support other methods.
             */
            enum class ContentCompAlgo(
              val code: Long,
            ) {
              /**
               * **Definition**
               * zlib compression [@!RFC1950].
               */
              zlib(0),
              /**
               * **Definition**
               * bzip2 compression [@?BZIP2], **SHOULD NOT** be used; see usage notes.
               */
              bzlib(1),
              /**
               * **Definition**
               * Lempel-Ziv-Oberhumer compression [@?LZO], **SHOULD NOT** be used; see usage notes.
               */
              lzo1x(2),
              /**
               * **Definition**
               * Octets in `ContentCompSettings` ((#contentcompsettings-element)) have been stripped
               * from each frame.
               */
              `Header Stripping`(3),
              ;

              companion object Declaration : ElementDeclaration.CUSTOM<ContentCompAlgo> {
                override val id: EbmlID = EbmlID("ContentCompAlgo", 0x42,
                    0x54)

                override suspend fun process(`data`: Memory): ContentCompAlgo {
                  val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                  return entries.first { it.code == code }
                }
              }
            }
          }

          /**
           * **Definition**
           * Settings describing the encryption used. This Element **MUST** be present if the value
           * of `ContentEncodingType` is 1 (encryption) and **MUST** be ignored otherwise. A Matroska
           * Player **MAY** support encryption.
           */
          object ContentEncryption :
              ElementDeclaration.MASTER.Actual(
                  EbmlID("ContentEncryption",
              0x50, 0x35)
              ) {
            /**
             * **Definition**
             * For public key algorithms this is the ID of the public key the data was encrypted
             * with.
             */
            val ContentEncKeyID: ElementDeclaration.BINARY =
                ElementDeclaration.BINARY.Actual(
                    EbmlID("ContentEncKeyID",
                0x47, 0xE2)
                )

            /**
             * **Definition**
             * A cryptographic signature of the contents.
             */
            val ContentSignature: ElementDeclaration.BINARY =
                ElementDeclaration.BINARY.Actual(
                    EbmlID("ContentSignature",
                0x47, 0xE3)
                )

            /**
             * **Definition**
             * This is the ID of the private key the data was signed with.
             */
            val ContentSigKeyID: ElementDeclaration.BINARY =
                ElementDeclaration.BINARY.Actual(
                    EbmlID("ContentSigKeyID",
                0x47, 0xE4)
                )

            init {
              add(ContentEncAlgo)
              add(ContentEncKeyID)
              add(ContentSignature)
              add(ContentSigKeyID)
              add(ContentSigAlgo)
              add(ContentSigHashAlgo)
              add(ContentEncAESSettings)
            }

            /**
             * **Definition**
             * The encryption algorithm used.
             */
            enum class ContentEncAlgo(
              val code: Long,
            ) {
              /**
               * **Definition**
               * The data are not encrypted.
               */
              `Not encrypted`(0),
              /**
               * **Definition**
               * Data Encryption Standard (DES) [@?FIPS.46-3].**UsageNotes**
               * This value **SHOULD** be avoided.
               */
              DES(1),
              /**
               * **Definition**
               * Triple Data Encryption Algorithm [@?SP.800-67].**UsageNotes**
               * This value **SHOULD** be avoided.
               */
              `3DES`(2),
              /**
               * **Definition**
               * Twofish Encryption Algorithm [@?Twofish].
               */
              Twofish(3),
              /**
               * **Definition**
               * Blowfish Encryption Algorithm [@?Blowfish].**UsageNotes**
               * This value **SHOULD** be avoided.
               */
              Blowfish(4),
              /**
               * **Definition**
               * Advanced Encryption Standard (AES) [@?FIPS.197].
               */
              AES(5),
              ;

              companion object Declaration : ElementDeclaration.CUSTOM<ContentEncAlgo> {
                override val id: EbmlID = EbmlID("ContentEncAlgo", 0x47, 0xE1)

                override suspend fun process(`data`: Memory): ContentEncAlgo {
                  val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                  return entries.first { it.code == code }
                }
              }
            }

            /**
             * **Definition**
             * The algorithm used for the signature.
             */
            enum class ContentSigAlgo(
              val code: Long,
            ) {
              `Not signed`(0),
              RSA(1),
              ;

              companion object Declaration : ElementDeclaration.CUSTOM<ContentSigAlgo> {
                override val id: EbmlID = EbmlID("ContentSigAlgo", 0x47, 0xE5)

                override suspend fun process(`data`: Memory): ContentSigAlgo {
                  val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                  return entries.first { it.code == code }
                }
              }
            }

            /**
             * **Definition**
             * The hash algorithm used for the signature.
             */
            enum class ContentSigHashAlgo(
              val code: Long,
            ) {
              `Not signed`(0),
              `SHA1-160`(1),
              MD5(2),
              ;

              companion object Declaration : ElementDeclaration.CUSTOM<ContentSigHashAlgo> {
                override val id: EbmlID = EbmlID("ContentSigHashAlgo", 0x47,
                    0xE6)

                override suspend fun process(`data`: Memory): ContentSigHashAlgo {
                  val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                  return entries.first { it.code == code }
                }
              }
            }

            /**
             * **Definition**
             * Settings describing the encryption algorithm used.
             */
            object ContentEncAESSettings :
                ElementDeclaration.MASTER.Actual(
                    EbmlID("ContentEncAESSettings",
                0x47, 0xE7)
                ) {
              init {
                add(AESSettingsCipherMode)
              }

              /**
               * **Definition**
               * The AES cipher mode used in the encryption.
               */
              enum class AESSettingsCipherMode(
                val code: Long,
              ) {
                /**
                 * **Definition**
                 * Counter [@?SP.800-38A].
                 */
                `AES-CTR`(1),
                /**
                 * **Definition**
                 * Cipher Block Chaining [@?SP.800-38A].
                 */
                `AES-CBC`(2),
                ;

                companion object Declaration :
                    ElementDeclaration.CUSTOM<AESSettingsCipherMode> {
                  override val id: EbmlID = EbmlID("AESSettingsCipherMode",
                      0x47, 0xE8)

                  override suspend fun process(`data`: Memory): AESSettingsCipherMode {
                    val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                    return entries.first { it.code == code }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * **Definition**
   * A Top-Level Element to speed seeking access. All entries are local to the Segment.
   */
  object Cues : ElementDeclaration.MASTER.Actual(
      EbmlID("Cues", 0x1C,
      0x53, 0xBB, 0x6B)
  ) {
    init {
      add(CuePoint)
    }

    /**
     * **Definition**
     * Contains all information relative to a seek point in the Segment.
     */
    object CuePoint :
        ElementDeclaration.MASTER.Actual(EbmlID("CuePoint", 0xBB)) {
      /**
       * **Definition**
       * Absolute timestamp of the seek point, expressed in Matroska Ticks -- i.e., in nanoseconds;
       * see (#timestamp-ticks).
       */
      val CueTime: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("CueTime",
          0xB3), EBMLIntegerType.Unsigned)

      init {
        add(CueTime)
        add(CueTrackPositions)
      }

      /**
       * **Definition**
       * Contain positions for different tracks corresponding to the timestamp.
       */
      object CueTrackPositions :
          ElementDeclaration.MASTER.Actual(EbmlID("CueTrackPositions", 0xB7))
          {
        /**
         * **Definition**
         * The track for which a position is given.
         */
        val CueTrack: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("CueTrack",
            0xF7), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * The Segment Position ((#segment-position)) of the Cluster containing the associated
         * Block.
         */
        val CueClusterPosition: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("CueClusterPosition",
            0xF1), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * The relative position inside the Cluster of the referenced SimpleBlock or BlockGroup with
         * 0 being the first possible position for an Element inside that Cluster.
         */
        val CueRelativePosition: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("CueRelativePosition",
            0xF0), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * The duration of the block, expressed in Segment Ticks which is based on TimestampScale;
         * see (#timestamp-ticks). If missing, the track's DefaultDuration does not apply and no
         * duration information is available in terms of the cues.
         */
        val CueDuration: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("CueDuration",
            0xB2), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * Number of the Block in the specified Cluster.
         */
        val CueBlockNumber: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("CueBlockNumber",
            0x53, 0x78), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * The Segment Position ((#segment-position)) of the Codec State corresponding to this Cue
         * Element. 0 means that the data is taken from the initial Track Entry.
         */
        val CueCodecState: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("CueCodecState",
            0xEA), EBMLIntegerType.Unsigned)

        init {
          add(CueTrack)
          add(CueClusterPosition)
          add(CueRelativePosition)
          add(CueDuration)
          add(CueBlockNumber)
          add(CueCodecState)
          add(CueReference)
        }

        /**
         * **Definition**
         * The Clusters containing the referenced Blocks.
         */
        object CueReference :
            ElementDeclaration.MASTER.Actual(EbmlID("CueReference", 0xDB)) {
          /**
           * **Definition**
           * Timestamp of the referenced Block, expressed in Matroska Ticks -- i.e., in nanoseconds;
           * see (#timestamp-ticks).
           */
          val CueRefTime: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("CueRefTime",
              0x96), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * The Segment Position of the Cluster containing the referenced Block.
           */
          val CueRefCluster: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("CueRefCluster",
              0x97), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * Number of the referenced Block of Track X in the specified Cluster.
           */
          val CueRefNumber: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("CueRefNumber",
              0x53, 0x5F), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * The Segment Position of the Codec State corresponding to this referenced Element. 0
           * means that the data is taken from the initial Track Entry.
           */
          val CueRefCodecState: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("CueRefCodecState",
              0xEB), EBMLIntegerType.Unsigned)

          init {
            add(CueRefTime)
            add(CueRefCluster)
            add(CueRefNumber)
            add(CueRefCodecState)
          }
        }
      }
    }
  }

  /**
   * **Definition**
   * Contain attached files.
   */
  object Attachments :
      ElementDeclaration.MASTER.Actual(
          EbmlID("Attachments", 0x19, 0x41, 0xA4,
      0x69)
      ) {
    init {
      add(AttachedFile)
    }

    /**
     * **Definition**
     * An attached file.
     */
    object AttachedFile :
        ElementDeclaration.MASTER.Actual(EbmlID("AttachedFile", 0x61, 0xA7)) {
      /**
       * **Definition**
       * A human-friendly name for the attached file.
       */
      val FileDescription: ElementDeclaration.STRING =
          ElementDeclaration.STRING.Actual(
              EbmlID("FileDescription",
          0x46, 0x7E), true)

      /**
       * **Definition**
       * Filename of the attached file.
       */
      val FileName: ElementDeclaration.STRING =
          ElementDeclaration.STRING.Actual(
              EbmlID("FileName",
          0x46, 0x6E), true)

      /**
       * **Definition**
       * Media type of the file following the [@!RFC6838] format.
       */
      val FileMediaType: ElementDeclaration.STRING =
          ElementDeclaration.STRING.Actual(
              EbmlID("FileMediaType",
          0x46, 0x60), false)

      /**
       * **Definition**
       * The data of the file.
       */
      val FileData: ElementDeclaration.BINARY =
          ElementDeclaration.BINARY.Actual(
              EbmlID("FileData",
          0x46, 0x5C)
          )

      /**
       * **Definition**
       * Unique ID representing the file, as random as possible.
       */
      val FileUID: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FileUID",
          0x46, 0xAE), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * A binary value that a track/codec can refer to when the attachment is needed.
       */
      val FileReferral: ElementDeclaration.BINARY =
          ElementDeclaration.BINARY.Actual(
              EbmlID("FileReferral",
          0x46, 0x75)
          )

      /**
       * **Definition**
       * The timestamp at which this optimized font attachment comes into context, expressed in
       * Segment Ticks which is based on TimestampScale. See [@?DivXWorldFonts].**UsageNotes**
       * This element is reserved for future use and if written **MUST** be the segment start
       * timestamp.
       */
      val FileUsedStartTime: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FileUsedStartTime",
          0x46, 0x61), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * The timestamp at which this optimized font attachment goes out of context, expressed in
       * Segment Ticks which is based on TimestampScale. See [@?DivXWorldFonts].**UsageNotes**
       * This element is reserved for future use and if written **MUST** be the segment end
       * timestamp.
       */
      val FileUsedEndTime: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("FileUsedEndTime",
          0x46, 0x62), EBMLIntegerType.Unsigned)

      init {
        add(FileDescription)
        add(FileName)
        add(FileMediaType)
        add(FileData)
        add(FileUID)
        add(FileReferral)
        add(FileUsedStartTime)
        add(FileUsedEndTime)
      }
    }
  }

  /**
   * **Definition**
   * A system to define basic menus and partition data. For more detailed information, look at the
   * Chapters explanation in (#chapters).
   */
  object Chapters : ElementDeclaration.MASTER.Actual(
      EbmlID("Chapters",
      0x10, 0x43, 0xA7, 0x70)
  ) {
    init {
      add(EditionEntry)
    }

    /**
     * **Definition**
     * Contains all information about a Segment edition.
     */
    object EditionEntry :
        ElementDeclaration.MASTER.Actual(EbmlID("EditionEntry", 0x45, 0xB9)) {
      /**
       * **Definition**
       * A unique ID to identify the edition. It's useful for tagging an edition.
       */
      val EditionUID: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("EditionUID",
          0x45, 0xBC), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Set to 1 if an edition is hidden. Hidden editions **SHOULD NOT** be available to the user
       * interface (but still to Control Tracks; see (#chapter-flags) on Chapter flags).
       */
      val EditionFlagHidden: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("EditionFlagHidden",
          0x45, 0xBD), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Set to 1 if the edition **SHOULD** be used as the default one.
       */
      val EditionFlagDefault: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("EditionFlagDefault",
          0x45, 0xDB), EBMLIntegerType.Unsigned)

      /**
       * **Definition**
       * Set to 1 if the chapters can be defined multiple times and the order to play them is
       * enforced; see (#editionflagordered).
       */
      val EditionFlagOrdered: ElementDeclaration.INTEGER =
          ElementDeclaration.INTEGER.Actual(
              EbmlID("EditionFlagOrdered",
          0x45, 0xDD), EBMLIntegerType.Unsigned)

      init {
        add(EditionUID)
        add(EditionFlagHidden)
        add(EditionFlagDefault)
        add(EditionFlagOrdered)
        add(EditionDisplay)
        add(ChapterAtom)
      }

      /**
       * **Definition**
       * Contains a possible string to use for the edition display for the given languages.
       */
      object EditionDisplay :
          ElementDeclaration.MASTER.Actual(
              EbmlID("EditionDisplay", 0x45,
          0x20)
          ) {
        /**
         * **Definition**
         * Contains the string to use as the edition name.
         */
        val EditionString: ElementDeclaration.STRING =
            ElementDeclaration.STRING.Actual(
                EbmlID("EditionString",
            0x45, 0x21), true)

        /**
         * **Definition**
         * One language corresponding to the EditionString, in the [@!BCP47] form; see
         * (#language-codes) on language codes.
         */
        val EditionLanguageIETF: ElementDeclaration.STRING =
            ElementDeclaration.STRING.Actual(
                EbmlID("EditionLanguageIETF",
            0x45, 0xE4), false)

        init {
          add(EditionString)
          add(EditionLanguageIETF)
        }
      }

      /**
       * **Definition**
       * Contains the atom information to use as the chapter atom (apply to all tracks).
       */
      object ChapterAtom :
          ElementDeclaration.MASTER.Actual(EbmlID("ChapterAtom", 0xB6)) {
        /**
         * **Definition**
         * A unique ID to identify the Chapter.
         */
        val ChapterUID: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("ChapterUID",
            0x73, 0xC4), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * A unique string ID to identify the Chapter. For example it is used as the storage for
         * [@?WebVTT] cue identifier values.
         */
        val ChapterStringUID: ElementDeclaration.STRING =
            ElementDeclaration.STRING.Actual(
                EbmlID("ChapterStringUID",
            0x56, 0x54), true)

        /**
         * **Definition**
         * Timestamp of the start of Chapter, expressed in Matroska Ticks -- i.e., in nanoseconds;
         * see (#timestamp-ticks).
         */
        val ChapterTimeStart: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("ChapterTimeStart",
            0x91), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * Timestamp of the end of Chapter timestamp excluded, expressed in Matroska Ticks -- i.e.,
         * in nanoseconds; see (#timestamp-ticks). The value **MUST** be greater than or equal to the
         * `ChapterTimeStart` of the same `ChapterAtom`.**UsageNotes**
         * The `ChapterTimeEnd` timestamp value being excluded, it **MUST** take in account the
         * duration of the last frame it includes, especially for the `ChapterAtom` using the last
         * frames of the `Segment`.
         */
        val ChapterTimeEnd: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("ChapterTimeEnd",
            0x92), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * Set to 1 if a chapter is hidden. Hidden chapters **SHOULD NOT** be available to the user
         * interface (but still to Control Tracks; see (#chapterflaghidden) on Chapter flags).
         */
        val ChapterFlagHidden: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("ChapterFlagHidden",
            0x98), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * Set to 1 if the chapter is enabled. It can be enabled/disabled by a Control Track. When
         * disabled, the movie **SHOULD** skip all the content between the TimeStart and TimeEnd of
         * this chapter; see (#chapter-flags) on Chapter flags.
         */
        val ChapterFlagEnabled: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("ChapterFlagEnabled",
            0x45, 0x98), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * The SegmentUUID of another Segment to play during this chapter.**UsageNotes**
         * The value **MUST NOT** be the `SegmentUUID` value of the `Segment` it belongs to.
         */
        val ChapterSegmentUUID: ElementDeclaration.BINARY =
            ElementDeclaration.BINARY.Actual(
                EbmlID("ChapterSegmentUUID",
            0x6E, 0x67)
            )

        /**
         * **Definition**
         * The EditionUID to play from the Segment linked in ChapterSegmentUUID. If
         * ChapterSegmentEditionUID is undeclared, then no Edition of the linked Segment is used; see
         * (#medium-linking) on medium-linking Segments.
         */
        val ChapterSegmentEditionUID: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("ChapterSegmentEditionUID",
            0x6E, 0xBC), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * Specify the physical equivalent of this ChapterAtom like "DVD" (60) or "SIDE" (50); see
         * (#physical-types) for a complete list of values.
         */
        val ChapterPhysicalEquiv: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("ChapterPhysicalEquiv",
            0x63, 0xC3), EBMLIntegerType.Unsigned)

        init {
          add(ChapterUID)
          add(ChapterStringUID)
          add(ChapterTimeStart)
          add(ChapterTimeEnd)
          add(ChapterFlagHidden)
          add(ChapterFlagEnabled)
          add(ChapterSegmentUUID)
          add(ChapterSkipType)
          add(ChapterSegmentEditionUID)
          add(ChapterPhysicalEquiv)
          add(ChapterTrack)
          add(ChapterDisplay)
          add(ChapProcess)
        }

        /**
         * **Definition**
         * Indicate what type of content the ChapterAtom contains and might be skipped. It can be
         * used to automatically skip content based on the type. If a `ChapterAtom` is inside a
         * `ChapterAtom` that has a `ChapterSkipType` set, it **MUST NOT** have a `ChapterSkipType` or
         * have a `ChapterSkipType` with the same value as it's parent `ChapterAtom`. If the
         * `ChapterAtom` doesn't contain a `ChapterTimeEnd`, the value of the `ChapterSkipType` is only
         * valid until the next `ChapterAtom` with a `ChapterSkipType` value or the end of the file.
         */
        enum class ChapterSkipType(
          val code: Long,
        ) {
          /**
           * **Definition**
           * Content which should not be skipped.
           */
          `No Skipping`(0),
          /**
           * **Definition**
           * Credits usually found at the beginning of the content.
           */
          `Opening Credits`(1),
          /**
           * **Definition**
           * Credits usually found at the end of the content.
           */
          `End Credits`(2),
          /**
           * **Definition**
           * Recap of previous episodes of the content, usually found around the beginning.
           */
          Recap(3),
          /**
           * **Definition**
           * Preview of the next episode of the content, usually found around the end. It may
           * contain spoilers the user wants to avoid.
           */
          `Next Preview`(4),
          /**
           * **Definition**
           * Preview of the current episode of the content, usually found around the beginning. It
           * may contain spoilers the user want to avoid.
           */
          Preview(5),
          /**
           * **Definition**
           * Advertisement within the content.
           */
          Advertisement(6),
          ;

          companion object Declaration : ElementDeclaration.CUSTOM<ChapterSkipType> {
            override val id: EbmlID = EbmlID("ChapterSkipType", 0x45, 0x88)

            override suspend fun process(`data`: Memory): ChapterSkipType {
              val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
              return entries.first { it.code == code }
            }
          }
        }

        /**
         * **Definition**
         * List of tracks on which the chapter applies. If this Element is not present, all tracks
         * apply
         */
        object ChapterTrack :
            ElementDeclaration.MASTER.Actual(EbmlID("ChapterTrack", 0x8F)) {
          /**
           * **Definition**
           * UID of the Track to apply this chapter to. In the absence of a control track, choosing
           * this chapter will select the listed Tracks and deselect unlisted tracks. Absence of this
           * Element indicates that the Chapter **SHOULD** be applied to any currently used Tracks.
           */
          val ChapterTrackUID: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("ChapterTrackUID",
              0x89), EBMLIntegerType.Unsigned)

          init {
            add(ChapterTrackUID)
          }
        }

        /**
         * **Definition**
         * Contains all possible strings to use for the chapter display.
         */
        object ChapterDisplay :
            ElementDeclaration.MASTER.Actual(EbmlID("ChapterDisplay", 0x80)) {
          /**
           * **Definition**
           * Contains the string to use as the chapter atom.
           */
          val ChapString: ElementDeclaration.STRING =
              ElementDeclaration.STRING.Actual(
                  EbmlID("ChapString",
              0x85), true)

          /**
           * **Definition**
           * A language corresponding to the string, in the Matroska languages form; see
           * (#language-codes) on language codes. This Element **MUST** be ignored if a
           * ChapLanguageBCP47 Element is used within the same ChapterDisplay Element.
           */
          val ChapLanguage: ElementDeclaration.STRING =
              ElementDeclaration.STRING.Actual(
                  EbmlID("ChapLanguage",
              0x43, 0x7C), false)

          /**
           * **Definition**
           * A language corresponding to the ChapString, in the [@!BCP47] form; see
           * (#language-codes) on language codes. If a ChapLanguageBCP47 Element is used, then any
           * ChapLanguage and ChapCountry Elements used in the same ChapterDisplay **MUST** be ignored.
           */
          val ChapLanguageBCP47: ElementDeclaration.STRING =
              ElementDeclaration.STRING.Actual(
                  EbmlID("ChapLanguageBCP47",
              0x43, 0x7D), false)

          /**
           * **Definition**
           * A country corresponding to the string, in the Matroska countries form; see
           * (#country-codes) on country codes. This Element **MUST** be ignored if a ChapLanguageBCP47
           * Element is used within the same ChapterDisplay Element.
           */
          val ChapCountry: ElementDeclaration.STRING =
              ElementDeclaration.STRING.Actual(
                  EbmlID("ChapCountry",
              0x43, 0x7E), false)

          init {
            add(ChapString)
            add(ChapLanguage)
            add(ChapLanguageBCP47)
            add(ChapCountry)
          }
        }

        /**
         * **Definition**
         * Contains all the commands associated to the Atom.
         */
        object ChapProcess :
            ElementDeclaration.MASTER.Actual(
                EbmlID("ChapProcess", 0x69,
            0x44)
            ) {
          /**
           * **Definition**
           * Contains the type of the codec used for the processing. A value of 0 means built-in
           * Matroska processing (to be defined), a value of 1 means the DVD command set is used; see
           * (#menu-features) on DVD menus. More codec IDs can be added later.
           */
          val ChapProcessCodecID: ElementDeclaration.INTEGER =
              ElementDeclaration.INTEGER.Actual(
                  EbmlID("ChapProcessCodecID",
              0x69, 0x55), EBMLIntegerType.Unsigned)

          /**
           * **Definition**
           * Some optional data attached to the ChapProcessCodecID information. For
           * ChapProcessCodecID = 1, it is the "DVD level" equivalent; see (#menu-features) on DVD
           * menus.
           */
          val ChapProcessPrivate: ElementDeclaration.BINARY =
              ElementDeclaration.BINARY.Actual(
                  EbmlID("ChapProcessPrivate",
              0x45, 0x0D)
              )

          init {
            add(ChapProcessCodecID)
            add(ChapProcessPrivate)
            add(ChapProcessCommand)
          }

          /**
           * **Definition**
           * Contains all the commands associated to the Atom.
           */
          object ChapProcessCommand :
              ElementDeclaration.MASTER.Actual(
                  EbmlID("ChapProcessCommand",
              0x69, 0x11)
              ) {
            /**
             * **Definition**
             * Contains the command information. The data **SHOULD** be interpreted depending on the
             * ChapProcessCodecID value. For ChapProcessCodecID = 1, the data correspond to the binary
             * DVD cell pre/post commands; see (#menu-features) on DVD menus.
             */
            val ChapProcessData: ElementDeclaration.BINARY =
                ElementDeclaration.BINARY.Actual(
                    EbmlID("ChapProcessData",
                0x69, 0x33)
                )

            init {
              add(ChapProcessTime)
              add(ChapProcessData)
            }

            /**
             * **Definition**
             * Defines when the process command **SHOULD** be handled
             */
            enum class ChapProcessTime(
              val code: Long,
            ) {
              `during the whole chapter`(0),
              `before starting playback`(1),
              `after playback of the chapter`(2),
              ;

              companion object Declaration : ElementDeclaration.CUSTOM<ChapProcessTime> {
                override val id: EbmlID = EbmlID("ChapProcessTime", 0x69,
                    0x22)

                override suspend fun process(`data`: Memory): ChapProcessTime {
                  val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
                  return entries.first { it.code == code }
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * **Definition**
   * Element containing metadata describing Tracks, Editions, Chapters, Attachments, or the Segment
   * as a whole. A list of valid tags can be found in [@?MatroskaTags].
   */
  object Tags : ElementDeclaration.MASTER.Actual(
      EbmlID("Tags", 0x12,
      0x54, 0xC3, 0x67)
  ) {
    init {
      add(Tag)
    }

    /**
     * **Definition**
     * A single metadata descriptor.
     */
    object Tag : ElementDeclaration.MASTER.Actual(
        EbmlID("Tag", 0x73,
        0x73)
    ) {
      init {
        add(Targets)
        add(SimpleTag)
      }

      /**
       * **Definition**
       * Specifies which other elements the metadata represented by the Tag applies to. If empty or
       * omitted, then the Tag describes everything in the Segment.
       */
      object Targets :
          ElementDeclaration.MASTER.Actual(EbmlID("Targets", 0x63, 0xC0)) {
        /**
         * **Definition**
         * An informational string that can be used to display the logical level of the target like
         * "ALBUM", "TRACK", "MOVIE", "CHAPTER", etc.
         */
        val TargetType: ElementDeclaration.STRING =
            ElementDeclaration.STRING.Actual(
                EbmlID("TargetType",
            0x63, 0xCA), false)

        /**
         * **Definition**
         * A unique ID to identify the Track(s) the tags belong to.**UsageNotes**
         * If the value is 0 at this level, the tags apply to all tracks in the Segment. If set to
         * any other value, it **MUST** match the `TrackUID` value of a track found in this Segment.
         */
        val TagTrackUID: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("TagTrackUID",
            0x63, 0xC5), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * A unique ID to identify the EditionEntry(s) the tags belong to.**UsageNotes**
         * If the value is 0 at this level, the tags apply to all editions in the Segment. If set to
         * any other value, it **MUST** match the `EditionUID` value of an edition found in this
         * Segment.
         */
        val TagEditionUID: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("TagEditionUID",
            0x63, 0xC9), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * A unique ID to identify the Chapter(s) the tags belong to.**UsageNotes**
         * If the value is 0 at this level, the tags apply to all chapters in the Segment. If set to
         * any other value, it **MUST** match the `ChapterUID` value of a chapter found in this
         * Segment.
         */
        val TagChapterUID: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("TagChapterUID",
            0x63, 0xC4), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * A unique ID to identify the Attachment(s) the tags belong to.**UsageNotes**
         * If the value is 0 at this level, the tags apply to all the attachments in the Segment. If
         * set to any other value, it **MUST** match the `FileUID` value of an attachment found in this
         * Segment.
         */
        val TagAttachmentUID: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("TagAttachmentUID",
            0x63, 0xC6), EBMLIntegerType.Unsigned)

        init {
          add(TargetTypeValue)
          add(TargetType)
          add(TagTrackUID)
          add(TagEditionUID)
          add(TagChapterUID)
          add(TagAttachmentUID)
        }

        /**
         * **Definition**
         * A number to indicate the logical level of the target.
         */
        enum class TargetTypeValue(
          val code: Long,
        ) {
          /**
           * **Definition**
           * The highest hierarchical level that tags can describe.
           */
          COLLECTION(70),
          /**
           * `EDITION / ISSUE / VOLUME / OPUS / SEASON / SEQUEL`
           *
           * **Definition**
           * A list of lower levels grouped together.
           */
          `EDITION ISSUE VOLUME OPUS SEASON SEQUEL`(60),
          /**
           * `ALBUM / OPERA / CONCERT / MOVIE / EPISODE`
           *
           * **Definition**
           * The most common grouping level of music and video (equals to an episode for TV series).
           */
          `ALBUM OPERA CONCERT MOVIE EPISODE`(50),
          /**
           * `PART / SESSION`
           *
           * **Definition**
           * When an album or episode has different logical parts.
           */
          `PART SESSION`(40),
          /**
           * `TRACK / SONG / CHAPTER`
           *
           * **Definition**
           * The common parts of an album or movie.
           */
          `TRACK SONG CHAPTER`(30),
          /**
           * `SUBTRACK / MOVEMENT / SCENE`
           *
           * **Definition**
           * Corresponds to parts of a track for audio like a movement, or a scene in a movie.
           */
          `SUBTRACK MOVEMENT SCENE`(20),
          /**
           * **Definition**
           * The lowest hierarchy found in music or movies.
           */
          SHOT(10),
          ;

          companion object Declaration : ElementDeclaration.CUSTOM<TargetTypeValue> {
            override val id: EbmlID = EbmlID("TargetTypeValue", 0x68, 0xCA)

            override suspend fun process(`data`: Memory): TargetTypeValue {
              val code = data.readEBMLInteger_(EBMLIntegerType.Unsigned)
              return entries.first { it.code == code }
            }
          }
        }
      }

      /**
       * **Definition**
       * Contains general information about the target.
       */
      object SimpleTag :
          ElementDeclaration.MASTER.Actual(EbmlID("SimpleTag", 0x67, 0xC8)) {
        /**
         * **Definition**
         * The name of the Tag that is going to be stored.
         */
        val TagName: ElementDeclaration.STRING =
            ElementDeclaration.STRING.Actual(
                EbmlID("TagName",
            0x45, 0xA3), true)

        /**
         * **Definition**
         * Specifies the language of the tag specified, in the Matroska languages form; see
         * (#language-codes) on language codes. This Element **MUST** be ignored if the
         * TagLanguageBCP47 Element is used within the same SimpleTag Element.
         */
        val TagLanguage: ElementDeclaration.STRING =
            ElementDeclaration.STRING.Actual(
                EbmlID("TagLanguage",
            0x44, 0x7A), false)

        /**
         * **Definition**
         * The language used in the TagString, in the [@!BCP47] form; see (#language-codes) on
         * language codes. If this Element is used, then any TagLanguage Elements used in the same
         * SimpleTag **MUST** be ignored.
         */
        val TagLanguageBCP47: ElementDeclaration.STRING =
            ElementDeclaration.STRING.Actual(
                EbmlID("TagLanguageBCP47",
            0x44, 0x7B), false)

        /**
         * **Definition**
         * A boolean value to indicate if this is the default/original language to use for the given
         * tag.
         */
        val TagDefault: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("TagDefault",
            0x44, 0x84), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * A variant of the TagDefault element with a bogus Element ID; see (#tagdefault-element).
         */
        val TagDefaultBogus: ElementDeclaration.INTEGER =
            ElementDeclaration.INTEGER.Actual(
                EbmlID("TagDefaultBogus",
            0x44, 0xB4), EBMLIntegerType.Unsigned)

        /**
         * **Definition**
         * The value of the Tag.
         */
        val TagString: ElementDeclaration.STRING =
            ElementDeclaration.STRING.Actual(
                EbmlID("TagString",
            0x44, 0x87), true)

        /**
         * **Definition**
         * The values of the Tag, if it is binary. Note that this cannot be used in the same
         * SimpleTag as TagString.
         */
        val TagBinary: ElementDeclaration.BINARY =
            ElementDeclaration.BINARY.Actual(
                EbmlID("TagBinary",
            0x44, 0x85)
            )

        init {
          add(TagName)
          add(TagLanguage)
          add(TagLanguageBCP47)
          add(TagDefault)
          add(TagDefaultBogus)
          add(TagString)
          add(TagBinary)
        }
      }
    }
  }
}