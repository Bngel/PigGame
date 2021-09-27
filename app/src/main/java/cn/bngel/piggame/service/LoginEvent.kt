package cn.bngel.piggame.service

import cn.bngel.piggame.dao.postUserLogin.PostUserLogin

interface LoginEvent {

    fun  success(data: PostUserLogin)

    fun  failure(data: PostUserLogin?)
}