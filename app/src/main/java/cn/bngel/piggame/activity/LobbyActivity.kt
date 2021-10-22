package cn.bngel.piggame.activity

import android.os.Bundle
import cn.bngel.piggame.databinding.ActivityLobbyBinding
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.text.InputType
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import cn.bngel.piggame.R
import cn.bngel.piggame.dao.DefaultData
import cn.bngel.piggame.dao.getGameIndex.GetGameIndex
import cn.bngel.piggame.dao.getGameUUID.GetGameUUID
import cn.bngel.piggame.dao.getGameUUIDLast.GetGameUUIDLast
import cn.bngel.piggame.dao.postGame.PostGame
import cn.bngel.piggame.dao.postGameUUID.PostGameUUID
import cn.bngel.piggame.repository.StatusRepository
import cn.bngel.piggame.repository.UIRepository
import cn.bngel.piggame.service.DaoEvent
import cn.bngel.piggame.service.GameService
import cn.bngel.piggame.widget.GameCardView
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog.SingleButtonCallback


class LobbyActivity : BaseActivity() {

    private lateinit var binding: ActivityLobbyBinding
    private val gameService = GameService()
    private var curPage = 1
    private val pageSize = "4"
    private var totalPages = -1
    private var totalGames = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initGames()
        binding.createGameActivityLobby.setOnClickListener {
            createGame()
        }
        binding.joinGameActivityLobby.setOnClickListener {
            joinGame()
        }
        binding.prevGamesActivityLobby.setOnClickListener {
            if (curPage == 1) {
                Toast.makeText(this, "再往前翻没有啦~", Toast.LENGTH_SHORT).show()
            }
            else {
                prevGames()
            }
        }
        binding.nextGamesActivityLobby.setOnClickListener {
            if (curPage == totalPages) {
                Toast.makeText(this, "已经是最后一页啦~", Toast.LENGTH_SHORT).show()
            }
            else {
                nextGames()
            }
        }
        binding.quitAccountActivityLobby.setOnClickListener {
            val build = MaterialDialog.Builder(this)
                .iconRes(R.drawable.dialog_tip)
                .limitIconToDefaultSize()
                .title("提示:")
                .content("是否确定退出账号?")
                .positiveText("确定")
                .cancelable(false)
                .onPositive() { dialog, _ ->
                    StatusRepository.clearAccountFromPreferences(this)
                    finish()
                }
                .negativeText("取消")
                .build()
            build.show()
        }
    }

    private fun prevGames(){
        val loadingDialog = UIRepository.createSimpleLoadingTipDialog(this, "房间列表加载中...")
        loadingDialog.show()
        curPage -= 1
        gameService.getGameIndex(pageSize, curPage.toString(), StatusRepository.userToken, object:DaoEvent{
            override fun <T> success(data: DefaultData<T>) {
                val games = data.data as GetGameIndex
                val curGames = games.games
                totalGames = games.total
                totalPages = games.total_page_num
                val params = LinearLayout.LayoutParams(0, MATCH_PARENT)
                params.weight = 1.0f
                params.setMargins(20,20,20,20)
                binding.gameCardsActivityLobby.removeAllViews()
                for (game in curGames) {
                    val view = GameCardView(this@LobbyActivity, game)
                    view.layoutParams = params
                    binding.gameCardsActivityLobby.addView(view, 0)
                }
                binding.titleActivityLobby.setCenterBottomString("总场次: $totalGames")
                binding.titleActivityLobby.setRightBottomString("页数: ${curPage}/${totalPages}")
                loadingDialog.dismiss()
            }

            override fun <T> failure(data: DefaultData<T>?) {
                println(data?.message)
                loadingDialog.dismiss()
            }

        })
    }

    private fun nextGames(){
        val loadingDialog = UIRepository.createSimpleLoadingTipDialog(this, "房间列表加载中...")
        loadingDialog.show()
        curPage += 1
        gameService.getGameIndex(pageSize, curPage.toString(), StatusRepository.userToken, object:DaoEvent{
            override fun <T> success(data: DefaultData<T>) {
                val games = data.data as GetGameIndex
                val curGames = games.games
                totalGames = games.total
                totalPages = games.total_page_num
                val params = LinearLayout.LayoutParams(0, MATCH_PARENT)
                params.weight = 1.0f
                params.setMargins(20,20,20,20)
                binding.gameCardsActivityLobby.removeAllViews()
                for (game in curGames) {
                    val view = GameCardView(this@LobbyActivity, game)
                    view.layoutParams = params
                    binding.gameCardsActivityLobby.addView(view)
                }
                if (curGames.size < pageSize.toInt()) {
                    for (i in 1..(pageSize.toInt() - curGames.size)) {
                        val view = GameCardView(this@LobbyActivity, curGames[0])
                        view.layoutParams = params
                        view.visibility = View.INVISIBLE
                        binding.gameCardsActivityLobby.addView(view)
                    }
                }
                binding.titleActivityLobby.setCenterBottomString("总场次: $totalGames")
                binding.titleActivityLobby.setRightBottomString("页数: ${curPage}/${totalPages}")
                loadingDialog.dismiss()
            }

            override fun <T> failure(data: DefaultData<T>?) {
                println(data?.message)
                loadingDialog.dismiss()
            }

        })
    }

    private fun initGames() {
        val loadingDialog = UIRepository.createSimpleLoadingTipDialog(this, "房间列表加载中...")
        loadingDialog.show()
        gameService.getGameIndex(pageSize, curPage.toString(), StatusRepository.userToken, object:DaoEvent{
            override fun <T> success(data: DefaultData<T>) {
                val games = data.data as GetGameIndex
                totalGames = games.total
                totalPages = games.total_page_num
                val curGames = games.games
                val params = LinearLayout.LayoutParams(0, MATCH_PARENT)
                params.weight = 1.0f
                params.setMargins(20,20,20,20)
                binding.gameCardsActivityLobby.removeAllViews()
                for (game in curGames) {
                    val view = GameCardView(this@LobbyActivity, game)
                    view.layoutParams = params
                    binding.gameCardsActivityLobby.addView(view)
                }
                binding.titleActivityLobby.setCenterBottomString("总场次: $totalGames")
                binding.titleActivityLobby.setRightBottomString("页数: ${curPage}/${totalPages}")
                loadingDialog.dismiss()
            }

            override fun <T> failure(data: DefaultData<T>?) {
                println(data?.message)
                Toast.makeText(this@LobbyActivity, "房间列表加载失败", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
                finish()
            }

        })
    }

    private fun joinGame() {
        val materialDialog = MaterialDialog.Builder(this)
            .iconRes(R.drawable.dialog_tip)
            .title("提示:")
            .content("请输入房间代码")
            .inputType(
                InputType.TYPE_CLASS_TEXT
                        or InputType.TYPE_TEXT_VARIATION_NORMAL
                        or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            )
            .input("房间代码", "") { _, _ ->  }
            .positiveText("加入")
            .negativeText("取消")
            .onPositive((SingleButtonCallback { dialog: MaterialDialog, _: DialogAction? ->
                val uuid = dialog.inputEditText?.text.toString()
                val materialDialog = UIRepository.createSimpleLoadingTipDialog(this, "正在加入房间...")
                materialDialog.show()
                gameService.postGameUUID(uuid, StatusRepository.userToken, object : DaoEvent {
                    override fun <T> success(data: DefaultData<T>) {
                        val intent = Intent(this@LobbyActivity, GameActivity::class.java)
                        gameService.getGameUUIDLast(
                            uuid, StatusRepository.userToken, object: DaoEvent {
                                override fun <T> success(data: DefaultData<T>) {
                                    val last = data.data as GetGameUUIDLast
                                    Toast.makeText(this@LobbyActivity, "加入房间成功", Toast.LENGTH_SHORT).show()
                                    intent.putExtra("game", last)
                                    intent.putExtra("uuid", uuid)
                                    intent.putExtra("host", false)
                                    startActivity(intent)
                                }

                                override fun <T> failure(data: DefaultData<T>?) {
                                    println(data)
                                    if (data?.code == 403) {
                                        Toast.makeText(
                                            this@LobbyActivity,
                                            "房间已满, 请更换房间加入",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@LobbyActivity,
                                            "加入房间失败, 请重试",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            })
                        materialDialog.dismiss()
                    }

                    override fun <T> failure(data: DefaultData<T>?) {
                        Toast.makeText(this@LobbyActivity, "加入房间失败, 请重试", Toast.LENGTH_SHORT).show()
                        materialDialog.dismiss()
                    }

                })
            }))
            .cancelable(false)
            .build()

        materialDialog.show()
    }

    private fun createGame() {

        val createGame = MaterialDialog.Builder(this)
            .title("房间类型")
            .items(listOf("私有房间", "公开房间"))
            .itemsCallbackSingleChoice(0) { _, _, _, _ ->
                true
            }
            .itemsCallbackSingleChoice(1) { _, _, _, _ ->
                true
            }
            .positiveText("确定")
            .negativeText("取消")
            .onPositive { dialog, which ->
                val index = dialog.selectedIndex
                postGame(index == 0)
            }
            .cancelable(false)
            .build()

        createGame.show()
    }

    private fun postGame(private: Boolean) {
        val materialDialog = UIRepository.createSimpleLoadingTipDialog(this, "正在创建房间...")
        materialDialog.show()
        gameService.postGame(private, StatusRepository.userToken, object:DaoEvent {
            override fun <T> success(data: DefaultData<T>) {
                val game = data.data as PostGame
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("uuid", game.uuid)
                clipboard.setPrimaryClip(clipData)
                Toast.makeText(this@LobbyActivity, "房间代码: {${game.uuid}}\n已复制到剪贴板, 快去分享给好友吧~", Toast.LENGTH_SHORT).show()
                materialDialog.dismiss()
                val intent = Intent(this@LobbyActivity, GameActivity::class.java)
                intent.putExtra("uuid", game.uuid)
                intent.putExtra("host", true)
                startActivity(intent)
            }

            override fun <T> failure(data: DefaultData<T>?) {
                Toast.makeText(this@LobbyActivity, "创建房间失败", Toast.LENGTH_SHORT).show()
                materialDialog.dismiss()
            }

        })
    }
}