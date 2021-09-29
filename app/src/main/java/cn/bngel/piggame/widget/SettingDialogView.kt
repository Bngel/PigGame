package cn.bngel.piggame.widget

import android.content.Context
import android.media.AudioManager
import android.view.LayoutInflater
import cn.bngel.piggame.R
import com.xuexiang.xui.widget.layout.XUILinearLayout

class SettingDialogView : XUILinearLayout{

    constructor(context: Context): super(context) {
        val audioManager =  context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val volume = findViewById<com.xuexiang.xui.widget.picker.XSeekBar>(R.id.volume_sum_view_dialog_setting)
        val streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volume.max = streamMaxVolume
        volume.setDefaultValue(currentVolume)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_dialog_setting, this)
    }
}