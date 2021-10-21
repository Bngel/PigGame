package cn.bngel.piggame

import androidx.lifecycle.MutableLiveData
import cn.bngel.piggame.repository.RobotRepository
import cn.bngel.piggame.repository.UIRepository
import org.junit.Test

import org.junit.Assert.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    /**
     * x: 我方手牌
     * y: 对方手牌
     * z: 放置区牌堆
     */

    @Test
    fun test1() {
        val x = ArrayList<String>()
        val y = ArrayList<String>()
        val z = Stack<String>()
        x.addAll(listOf("D1","D2","D3","D4","D5","D6"))
        y.addAll(listOf())
        z.addAll(listOf())
        assertEquals("D1",RobotRepository.getRobotCard(x, y, z))
    }


}