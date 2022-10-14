package com.zimmy.best.airbnb.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.zimmy.best.airbnb.konstants.Konstants


class DateContracts : ActivityResultContract<Intent, Pair<Long, Long>?>() {
    override fun createIntent(context: Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Pair<Long, Long>? {
        if (resultCode == Activity.RESULT_OK) {
            Log.d(DateContracts::class.java.simpleName, "result ok")
            if (intent != null) {
                return intent.getSerializableExtra(Konstants.DATA) as Pair<Long, Long>
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.v(DateContracts::class.java.simpleName, "result cancelled")
        }
        return null
    }
}