package com.example.womensaftey.data

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.womensaftey.LoginActivity2
import com.example.womensaftey.R
import com.example.womensaftey.mainactivity


class Splash_Screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        SharedPref.init(this)

        val isUserLoggedIn = SharedPref.getBoolean("isUserLoggedIn")

        if (isUserLoggedIn){
            startActivity(Intent(this, mainactivity::class.java))
            finish()
        }else{
            startActivity(Intent(this, LoginActivity2::class.java))
            finish()
        }
    }
}