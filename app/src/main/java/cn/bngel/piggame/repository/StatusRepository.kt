package cn.bngel.piggame.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.bngel.piggame.dao.postUserLogin.PostUserLogin

object StatusRepository {

    var userToken = ""

    val loginStatus = MutableLiveData<PostUserLogin>()
}