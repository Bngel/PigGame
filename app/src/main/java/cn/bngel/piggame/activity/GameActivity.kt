package cn.bngel.piggame.activity

import android.os.Bundle
import cn.bngel.piggame.databinding.ActivityGameBinding

class GameActivity : BaseActivity() {

    lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}