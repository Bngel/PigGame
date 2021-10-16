package cn.bngel.piggame.repository

import android.util.Log
import java.util.*
import java.util.Collections.max
import kotlin.collections.ArrayList

object RobotRepository {

    /**
     * 人机返回当前应该出的牌
     * String:
     * 返回牌型则表示该出的牌, 返回空字符串则表示翻牌
     * 算法优先级:
     * ---------------------------------
     *  x：我方手牌 y:对手手牌 z：放置区叠的牌
     *  --------------------------------
     *  1. 必胜条件 (满足后只需要不断翻牌即可获得胜利):
     *      x + 2y + z >= 78
     *  2. 牌库仅剩一张时, 假设手牌中存在该花色牌且当前为机器人回合, 且放置区顶非该花色牌. 则打出该花色牌.
     *  3.  x > 52 - x - y - z (手牌中的牌多于牌堆的牌):
     *      3.1 有牌则打牌, 使用4.2规则
     *      3.2 无其他花色牌则翻牌
     *  4.  x + z < 5 直接翻牌
     *  5.  x + 1 + z < y  直接翻牌
     *  6.  x > 5 或 z > 5 只出牌
     *      6.1 无牌可打, 翻牌
     *      6.2 有牌可打
     *          6.2.1 优先出手牌中花色数量最多的牌
     *  --------------------------------
     */
    fun getRobotCard(x: List<String>, y: List<String>, z: Stack<String>): String {
        val totalPile = ArrayList(UIRepository.cards.keys)
        val curPile = totalPile.clone() as ArrayList<String>
        curPile.removeAll(x)
        curPile.removeAll(y)
        curPile.removeAll(z)
        when {
            x.isEmpty() -> {
                return ""
            }
            curPile.size == 1 -> {
                return when {
                    x.size + 1 + z.size < y.size -> ""
                    z.size > 0 -> whenPileOnlyOne(curPile, x, z.peek()[0])
                    else -> whenPileOnlyOne(curPile, x, ' ')
                }
            }
            x.size + 2 * y.size + z.size >= 78 -> {
                return ""
            }
            x.size > 52 - x.size - y.size - z.size -> {
                return if (x.isNotEmpty()) {
                    if (z.size > 0) {
                        getMostCardFromHand(x, z.peek()[0])
                    }
                    else
                        getMostCardFromHand(x, ' ')
                } else {
                    ""
                }
            }
            x.size + z.size <= 5 -> {
                return ""
            }
            x.size + 1 + z.size <= y.size -> {
                return ""
            }
            x.size > 5 -> {
                return if (z.isNotEmpty() && judgeSuitExists(z.peek()[0], x)) {
                    if (z.size > 0)
                        getMostCardFromHand(x, z.peek()[0])
                    else
                        getMostCardFromHand(x, ' ')
                }
                else {
                    ""
                }
            }
            z.size > 5 -> {
                return if (z.isNotEmpty() && judgeSuitExists(z.peek()[0], x)) {
                    if (z.size > 0)
                        getMostCardFromHand(x, z.peek()[0])
                    else
                        getMostCardFromHand(x, ' ')
                }
                else {
                    ""
                }
            }
            else -> {
                return ""
            }
        }
    }

    private fun judgeSuitExists(suit: Char, cards: List<String>): Boolean {
        var res = false
        for (card in cards) {
            if (card[0] != suit){
                res = true
                break
            }
        }
        return res
    }

    private fun getMostCardFromHand(hands: List<String>, suit: Char): String {
        var spade = 0
        var heart = 0
        var club = 0
        var diamond = 0
        if (hands.isNotEmpty()) {
            for (hand in hands) {
                when (hand[0]) {
                    'S' -> spade += 1
                    'H' -> heart += 1
                    'C' -> club += 1
                    'D' -> diamond += 1
                }
            }
        }
        var res = ""
        val list = listOf(spade, heart, club, diamond).sorted().reversed()
        for (l in list) {
            val s = when (l) {
                spade -> "S"
                heart -> "H"
                club -> "C"
                diamond -> "D"
                else -> ""
            }
            if ((s != "" && s[0] != suit) || suit == ' ') {
                if (hands.isNotEmpty()){
                    for (hand in hands) {
                        if (hand[0] == s[0]) {
                            res = hand
                            break
                        }
                    }
                }
            }
        }
        return res
    }

    private fun whenPileOnlyOne(pile: List<String>, hands: List<String>, suit: Char): String {
        if (pile[0][0] != suit || suit == ' ') {
            for (hand in hands) {
                if (hand[0] == pile[0][0]) {
                    return hand;
                }
            }
        }
        return "";
    }
}