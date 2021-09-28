package cn.bngel.piggame.repository

import android.content.Context
import cn.bngel.piggame.R
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog

object UIRepository {

    val cards = mapOf(
        "S1" to R.drawable.s01,
        "S2" to R.drawable.s02,
        "S3" to R.drawable.s03,
        "S4" to R.drawable.s04,
        "S5" to R.drawable.s05,
        "S6" to R.drawable.s06,
        "S7" to R.drawable.s07,
        "S8" to R.drawable.s08,
        "S9" to R.drawable.s09,
        "S10" to R.drawable.s10,
        "SJ" to R.drawable.s11,
        "SQ" to R.drawable.s12,
        "SK" to R.drawable.s13,
        "H1" to R.drawable.h01,
        "H2" to R.drawable.h02,
        "H3" to R.drawable.h03,
        "H4" to R.drawable.h04,
        "H5" to R.drawable.h05,
        "H6" to R.drawable.h06,
        "H7" to R.drawable.h07,
        "H8" to R.drawable.h08,
        "H9" to R.drawable.h09,
        "H10" to R.drawable.h10,
        "HJ" to R.drawable.h11,
        "HQ" to R.drawable.h12,
        "HK" to R.drawable.h13,
        "C1" to R.drawable.c01,
        "C2" to R.drawable.c02,
        "C3" to R.drawable.c03,
        "C4" to R.drawable.c04,
        "C5" to R.drawable.c05,
        "C6" to R.drawable.c06,
        "C7" to R.drawable.c07,
        "C8" to R.drawable.c08,
        "C9" to R.drawable.c09,
        "C10" to R.drawable.c10,
        "CJ" to R.drawable.c11,
        "CQ" to R.drawable.c12,
        "CK" to R.drawable.c13,
        "D1" to R.drawable.d01,
        "D2" to R.drawable.d02,
        "D3" to R.drawable.d03,
        "D4" to R.drawable.d04,
        "D5" to R.drawable.d05,
        "D6" to R.drawable.d06,
        "D7" to R.drawable.d07,
        "D8" to R.drawable.d08,
        "D9" to R.drawable.d09,
        "D10" to R.drawable.d10,
        "DJ" to R.drawable.d11,
        "DQ" to R.drawable.d12,
        "DK" to R.drawable.d13,
    )

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