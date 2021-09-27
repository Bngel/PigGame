package cn.bngel.piggame.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Toast
import cn.bngel.piggame.R
import cn.bngel.piggame.dao.DefaultData
import cn.bngel.piggame.dao.getGameIndex.Game
import cn.bngel.piggame.databinding.ViewGameCardBinding
import cn.bngel.piggame.repository.StatusRepository
import cn.bngel.piggame.repository.UIRepository
import cn.bngel.piggame.service.DaoEvent
import cn.bngel.piggame.service.GameService
import com.xuexiang.xui.widget.layout.XUILinearLayout

class GameCardView: XUILinearLayout {

    private lateinit var binding: ViewGameCardBinding
    private val gameService = GameService()

    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, game: Game): super(context) {
        binding = ViewGameCardBinding.bind(this)
        binding.gameIdViewGameCard.setCenterBottomString(game.uuid)
        binding.joinGameViewGameCard.setOnClickListener {
            val materialDialog = UIRepository.createSimpleLoadingTipDialog(context, "正在加入房间...")
            materialDialog.show()
            gameService.postGameUUID(game.uuid, StatusRepository.userToken, object : DaoEvent {
                override fun <T> success(data: DefaultData<T>) {
                    Toast.makeText(context, "加入房间成功", Toast.LENGTH_SHORT).show()
                    materialDialog.dismiss()
                }

                override fun <T> failure(data: DefaultData<T>?) {
                    Toast.makeText(context, "加入房间失败, 请重试", Toast.LENGTH_SHORT).show()
                    materialDialog.dismiss()
                }
            })
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_game_card, this)
    }
}