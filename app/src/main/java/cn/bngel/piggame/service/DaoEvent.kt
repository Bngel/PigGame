package cn.bngel.piggame.service

import cn.bngel.piggame.dao.DefaultData

interface DaoEvent {

    fun <T> success(data: DefaultData<T>)

    fun failure()
}