package com.bmac.surwayapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bmac.surwayapp.Fragments.SavedSurveyFragment
import com.bmac.surwayapp.Fragments.StartSurwayFragment
import com.bmac.surwayapp.Fragments.SynchronizeDataFragment
import com.bmac.surwayapp.R
import com.bmac.surwayapp.databinding.ActivityMainMenuBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView


class MainMenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {

    private lateinit var binding:ActivityMainMenuBinding
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    var machindID = ""

    var company_name = ""
    lateinit var currentLocation: Location
    lateinit var locationManager: LocationManager
    private var isGPSEnable = false
    private var doubleBackToExitPressedOnce = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermission()

        var i = intent
        machindID = i.getStringExtra("machineID").toString()
        company_name = i.getStringExtra("company_name").toString()


        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.myDrawerLayout,
            com.bmac.surwayapp.R.string.nav_open, com.bmac.surwayapp.R.string.nav_close)
        setSupportActionBar(binding.toolStartSurway)

        binding.btnfabAddSurway.setColorFilter(Color.WHITE)
        binding.myDrawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.drawerArrowDrawable.color = Color.parseColor("#FFFFFF");
        actionBarDrawerToggle.syncState()

        binding.myNavigationMenu.setNavigationItemSelectedListener(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
             binding.btnfabAddSurway.setOnClickListener {

            var surwayFragment = StartSurwayFragment()
            var fm: FragmentManager = supportFragmentManager
            var ft: FragmentTransaction = fm.beginTransaction()
            ft.replace(R.id.myFrame,surwayFragment).commit()
            binding.btnfabAddSurway.visibility = View.GONE
        }

        // new method for handle onbackpress old method is deprecated now
        val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val dialog = MaterialAlertDialogBuilder(this@MainMenuActivity)
                dialog.setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton("YES") { dialogInterface: DialogInterface, i: Int ->
                        finishAffinity()
                    }
                    .setNegativeButton("NO") { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.dismiss()
                    }
                    .show()
            }
        }
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
      //  getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback)
    }

    @SuppressLint("MissingPermission")
    private fun checkLocationOnStart(){
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (isLocationEnabled()) {

            var lattitude1 = 0.00
            var longitude1 = 0.00
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0F, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        lattitude1 = location.latitude
                        longitude1 = location.longitude

                       // Toast.makeText(applicationContext, "${location.accuracy}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onProviderDisabled(provider: String) {}

                    override fun onProviderEnabled(provider: String) {}

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                })
                try {
                    currentLocation =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
                    if (currentLocation != null) {

                        lattitude1 = currentLocation!!.latitude
                        longitude1 = currentLocation!!.longitude
                        // Toast.makeText(applicationContext, "$lattitude1   $longitude1   ${currentLocation.accuracy}", Toast.LENGTH_SHORT).show()
//                        Toast.makeText(applicationContext, "${currentLocation.accuracy}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
                    Log.d("error", "${e.message}")
                }
            }

        } else { showAlert() }

    }
        private fun showAlert() {
            val dialog = MaterialAlertDialogBuilder(this)
            dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is 'Off'.\n\nPlease Enable Location For Measure Area")
                .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                    val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(myIntent)
                    paramDialogInterface.dismiss()
                }
                .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
            dialog.show()
        }
    private fun isLocationEnabled(): Boolean {
        var locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displayItem(item.itemId)
        return  true
    }

    fun displayItem(ItemID :Int) {
        var fragment: Fragment? = null

        when (ItemID) {

            R.id.nav_home_screen -> {
                // display home screen
                startActivity(Intent(applicationContext, MainMenuActivity::class.java))
            }

            R.id.nav_synchronized_data -> {
                fragment = SynchronizeDataFragment()
                val mBundle = Bundle()
                mBundle.putString("m_id",machindID)
                fragment.arguments = mBundle
                binding.btnfabAddSurway.visibility = View.GONE
                binding.toolStartSurway.title = getString(R.string.synchronize_data)
            }

            R.id.nav_saved_survey -> {
                fragment = SavedSurveyFragment()
                binding.btnfabAddSurway.visibility = View.GONE
                binding.toolStartSurway.title = getString(R.string.saved_survey)
            }
            R.id.nav_map_survey -> {
                val url = "http://122.176.50.103:8080/cane_indu/Dist/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)

            }


            R.id.nav_log_out ->
            {
                val intent: Intent = Intent(applicationContext, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.startActivity(intent)
                finish()
            }


        }
        if (fragment != null) {
            val fragmentManager = supportFragmentManager
            fragmentManager.beginTransaction().replace(R.id.myFrame, fragment).commit()
            binding.myDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }


    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions(arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION//use for android sdk 34(android 14)
                ), 999
            )
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 999)

        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 999
                )
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            if (requestCode == 999) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        checkLocationOnStart()
                } else {
                    Toast.makeText(applicationContext, "Permission Not Granted", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (requestCode == 999) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationOnStart()

                } else {
                    Toast.makeText(applicationContext, "Permission Not Granted", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (requestCode == 999) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationOnStart()

                } else {
                    Toast.makeText(applicationContext, "Permission Not Granted", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        else { Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show() }

    }
}
