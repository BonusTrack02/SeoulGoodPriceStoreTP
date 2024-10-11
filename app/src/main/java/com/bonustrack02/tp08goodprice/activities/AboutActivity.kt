package com.bonustrack02.tp08goodprice.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bonustrack02.tp08goodprice.databinding.ActivityAboutBinding
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

class AboutActivity : AppCompatActivity() {
    private val binding: ActivityAboutBinding by lazy { ActivityAboutBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonOpenSource.setOnClickListener { onClickOpenSource() }
    }

    private fun onClickOpenSource() {
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
    }
}