package cc.astron.ui.player

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector

class StreamController(private val player: ExoPlayer) {

    fun setVideoQuality(maxHeight: Int) {
        val trackSelector = player.trackSelector as? DefaultTrackSelector ?: return
        val parameters = trackSelector.buildUponParameters()
            .setMaxVideoSize(Int.MAX_VALUE, maxHeight)
            .build()
        trackSelector.setParameters(parameters)
    }

    fun setAudioLanguage(languageCode: String) {
        val trackSelector = player.trackSelector as? DefaultTrackSelector ?: return
        val parameters = trackSelector.buildUponParameters()
            .setPreferredAudioLanguage(languageCode)
            .build()
        trackSelector.setParameters(parameters)
    }

    fun bypassQualityRestrictions() {
        // Force selection of highest available tracks regardless of network conditions
        val trackSelector = player.trackSelector as? DefaultTrackSelector ?: return
        val parameters = trackSelector.buildUponParameters()
            .setForceLowestBitrate(false)
            .setForceHighestSupportedBitrate(true)
            .build()
        trackSelector.setParameters(parameters)
    }
}
