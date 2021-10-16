package cn.bngel.piggame.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.bngel.piggame.dao.postUserLogin.PostUserLogin

object StatusRepository {

    var userToken = ""

    private val ACCOUNT_KEY = "account_key"

    fun getAccountFromPreferences(context: Context): List<String>? {
        val sp = context.getSharedPreferences(ACCOUNT_KEY, Context.MODE_PRIVATE)
        if (sp.getString("username", "") != "") {
            return listOf(sp.getString("username", "")!!, sp.getString("password","")!!)
        }
        return null
    }

    fun setAccountToPreferences(context: Context, username: String, password: String){
        context.getSharedPreferences(ACCOUNT_KEY, Context.MODE_PRIVATE).edit().apply {
            putString("username", username)
            putString("password", password)
            apply()
        }
    }

    fun clearAccountFromPreferences(context: Context) {
        context.getSharedPreferences(ACCOUNT_KEY, Context.MODE_PRIVATE).edit().apply {
            remove("username")
            remove("password")
            apply()
        }
    }
}