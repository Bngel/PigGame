package cn.bngel.piggame.dao.getGameUUID

data class GetGameUUID(
    val CreatedAt: String,
    val DeletedAt: Any,
    val ID: Int,
    val UpdatedAt: String,
    val card_group: String,
    val card_placement: String,
    val client_hand: String,
    val client_id: Int,
    val finished: Boolean,
    val host_hand: String,
    val host_id: Int,
    val last: String,
    val `private`: Boolean,
    val top: String,
    val turn: Int,
    val uuid: String,
    val winner: Int
)