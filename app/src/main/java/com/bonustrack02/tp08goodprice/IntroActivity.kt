package com.bonustrack02.tp08goodprice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.bonustrack02.tp08goodprice.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    val binding: ActivityIntroBinding by lazy { ActivityIntroBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        handler.sendEmptyMessageDelayed(0, 1500)
    }

    val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val intent = Intent(this@IntroActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}