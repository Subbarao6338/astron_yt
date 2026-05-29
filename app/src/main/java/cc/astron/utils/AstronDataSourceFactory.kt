package cc.astron.utils

import android.content.Context
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource

class AstronDataSourceFactory(
    private val context: Context,
    private val userAgent: String,
    private val adBlocker: AdBlocker,
    private val innerTubeResolver: InnerTubeResolver
) : DataSource.Factory {

    override fun createDataSource(): DataSource {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(userAgent)
            .setAllowCrossProtocolRedirects(true)

        val headers = innerTubeResolver.getRequestHeaders(InnerTubeResolver.ClientType.ANDROID_TV)
        httpDataSourceFactory.setDefaultRequestProperties(headers)

        return AstronDataSource(httpDataSourceFactory.createDataSource(), adBlocker)
    }
}

class AstronDataSource(
    private val baseDataSource: DataSource,
    private val adBlocker: AdBlocker
) : DataSource by baseDataSource {

    override fun open(dataSpec: com.google.android.exoplayer2.upstream.DataSpec): Long {
        if (adBlocker.shouldBlockStream(dataSpec.uri.toString())) {
            throw java.io.IOException("Blocked by AdBlocker")
        }
        return baseDataSource.open(dataSpec)
    }
}
