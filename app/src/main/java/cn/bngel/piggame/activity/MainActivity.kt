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
    }

    private fun online() {

        val materialDialog = UIRepository.createSimpleLoadingTipDialog(this, "登录中...")

        val intent = Intent(this, LobbyActivity::class.java)
        materialDialog.show()
        loginService.postUserLogin("061900209", "a147258369", object : LoginEvent {
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
}
