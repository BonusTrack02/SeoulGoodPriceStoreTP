package com.bonustrack02.tp08goodprice

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bonustrack02.tp08goodprice.databinding.ActivityMainBinding
import java.util.Date
import kotlin.system.exitProcess

class MainActivity: AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val fragmentManager: FragmentManager by lazy { supportFragmentManager }
    private val fragments = mutableListOf<Fragment?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        fragments.add(KoreanFragment())

        fragmentManager.beginTransaction().add(R.id.container, fragments[0]!!).commit()

        for (i in 0 until 4) fragments.add(null)

        binding.bottomNavigation.setOnItemSelectedListener {
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()

            for (j in 0 until 4) {
                if (fragments[j] != null) transaction.hide(fragments[j]!!)
            }

            when (it.itemId) {
                R.id.menu_bottom_korean -> transaction.show(fragments[0]!!)
                R.id.menu_bottom_chinese -> {
                    if (fragments[1] == null) {
                        fragments[1] = ChineseFragment()
                        transaction.add(binding.container.id, fragments[1]!!)
                    }
                    transaction.show(fragments[1]!!)
                }
                R.id.menu_bottom_japanese -> {
                    if (fragments[2] == null) {
                        fragments[2] = JapaneseFragment()
                        transaction.add(binding.container.id, fragments[2]!!)
                    }
                    transaction.show(fragments[2]!!)
                }
                R.id.menu_bottom_others -> {
                    if (fragments[3] == null) {
                        fragments[3] = OthersFragment()
                        transaction.add(binding.container.id, fragments[3]!!)
                    }
                    transaction.show(fragments[3]!!)
                }
            }
            transaction.commit()
            return@setOnItemSelectedListener true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    var wasPressed = false
    var lastTime = 0L
    override fun onBackPressed() {
        if (!wasPressed) {
            Toast.makeText(this, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
            wasPressed = true
            lastTime = Date().time
        } else {
            var now = Date().time
            if (now - lastTime > 3000) wasPressed = false
            else {
                finishAffinity()
                exitProcess(0)
            }
        }
    }
}