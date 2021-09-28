package cn.bngel.piggame.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.get
import androidx.core.view.marginLeft
import androidx.core.view.size
import cn.bngel.piggame.R
import cn.bngel.piggame.databinding.ActivityGameBinding
import cn.bngel.piggame.repository.UIRepository
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

class GameActivity : BaseActivity() {

    lateinit var binding: ActivityGameBinding
    var myCardCount = 0
    var enemyCardCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.settingActivityGame.setOnClickListener {
            val setDialog = MaterialDialog.Builder(this)
                .customView(R.layout.view_dialog_setting, true)
                .positiveText("确定")
                .negativeText("取消")
                .title("系统设置")
                .build()
            setDialog.show()
        }
        binding.outCardActivityGame.setOnClickListener {
            addMyCard("H1")
            addEnemyCard()
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
        binding.myCardsActivityGame.addView(imageView)
        myCardCount += 1
    }

    private fun addEnemyCard() {
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
        binding.enemyCardsActivityGame.addView(imageView)
        enemyCardCount += 1
    }



}