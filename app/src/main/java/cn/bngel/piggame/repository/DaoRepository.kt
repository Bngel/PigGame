package cn.bngel.piggame.repository

import cn.bngel.piggame.dao.DefaultData
import cn.bngel.piggame.service.DaoEvent
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
                val login = response.body()
                if (login != null && login.status == 200) {
                    event.success(login)
                }
                else {
                    event.failure()
                }
            }

            override fun onFailure(call: Call<DefaultData<T>>, t: Throwable) {
                t.printStackTrace()
                event.failure()
            }
        })
    }
}