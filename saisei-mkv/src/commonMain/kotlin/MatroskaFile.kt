package saisei.container.mkv

import saisei.io.format.ebml.element.MasterElement

data class MatroskaFile(
    /**
     * The cue points that were found.
     */
    val cues: MatroskaCues,
    /**
     * The tracks that are contained within this MKV file.
     */
    val tracks: List<MatroskaTrack>,
    /**
     * The first cluster element that was encountered.
     */
    val firstCluster: MasterElement,
)
