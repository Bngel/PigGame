package cn.bngel.piggame.dao.getGameIndex

import java.io.Serializable

data class Game(
    val client_id: Int,
    val created_at: String,
    val host_id: Int,
    val uuid: String
)