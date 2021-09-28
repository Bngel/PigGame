package cn.bngel.piggame.dao.getGameUUIDLast

import java.io.Serializable

data class GetGameUUIDLast(
    val last_code: String,
    val last_msg: String,
    val your_turn: Boolean
): Serializable