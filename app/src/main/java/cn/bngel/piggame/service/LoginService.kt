package cn.bngel.piggame.service

import cn.bngel.piggame.dao.LoginDao
import cn.bngel.piggame.repository.DaoRepository.enqueueLogin
import java.lang.Exception

class LoginService {

    private val loginDao = LoginDao.create()

    fun postUserLogin(id: String, password: String, event: LoginEvent) {
        try {
            val postUserLogin = loginDao.postUserLogin(id, password)
            enqueueLogin(postUserLogin, event)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}