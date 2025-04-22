package com.bmac.surwayapp.Activity

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bmac.surwayapp.ApiClient.CRACApiClient
import com.bmac.surwayapp.ApiInterface.ApiService
import com.bmac.surwayapp.R
import com.bmac.surwayapp.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences:SharedPreferences
    lateinit var apiService: ApiService

    var m_androidId = ""
    var company_name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        apiService = CRACApiClient.getapiclient().create(ApiService::class.java)
        sharedPreferences = getSharedPreferences("C_NM_M_ID_PREF", MODE_PRIVATE)

       /* val DarkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK// Retrieve the Mode of the App.
        val isDarkModeOn = DarkModeFlags == Configuration.UI_MODE_NIGHT_YES//Check if the Dark Mode is On

        if(isDarkModeOn)
        { binding.linearLogin.setBackgroundColor(Color.parseColor("#FFFFFF")) }
        else
        {    binding.linearLogin.setBackgroundColor(Color.parseColor("#FFFFFF")) }*/

        // generate device id

        m_androidId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID).toString()
        //Log.d("machine", "$m_androidId")

        getCompanyName()
        binding.txtMachineID.text = m_androidId
        //val appVersion = getString(R.string.app_version)
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val version_name = pInfo.versionName
        val version_code = pInfo.versionCode
        binding.txtBuildno.text = "Version : $version_name"


        //copy to clipboard
        binding.txtMachineID.setOnClickListener {
            val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", m_androidId)
            if (clipboard == null || clip == null){
                return@setOnClickListener
            }else{
                clipboard.setPrimaryClip(clip)
                Toast.makeText(applicationContext,
                    getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show()
            }
        }

        val spannableString = SpannableString(binding.txtRegister.text.toString())
        spannableString.setSpan(UnderlineSpan(), 24, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.txtRegister.text = spannableString

        binding.txtRegister.setOnClickListener {
            Toast.makeText(applicationContext, getString(R.string.registration), Toast.LENGTH_SHORT).show()
        }
        binding.btnLogin.setOnClickListener {

            var usernm = binding.edtUserName.text.toString()
            var password = binding.edtUserPassword.text.toString()

            if((usernm == "admin") && (password == "admin")){

                var i = Intent(applicationContext, MainMenuActivity::class.java)
                startActivity(i)

                val c_nm_m_id_pref = sharedPreferences.edit()
                c_nm_m_id_pref.putString("machineID", m_androidId)
                c_nm_m_id_pref.putString("company_name", company_name)
                Log.d("idss","${c_nm_m_id_pref.putString("machineID", m_androidId)}  ${c_nm_m_id_pref.putString("company_name", company_name)}")
                c_nm_m_id_pref.commit()

                //Toast.makeText(applicationContext, "name  $company_name", Toast.LENGTH_SHORT).show()
            }
            else
            { Toast.makeText(applicationContext,
                getString(R.string.please_enter_valid_email_or_password), Toast.LENGTH_SHORT).show() } }
    }

    // control api response
    @SuppressLint("SuspiciousIndentation")
    private fun getCompanyName() {
       // var call: Call<String> = apiService.getCompanyNameapi()
    var call: Call<String> = apiService.getCompanyName(m_androidId)
        call.enqueue(object :Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.isSuccessful){
                   // Toast.makeText(applicationContext, "Response  ${response.body()}", Toast.LENGTH_SHORT).show()
                    Log.d("response","${response.body()}")
                    company_name = response.body()!!
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
               // Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                Log.d("error","${t.message}")
            }
        })
    }
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Really Exit?")
            .setMessage("Are you sure you want to exit?")
            .setNegativeButton(android.R.string.no, null)
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    super.onBackPressed()
                    quit()
                }).create().show()
    }


    fun quit() {
        val start = Intent(Intent.ACTION_MAIN)
        start.addCategory(Intent.CATEGORY_HOME)
        start.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(start)
    }
}