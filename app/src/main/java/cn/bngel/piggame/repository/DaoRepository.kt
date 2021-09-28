package cn.bngel.piggame.repository

import cn.bngel.piggame.dao.DefaultData
import cn.bngel.piggame.dao.postUserLogin.PostUserLogin
import cn.bngel.piggame.service.DaoEvent
import cn.bngel.piggame.service.LoginEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object DaoRepository {

    fun <T> enqueue(call: Call<DefaultData<T>>, event: DaoEvent) {
        call.enqueue(object: Callback<DefaultData<T>> {

            override fun onResponse(
                call: Call<DefaultData<T>>,
                response: Response<DefaultData<T>>
            ) {
                val body = response.body()
                if (body != null && body.code == 200) {
                    event.success(body)
                }
                else {
                    event.failure(body)
                }
            }

            override fun onFailure(call: Call<DefaultData<T>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun enqueueLogin(call: Call<PostUserLogin>, event: LoginEvent) {
        call.enqueue(object: Callback<PostUserLogin> {

            override fun onResponse(
                call: Call<PostUserLogin>,
                response: Response<PostUserLogin>
            ) {
                val login = response.body()
                if (login != null && login.status == 200) {
                    event.success(login)
                }
                else {
                    event.failure(login)
                }
            }

            override fun onFailure(call: Call<PostUserLogin>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}