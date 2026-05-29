package cc.astron.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("astron_prefs", Context.MODE_PRIVATE)

    fun setProEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("pro_enabled", enabled).apply()
    }

    fun isProEnabled(): Boolean {
        return prefs.getBoolean("pro_enabled", false)
    }

    fun setUserLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean("user_logged_in", loggedIn).apply()
    }

    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean("user_logged_in", false)
    }

    fun getAccounts(): List<String> {
        return prefs.getStringSet("accounts", emptySet())?.toList() ?: emptyList()
    }

    fun addAccount(accountId: String) {
        val accounts = getAccounts().toMutableSet()
        accounts.add(accountId)
        prefs.edit().putStringSet("accounts", accounts).apply()
    }

    fun getActiveAccountId(): String? {
        return prefs.getString("active_account_id", null)
    }

    fun setActiveAccountId(accountId: String?) {
        prefs.edit().putString("active_account_id", accountId).apply()
    }

    fun setCookies(accountId: String, cookies: String) {
        prefs.edit().putString("cookies_$accountId", cookies).apply()
    }

    fun getCookies(accountId: String): String? {
        return prefs.getString("cookies_$accountId", null)
    }
}
