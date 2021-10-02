package cn.bngel.piggame.repository

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
     *  2.  x > 52 - x - y - z (手牌中的牌多于牌堆的牌):
     *      2.1 有牌则打牌, 使用4.2规则
     *      2.2 无其他花色牌则翻牌
     *  3.  x + z < 10 直接翻牌
     *  4.  x > 10 只出牌
     *      4.1 无牌可打, 翻牌
     *      4.2 有牌可打
     *          4.2.1 优先出手牌中花色数量最多的牌
     *          4.2.2 优先出牌堆中花色数量最多的牌
     *          4.2.3 若牌堆中该花色数量大于手牌中该花色数量, 优先使用4.2.2规则, 否则使用4.2.1规则
     *  --------------------------------
     */
    fun getRobotCard(x: List<String>, y: List<String>, z: Stack<String>): String {
        val totalPile = ArrayList(UIRepository.cards.keys)
        val curPile = totalPile.clone() as ArrayList<String>
        curPile.removeAll(x)
        curPile.removeAll(y)
        curPile.removeAll(z)
        when {
            x.isEmpty() -> return ""
            x.size + 2 * y.size + z.size >= 78 -> return ""
            x.size > 52 - x.size - y.size - z.size -> {
                return if (x.isNotEmpty()) {
                    if (z.size > 0)
                        coreOutCard(curPile, x, z.peek()[0])
                    else
                        coreOutCard(curPile, x, ' ')
                } else {
                    ""
                }
            }
            x.size + z.size < 10 -> return ""
            x.size > 10 -> {
                return if (z.isNotEmpty() && !judgeSuitExists(z.peek()[0], x))
                    ""
                else {
                    if (z.size > 0)
                        coreOutCard(curPile, x, z.peek()[0])
                    else
                        coreOutCard(curPile, x, ' ')
                }
            }
            else -> return ""
        }
    }

    private fun judgeSuitExists(suit: Char, cards: List<String>): Boolean {
        var res = false
        for (card in cards) {
            if (card[0] == suit){
                res = true
                break
            }
        }
        return res
    }

    private fun coreOutCard(pile: List<String>, hands: List<String>, suit: Char): String {
        var suitInPile = 0
        for (p in pile) {
            if (p[0] == suit)
                suitInPile += 1
        }
        var suitInHand = 0
        for (hand in hands) {
            if (hand[0] == suit)
                suitInHand += 1
        }
        return if (suitInPile > suitInHand) {
            getMostCardFromPile(pile, hands, suit)
        } else {
            getMostCardFromHand(hands, suit)
        }
    }

    private fun getMostCardFromPile(pile: List<String>, hands: List<String>, suit: Char): String {
        var spade = 0
        var heart = 0
        var club = 0
        var diamond = 0
        for (p in pile) {
            when (p[0]) {
                'S' -> spade += 1
                'H' -> heart += 1
                'C' -> club += 1
                'D' -> diamond += 1
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
            if (s[0] != suit || suit == ' ') {
                res = s
                break
            }
        }
        for (hand in hands) {
            if (hand[0] == res[0]) {
                res = hand
                break
            }
        }
        return if (res.length <= 1) "" else res
    }

    private fun getMostCardFromHand(hands: List<String>, suit: Char): String {
        var spade = 0
        var heart = 0
        var club = 0
        var diamond = 0
        for (hand in hands) {
            when (hand[0]) {
                'S' -> spade += 1
                'H' -> heart += 1
                'C' -> club += 1
                'D' -> diamond += 1
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
            if (s[0] != suit || suit == ' ') {
                res = s
                break
            }
        }
        for (hand in hands) {
            if (hand[0] == res[0]) {
                res = hand
                break
            }
        }
        return if (res.length <= 1) "" else res
    }
}