package cc.astron.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cc.astron.R
import cc.astron.model.MusicItem
import cc.astron.ui.player.PlayerActivity

class MusicFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_music, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.music_recycler_view)

        val musicList = listOf(
            MusicItem("dQw4w9WgXcQ", "Never Gonna Give You Up", "Rick Astley", ""),
            MusicItem("L_jWHffIx5E", "Smash Into Pieces", "All Eyes on You", ""),
            MusicItem("fJ9rUzIMcZQ", "Bohemian Rhapsody", "Queen", "")
        )

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MusicAdapter(musicList) { item ->
            startActivity(Intent(requireContext(), PlayerActivity::class.java).apply {
                putExtra("video_id", item.videoId)
            })
        }

        return view
    }
}
