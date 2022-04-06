package com.jms.a20220327_criminalintent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jms.a20220327_criminalintent.databinding.ActivityIntroBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch{
            launch {
                val binding = ActivityIntroBinding.inflate(layoutInflater)
                setContentView(binding.root)
            }.join()
            delay(1000)
            launch {
                val intent = Intent(applicationContext,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}