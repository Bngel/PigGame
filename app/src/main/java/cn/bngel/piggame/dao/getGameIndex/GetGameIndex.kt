package cn.bngel.piggame.dao.getGameIndex

data class GetGameIndex(
    val games: List<Game>,
    val total: Int,
    val total_page_num: Int
)