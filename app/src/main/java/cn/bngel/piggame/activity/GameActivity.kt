package cn.bngel.piggame.activity

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.STREAM_MUSIC
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.core.view.size
import androidx.lifecycle.MutableLiveData
import cn.bngel.piggame.R
import cn.bngel.piggame.dao.DefaultData
import cn.bngel.piggame.dao.getGameUUID.GetGameUUID
import cn.bngel.piggame.dao.getGameUUIDLast.GetGameUUIDLast
import cn.bngel.piggame.dao.putGameUUID.PutGameUUID
import cn.bngel.piggame.databinding.ActivityGameBinding
import cn.bngel.piggame.repository.RobotRepository
import cn.bngel.piggame.repository.StatusRepository
import cn.bngel.piggame.repository.UIRepository
import cn.bngel.piggame.service.DaoEvent
import cn.bngel.piggame.service.GameService
import cn.bngel.piggame.widget.SettingDialogView
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import java.util.*
import kotlin.collections.ArrayList

class GameActivity : BaseActivity() {

    lateinit var binding: ActivityGameBinding
    private var myCardCount = 0
    private var enemyCardCount = 0
    private val myTurn = MutableLiveData<Boolean>()
    private val gameService = GameService()
    private val outCards = Stack<String>()
    private var curSelectedCard: ImageView?= null
    private var curSelectedCardType = ""
    private val myCards = ArrayList<String>()
    private val enemyCards = ArrayList<String>()
    private val mediaPlayer = MediaPlayer()
    private var gameStarted = MutableLiveData(false)
    private lateinit var gameUUIDLast: GetGameUUIDLast
    private lateinit var timer: Timer
    private var endGame = false
    private var isRobot = MutableLiveData(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.settingActivityGame.setOnClickListener {
            val setDialog = MaterialDialog.Builder(this)
                .customView(SettingDialogView(this), true)
                .positiveText("确定")
                .negativeText("取消")
                .title("系统设置")
                .onPositive { dialog, _ ->
                    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    val volume = dialog.findViewById<com.xuexiang.xui.widget.picker.XSeekBar>(R.id.volume_sum_view_dialog_setting)
                    audioManager.setStreamVolume(STREAM_MUSIC, volume.selectedNumber, AudioManager.FLAG_PLAY_SOUND)
                }
                .build()
            setDialog.show()
        }
        binding.outCardActivityGame.setOnClickListener {
            if (myTurn.value!= null && myTurn.value==true) {
                if (curSelectedCardType == "") {
                    Toast.makeText(this, "请选择要出的牌~", Toast.LENGTH_SHORT).show()
                }
                else {
                    val uuid = intent.getStringExtra("uuid")!!
                    Log.d("pigTest", "card: $curSelectedCardType")
                    gameService.putGameUUID(1, curSelectedCardType, uuid, StatusRepository.userToken, object: DaoEvent{
                        override fun <T> success(data: DefaultData<T>) {
                            val last = data.data as PutGameUUID
                            loadMyCode(last)
                        }

                        override fun <T> failure(data: DefaultData<T>?) {
                            if (data?.code == 401 && data.message == "鉴权失败")
                                initTimer()
                            else
                                Toast.makeText(this@GameActivity, "出牌失败, 请重试", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            else {
                Toast.makeText(this, "还没有到你的回合噢~", Toast.LENGTH_SHORT).show()
            }
        }
        binding.flopCardActivityGame.setOnClickListener {
            if (myTurn.value != null && myTurn.value == true) {
                val uuid = intent.getStringExtra("uuid")!!
                gameService.putGameUUID(0, null, uuid, StatusRepository.userToken, object: DaoEvent{
                    override fun <T> success(data: DefaultData<T>) {
                        val last = data.data as PutGameUUID
                        loadMyCode(last)
                    }

                    override fun <T> failure(data: DefaultData<T>?) {
                        Toast.makeText(this@GameActivity, "翻牌失败, 请重试", Toast.LENGTH_SHORT).show()
                    }

                })
            }
            else {
                Toast.makeText(this, "还没有到你的回合噢~", Toast.LENGTH_SHORT).show()
            }
        }
        binding.robotBtnActivityGame.setOnClickListener {
            val robot = it as com.xuexiang.xui.widget.textview.supertextview.SuperButton
            if (isRobot.value != null && isRobot.value == true) {
                robot.text = "托管"
                binding.flopCardActivityGame.isClickable = true
                binding.outCardActivityGame.isClickable = true
                isRobot.value = false
            }
            else {
                robot.text = "取消"
                binding.flopCardActivityGame.isClickable = false
                binding.outCardActivityGame.isClickable = false
                isRobot.value = true
            }
        }
        gameStarted.observe(this) { started ->
            if (started) {
                myTurn.observe(this) { isMe ->
                    if (isMe) {
                        binding.flopCardActivityGame.visibility = View.VISIBLE
                        binding.outCardActivityGame.visibility = View.VISIBLE
                        binding.turnActivityGame.text = "你的回合"
                    }
                    else {
                        binding.flopCardActivityGame.visibility = View.INVISIBLE
                        binding.outCardActivityGame.visibility = View.INVISIBLE
                        binding.turnActivityGame.text = "对方回合"
                    }
                }
            }
            else {
                binding.turnActivityGame.text = "对局未开始"
            }
        }
        playBGM()
        initTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        timer.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (!mediaPlayer.isPlaying)
            mediaPlayer.start()
        val uuid = intent.getStringExtra("uuid")
        if (uuid == null) {
            val build = MaterialDialog.Builder(this@GameActivity)
                .iconRes(R.drawable.dialog_tip)
                .limitIconToDefaultSize()
                .title("提示:")
                .content("获取房间UUID异常")
                .positiveText("退出对局")
                .onPositive() { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .cancelable(false)
                .cancelListener {
                    finish()
                }
                .build()
            build.show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    private fun playBGM() {
        val fd = assets.openFd("happyddz.mp3")
        mediaPlayer.reset()
        mediaPlayer.isLooping = true
        mediaPlayer.setDataSource(fd.fileDescriptor, fd.startOffset, fd.length)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            it.start()
        }
    }

    private fun addMyCard(card: String) {
        val cardRes = UIRepository.cards[card]
        val params = RelativeLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        if (binding.myCardsActivityGame.size > 0) {
            val view = binding.myCardsActivityGame[0]
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            params.leftMargin = myCardCount * binding.myCardsActivityGame[0].measuredWidth / 4
        }
        val imageView = ImageView(this)
        imageView.adjustViewBounds = true
        imageView.setImageResource(cardRes?:R.drawable.c01)
        imageView.layoutParams = params
        imageView.setPadding(3, 3, 3, 3)
        imageView.setOnClickListener {
            if (myTurn.value != null && myTurn.value == true) {
                if (curSelectedCard != null) {
                    if (curSelectedCard != imageView) {
                        curSelectedCard?.setBackgroundResource(0)
                        imageView.setBackgroundResource(R.drawable.stroke_card_selected)
                        curSelectedCard = imageView
                        curSelectedCardType = card
                    }
                    else {
                        curSelectedCard?.setBackgroundResource(0)
                        curSelectedCard = null
                        curSelectedCardType = ""
                    }
                }
                else{
                    imageView.setBackgroundResource(R.drawable.stroke_card_selected)
                    curSelectedCard = imageView
                    curSelectedCardType = card
                }
            }
        }
        binding.myCardsActivityGame.addView(imageView)
        myCardCount += 1
    }

    private fun removeMyCard() {
        val baseIndex = binding.myCardsActivityGame.indexOfChild(curSelectedCard)
        binding.myCardsActivityGame.forEachIndexed { index, view ->
            if (index > baseIndex) {
                val params = view.layoutParams as RelativeLayout.LayoutParams
                if (binding.myCardsActivityGame.size > 0) {
                    params.leftMargin = (index-1) * (binding.myCardsActivityGame[0].measuredWidth / 4)
                }
                view.layoutParams = params
            }
        }
        myCards.remove(curSelectedCardType)
        binding.myCardsActivityGame.removeView(curSelectedCard)
    }

    private fun addEnemyCard(card: String) {
        val params = RelativeLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        if (binding.enemyCardsActivityGame.size > 0) {
            val view = binding.enemyCardsActivityGame[0]
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            params.leftMargin = enemyCardCount * binding.enemyCardsActivityGame[0].measuredWidth / 4
        }
        val imageView = ImageView(this)
        imageView.adjustViewBounds = true
        imageView.setImageResource(R.drawable.card_bk1)
        imageView.layoutParams = params
        imageView.setPadding(3, 3, 3, 3)
        binding.enemyCardsActivityGame.addView(imageView)
        enemyCardCount += 1
    }

    private fun initTimer() {
        val uuid = intent.getStringExtra("uuid")!!
        val timerTask = MyTimerTask(uuid)
        timer = Timer()
        timer.schedule(timerTask, 0, 1000)
    }

    private fun dropTimer() {
        timer.cancel()
    }

    inner class MyTimerTask(private val uuid: String): TimerTask() {
        override fun run() {
            gameService.getGameUUIDLast(uuid, StatusRepository.userToken, object:DaoEvent {
                override fun <T> success(data: DefaultData<T>) {
                    if (data.code == 200 && !endGame) {
                        val last = data.data as GetGameUUIDLast
                        gameUUIDLast = last
                        Log.d("pigHandler", (data.data as GetGameUUIDLast).toString())
                        if (gameStarted.value == false) {
                            gameStarted.value = true
                            myTurn.value = last.your_turn
                            if (last.your_turn) {
                                loadEnemyCode(last)
                            }
                        } else {
                            if (gameStarted.value == true) {
                                if (last.your_turn)
                                    loadEnemyCode(last)
                                if (myTurn.value != last.your_turn) {
                                    myTurn.value = last.your_turn
                                }
                            }
                        }
                    }
                }
                override fun <T> failure(data: DefaultData<T>?) {
                    if (data?.code == 400 && !endGame) {
                        endGame = true
                        refresh()
                        gameService.getGameUUID(uuid, StatusRepository.userToken, object: DaoEvent {
                            override fun <T> success(data: DefaultData<T>) {
                                val last = data.data as GetGameUUID
                                if (myTurn.value == false) {
                                    val info = GetGameUUIDLast(last.last, "", true)
                                    loadEnemyCode(info)
                                }
                                val msg = if (myCardCount < enemyCardCount) "你赢了" else {
                                    if (myCardCount == enemyCardCount)
                                        "平局"
                                    else
                                        "你输了"
                                }
                                val build = MaterialDialog.Builder(this@GameActivity)
                                    .iconRes(R.drawable.dialog_tip)
                                    .limitIconToDefaultSize()
                                    .title("提示:")
                                    .content("游戏已结束\n$msg")
                                    .positiveText("退出对局")
                                    .onPositive() { dialog, _ ->
                                        dialog.dismiss()
                                        finish()
                                    }
                                    .cancelable(false)
                                    .cancelListener {
                                        finish()
                                    }
                                    .build()
                                build.show()
                            }

                            override fun <T> failure(data: DefaultData<T>?) {
                                Toast.makeText(this@GameActivity, "获取对局结束信息失败", Toast.LENGTH_SHORT).show()
                                finish()
                            }

                        })

                    }
                }
            })
        }
    }

    private fun refresh() {
        binding.myCardsActivityGame.removeAllViews()
        binding.enemyCardsActivityGame.removeAllViews()
        myCardCount = 0
        enemyCardCount = 0
        if (myCards.size > 0) {
            for (c in myCards)
                addMyCard(c)
        }
        binding.myCountActivityGame.text = myCardCount.toString()
        if (enemyCards.size > 0) {
            for (c in enemyCards)
                addEnemyCard(c)
        }
        binding.enemyCountActivityGame.text = enemyCardCount.toString()
    }

    private fun onRobot() {
        val card = RobotRepository.getRobotCard(myCards, enemyCards, outCards)
        val uuid = intent.getStringExtra("uuid")!!
        Log.d("pigTest", "onRobot: 机器人的操作: [$card]")
        if (card == "") {
            gameService.putGameUUID(0, null, uuid, StatusRepository.userToken, object: DaoEvent{
                override fun <T> success(data: DefaultData<T>) {
                    val last = data.data as PutGameUUID
                    loadMyCode(last)
                }
                override fun <T> failure(data: DefaultData<T>?) {
                    Toast.makeText(this@GameActivity, "机器人翻牌失败, 请重试", Toast.LENGTH_SHORT).show()
                }
            })
        }
        else {
            gameService.putGameUUID(1, card, uuid, StatusRepository.userToken, object: DaoEvent{
                override fun <T> success(data: DefaultData<T>) {
                    val last = data.data as PutGameUUID
                    loadMyCode(last)
                }
                override fun <T> failure(data: DefaultData<T>?) {
                    Toast.makeText(this@GameActivity, "机器人出牌失败, 请重试", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onBackPressed() {
        val build = MaterialDialog.Builder(this)
            .iconRes(R.drawable.dialog_tip)
            .limitIconToDefaultSize()
            .title("提示:")
            .content("是否确定退出游戏?")
            .cancelable(false)
            .positiveText("确定")
            .onPositive() { dialog, _ ->
                super.onBackPressed()
            }
            .negativeText("取消")
            .build()
        build.show()
    }

    private fun loadMyCode(last: PutGameUUID) {
        if (last.last_code != "") {
            val code = last.last_code.split(" ")
            val player = code[0]
            val operation = code[1]
            val card = code[2]
            if (operation == "0") {
                if (outCards.size > 0 && card[0] == outCards.peek()[0]) {
                    outCards.push(card)
                    for (c in outCards) {
                        myCards.add(c)
                        myCardCount += 1
                    }
                    outCards.clear()
                    binding.curCardActivityGame.setImageResource(R.drawable.transparentcard)
                } else {
                    outCards.push(card)
                    binding.curCardActivityGame.setImageResource(UIRepository.cards[card]!!)
                }
            } else if (operation == "1") {
                myCards.remove(card)
                if (outCards.size > 0 && card[0] == outCards.peek()[0]) {
                    outCards.push(card)
                    for (c in outCards) {
                        myCards.add(c)
                        myCardCount += 1
                    }
                    outCards.clear()
                    binding.curCardActivityGame.setImageResource(R.drawable.transparentcard)
                } else {
                    outCards.push(card)
                    binding.curCardActivityGame.setImageResource(UIRepository.cards[card]!!)
                    myCardCount -= 1
                }
            }
            refresh()
        }
        initTimer()
    }

    private fun loadEnemyCode(last: GetGameUUIDLast) {
        dropTimer()
        if (last.last_code != "") {
            val code = last.last_code.split(" ")
            val player = code[0]
            val operation = code[1]
            val card = code[2]
            if (last.your_turn) {
                if (operation == "0") {
                    if (outCards.size > 0 && card[0] == outCards.peek()[0]) {
                        outCards.push(card)
                        for (c in outCards) {
                            enemyCards.add(c)
                            enemyCardCount += 1
                        }
                        outCards.clear()
                        binding.curCardActivityGame.setImageResource(R.drawable.transparentcard)
                    } else {
                        outCards.push(card)
                        binding.curCardActivityGame.setImageResource(UIRepository.cards[card]!!)
                    }
                } else if (operation == "1") {
                    if (outCards.size > 0 && card[0] == outCards.peek()[0]) {
                        enemyCards.remove(card)
                        outCards.push(card)
                        for (c in outCards) {
                            enemyCards.add(c)
                            enemyCardCount += 1
                        }
                        outCards.clear()
                        binding.curCardActivityGame.setImageResource(R.drawable.transparentcard)
                    } else {
                        outCards.push(card)
                        binding.curCardActivityGame.setImageResource(UIRepository.cards[card]!!)
                        enemyCards.remove(card)
                        enemyCardCount -= 1
                    }
                }
            }
        }
        refresh()
        if (isRobot.value == true) {
            onRobot()
        }
    }
}