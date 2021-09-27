package cn.bngel.piggame.service

import cn.bngel.piggame.dao.LoginDao
import cn.bngel.piggame.repository.DaoRepository.enqueue
import java.lang.Exception

class LoginService {

    private val loginDao = LoginDao.create()

    fun postUserLogin(id: String, password: String, event: DaoEvent) {
        try {
            val postUserLogin = loginDao.postUserLogin(id, password)
            enqueue(postUserLogin, event)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}