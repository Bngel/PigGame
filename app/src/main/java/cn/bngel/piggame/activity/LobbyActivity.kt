package cn.bngel.piggame.activity

import android.os.Bundle
import cn.bngel.piggame.databinding.ActivityLobbyBinding

class LobbyActivity : BaseActivity() {

    lateinit var binding: ActivityLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLobbyBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}