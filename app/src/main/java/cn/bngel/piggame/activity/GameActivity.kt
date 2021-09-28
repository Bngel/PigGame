package cn.bngel.piggame.activity

import android.os.Bundle
import cn.bngel.piggame.R
import cn.bngel.piggame.databinding.ActivityGameBinding
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

class GameActivity : BaseActivity() {

    lateinit var binding: ActivityGameBinding

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
    }


}