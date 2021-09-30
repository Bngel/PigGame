package cn.bngel.piggame.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.*
import cn.bngel.piggame.R
import cn.bngel.piggame.dao.DefaultData
import cn.bngel.piggame.dao.postUserLogin.PostUserLogin
import cn.bngel.piggame.databinding.ActivityMainBinding
import cn.bngel.piggame.repository.StatusRepository
import cn.bngel.piggame.repository.UIRepository
import cn.bngel.piggame.service.DaoEvent
import cn.bngel.piggame.service.LoginEvent
import cn.bngel.piggame.service.LoginService
import com.xuexiang.xui.widget.dialog.materialdialog.CustomMaterialDialog
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val loginService = LoginService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.onlineBtnActivityMain.setOnClickListener {
            online()
        }
        
        binding.robotBtnActivityMain.setOnClickListener {
            robot()
        }

        binding.friendsBtnActivityMain.setOnClickListener {
            friends()
        }
    }

    private fun online() {

        val materialDialog = UIRepository.createSimpleLoadingTipDialog(this, "登录中...")

        val intent = Intent(this, LobbyActivity::class.java)
        val loginDialog = MaterialDialog.Builder(this)
            .title("登录界面")
            .customView(R.layout.view_dialog_login, true)
            .positiveText("登录")
            .negativeText("取消")
            .cancelable(false)
            .onPositive { dialog, which ->
                val account = dialog.findViewById<com.xuexiang.xui.widget.edittext.ClearEditText>(R.id.account_view_dialog_login)
                val password = dialog.findViewById<com.xuexiang.xui.widget.edittext.PasswordEditText>(R.id.password_view_dialog_login)
                materialDialog.show()
                loginService.postUserLogin(account.text.toString(), password.text.toString(), object : LoginEvent {
                    override fun success(data: PostUserLogin) {
                        val login = data.data
                        StatusRepository.userToken = login.token
                        Toast.makeText(this@MainActivity, "登录成功", Toast.LENGTH_SHORT).show()
                        materialDialog.dismiss()
                        startActivity(intent)
                    }

                    override fun failure(data: PostUserLogin?) {
                        if (!materialDialog.isCancelled) {
                            Toast.makeText(this@MainActivity, "登录失败", Toast.LENGTH_SHORT).show()
                            materialDialog.dismiss()
                        }
                    }
                })
            }
            .build()
        loginDialog.show()
    }

    private fun friends() {
        val intent = Intent(this, FriendActivity::class.java)
        startActivity(intent)
    }

    private fun robot() {
    }
}
