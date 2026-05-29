package cc.astron.utils

import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class SponsorBlockInterceptor : Interceptor {

    data class Segment(val start: Long, val end: Long)

    private val segments = mutableMapOf<String, List<Segment>>()
    private val client = OkHttpClient()

    fun fetchSegments(videoId: String) {
        val url = "https://sponsor.ajay.app/api/skipSegments?videoID=$videoId&categories=[\"sponsor\",\"intro\",\"outro\",\"interaction\",\"selfpromo\",\"music_offtopic\"]"
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    val jsonArray = JSONArray(body)
                    val newSegments = mutableListOf<Segment>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val segmentArray = obj.getJSONArray("segment")
                        newSegments.add(Segment(
                            (segmentArray.getDouble(0) * 1000).toLong(),
                            (segmentArray.getDouble(1) * 1000).toLong()
                        ))
                    }
                    segments[videoId] = newSegments
                }
            }
        })
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()

        // This is where we would intercept requests if SponsorBlock needed to modify them
        return chain.proceed(request)
    }

    fun getSegmentsForVideo(videoId: String): List<Segment> {
        return segments[videoId] ?: emptyList()
    }
}
