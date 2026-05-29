package cc.astron.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import cc.astron.utils.PreferenceManager
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.UUID

class LoginActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val cookies = CookieManager.getInstance().getCookie(url)
                if (cookies != null && cookies.contains("SAPISID")) {
                    // This is a sign that the user is logged in
                    handleLoginSuccess(cookies)
                }
            }
        }

        webView.loadUrl("https://accounts.google.com/ServiceLogin?service=youtube")
    }

    private fun handleLoginSuccess(cookies: String) {
        val client = OkHttpClient()
        val json = JSONObject().apply {
            put("context", JSONObject().apply {
                put("client", JSONObject().apply {
                    put("clientName", "ANDROID")
                    put("clientVersion", "19.05.36")
                })
            })
        }
        val requestBody = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://www.youtube.com/youtubei/v1/account/get_account_menu")
            .header("Cookie", cookies)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Fallback to anonymous save
                saveAccount(cookies, "Unknown User", null)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (response.isSuccessful && body != null) {
                    try {
                        val jsonBody = JSONObject(body)
                        // Simplified parsing logic for demo purposes
                        val name = "YouTube User"
                        saveAccount(cookies, name, null)
                    } catch (e: Exception) {
                        saveAccount(cookies, "YouTube User", null)
                    }
                } else {
                    saveAccount(cookies, "YouTube User", null)
                }
            }
        })
    }

    private fun saveAccount(cookies: String, name: String, avatarUrl: String?) {
        val preferenceManager = PreferenceManager(this)
        val newId = UUID.randomUUID().toString()
        preferenceManager.addAccount(newId)
        preferenceManager.setCookies(newId, cookies)
        preferenceManager.setUserLoggedIn(true)
        preferenceManager.setActiveAccountId(newId)
        runOnUiThread { finish() }
    }
}
