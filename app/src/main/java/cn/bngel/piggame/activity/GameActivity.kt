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
import cn.bngel.piggame.dao.getGameUUIDLast.GetGameUUIDLast
import cn.bngel.piggame.databinding.ActivityGameBinding
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
    private val timer = Timer()
    private var endGame = false

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
                            removeMyCard()
                        }

                        override fun <T> failure(data: DefaultData<T>?) {
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
                val host = intent.getBooleanExtra("host", false)
                gameService.putGameUUID(0, null, uuid, StatusRepository.userToken, object: DaoEvent{
                    override fun <T> success(data: DefaultData<T>) {
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
        gameStarted.observe(this) { started ->
            if (started) {
                myTurn.observe(this) { isMe ->
                    if (!isMe) {
                        binding.flopCardActivityGame.visibility = View.INVISIBLE
                        binding.outCardActivityGame.visibility = View.INVISIBLE
                        binding.turnActivityGame.text = "对方回合"
                    }
                    else {
                        binding.flopCardActivityGame.visibility = View.VISIBLE
                        binding.outCardActivityGame.visibility = View.VISIBLE
                        binding.turnActivityGame.text = "你的回合"
                    }
                    refresh(gameUUIDLast)
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
        myCards.add(card)
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
        enemyCards.add(card)
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
        timer.schedule(timerTask, 0, 1000)
    }

    inner class MyTimerTask(private val uuid: String): TimerTask() {
        override fun run() {
            gameService.getGameUUIDLast(uuid, StatusRepository.userToken, object:DaoEvent {
                override fun <T> success(data: DefaultData<T>) {
                    if (data.code == 200 && !endGame) {
                        val last = data.data as GetGameUUIDLast
                        gameUUIDLast = last
                        Log.d("pigHandler", (data.data as GetGameUUIDLast).toString())
                        if (last.last_msg == "对局刚开始") {
                            if (gameStarted.value == false) {
                                gameStarted.value = true
                                myTurn.value = last.your_turn
                            }
                        } else {
                            if (myTurn.value != last.your_turn)
                                myTurn.value = last.your_turn
                        }
                    }
                }
                override fun <T> failure(data: DefaultData<T>?) {
                    if (data?.code == 400 && !endGame) {
                        endGame = true
                        refresh(gameUUIDLast)
                        Toast.makeText(this@GameActivity, "对局已结束", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    private fun refresh(last: GetGameUUIDLast) {
        if (last.last_code != "") {
            val code = last.last_code.split(" ")
            val player = code[0]
            val operation = code[1]
            val card = code[2]

            if (operation == "0") {
                if (outCards.size > 0 && card[0] == outCards.peek()[0]) {
                    outCards.push(card)
                    val host = intent.getBooleanExtra("host", false)
                    if (player == "0" && host || player == "1" && !host) {
                        for (c in outCards) {
                            runOnUiThread {
                                addMyCard(c)
                            }
                        }
                        outCards.clear()
                    } else if (player == "1" && host || player == "0" && !host) {
                        for (c in outCards) {
                            runOnUiThread {
                                addEnemyCard(c)
                            }
                        }
                        outCards.clear()
                    }
                    binding.curCardActivityGame.setImageResource(R.drawable.transparentcard)
                } else {
                    outCards.push(card)
                    binding.curCardActivityGame.setImageResource(UIRepository.cards[card]!!)
                }
            } else if (operation == "1") {
                if (outCards.size > 0 && card[0] == outCards.peek()[0]) {
                    val host = intent.getBooleanExtra("host", true)
                    if (player == "0" && host || player == "1" && !host) {
                        outCards.push(card)
                        for (c in outCards) {
                            runOnUiThread {
                                addMyCard(c)
                            }
                        }
                        outCards.clear()
                    } else if (player == "1" && host || player == "0" && !host) {
                        for (c in outCards) {
                            runOnUiThread {
                                addEnemyCard(c)
                            }
                        }
                        outCards.clear()
                    }
                    binding.curCardActivityGame.setImageResource(R.drawable.transparentcard)
                } else {
                    outCards.push(card)
                    binding.curCardActivityGame.setImageResource(UIRepository.cards[card]!!)
                    val host = intent.getBooleanExtra("host", true)
                    Log.d("pigTest", "host: $host, player: $player")
                    if ((player == "0" && host) || (player == "1" && !host)) {
                        myCardCount -= 1
                    } else if (player == "1" && host || player == "0" && !host) {
                        binding.enemyCardsActivityGame.removeViewAt(enemyCardCount - 1)
                        enemyCardCount -= 1
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        val build = MaterialDialog.Builder(this)
            .iconRes(R.drawable.dialog_tip)
            .limitIconToDefaultSize()
            .title("提示:")
            .content("是否确定退出游戏?")
            .positiveText("确定")
            .onPositive() { dialog, _ ->
                super.onBackPressed()
            }
            .negativeText("取消")
            .build()
        build.show()
    }
}