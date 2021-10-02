package cn.bngel.piggame.activity

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.core.view.size
import cn.bngel.piggame.R
import cn.bngel.piggame.databinding.ActivityFriendBinding
import cn.bngel.piggame.repository.UIRepository
import cn.bngel.piggame.widget.SettingDialogView
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import org.jetbrains.annotations.TestOnly
import java.util.*
import kotlin.collections.ArrayList

class FriendActivity : BaseActivity() {

    /**
     * PS: 主视角为 Player1 对家为 Player2
     */

    private lateinit var binding: ActivityFriendBinding

    private val remainCards = Stack<String>()
    private val outCards = Stack<String>()
    private val player1Cards = ArrayList<String>()
    private var player1CardsCount = 0
    private val player2Cards = ArrayList<String>()
    private var player2CardsCount = 0
    private var curSelectedCard: ImageView?= null
    private var curSelectedCardType = ""
    private val mediaPlayer = MediaPlayer()
    private var curPlayer = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.settingActivityFriend.setOnClickListener {
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
        binding.flopCardActivityFriend.setOnClickListener {
            flopCard()
        }
        binding.outCardActivityFriend.setOnClickListener {
            outCard(curSelectedCardType)
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

    private fun addPlayer1Card(card: String) {
        val cardRes = UIRepository.cards[card]
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        if (binding.myCardsActivityFriend.size > 0) {
            val view = binding.myCardsActivityFriend[0]
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            params.leftMargin = player1CardsCount * binding.myCardsActivityFriend[0].measuredWidth / 4
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
        binding.myCardsActivityFriend.addView(imageView)
        player1CardsCount += 1
    }

    private fun addPlayer2Card(card: String) {
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        if (binding.enemyCardsActivityFriend.size > 0) {
            val view = binding.enemyCardsActivityFriend[0]
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            params.leftMargin = player2CardsCount * binding.enemyCardsActivityFriend[0].measuredWidth / 4
        }
        val imageView = ImageView(this)
        imageView.adjustViewBounds = true
        imageView.setImageResource(R.drawable.card_bk1)
        imageView.layoutParams = params
        imageView.setPadding(3, 3, 3, 3)
        binding.enemyCardsActivityFriend.addView(imageView)
        player2CardsCount += 1
    }

    private fun removePlayer1Card(card: String) {
        player1Cards.remove(card)
        player1CardsCount -= 1
    }

    private fun removePlayer2Card(card: String) {
        binding.enemyCardsActivityFriend.removeViewAt(player2CardsCount - 1)
        player2CardsCount -= 1
    }

    private fun outCard(card: String) {
        if (curSelectedCardType == "") {
            Toast.makeText(this, "请选择要出的牌~", Toast.LENGTH_SHORT).show()
        }
        else {
            if (outCards.size > 0 && card[0] == outCards.peek()[0]){
                for (c in outCards) {
                    player1Cards.add(c)
                }
                outCards.clear()
                binding.curCardActivityFriend.setImageResource(R.drawable.transparentcard)
            } else {
                outCards.push(card)
                binding.curCardActivityFriend.setImageResource(UIRepository.cards[card]!!)
                removePlayer1Card(card)
            }
            if (curSelectedCard != null) {
                curSelectedCard?.setBackgroundResource(0)
                curSelectedCard = null
                curSelectedCardType = ""
            }
            switchPlayers()
        }
    }

    private fun flopCard() {
        val flop = remainCards.pop()
        Log.d("pigFriend", "flopCard: 当前玩家翻开了一张: $flop")
        if (outCards.size > 0 && flop[0] == outCards.peek()[0]) {
            outCards.push(flop)
            for (c in outCards) {
                player1Cards.add(c)
            }
            outCards.clear()
            binding.curCardActivityFriend.setImageResource(R.drawable.transparentcard)
        }
        else {
            outCards.push(flop)
            binding.curCardActivityFriend.setImageResource(UIRepository.cards[flop]!!)
        }
        switchPlayers()
    }

    private fun switchPlayers() {
        if (remainCards.size <= 0) {
            refreshGame()
            val win = player1CardsCount < player2CardsCount
            val msg = if (win) "当前玩家胜利" else {
                if (player1CardsCount == player2CardsCount)
                    "平局"
                else
                    "对立玩家胜利"
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
            swapCounts()
            swapCards()
            swapAvt()
            refreshGame()
        }
    }

    private fun swapAvt() {
        if (curPlayer == 1) {
            binding.myAvtActivityFriend.setImageResource(R.drawable.pig_girl)
            binding.enemyAvtActivityFriend.setImageResource(R.drawable.pig_boy)
            curPlayer = 2
        }
        else {
            binding.myAvtActivityFriend.setImageResource(R.drawable.pig_boy)
            binding.enemyAvtActivityFriend.setImageResource(R.drawable.pig_girl)
            curPlayer = 1
        }
    }

    private fun refreshGame() {
        binding.myCardsActivityFriend.removeAllViews()
        binding.enemyCardsActivityFriend.removeAllViews()
        if (player1Cards.size > 0) {
            player1CardsCount = 0
            for (c in player1Cards)
                addPlayer1Card(c)
        }
        if (player2Cards.size > 0) {
            player2CardsCount = 0
            for (c in player2Cards)
                addPlayer2Card(c)
        }
    }

    private fun swapCards() {
        val tempCards = ArrayList<String>(player1Cards)
        player1Cards.clear()
        player1Cards.addAll(player2Cards)
        player2Cards.clear()
        player2Cards.addAll(tempCards)
        tempCards.clear()
    }

    private fun swapCounts() {
        val tempCount = player1CardsCount
        player1CardsCount = player2CardsCount
        player2CardsCount = tempCount
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