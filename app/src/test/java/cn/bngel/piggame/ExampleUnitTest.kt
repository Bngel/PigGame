package cn.bngel.piggame

import androidx.lifecycle.MutableLiveData
import cn.bngel.piggame.repository.RobotRepository
import cn.bngel.piggame.repository.UIRepository
import org.junit.Test

import org.junit.Assert.*
import java.util.*
import kotlin.collections.ArrayList

class ExampleUnitTest {
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
     *  3.  x + z < 5 直接翻牌
     *  4.  x + 1 + z < y  直接翻牌
     *  5.  x > 5 或 z > 5 只出牌
     *      5.1 无牌可打, 翻牌
     *      5.2 有牌可打
     *          5.2.1 优先出手牌中花色数量最多的牌
     *  --------------------------------
     *牌的花色 D方块 S黑桃 H红桃 C梅花
     */

    /**
     * 测试必胜规则,此时只翻牌
     */
    @Test
    fun test1() {
        val x = ArrayList<String>()
        val y = ArrayList<String>()
        val z = Stack<String>()
        x.addAll(listOf("D1","D2","D3","D4","D5","D6","D7"))
        y.addAll(listOf("DJ","DQ","DK","S4","S5","S6","S7","S8","S9","H1","H2","H4","C5","C7","C8","C9","CJ","C10","C1","HK","H7","C4","C6","C2","D10","D9","H6","HK","HQ","H10","H3","D8","S2"))
        z.addAll(listOf("S3","S4","S5","S9","S10","SK"))
        assertEquals("",RobotRepository.getRobotCard(x, y, z))
    }
     /**
     * 测试规则2,此时打出牌库中剩下唯一一张花色的牌
     */
    @Test
    fun test2() {
        val x = ArrayList<String>()
        val y = ArrayList<String>()
        val z = Stack<String>()
        x.addAll(listOf("D1","D3","S10","SJ","SQ","S1","D2","S3","D4","C3","D5","D6","D7","CQ","S7","S8","S9","H1","H2","H4","C5","C7","C8","C9","CJ","C10","C1","HK","H7","C4","C6","C2"))
        y.addAll(listOf("DJ","DQ","DK","S4","S5","S6","D10","D9","H6","HJ","HQ","H10","H3","D8","S2"))
        z.addAll(listOf("CK","H5","H8","H9"))
        assertEquals("S10",RobotRepository.getRobotCard(x, y, z))
    }
     /**
     *  测试规则3,此时x + z < 5,直接翻牌
     */
    @Test
    fun test3() {
        val x = ArrayList<String>()
        val y = ArrayList<String>()
        val z = Stack<String>()
        x.addAll(listOf("H1","C2","D3"))
        y.addAll(listOf("H4","S10"))
        z.addAll(listOf("HJ"))
        assertEquals("",RobotRepository.getRobotCard(x, y, z))
    }
     /**
     *  测试规则4，此时应直接翻牌
     */
    @Test
    fun test4() {
        val x = ArrayList<String>()
        val y = ArrayList<String>()
        val z = Stack<String>()
        x.addAll(listOf("HJ","C2","SK","D6","H6"))
        y.addAll(listOf("DK","S4","S5","S6","S7","S8","S9","H1","H2","H4","C5"))
        z.addAll(listOf("H8","C10","D1","C1"))
        assertEquals("",RobotRepository.getRobotCard(x, y, z))
    }
     /**
     *  测试规则5，此时x > 5 或 z > 5 选择出牌,且有牌可打符合5.2.1，选择出手牌中与放置区顶不相同的牌打出
     */
    @Test
    fun test5() {
        val x = ArrayList<String>()
        val y = ArrayList<String>()
        val z = Stack<String>()
        x.addAll(listOf("C9","H3","HJ","C2","SK","D6","H6"))
        y.addAll(listOf("DK","S4","S5","S6","S7","S8","S9","H1","H2","H4","C5"))
        z.addAll(listOf("C10","D1","C1","H8"))
        assertEquals("C9",RobotRepository.getRobotCard(x, y, z))
    }
     /**
     *  测试6，此时x > 5 或 z > 5 选择出牌,但无牌可打，符合5.1,选择翻牌
     */
    @Test
    fun test6() {
        val x = ArrayList<String>()
        val y = ArrayList<String>()
        val z = Stack<String>()
        x.addAll(listOf())
        y.addAll(listOf("DK","S4","S5","S6","S9","H1","H2","H4","C5"))
        z.addAll(listOf("H8","C10","D1","C1","S7","S8","HK","H7","C4","C6","C2","D10"))
        assertEquals("",RobotRepository.getRobotCard(x, y, z))
    }
     /**
     *  测试7，此时x > 5 或 z > 5 选择出牌,且有牌可打符合5.2.1，选择出手牌中与放置区顶不相同的牌打出
     */
    @Test
    fun test7() {
        val x = ArrayList<String>()
        val y = ArrayList<String>()
        val z = Stack<String>()
        x.addAll(listOf("C9","H3","HJ","C2","SK","D6","H6"))
        y.addAll(listOf("DK","S4","S5","S6","S7","S8","S9","H1","H2","H4","C5"))
        z.addAll(listOf("CQ","H8","C10","D1","C1"))
        assertEquals("H3",RobotRepository.getRobotCard(x, y, z))
    }
     /**
     *  测试8，此时x > 5 或 z > 5 选择出牌,且有牌可打符合5.2.1，选择出手牌中与放置区顶不相同的牌打出
     */
    @Test
    fun test8() {
        val x = ArrayList<String>()
        val y = ArrayList<String>()
        val z = Stack<String>()
        x.addAll(listOf("C9","C2","H3","HJ","SK","D6","H6"))
        y.addAll(listOf("DK","S4","S5","S6","S7","S8","S9","H1","H2","H4","C5"))
        z.addAll(listOf("CQ","H8","C10","D1","C1"))
        assertEquals("H3",RobotRepository.getRobotCard(x, y, z))
    }
     /**
     *  测试9,随机情况测试
     */
    @Test
    fun test9() {
        val x = ArrayList<String>()
        val y = ArrayList<String>()
        val z = Stack<String>()
        x.addAll(listOf("CJ","C10","C1","HK","H7","C4","C6","C2","D10"))
        y.addAll(listOf("DK","S4","C5","H6","S7","S8","S9","H1","H2","H4","C5","D1","SJ","C3"))
        z.addAll(listOf("H8","C10","D1","C9","D8","CQ"))
        assertEquals("HK",RobotRepository.getRobotCard(x, y, z))
    }
     /**
     *  测试10,随机情况测试
     */
    @Test
    fun test10() {
        val x = ArrayList<String>()
        val y = ArrayList<String>()
        val z = Stack<String>()
        x.addAll(listOf("C4","C6","C2","D10","C6","C2","D10","D9","H6","HK"))
        y.addAll(listOf("DK","S4","C5","H6","S7","S8","S9","H1","H2","H4","C5","D1","SJ","C3","CJ","C10","C1","HK","H7"))
        z.addAll(listOf("CQ","H8","C10","D1","C9","D8"))
        assertEquals("",RobotRepository.getRobotCard(x, y, z))
    }
}