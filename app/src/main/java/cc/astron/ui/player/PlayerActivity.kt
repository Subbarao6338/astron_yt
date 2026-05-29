package cc.astron.ui.player

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cc.astron.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.StyledPlayerView
import cc.astron.utils.AdBlocker
import cc.astron.utils.InnerTubeResolver
import cc.astron.utils.AstronDataSourceFactory

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private var playbackService: PlaybackService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlaybackService.LocalBinder
            playbackService = binder.getService()
            playbackService?.player = player
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }
    private lateinit var streamController: StreamController
    private lateinit var subtitleController: SubtitleController
    private val sponsorBlockInterceptor = cc.astron.utils.SponsorBlockInterceptor()
    private var currentVideoId: String = "dQw4w9WgXcQ"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        currentVideoId = getIntent().getStringExtra("video_id") ?: "dQw4w9WgXcQ"

        val intent = Intent(this, PlaybackService::class.java)
        startService(intent)
        bindService(intent, connection, Context.BIND_AUTO_CREATE)

        val playerView: StyledPlayerView = findViewById(R.id.player_view)

        val dataSourceFactory = AstronDataSourceFactory(
            this,
            "ASTRON/1.0",
            AdBlocker(),
            InnerTubeResolver()
        )
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
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
        setupSponsorBlock()
    }

    private fun setupSponsorBlock() {
        sponsorBlockInterceptor.fetchSegments(currentVideoId)

        player?.addListener(object : Player.Listener {
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                checkSponsorSegments()
            }

            override fun onEvents(player: Player, events: Player.Events) {
                if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) || events.contains(Player.EVENT_IS_PLAYING_CHANGED)) {
                    checkSponsorSegments()
                }
            }
        })

        // Periodic check every second
        val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                checkSponsorSegments()
                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    private fun checkSponsorSegments() {
        val currentPos = player?.currentPosition ?: return
        val segments = sponsorBlockInterceptor.getSegmentsForVideo(currentVideoId)
        for (segment in segments) {
            if (currentPos in segment.start until segment.end) {
                player?.seekTo(segment.end)
                // Optionally show a toast: Toast.makeText(this, "Sponsor skipped", Toast.LENGTH_SHORT).show()
                break
            }
        }
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
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        // If background play is enabled, we don't release the player here
        val preferenceManager = cc.astron.utils.PreferenceManager(this)
        if (!preferenceManager.isProEnabled()) {
            player?.release()
            player = null
        }
    }
}
