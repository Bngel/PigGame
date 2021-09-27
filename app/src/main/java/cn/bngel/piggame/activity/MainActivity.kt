package cn.bngel.piggame.activity

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.*
import cn.bngel.piggame.dao.DefaultData
import cn.bngel.piggame.dao.postUserLogin.PostUserLogin
import cn.bngel.piggame.databinding.ActivityMainBinding
import cn.bngel.piggame.repository.StatusRepository
import cn.bngel.piggame.repository.StatusRepository.loginStatus
import cn.bngel.piggame.service.DaoEvent
import cn.bngel.piggame.service.LoginService
import com.dyhdyh.widget.loadingbar2.LoadingBar

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    val loginService = LoginService()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val loadingDialog = LoadingBar.dialog(this).extras(Array(1) { "登录中..." })
        binding.onlineBtnActivityMain.setOnClickListener {
            //val intent = Intent(this, LobbyActivity::class.java)
            //startActivity(intent)
            loadingDialog.show()
            loginService.postUserLogin("061900209", "a147258369", object : DaoEvent {
                override fun <T> success(data: DefaultData<T>) {
                    val login = data.data as PostUserLogin
                    StatusRepository.userToken = login.token
                    Toast.makeText(this@MainActivity, "登录成功", Toast.LENGTH_SHORT).show()
                    loadingDialog.cancel()
                }

                override fun failure() {
                    Toast.makeText(this@MainActivity, "登录失败", Toast.LENGTH_SHORT).show()
                    loadingDialog.cancel()
                }
            })
        }
    }
}
