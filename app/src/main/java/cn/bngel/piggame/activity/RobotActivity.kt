package cn.bngel.piggame.activity

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.size
import androidx.lifecycle.MutableLiveData
import cn.bngel.piggame.R
import cn.bngel.piggame.databinding.ActivityRobotBinding
import cn.bngel.piggame.repository.RobotRepository
import cn.bngel.piggame.repository.UIRepository
import cn.bngel.piggame.widget.SettingDialogView
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import java.util.*
import kotlin.collections.ArrayList

class RobotActivity : BaseActivity() {

    private lateinit var binding: ActivityRobotBinding

    private val remainCards = Stack<String>()
    private val outCards = Stack<String>()
    private val playerCards = ArrayList<String>()
    private var playerCardsCount = 0
    private val robotCards = ArrayList<String>()
    private var robotCardsCount = 0
    private val mediaPlayer = MediaPlayer()
    private var curSelectedCard: ImageView?= null
    private var curSelectedCardType = ""

    private val isPlayer = MutableLiveData(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRobotBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.settingActivityRobot.setOnClickListener {
            val setDialog = MaterialDialog.Builder(this)
                .customView(SettingDialogView(this), true)
                .positiveText("确定")
                .negativeText("取消")
                .title("系统设置")
                .onPositive { dialog, _ ->
                    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    val volume = dialog.findViewById<com.xuexiang.xui.widget.picker.XSeekBar>(R.id.volume_sum_view_dialog_setting)
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume.selectedNumber, AudioManager.FLAG_PLAY_SOUND)
                }
                .build()
            setDialog.show()
        }
        getWashedCards()
        playBGM()
        isPlayer.observe(this) { playerTurn ->
            if (!playerTurn) {
                robotOperation()
                isPlayer.value = true
            }
        }
        binding.outCardActivityRobot.setOnClickListener {
            outCard(curSelectedCardType)
        }
        binding.flopCardActivityRobot.setOnClickListener {
            flopCard()
        }
    }

    private fun getWashedCards() {
        val cards = UIRepository.cards.keys
        remainCards.addAll(cards.shuffled())
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
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

    private fun addPlayerCard(card: String) {
        val cardRes = UIRepository.cards[card]
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        if (binding.myCardsActivityRobot.size > 0) {
            val view = binding.myCardsActivityRobot[0]
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            params.leftMargin = playerCardsCount * binding.myCardsActivityRobot[0].measuredWidth / 4
        }
        val imageView = ImageView(this)
        imageView.adjustViewBounds = true
        imageView.setImageResource(cardRes?:R.drawable.c01)
        imageView.layoutParams = params
        imageView.setPadding(3, 3, 3, 3)
        imageView.setOnClickListener {
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
        binding.myCardsActivityRobot.addView(imageView)
        playerCardsCount += 1
    }

    private fun addRobotCard(card: String) {
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        if (binding.enemyCardsActivityRobot.size > 0) {
            val view = binding.enemyCardsActivityRobot[0]
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            params.leftMargin = robotCardsCount * binding.enemyCardsActivityRobot[0].measuredWidth / 4
        }
        val imageView = ImageView(this)
        imageView.adjustViewBounds = true
        imageView.setImageResource(R.drawable.card_bk1)
        imageView.layoutParams = params
        imageView.setPadding(3, 3, 3, 3)
        binding.enemyCardsActivityRobot.addView(imageView)
        robotCardsCount += 1
    }

    private fun removePlayerCard(card: String) {
        playerCards.remove(card)
        playerCardsCount -= 1
    }

    private fun removeRobotCard(card: String) {
        robotCards.remove(card)
        robotCardsCount -= 1
    }

    private fun outCard(card: String) {
        if (curSelectedCardType == "") {
            Toast.makeText(this, "请选择要出的牌~", Toast.LENGTH_SHORT).show()
        }
        else {
            if (outCards.size > 0 && card[0] == outCards.peek()[0]){
                for (c in outCards) {
                    playerCards.add(c)
                }
                outCards.clear()
                binding.curCardActivityRobot.setImageResource(R.drawable.transparentcard)
            } else {
                outCards.push(card)
                binding.curCardActivityRobot.setImageResource(UIRepository.cards[card]!!)
                removePlayerCard(card)
            }
            if (curSelectedCard != null) {
                curSelectedCard?.setBackgroundResource(0)
                curSelectedCard = null
                curSelectedCardType = ""
            }
            refresh()
        }
    }


    private fun flopCard() {
        val flop = remainCards.pop()
        Log.d("pigFriend", "flopCard: 当前玩家翻开了一张: $flop")
        if (outCards.size > 0 && flop[0] == outCards.peek()[0]) {
            outCards.push(flop)
            for (c in outCards) {
                playerCards.add(c)
            }
            outCards.clear()
            binding.curCardActivityRobot.setImageResource(R.drawable.transparentcard)
        } else {
            outCards.push(flop)
            binding.curCardActivityRobot.setImageResource(UIRepository.cards[flop]!!)
        }
        refresh()
    }

    private fun refresh() {
        binding.myCardsActivityRobot.removeAllViews()
        binding.enemyCardsActivityRobot.removeAllViews()
        if (playerCards.size > 0) {
            playerCardsCount = 0
            for (c in playerCards)
                addPlayerCard(c)
        }
        if (robotCards.size > 0) {
            robotCardsCount = 0
            for (c in robotCards)
                addRobotCard(c)
        }
        if (remainCards.size <= 0) {
            val win = playerCardsCount < robotCardsCount
            val msg = if (win) "玩家胜利" else {
                if (playerCardsCount == robotCardsCount)
                    "平局"
                else
                    "电脑胜利"
            }
            val build = MaterialDialog.Builder(this)
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
        else {
            if (isPlayer.value == true) {
                isPlayer.value = false
            }
        }
    }

    private fun robotOperation() {
        val operation = RobotRepository.getRobotCard(robotCards, playerCards, outCards)
        if (operation == "") {
                val flop = remainCards.pop()
                Log.d("pigFriend", "flopCard: 当前电脑翻开了一张: $flop")
                if (outCards.size > 0 && flop[0] == outCards.peek()[0]) {
                    outCards.push(flop)
                    for (c in outCards) {
                        robotCards.add(c)
                    }
                    outCards.clear()
                    binding.curCardActivityRobot.setImageResource(R.drawable.transparentcard)
                }
                else {
                    outCards.push(flop)
                    binding.curCardActivityRobot.setImageResource(UIRepository.cards[flop]!!)
                }
                refresh()
        }
        else {
            Log.d("pigFriend", "flopCard: 当前电脑打出了一张: $operation")
            if (outCards.size > 0 && operation[0] == outCards.peek()[0]){
                for (c in outCards) {
                    robotCards.add(c)
                }
                outCards.clear()
                binding.curCardActivityRobot.setImageResource(R.drawable.transparentcard)
            } else {
                outCards.push(operation)
                binding.curCardActivityRobot.setImageResource(UIRepository.cards[operation]!!)
                removeRobotCard(operation)
            }
            refresh()
        }
    }
}