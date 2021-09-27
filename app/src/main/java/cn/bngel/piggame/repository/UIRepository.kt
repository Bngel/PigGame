package cn.bngel.piggame.repository

import android.content.Context
import cn.bngel.piggame.R
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

object UIRepository {

    fun createSimpleLoadingTipDialog(context: Context, title: String, content: String): MaterialDialog = MaterialDialog.Builder(context)
        .iconRes(R.drawable.dialog_tip)
        .limitIconToDefaultSize()
        .title(title)
        .content(content)
        .progress(true, 0)
        .progressIndeterminateStyle(false)
        .build()

    fun createSimpleLoadingTipDialog(context: Context, content: String): MaterialDialog = MaterialDialog.Builder(context)
        .iconRes(R.drawable.dialog_tip)
        .limitIconToDefaultSize()
        .title("提示:")
        .content(content)
        .progress(true, 0)
        .progressIndeterminateStyle(false)
        .build()
}