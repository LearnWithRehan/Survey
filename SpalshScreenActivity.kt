package com.bmac.surwayapp.Activity


import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaPlayer.OnCompletionListener
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bmac.surwayapp.R
import com.bmac.surwayapp.databinding.ActivityMainBinding


class SpalshScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK// Retrieve the Mode of the App.
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES//Check if the Dark Mode is On

        if(isDarkModeOn)
        { binding.linearSplashScreen.setBackgroundColor(Color.parseColor("#FFFFFF")) }
        else
        {    binding.linearSplashScreen.setBackgroundColor(Color.parseColor("#FFFFFF")) }*/

        Handler().postDelayed(Runnable {
            startActivity(Intent(applicationContext,LoginActivity::class.java))

         /*   val conMgr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = conMgr.activeNetworkInfo
            if (netInfo == null) {

                AlertDialog.Builder(this)
                    .setTitle(resources.getString(R.string.app_name))
                    .setMessage(resources.getString(R.string.network_error))
                    .setPositiveButton("OK", null).show()
            } else {

                startActivity(Intent(applicationContext,LoginActivity::class.java))
                //startActivity(Intent(applicationContext,MainMenuActivity::class.java))
            }*/

        }, 3000)


    }


}