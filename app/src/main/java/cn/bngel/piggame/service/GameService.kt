package cn.bngel.piggame.service

import cn.bngel.piggame.dao.GameDao
import cn.bngel.piggame.repository.DaoRepository
import java.lang.Exception

class GameService {

    private val gameDao = GameDao.create()

    fun postGame(private: Boolean, token: String, event: DaoEvent) {
        try {
            val postGame = gameDao.postGame(private, token)
            DaoRepository.enqueue(postGame, event)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    fun postGameUUID(uuid: String, token: String, event: DaoEvent){
        try {
            val postGameUUID = gameDao.postGameUUID(uuid, token)
            DaoRepository.enqueue(postGameUUID, event)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    fun putGameUUID(type: Int, card: String, uuid: String, token: String, event: DaoEvent){
        try {
            val putGameUUID = gameDao.putGameUUID(type, card, uuid, token)
            DaoRepository.enqueue(putGameUUID, event)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    fun getGameUUID(uuid: String, token: String, event: DaoEvent){
        try {
            val getGameUUID = gameDao.getGameUUID(uuid, token)
            DaoRepository.enqueue(getGameUUID, event)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    fun getGameUUIDLast(uuid: String, token: String, event: DaoEvent){
        try {
            val getGameUUIDLast = gameDao.getGameUUIDLast(uuid, token)
            DaoRepository.enqueue(getGameUUIDLast, event)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    fun getGameIndex(page_size: String, page_num: String, token: String, event: DaoEvent){
        try {
            val getGameIndex = gameDao.getGameIndex(page_size, page_num, token)
            DaoRepository.enqueue(getGameIndex, event)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }
}