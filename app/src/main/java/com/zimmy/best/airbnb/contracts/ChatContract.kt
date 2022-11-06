package com.zimmy.best.airbnb.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.zimmy.best.airbnb.konstants.Konstants

class ChatContract : ActivityResultContract<Intent, Boolean>() {

    override fun createIntent(context: Context, input: Intent): Intent {
        return input
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean {
        if (resultCode == Activity.RESULT_OK) {
            Log.d(ChatContract::class.java.simpleName, "result ok")
            if (intent != null) {
                return intent.getSerializableExtra(Konstants.DATA) as Boolean
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(ChatContract::class.java.simpleName, "result cancelled")
        }
        return false
    }
}