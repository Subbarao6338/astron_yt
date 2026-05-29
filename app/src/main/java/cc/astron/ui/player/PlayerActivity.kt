package cc.astron.ui.player

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cc.astron.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.StyledPlayerView

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var streamController: StreamController
    private lateinit var subtitleController: SubtitleController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val playerView: StyledPlayerView = findViewById(R.id.player_view)
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        streamController = StreamController(player!!)
        subtitleController = SubtitleController(this, player!!)

        findViewById<ImageButton>(R.id.btn_playlist).setOnClickListener { showPlaylistDialog() }
        findViewById<ImageButton>(R.id.btn_subtitles).setOnClickListener { showSubtitleDialog() }
        findViewById<ImageButton>(R.id.btn_settings).setOnClickListener { showSettingsDialog() }

        val mediaItem = MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny/Relo/master.m3u8")
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true

        streamController.bypassQualityRestrictions()
    }

    private fun showPlaylistDialog() {
        val options = arrayOf("Favorites", "Watch Later", "New Playlist...")
        AlertDialog.Builder(this)
            .setTitle("Add to Playlist")
            .setItems(options) { _, which ->
                // Handle playlist selection
            }
            .show()
    }

    private fun showSubtitleDialog() {
        val options = arrayOf("English", "Spanish", "Off")
        AlertDialog.Builder(this)
            .setTitle("Subtitles")
            .setItems(options) { _, which ->
                subtitleController.toggleSubtitles(which != 2)
            }
            .show()
    }

    private fun showSettingsDialog() {
        val options = arrayOf("Quality (1080p)", "Audio Language (English)")
        AlertDialog.Builder(this)
            .setTitle("Settings")
            .setItems(options) { _, which ->
                if (which == 0) showQualityDialog() else showAudioDialog()
            }
            .show()
    }

    private fun showQualityDialog() {
        val options = arrayOf("1080p", "720p", "480p", "Auto")
        AlertDialog.Builder(this)
            .setTitle("Quality")
            .setItems(options) { _, which ->
                val res = when(which) {
                    0 -> 1080; 1 -> 720; 2 -> 480; else -> Int.MAX_VALUE
                }
                streamController.setVideoQuality(res)
            }
            .show()
    }

    private fun showAudioDialog() {
        val options = arrayOf("English", "Spanish", "Japanese")
        AlertDialog.Builder(this)
            .setTitle("Audio Language")
            .setItems(options) { _, which ->
                val lang = when(which) {
                    0 -> "en"; 1 -> "es"; else -> "ja"
                }
                streamController.setAudioLanguage(lang)
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
