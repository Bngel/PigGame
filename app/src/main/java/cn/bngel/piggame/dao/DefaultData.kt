package cn.bngel.piggame.dao


data class DefaultData <T>(
    val `data`: T?,
    val message: String,
    val status: Int
)
