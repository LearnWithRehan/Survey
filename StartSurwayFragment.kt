package com.bmac.surwayapp.Fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.bmac.surwayapp.Activity.MainMenuActivity
import com.bmac.surwayapp.ApiClient.CRACApiClient
import com.bmac.surwayapp.ApiInterface.ApiService
import com.bmac.surwayapp.Entity.CRACSurvayDetailClass
import com.bmac.surwayapp.Model.CRACSaveSurveyDetailsModel
import com.bmac.surwayapp.Model.VillageListModel
import com.bmac.surwayapp.R
import com.bmac.surwayapp.RoomDatabase.CRACDatabase
import com.bmac.surwayapp.Utils.DialogUtils
import com.bmac.surwayapp.Utils.PermissionsManager
import com.bmac.surwayapp.databinding.FragmentStartSurwayBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zcs.sdk.DriverManager
import com.zcs.sdk.Printer
import com.zcs.sdk.SdkResult
import com.zcs.sdk.Sys
import com.zcs.sdk.print.PrnStrFormat
import com.zcs.sdk.print.PrnTextFont
import com.zcs.sdk.print.PrnTextStyle
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.ExecutorService
import kotlin.math.round
import kotlin.math.roundToInt


class StartSurwayFragment : Fragment(), RadioGroup.OnCheckedChangeListener {


    private var _binding: FragmentStartSurwayBinding? = null
    private val binding get() = _binding!!

    lateinit var db: CRACDatabase
    var farmer_transfer = ""
    var rp_value = ""
    var entityVillage = VillageListModel()
    var entitySurvey = CRACSurvayDetailClass()
    var surveyModel = CRACSaveSurveyDetailsModel()
    var areapoint1 = ""
    var areapoint2 = ""
    var areapoint3 = ""
    var areapoint4 = ""
    var TotalHectorArea = 0.00
    var TotalDistance = 0.00

    //var TotalDistance = 0.00
    var listDistance = ArrayList<LatLng>()
    lateinit var currentLocation: Location
    lateinit var locationManager: LocationManager
    private var isGPSEnable = false
    private var isNetworkEnable = false
    lateinit var apiService: ApiService
    var loop = 10
    var m_androidId = ""
    var UniqueID = ""
    var plot_no = ""
    var Wardno = ""
    var plot_vcode = ""
    var Kitta = ""
    var east = ""
    var west = ""
    var north = ""
    var south = ""
    var share_holder = ""
    var company_name = ""
    var AreaDivision = 0.00
    private var mPermissionsManager: PermissionsManager? = null
    private var driverManager: DriverManager = DriverManager.getInstance()
    private var mSys: Sys? = null
    private var printer: Printer? = null
    private var mSingleThreadExecutor: ExecutorService? = null
    private lateinit var sharedPreferences: SharedPreferences

    var distancePoint12 = 0.00
    var distancePoint23 = 0.00
    var distancePoint34 = 0.00
    var distancePoint41 = 0.00
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSingleThreadExecutor = driverManager.singleThreadExecutor
    }


    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStartSurwayBinding.inflate(inflater, container, false)
        val view: View = binding.root

        sharedPreferences =
            requireActivity().getSharedPreferences("C_NM_M_ID_PREF", Context.MODE_PRIVATE)
        company_name = sharedPreferences.getString("company_name", "").toString()

        driverManager = DriverManager.getInstance()
        mSys = driverManager.baseSysDevice
        initSdk()

        printer = driverManager.printer
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGPSEnable = locationManager.isProviderEnabled(GPS_PROVIDER)
        isNetworkEnable = locationManager.isProviderEnabled(NETWORK_PROVIDER)

        apiService = CRACApiClient.getapiclient().create(ApiService::class.java)
        db = Room.databaseBuilder(requireActivity(), CRACDatabase::class.java, "FarmerDB")
            .allowMainThreadQueries().build()
        // generate android device id
        m_androidId =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()

        getSpinnerVillageCode()
        getSpinnerVerityCode()

        /* _binding!!.searchSpinGrowerCode.setOnClickListener {
             getSpinnerGrowerListData()
         }*/

// atv data

        /*     _binding!!.atvSelectVillageCode.setOnClickListener {
                 getVillageCode()
                 _binding!!.atvSelectVillageCode.showDropDown()
                 _binding!!.atvSelectVillageCode.threshold = 1

             }
             _binding!!.atvSelectFarmerCode.setOnClickListener {
                 if (_binding!!.atvSelectVillageCode.text.isEmpty()) {
                     Toast.makeText(requireContext(), "Please Select Village Code", Toast.LENGTH_SHORT).show()
                 } else {
                     getGrowerListData()
                     _binding!!.atvSelectFarmerCode.showDropDown()
                     _binding!!.atvSelectFarmerCode.threshold = 1
                 }
             }
            _binding!!.atvSelectShareHolderCode.setOnClickListener {
                     getGrowerListData()
                _binding!!.atvSelectShareHolderCode.showDropDown()
                _binding!!.atvSelectShareHolderCode.threshold = 1
            }
            _binding!!.atvSelectVerityCode.setOnClickListener {
                getVerityCode()
                _binding!!.atvSelectVerityCode.showDropDown()
                _binding!!.atvSelectVerityCode.threshold = 1
            }*/

        _binding!!.btnLatLng1.isEnabled = true
        _binding!!.btnLatLng2.isEnabled = false
        _binding!!.btnLatLng3.isEnabled = false
        _binding!!.btnLatLng4.isEnabled = false

        if (_binding!!.btnLatLng1.isEnabled) {
            _binding!!.btnLatLng1.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.green_btn))
        }
        if (!_binding!!.btnLatLng2.isEnabled) {
            _binding!!.btnLatLng2.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.grey))
        }
        if (!_binding!!.btnLatLng3.isEnabled) {
            _binding!!.btnLatLng3.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.grey))
        }
        if (!_binding!!.btnLatLng4.isEnabled) {
            _binding!!.btnLatLng4.backgroundTintList =
                ColorStateList.valueOf(resources.getColor(R.color.grey))
        }

        _binding!!.btnLatLng1.setOnClickListener {
            getLatLngLocations(_binding!!.btnLatLng1)
            /*  if (isLocationEnabled()) {
                  var dialog =
                      ProgressDialog.show(requireContext(), "", "Waiting For GPS Data...", true, false)
                  Handler().postDelayed(Runnable {
                      dialog.dismiss()
                      //find_Location1()
                      _binding!!.btnLatLng2.isEnabled = true
                      _binding!!.btnLatLng1.isEnabled = false

                      if (!_binding!!.btnLatLng1.isEnabled) {
                          _binding!!.btnLatLng1.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.grey))
                      }
                      _binding!!.btnLatLng2.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_btn))
                      _binding!!.btnLatLng2.setTextColor(Color.parseColor("#FFFFFF"))
                  }, 2000)
                  _binding!!.btnLatLng2.isEnabled = false
                  _binding!!.btnLatLng3.isEnabled = false
                  _binding!!.btnLatLng4.isEnabled = false

              } else { showAlert() }*/
        }
        _binding!!.btnLatLng2.setOnClickListener {
            getLatLngLocations(_binding!!.btnLatLng2)
        }
        _binding!!.btnLatLng3.setOnClickListener {
            getLatLngLocations(_binding!!.btnLatLng3)
        }
        _binding!!.btnLatLng4.setOnClickListener {
            getLatLngLocations(_binding!!.btnLatLng4)

        }



        _binding!!.edtShareHolder.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString() == "1") {
                    //_binding!!.atvSelectShareHolderCode.visibility = View.VISIBLE
                    //_binding!!.searchSpinShareHolder.visibility = View.VISIBLE
                    _binding!!.searchSpinCardViewShareHolder.visibility = View.VISIBLE
                } else {
                    //   _binding!!.atvSelectShareHolderCode.visibility = View.INVISIBLE
                    // _binding!!.searchSpinShareHolder.visibility = View.INVISIBLE
                    _binding!!.searchSpinCardViewShareHolder.visibility = View.INVISIBLE

                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })

        _binding!!.btnSubmit.setOnClickListener {

            if (/*_binding!!.atvSelectVillageCode.text.toString().isEmpty()*/
                _binding!!.searchSpinVillageCode.selectedItem.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please Select Village Code", Toast.LENGTH_SHORT)
                    .show()
            } else if (/*_binding!!.atvSelectFarmerCode.text.toString().isEmpty()*/
                _binding!!.searchSpinGrowerCode.selectedItem.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please Select Farmer Code", Toast.LENGTH_SHORT)
                    .show()
            } else if (/*_binding!!.atvSelectVerityCode.text.toString().isEmpty()*/
                _binding!!.searchSpinVerityCode.selectedItem.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please Select Vertiy Code", Toast.LENGTH_SHORT)
                    .show()
            } else if (_binding!!.radiogrup.checkedRadioButtonId == -1) {
                Toast.makeText(requireContext(), "Select YES or NO", Toast.LENGTH_SHORT)
                    .show()
            } else if (_binding!!.edtPlotNo.text.toString().isEmpty()) {
                _binding!!.edtPlotNo.error = "Enter Plot No."
                Toast.makeText(requireContext(), "Enter Plot No.", Toast.LENGTH_SHORT)
                    .show()
            }
            else if (_binding!!.edtward.text.toString().isEmpty()) {
                _binding!!.edtward.error = "Enter Ward No."
                Toast.makeText(requireContext(), "Enter Ward No.", Toast.LENGTH_SHORT)
                    .show()
            }


//            else if (_binding!!.edtPlotcode.text.toString().isEmpty()) {
//                _binding!!.edtPlotcode.error = "Enter Plot Code No."
//                Toast.makeText(requireContext(), "Enter Plot Code No.", Toast.LENGTH_SHORT)
//                    .show()
//
//            }
            else if (_binding!!.edtkitta.text.toString().isEmpty()){
                _binding!!.edtkitta.error = "Enter Kitta Value"
                Toast.makeText(requireContext(),"Enter Kitta Value",Toast.LENGTH_SHORT).show()
            }


            else if (_binding!!.edteast.text.toString().isEmpty()) {
                _binding!!.edteast.error = "Enter East Value."
                Toast.makeText(requireContext(), "Enter East Value.", Toast.LENGTH_SHORT)
                    .show()
            }



            else if (_binding!!.edtwest.text.toString().isEmpty()) {
                _binding!!.edtwest.error = "Enter West Value."
                Toast.makeText(requireContext(), "Enter West Value.", Toast.LENGTH_SHORT)
                    .show()
            }

            else if (_binding!!.edtnorth.text.toString().isEmpty()) {
                _binding!!.edtnorth.error = "Enter North Value."
                Toast.makeText(requireContext(), "Enter North Value.", Toast.LENGTH_SHORT)
                    .show()
            }

            else if (_binding!!.edtsouth.text.toString().isEmpty()) {
                _binding!!.edtsouth.error = "Enter South Value."
                Toast.makeText(requireContext(), "Enter South Value.", Toast.LENGTH_SHORT)
                    .show()
            }



//            else if  (_binding!!.edtPlotcode.text.toString().isEmpty()) {
//                _binding!!.edtPlotcode.error = "Enter Plot Village Code."
//                Toast.makeText(requireContext(), "Enter Plot Village Code.", Toast.LENGTH_SHORT)
//                    .show()
//
//            }

            else if (_binding!!.radiogrupRP.checkedRadioButtonId == -1) {
                _binding!!.btnRadioP.error = "Select R or P or A"
            } else if (_binding!!.edtShareHolder.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Enter Share Holder", Toast.LENGTH_SHORT)
                    .show()
            } else if (_binding!!.edtShareHolder.text.toString() == "1" && _binding!!.searchSpinShareHolder.selectedItem.toString().isEmpty()
            /* _binding!!.atvSelectShareHolderCode.text.toString().isEmpty()*/
            ) {
                Toast.makeText(requireContext(), "Please Select Share Holder", Toast.LENGTH_SHORT)
                    .show()
            } else if (areapoint1.isEmpty()) {
                Toast.makeText(requireContext(), "Please Enter P1 Value", Toast.LENGTH_SHORT).show()
            } else if (areapoint2.isEmpty()) {
                Toast.makeText(requireContext(), "Please Enter P2 Value", Toast.LENGTH_SHORT).show()
            } else if (areapoint3.isEmpty()) {
                Toast.makeText(requireContext(), "Please Enter P3 Value", Toast.LENGTH_SHORT).show()
            } else if (areapoint4.isEmpty()) {
                Toast.makeText(requireContext(), "Please Enter P4 Value", Toast.LENGTH_SHORT).show()
            } else {
                share_holder = _binding!!.edtShareHolder.text.toString()
                plot_no = _binding!!.edtPlotNo.text.toString()
                Wardno = _binding!!.edtward.text.toString()
              //  plot_vcode =  _binding!!.edtPlotcode.text.toString()
                Kitta = _binding!!.edtkitta.text.toString();
                east = _binding!!.edteast.text.toString()
                west = _binding!!.edtwest.text.toString()
                north = _binding!!.edtnorth.text.toString()
                south  = _binding!!.edtsouth.text.toString()
                AreaDivision = TotalHectorArea


                var finalArea = String.format("%.8f", AreaDivision)
                //  showPrintSurveyAlert()
                dialogPrintAlert()
                if (_binding!!.edtShareHolder.text.toString() == "1") {
                    AreaDivision = TotalHectorArea / 2
                    var strTotalDivArea = String.format("%.8f", AreaDivision)
                    //saveRecord(_binding!!.atvSelectFarmerCode.text.toString(), strTotalDivArea.toDouble())
                    saveRecord(_binding!!.searchSpinGrowerCode.selectedItem.toString(), strTotalDivArea.toDouble())
                    saveRecord(_binding!!.searchSpinShareHolder.selectedItem.toString(), strTotalDivArea.toDouble())
                    // saveRecord(_binding!!.atvSelectShareHolderCode.text.toString(), strTotalDivArea.toDouble())
                } else {
                    //     saveRecord(_binding!!.atvSelectFarmerCode.text.toString(), finalArea.toDouble())
                    saveRecord(_binding!!.searchSpinGrowerCode.selectedItem.toString(), finalArea.toDouble())

                }
            }
        }
        _binding!!.radiogrup.setOnCheckedChangeListener(this)
        _binding!!.radiogrupRP.setOnCheckedChangeListener(this)

        return view
    }

    private fun initSdk() {
        var status = mSys!!.sdkInit()
        if (status != SdkResult.SDK_OK) {


            mSys!!.sysPowerOn()
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        status = mSys!!.sdkInit()
        if (status != SdkResult.SDK_OK) {
            //init failed.
        }
    }

    fun saveRecord(farmer_code: String, total_area: Double) {
        val dFormat = DecimalFormat("##.######")
        val tsLongTime = System.currentTimeMillis() / 1000
        val ts = tsLongTime.toString()
        UniqueID = ts

        val sdf = SimpleDateFormat("dd-MMM-yyyy ' ' HH:mm:ss")
        val currentDateAndTime = sdf.format(Date())

        entitySurvey.Village_Code = _binding!!.searchSpinVillageCode.selectedItem.toString()
        entitySurvey.Verity_Code = _binding!!.searchSpinVerityCode.selectedItem.toString()
        // entitySurvey.Village_Code = _binding!!.atvSelectVillageCode.text.toString()
        // entitySurvey.Verity_Code = _binding!!.atvSelectVerityCode.text.toString()
        entitySurvey.Farmer_Code = farmer_code
        entitySurvey.Farmer_Transfer = farmer_transfer
        entitySurvey.MachineId = m_androidId
        entitySurvey.UniqueID = UniqueID
        entitySurvey.Plot_No = plot_no
        entitySurvey.Wardno = Wardno
        entitySurvey.Plot_Vcode = plot_vcode
        entitySurvey.Kitta = Kitta
        entitySurvey.East = east
        entitySurvey.West = west
        entitySurvey.North = north
        entitySurvey.South = south
        entitySurvey.RP_Value = rp_value
        entitySurvey.ShareHolderCode = share_holder
        entitySurvey.areaPoint1 = areapoint1
        entitySurvey.areaPoint2 = areapoint2
        entitySurvey.areaPoint3 = areapoint3
        entitySurvey.areaPoint4 = areapoint4
        entitySurvey.distancePoint12 = distancePoint12
        entitySurvey.distancePoint23 = distancePoint23
        entitySurvey.distancePoint34 = distancePoint34
        entitySurvey.distancePoint41 = distancePoint41
        entitySurvey.SurveyDateTime = currentDateAndTime
        entitySurvey.totalArea = java.lang.Double.valueOf(dFormat.format(total_area))

        db.daoclass().addSurway(entitySurvey)

        surveyModel.SurveyId = entitySurvey.SurveyId
        surveyModel.MachineId = entitySurvey.MachineId
        surveyModel.UniqueID = entitySurvey.UniqueID
        surveyModel.Village_Code = entitySurvey.Village_Code
        surveyModel.Farmer_Code = entitySurvey.Farmer_Code
        surveyModel.Verity_Code = entitySurvey.Verity_Code
        surveyModel.Farmer_Transfer = entitySurvey.Farmer_Transfer
        surveyModel.Plot_No = entitySurvey.Plot_No
        surveyModel.Wardno = entitySurvey.Wardno
        surveyModel.Plot_Vcode = entitySurvey.Plot_Vcode
        surveyModel.Kitta = entitySurvey.Kitta
        surveyModel.East = entitySurvey.East
        surveyModel.West = entitySurvey.West
        surveyModel.North = entitySurvey.North
        surveyModel.RP_Value = entitySurvey.RP_Value
        surveyModel.ShareHolderCode = entitySurvey.ShareHolderCode
        surveyModel.AreaPoint1 = entitySurvey.areaPoint1
        surveyModel.AreaPoint2 = entitySurvey.areaPoint2
        surveyModel.AreaPoint3 = entitySurvey.areaPoint3
        surveyModel.AreaPoint4 = entitySurvey.areaPoint4
        surveyModel.DistancePoint12 = entitySurvey.distancePoint12
        surveyModel.DistancePoint23 = entitySurvey.distancePoint23
        surveyModel.DistancePoint34 = entitySurvey.distancePoint34
        surveyModel.DistancePoint41 = entitySurvey.distancePoint41
        surveyModel.SurveyDateTime = entitySurvey.SurveyDateTime
        surveyModel.TotalArea = entitySurvey.totalArea

        listDistance.clear()
    }

    override fun onCheckedChanged(rdGrp: RadioGroup?, checkedId: Int) {

        if (checkedId == R.id.btnRadioYes) {
            farmer_transfer = "YES"
        } else if (checkedId == R.id.btnRadioNo) {
            farmer_transfer = "NO"
        }

        if (checkedId == R.id.btnRadioR) {
            rp_value = "R"
        } else if (checkedId == R.id.btnRadioP) {
            rp_value = "P"
        }else if (checkedId == R.id.btnRadioA){
            rp_value = "A"
        }
    }

    /*  @SuppressLint("MissingPermission", "SetTextI18n")
      private fun find_Location1() {
          var lattitude1 = 0.00
          var longitude1 = 0.00

          var locationListener: LocationListener = LocationListener { location ->
              lattitude1 = location.latitude
              longitude1 = location.longitude
          }
          if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
              locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0.0F, locationListener)
              try {
                  currentLocation = locationManager.getLastKnownLocation(GPS_PROVIDER)!!

                  var totalLat = 0.00
                  var totalLong = 0.00

                  if (currentLocation != null) {

                      for (i in 0..(loop - 1)) {
                          lattitude1 = currentLocation!!.latitude
                          longitude1 = currentLocation!!.longitude
                          totalLat += lattitude1
                          totalLong += longitude1
                      }

                      var avgLat = totalLat / loop
                      var avgLong = totalLong / loop

                      areapoint1 = "$avgLat, $avgLong"
                      _binding!!.txtLatLng1.text = avgLat.toString() + "\n" + avgLong
                      listDistance.add(LatLng(avgLat, avgLong))
                      Toast.makeText(requireContext(), "P1  $areapoint1", Toast.LENGTH_SHORT).show()
                  }
              } catch (e: Exception) {}
          }

      }

      @SuppressLint("MissingPermission")
      private fun find_Location2() {
          var lattitude2 = 0.00
          var longitude2 = 0.00

          var locationListener: LocationListener = LocationListener { location ->
              lattitude2 = location.latitude
              longitude2 = location.longitude
          }
          if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
              locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0.0F, locationListener)
              try {
                  currentLocation = locationManager.getLastKnownLocation(GPS_PROVIDER)!!
                  var totalLat = 0.00
                  var totalLong = 0.00

                  if (currentLocation != null) {

                      for (i in 0 .. loop - 1) {
                          lattitude2 = currentLocation!!.latitude
                          longitude2 = currentLocation!!.longitude
                          totalLat += lattitude2
                          totalLong += longitude2
                      }

                      var avgLat = totalLat / loop
                      var avgLong = totalLong / loop

                      areapoint2 = "$avgLat, $avgLong"
                      _binding!!.txtLatLng2.text = avgLat.toString() + "\n" + avgLong
                      listDistance.add(LatLng(avgLat, avgLong))
                      Toast.makeText(requireContext(), "P2  $areapoint2", Toast.LENGTH_SHORT).show()
                  }
              } catch (e: Exception) {
              }
          }
      }

      @SuppressLint("MissingPermission")
      private fun find_Location3() {
          var lattitude3 = 0.00
          var longitude3 = 0.00

          var locationListener: LocationListener = LocationListener { location ->
              lattitude3 = location.latitude
              longitude3 = location.longitude

          }
          if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
              locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0.0F, locationListener)
              try {
                  currentLocation = locationManager.getLastKnownLocation(GPS_PROVIDER)!!

                  var totalLat = 0.00
                  var totalLong = 0.00

                  if (currentLocation != null) {
                      for (i in 0..(loop - 1)) {
                          lattitude3 = currentLocation!!.latitude
                          longitude3 = currentLocation!!.longitude
                          totalLat += lattitude3
                          totalLong += longitude3
                      }

                      var avgLat = totalLat / loop
                      var avgLong = totalLong / loop
                      areapoint3 = "$avgLat, $avgLong"
                      _binding!!.txtLatLng3.text = avgLat.toString() + "\n" + avgLong
                      listDistance.add(LatLng(avgLat, avgLong))

                  }
              } catch (e: Exception) {
              }
          }
      }

      @SuppressLint("MissingPermission")
      private fun find_Location4() {
          var lattitude4 = 0.00
          var longitude4 = 0.00

          var locationListener: LocationListener = LocationListener { location ->
              lattitude4 = location.latitude
              longitude4 = location.longitude
          }
          if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
              locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0.0F, locationListener)
              try {
                  currentLocation = locationManager.getLastKnownLocation(GPS_PROVIDER)!!

                  if (currentLocation != null) {
                      var totalLat = 0.00
                      var totalLong = 0.00

                      for (i in 0..(loop - 1)) {
                          lattitude4 = currentLocation!!.latitude
                          longitude4 = currentLocation!!.longitude
                          totalLat += lattitude4
                          totalLong += longitude4
                      }
                      var avgLat = totalLat / loop
                      var avgLong = totalLong / loop
                      areapoint4 = "$avgLat, $avgLong"
                      _binding!!.txtLatLng4.text = avgLat.toString() + "\n" + avgLong
                      listDistance.add(LatLng(avgLat, avgLong))
                  }
              } catch (e: Exception) {}
          }
     }*/

    @SuppressLint("MissingPermission")
    fun getLatLngLocations(LatLngBtn: Button) {
        when (LatLngBtn) {
            _binding!!.btnLatLng1 -> {
                if (isLocationEnabled()) {
                    var dialog = ProgressDialog.show(
                        requireContext(),
                        "",
                        "Waiting For GPS Data...",
                        true,
                        false
                    )

                    Handler().postDelayed(Runnable {
                        var lattitude1 = 0.00
                        var longitude1 = 0.00
                        val dFormat = DecimalFormat("##.######")

                        if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
                            locationManager.requestLocationUpdates(
                                GPS_PROVIDER,
                                0,
                                0.0F,
                                object : LocationListener {
                                    override fun onLocationChanged(location: Location) {
                                        lattitude1 = location.latitude
                                        longitude1 = location.longitude
                                    }

                                    override fun onProviderDisabled(provider: String) {}

                                    override fun onProviderEnabled(provider: String) {}

                                    override fun onStatusChanged(
                                        provider: String,
                                        status: Int,
                                        extras: Bundle
                                    ) {
                                    }
                                })
                            try {
                                currentLocation =
                                    locationManager.getLastKnownLocation(GPS_PROVIDER)!!

                                if (currentLocation != null) {
                                    var totalLat = 0.00
                                    var totalLong = 0.00

                                    for (i in 0..(loop - 1)) {
                                        lattitude1 =
                                            java.lang.Double.valueOf(dFormat.format(currentLocation!!.latitude))
                                        longitude1 =
                                            java.lang.Double.valueOf(dFormat.format(currentLocation!!.longitude))
//                                        lattitude1 = currentLocation!!.latitude
//                                        longitude1 = currentLocation!!.longitude
                                        totalLat += lattitude1
                                        totalLong += longitude1

                                    }
                                    var avgLat = totalLat / loop
                                    var avgLong = totalLong / loop
                                    var lat = java.lang.Double.valueOf(dFormat.format(avgLat))
                                    var lng = java.lang.Double.valueOf(dFormat.format(avgLong))
                                    areapoint1 = "$lat, $lng"
                                    _binding!!.txtLatLng1.text = lat.toString() + "\n" + lng
                                    //       listDistance.add(LatLng(22.32183166666667, 70.76700500000001))
                                    listDistance.add(LatLng(lat, lng))

                                }

                            } catch (e: Exception) {
                            }
                        }

                        _binding!!.btnLatLng2.isEnabled = true
                        _binding!!.btnLatLng2.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.green_btn))
                        _binding!!.btnLatLng2.setTextColor(Color.parseColor("#FFFFFF"))

                        /*
                        _binding!!.btnLatLng1.isEnabled = false

                        if (!_binding!!.btnLatLng1.isEnabled) {
                            _binding!!.btnLatLng1.backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.grey))
                        }
                        */
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "P1  $areapoint1", Toast.LENGTH_SHORT)
                            .show()
                    }, 2000)
                    /* _binding!!.btnLatLng2.isEnabled = false
                     _binding!!.btnLatLng3.isEnabled = false
                     _binding!!.btnLatLng4.isEnabled = false*/
                } else {
                    showAlert()
                }

            }

            _binding!!.btnLatLng2 -> {

                if (isLocationEnabled()) {

                    var dialog =
                        ProgressDialog.show(
                            requireContext(),
                            "",
                            "Waiting For GPS Data...",
                            true,
                            false
                        )

                    Handler().postDelayed(Runnable {

                        var lattitude2 = 0.00
                        var longitude2 = 0.00
                        val dFormat = DecimalFormat("##.######")
                        if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
                            locationManager.requestLocationUpdates(
                                GPS_PROVIDER,
                                0,
                                0.0F,
                                object : LocationListener {
                                    override fun onLocationChanged(location: Location) {
                                        lattitude2 = location.latitude
                                        longitude2 = location.longitude
                                    }

                                    override fun onProviderDisabled(provider: String) {}
                                    override fun onProviderEnabled(provider: String) {}
                                    override fun onStatusChanged(
                                        provider: String,
                                        status: Int,
                                        extras: Bundle
                                    ) {
                                    }
                                })
                            try {
                                currentLocation =
                                    locationManager.getLastKnownLocation(GPS_PROVIDER)!!

                                if (currentLocation != null) {
                                    var totalLat = 0.00
                                    var totalLong = 0.00

                                    for (i in 0..(loop - 1)) {
                                        lattitude2 =
                                            java.lang.Double.valueOf(dFormat.format(currentLocation!!.latitude))
                                        longitude2 =
                                            java.lang.Double.valueOf(dFormat.format(currentLocation!!.longitude))
//                                        lattitude2 = currentLocation!!.latitude
//                                        longitude2 = currentLocation!!.longitude
                                        totalLat += lattitude2
                                        totalLong += longitude2
                                    }
                                    var avgLat = totalLat / loop
                                    var avgLong = totalLong / loop
                                    var lat = java.lang.Double.valueOf(dFormat.format(avgLat))
                                    var lng = java.lang.Double.valueOf(dFormat.format(avgLong))
                                    areapoint2 = "$lat, $lng"
                                    _binding!!.txtLatLng2.text = lat.toString() + "\n" + lng
//                                    listDistance.add(LatLng(22.321665, 70.76702166666668))
                                    listDistance.add(LatLng(lat, lng))

                                }
                            } catch (e: Exception) {
                            }
                        }
                        _binding!!.btnLatLng3.isEnabled = true
                        _binding!!.btnLatLng3.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.green_btn));
                        _binding!!.btnLatLng3.setTextColor(Color.parseColor("#FFFFFF"))
                        /*
                        _binding!!.btnLatLng2.isEnabled = false
                        if (!_binding!!.btnLatLng2.isEnabled) {
                            _binding!!.btnLatLng2.backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.grey));
                        }
                       */
                        dialog.dismiss()

                        Toast.makeText(requireContext(), "P2  $areapoint2", Toast.LENGTH_SHORT)
                            .show()
                    }, 2000)
                } else {
                    showAlert()
                }
                //getLineDistance(listDistance)
            }

            _binding!!.btnLatLng3 -> {
                if (isLocationEnabled()) {

                    var dialog =
                        ProgressDialog.show(
                            requireContext(),
                            "",
                            "Waiting For GPS Data...",
                            true,
                            false
                        )

                    Handler().postDelayed(Runnable {
                        var lattitude3 = 0.00
                        var longitude3 = 0.00
                        val dFormat = DecimalFormat("##.######")

                        /*  var locationListener: LocationListener = LocationListener { location ->
                              lattitude3 = location.latitude
                              longitude3 = location.longitude
                          }*/
                        if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
                            locationManager.requestLocationUpdates(
                                GPS_PROVIDER,
                                0,
                                0.0F,
                                object : LocationListener {
                                    override fun onLocationChanged(location: Location) {
                                        lattitude3 = location.latitude
                                        longitude3 = location.longitude
                                    }

                                    override fun onProviderDisabled(provider: String) {}
                                    override fun onProviderEnabled(provider: String) {}
                                    override fun onStatusChanged(
                                        provider: String,
                                        status: Int,
                                        extras: Bundle
                                    ) {
                                    }
                                })
                            try {
                                currentLocation =
                                    locationManager.getLastKnownLocation(GPS_PROVIDER)!!

                                if (currentLocation != null) {
                                    var totalLat = 0.00
                                    var totalLong = 0.00

                                    for (i in 0..(loop - 1)) {

                                        lattitude3 =
                                            java.lang.Double.valueOf(dFormat.format(currentLocation!!.latitude))
                                        longitude3 =
                                            java.lang.Double.valueOf(dFormat.format(currentLocation!!.longitude))

//                                        lattitude3 = currentLocation!!.latitude
//                                        longitude3 = currentLocation!!.longitude
                                        totalLat += lattitude3
                                        totalLong += longitude3
                                    }
                                    var avgLat = totalLat / loop
                                    var avgLong = totalLong / loop
                                    var lat = java.lang.Double.valueOf(dFormat.format(avgLat))
                                    var lng = java.lang.Double.valueOf(dFormat.format(avgLong))

                                    areapoint3 = "$lat, $lng"
                                    _binding!!.txtLatLng3.text = lat.toString() + "\n" + lng
//                                    listDistance.add(LatLng(22.32122833333334, 70.76681666666668))
                                    listDistance.add(LatLng(lat, lng))

                                }
                            } catch (e: Exception) {
                            }
                        }
                        _binding!!.btnLatLng4.isEnabled = true
                        _binding!!.btnLatLng4.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.green_btn));
                        _binding!!.btnLatLng4.setTextColor(Color.parseColor("#FFFFFF"))
                        /*
                        _binding!!.btnLatLng3.isEnabled = false
                        if (!_binding!!.btnLatLng3.isEnabled) {
                            _binding!!.btnLatLng3.backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.grey));
                        }*/
                        dialog.dismiss()

                        Toast.makeText(requireContext(), "P3  $areapoint3", Toast.LENGTH_SHORT)
                            .show()
                    }, 2000)
                } else {
                    showAlert()
                }
            }

            _binding!!.btnLatLng4 -> {
                if (isLocationEnabled()) {
                    var dialog =
                        ProgressDialog.show(
                            requireContext(),
                            "",
                            "Waiting For GPS Data...",
                            true,
                            false
                        )

                    Handler().postDelayed(Runnable {
                        var lattitude4 = 0.00
                        var longitude4 = 0.00
                        val dFormat = DecimalFormat("##.######")

                        /*   var locationListener: LocationListener = LocationListener { location ->
                               lattitude4 = location.latitude
                               longitude4 = location.longitude
                           }*/
                        if (locationManager.isProviderEnabled(GPS_PROVIDER)) {
                            locationManager.requestLocationUpdates(
                                GPS_PROVIDER,
                                0,
                                0.0F,
                                object : LocationListener {
                                    override fun onLocationChanged(location: Location) {
                                        lattitude4 = location.latitude
                                        longitude4 = location.longitude
                                    }

                                    override fun onProviderDisabled(provider: String) {}
                                    override fun onProviderEnabled(provider: String) {}
                                    override fun onStatusChanged(
                                        provider: String,
                                        status: Int,
                                        extras: Bundle
                                    ) {
                                    }
                                })
                            try {
                                currentLocation =
                                    locationManager.getLastKnownLocation(GPS_PROVIDER)!!

                                if (currentLocation != null) {
                                    var totalLat = 0.00
                                    var totalLong = 0.00

                                    for (i in 0..(loop - 1)) {

                                        lattitude4 =
                                            java.lang.Double.valueOf(dFormat.format(currentLocation!!.latitude))
                                        longitude4 =
                                            java.lang.Double.valueOf(dFormat.format(currentLocation!!.longitude))

                                        // lattitude4 = currentLocation!!.latitude
                                        //longitude4 = currentLocation!!.longitude
                                        totalLat += lattitude4
                                        totalLong += longitude4
                                    }
                                    var avgLat = totalLat / loop
                                    var avgLong = totalLong / loop

                                    var lat = java.lang.Double.valueOf(dFormat.format(avgLat))
                                    var lng = java.lang.Double.valueOf(dFormat.format(avgLong))

                                    areapoint4 = "$lat, $lng"
                                    _binding!!.txtLatLng4.text = lat.toString() + "\n" + lng
                                    listDistance.add(LatLng(lat, lng))
                                }
                            } catch (e: Exception) {
                            }
                        }
                        dialog.dismiss()
                        measureArea(requireContext(), listDistance)
                        getLineDistance(listDistance)
                        var strDistance = String.format("%.6f", TotalHectorArea)
                        _binding!!.txtShowMeasureHectares.text = "Area: $strDistance"
                        _binding!!.txtarea.text = "Distance1: $distancePoint12"
                        _binding!!.txtd1.text = "Distance2: $distancePoint23"
                        _binding!!.txtd2.text = "Distance3: $distancePoint34"
                        _binding!!.txtd3.text = "Distance4: $distancePoint41"

                        /*_binding!!.btnLatLng4.isEnabled = false
                        _binding!!.btnLatLng1.isEnabled = false
                        if (!_binding!!.btnLatLng4.isEnabled && !_binding!!.btnLatLng1.isEnabled) {
                            _binding!!.btnLatLng4.backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.grey));

                            _binding!!.btnLatLng1.backgroundTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.grey));
                        }*/
                        Toast.makeText(requireContext(), "P4  $areapoint4", Toast.LENGTH_SHORT)
                            .show()
                    }, 2000)
                } else {
                    showAlert()
                }
            }
        }
    }

    private fun getSpinnerVillageCode() {
        var vdata = db.daoclass().viewVillageData()
        var atvDataLists: ArrayList<String> = ArrayList()
        if(vdata.isEmpty()){
            Toast.makeText(requireContext(), "Village Data Not Synchronized", Toast.LENGTH_SHORT).show()
        }else{

            for(i in vdata.indices){
                atvDataLists.add("${vdata[i].VillageCode}, ${vdata[i].VillageName}, ${vdata[i].PlotNo},  ${vdata[i].PlotCode}")

                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.design_atvlistdata, atvDataLists)
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
                _binding!!.searchSpinVillageCode.adapter = arrayAdapter
            }

            _binding!!.searchSpinVillageCode.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                        var splitText = _binding!!.searchSpinVillageCode.selectedItem.toString().split(",".toRegex())
                            .dropLastWhile { it.isEmpty() }
                        entityVillage.VillageCode = splitText[0]
                        Log.d("farmername","${splitText[0]}  ${entityVillage.VillageCode}")
                        getSpinnerGrowerListData()

                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}

                }
        }

        //getSpinnerGrowerListData()
    }

    private fun getSpinnerVerityCode() {
        var veritydata = db.daoclass().viewVerityData()
        var atvDataList: ArrayList<String> = ArrayList()

        if(veritydata.isEmpty()){
            Toast.makeText(requireContext(), "Verity Data Not Synchronized", Toast.LENGTH_SHORT).show()
        }
        else{

            for(i in veritydata.indices){
                atvDataList.add("${veritydata[i].VerityId}, ${veritydata[i].VerityCode}")
                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.design_atvlistdata, atvDataList)
                arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
                _binding!!.searchSpinVerityCode.adapter = arrayAdapter
            }

            _binding!!.searchSpinVerityCode.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {}
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }
    private fun getSpinnerGrowerListData() {
        Log.d("farmername1","${entityVillage.VillageCode}")

        var vdata = db.daoclass().viewFarmerData(entityVillage.VillageCode)
        var atvDataList: ArrayList<String> = ArrayList()

        if(vdata.isEmpty()){
            Toast.makeText(requireContext(), "Farmer Data Not Synchronized", Toast.LENGTH_SHORT).show()
        }
        else{

            for(i in vdata.indices){

                atvDataList.add("${vdata[i].VillageCode}, ${vdata[i].FarmerCode}, ${vdata[i].First_FarmerName},  ${vdata[i].Second_FarmerName}")

                val arrayAdapter = ArrayAdapter(requireContext(), R.layout.design_atvlistdata, atvDataList)
                arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
                _binding!!.searchSpinGrowerCode.adapter = arrayAdapter
                _binding!!.searchSpinShareHolder.adapter = arrayAdapter
            }

            _binding!!.searchSpinGrowerCode.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {}
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }
    // verity code function
    /* private fun getVerityCode() {
         var veritydata = db.daoclass().viewVerityData()
         var atvDataList: ArrayList<String> = ArrayList()

         if (veritydata.isEmpty()) {
             _binding!!.atvSelectVerityCode.error = "Verity Data Not Synchronized"

             Toast.makeText(requireContext(), "Verity Data Not Synchronized", Toast.LENGTH_SHORT)
                 .show()
         } else {

             for (i in veritydata.indices) {
                 atvDataList.add("${veritydata[i].VerityId}, ${veritydata[i].VerityCode}")
                 val atvAdapterVerityC = ArrayAdapter(
                     requireContext(),
                     R.layout.design_atvlistdata, atvDataList
                 )
                 _binding!!.atvSelectVerityCode.setAdapter(atvAdapterVerityC)
             }
         }
     }*/

    // village data
    /*    private fun getVillageCode() {
            var vdata = db.daoclass().viewVillageData()
            var atvDataLists: ArrayList<String> = ArrayList()

            if (vdata.isEmpty()) {
                _binding!!.atvSelectVillageCode.error = "Village Data Not Synchronized"
                Toast.makeText(requireContext(), "Village Data Not Synchronized", Toast.LENGTH_SHORT).show()
            } else {

                for (i in vdata.indices) {

                    atvDataLists.add("${vdata[i].VillageCode}, ${vdata[i].VillageName}, ${vdata[i].PlotNo},  ${vdata[i].PlotCode}")

                    val atvAdapterVerityC = ArrayAdapter(requireContext(), R.layout.design_atvlistdata, atvDataLists)
                    _binding!!.atvSelectVillageCode.setAdapter(atvAdapterVerityC)
                }
            }
        }*/

    // grower data
    /*private fun getGrowerListData() {

        var splitText = _binding!!.atvSelectVillageCode.text.toString().split(",".toRegex())
            .dropLastWhile { it.isEmpty() }
        entityVillage.VillageCode = splitText[0]

        var vdata = db.daoclass().viewFarmerData(entityVillage.VillageCode)
        var atvDataList: ArrayList<String> = ArrayList()

        if (vdata.isEmpty()) {
            _binding!!.atvSelectFarmerCode.error = "Farmer Data Not Synchronized"
            Toast.makeText(requireContext(), "Farmer Data Not Synchronized", Toast.LENGTH_SHORT)
                .show()
        } else {
            for (i in vdata.indices) {

                atvDataList.add("${vdata[i].VillageCode}, ${vdata[i].FarmerCode}, ${vdata[i].First_FarmerName},  ${vdata[i].Second_FarmerName}")

                val atvAdapterVerityC =
                    ArrayAdapter(requireContext(), R.layout.design_atvlistdata, atvDataList)
                _binding!!.atvSelectFarmerCode.setAdapter(atvAdapterVerityC)
                _binding!!.atvSelectShareHolderCode.setAdapter(atvAdapterVerityC)

            }
        }

    }*/

    fun measureArea(context: Context, listArea: ArrayList<LatLng>): String {
        var strDistance = ""

        if (listArea.size > 0) {
            try {
                val geoPoints: MutableList<Location> = ArrayList()
                val allLatLongs = StringBuilder()
                for (i in listArea.indices) {
                    val lc = Location("")
                    lc.latitude = listArea[i].latitude
                    lc.longitude = listArea[i].longitude
                    geoPoints.add(lc)
                    allLatLongs.append("" + listArea[i].latitude + " , " + listArea[i].longitude + " / ")
                }
                val area: Double = calculateAreaOfGPSPolygonOnEarthInSquareMeters(geoPoints)
                val area_float = area.toFloat()

                // hactor
                val valueInHector = area_float / 10000
                strDistance = String.format("%.6f", valueInHector)

                val areas = strDistance
                TotalHectorArea = strDistance.toDouble()

                var divide = TotalHectorArea * 7 / 100

                TotalHectorArea += divide
                // Toast.makeText(requireContext(), "$TotalHectorArea   $divide", Toast.LENGTH_SHORT).show()
                val square_sign = context.getString(R.string.superscript_sq)
                strDistance = if (areas.contains(square_sign)) areas else areas + square_sign
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return strDistance
       //binding.edtarea.setText("3")
    }

    fun getLineDistance(listLine: ArrayList<LatLng>): String {
        val dFormat = DecimalFormat("##.######")
        var strDistance = ""
        if (listLine.size > 0) {

            try {
                var result = 0.0
                val resultList1 = FloatArray(1)
                val resultList2 = FloatArray(1)
                val resultList3 = FloatArray(1)
                val resultList4 = FloatArray(1)
                for (k in 0 until listLine.size - 3) {
                    Location.distanceBetween(listLine[k].latitude, listLine[k].longitude, listLine[k + 1].latitude, listLine[k + 1].longitude, resultList1)
                    Location.distanceBetween(listLine[k + 1].latitude, listLine[k + 1].longitude, listLine[k + 2].latitude, listLine[k + 2].longitude, resultList2)
                    Location.distanceBetween(listLine[k + 2].latitude, listLine[k + 2].longitude, listLine[k + 3].latitude, listLine[k + 3].longitude, resultList3)
                    Location.distanceBetween(listLine[k + 3].latitude, listLine[k + 3].longitude, listLine[k].latitude, listLine[k].longitude, resultList4)
                    result += resultList1[0] // by default returns in meters..
                    result += resultList2[0] // by default returns in meters..
                    result += resultList3[0] // by default returns in meters..
                    result += resultList4[0] // by default returns in meters..
                }


                distancePoint12 = java.lang.Double.valueOf(dFormat.format(resultList1[0]))
                distancePoint23 = java.lang.Double.valueOf(dFormat.format(resultList2[0]))
                distancePoint34 = java.lang.Double.valueOf(dFormat.format(resultList3[0]))
                distancePoint41 = java.lang.Double.valueOf(dFormat.format(resultList4[0]))

                /*  strDistance = String.format("%.6f", result)
                  TotalDistance = strDistance.toDouble()*/

            } catch (e: Exception) {
                Log.d("error123", "${e.message}")
                e.printStackTrace()
            }
        }
        listLine.clear()
        return strDistance
    }

    fun calculateAreaOfGPSPolygonOnEarthInSquareMeters(locations: List<Location?>?): Double {
        val EARTH_RADIUS = 6371000.0 // meters
        return calculateAreaOfGPSPolygonOnSphereInSquareMeters(
            locations as List<Location>,
            EARTH_RADIUS
        )
    }

    private fun calculateAreaOfGPSPolygonOnSphereInSquareMeters(
        locations: List<Location>,
        radius: Double
    ): Double {
        val LOG_TAG = "Title"
        if (locations.size < 3) {
            return 0.0
        }
        val diameter = radius * 2
        val circumference = diameter * Math.PI
        val listY: MutableList<Double> = ArrayList()
        val listX: MutableList<Double> = ArrayList()
        val listArea: MutableList<Double> = ArrayList()
        // calculate segment x and y in degrees for each point
        val latitudeRef = locations[0].latitude
        val longitudeRef = locations[0].longitude
        for (i in 1 until locations.size) {
            val latitude = locations[i].latitude
            val longitude = locations[i].longitude
            listY.add(calculateYSegment(latitudeRef, latitude, circumference))

            listX.add(calculateXSegment(longitudeRef, longitude, latitude, circumference))

        }

        // calculate areas for each triangle segment
        for (i in 1 until listX.size) {
            val x1 = listX[i - 1]
            val y1 = listY[i - 1]
            val x2 = listX[i]
            val y2 = listY[i]
            listArea.add(calculateAreaInSquareMeters(x1, x2, y1, y2))
            //Log.d(LOG_TAG, String.format("area %s: %s", listArea.size() - 1, listArea.get(listArea.size() - 1)));
        }

        // sum areas of all triangle segments
        var areasSum = 0.0
        for (area in listArea) {
            areasSum = areasSum + area


        }
        // get absolute value of area, it can't be negative
        return Math.abs(areasSum) // Math.sqrt(areasSum * areasSum);

    }

    private fun calculateAreaInSquareMeters(
        x1: Double,
        x2: Double,
        y1: Double,
        y2: Double
    ): Double {
        return (y1 * x2 - x1 * y2) / 2
    }

    private fun calculateYSegment(
        latitudeRef: Double,
        latitude: Double,
        circumference: Double
    ): Double {
        return (latitude - latitudeRef) * circumference / 360.0
    }

    private fun calculateXSegment(
        longitudeRef: Double,
        longitude: Double,
        latitude: Double,
        circumference: Double
    ): Double {
        return (longitude - longitudeRef) * circumference * Math.cos(Math.toRadians(latitude)) / 360.0
    }

    private fun showAlert() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
        dialog.setTitle("Enable Location")
            .setMessage("Your Locations Settings is 'Off'.\n\nPlease Enable Location For Measure Area")
            .setPositiveButton("Location Settings") { paramDialogInterface, paramInt ->
                val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(myIntent)
            }
            .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> }
        dialog.show()
    }
// old dialog box for survay print alert
    /* private fun showPrintSurveyAlert() {
         val dialog = MaterialAlertDialogBuilder(requireContext())
         dialog.setTitle("Survey Print")
             .setMessage("Do You Want To Print Survay Details?")
             // when print it saves in db and also take printout
             .setPositiveButton("YES") { paramDialogInterface, paramInt ->
                 var dialog =
                     ProgressDialog.show(requireContext(), "", "Submit Survey...", true, false)

                 Handler().postDelayed(Runnable {
                     paramDialogInterface.dismiss()
                     dialog.dismiss()
                     try {
                         val file = File(
                             Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                 .toString() + "/fonts/fangzhengyouyuan.ttf"
                         )
                         if (!file.exists()) {
                             val mAssetManger = requireActivity()!!.assets
                             val `in` = mAssetManger.open("fonts/fangzhengyouyuan.ttf")
                             saveFile(`in`, "fangzhengyouyuan.ttf")
                         }

                     } catch (e: IOException) {
                         e.printStackTrace()
                     }
                     Toast.makeText(requireContext(), "Saved Successfully", Toast.LENGTH_SHORT)
                         .show()
                     clearFormAfterSubmit()

                     // startActivity(Intent(requireContext(), MainMenuActivity::class.java))
                 }, 2000)
                 var id = db.daoclass().getLastSurvay()
                 if (_binding!!.edtShareHolder.text.toString() == "1") {
                     var strTotalDivArea = String.format("%.7f", AreaDivision)
                     printMatrixText(
                         _binding!!.atvSelectFarmerCode.text.toString(),
                         strTotalDivArea.toDouble(),
                         id[1]
                     )
                     printMatrixText(
                         _binding!!.atvSelectShareHolderCode.text.toString(),
                         strTotalDivArea.toDouble(),
                         id[0]
                     )
                 } else {
                     printMatrixText(
                         _binding!!.atvSelectFarmerCode.text.toString(),
                         TotalHectorArea.toDouble(),
                         id[0]
                     )
                 }
             }
             // when cancel it only save in database
             .setNegativeButton("NO") { paramDialogInterface, paramInt ->
                 paramDialogInterface.dismiss()
                 var dialog =
                     ProgressDialog.show(requireContext(), "", "Submit Survey...", true, false)

                 Handler().postDelayed(Runnable {
                     clearFormAfterSubmit()
                     Toast.makeText(requireContext(), "Saved Successfully", Toast.LENGTH_SHORT)
                         .show()
                     dialog.dismiss()
                 }, 2000)
             }
         dialog.show()
     }*/

    private fun dialogPrintAlert(){
        val txtDialog_Yes: TextView
        val txtDialog_No: TextView
        val printdialog = Dialog(requireContext())
        val view = printdialog.window
        view!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        view.setBackgroundDrawableResource(R.drawable.btn_rounded_transparent)
        printdialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        printdialog.setContentView(R.layout.dialog_clear)
        txtDialog_Yes = printdialog.findViewById<TextView>(R.id.txtDialog_Yes)
        txtDialog_No = printdialog.findViewById<TextView>(R.id.txtDialog_No)
        txtDialog_Yes.setOnClickListener {
            printdialog.dismiss()
            var dialog =
                ProgressDialog.show(requireContext(), "", "Submit Survey...", true, false)
            Handler().postDelayed(Runnable {

                dialog.dismiss()
                try {
                    val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .toString() + "/fonts/fangzhengyouyuan.ttf"
                    )
                    if (!file.exists()) {
                        val mAssetManger = requireActivity()!!.assets
                        val `in` = mAssetManger.open("fonts/fangzhengyouyuan.ttf")
                        saveFile(`in`, "fangzhengyouyuan.ttf")
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }
                Toast.makeText(requireContext(), "Saved Successfully", Toast.LENGTH_SHORT)
                    .show()
                clearFormAfterSubmit()

                // startActivity(Intent(requireContext(), MainMenuActivity::class.java))
            }, 2000)
            var id = db.daoclass().getLastSurvay()
            if (_binding!!.edtShareHolder.text.toString() == "1") {
                var strTotalDivArea = String.format("%.7f", AreaDivision)
                //printMatrixText(_binding!!.atvSelectShareHolderCode.text.toString(), strTotalDivArea.toDouble(), id[0])
                //  printMatrixText(_binding!!.atvSelectFarmerCode.text.toString(), strTotalDivArea.toDouble(), id[1])
                printMatrixText(_binding!!.searchSpinGrowerCode.selectedItem.toString(), strTotalDivArea.toDouble(), id[1])
                printMatrixText(_binding!!.searchSpinShareHolder.selectedItem.toString(), strTotalDivArea.toDouble(), id[0])
            } else {
                //printMatrixText(_binding!!.atvSelectFarmerCode.text.toString(), TotalHectorArea.toDouble(), id[0])
                printMatrixText(_binding!!.searchSpinGrowerCode.selectedItem.toString(), TotalHectorArea.toDouble(), id[0])
            }
        }
        txtDialog_No.setOnClickListener {
            printdialog.dismiss()
            var dialog =
                ProgressDialog.show(requireContext(), "", "Submit Survey...", true, false)

            Handler().postDelayed(Runnable {
                clearFormAfterSubmit()
                Toast.makeText(requireContext(), "Saved Successfully", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()

            }, 2000) }
        printdialog.setCancelable(false)
        printdialog.show()
    }
    fun clearFormAfterSubmit() {
        // _binding!!.atvSelectVillageCode.setText("")
        //  _binding!!.atvSelectFarmerCode.setText("")
        // _binding!!.atvSelectVerityCode.setText("")
        // _binding!!.atvSelectShareHolderCode.setText("")
        _binding!!.searchSpinVillageCode.setTitle("")
        _binding!!.searchSpinGrowerCode.setTitle("")
        _binding!!.searchSpinVerityCode.setTitle("")
        _binding!!.searchSpinShareHolder.setTitle("")
        _binding!!.edtPlotNo.setText("")
        _binding!!.edtward.setText("")
       // _binding!!.edtPlotcode.setText("")
        _binding!!.edtkitta.setText("")
        _binding!!.edteast.setText("")
        _binding!!.edtwest.setText("")
        _binding!!.edtnorth.setText("")
        _binding!!.edtsouth.setText("")
        _binding!!.edtShareHolder.setText("")
        _binding!!.txtLatLng1.text = ""
        _binding!!.txtLatLng2.text = ""
        _binding!!.txtLatLng3.text = ""
        _binding!!.txtLatLng4.text = ""
        _binding!!.txtShowMeasureHectares.text = ""
        _binding!!.txtarea.text = ""
        _binding!!.txtd1.text = ""
        _binding!!.txtd2.text = ""
        _binding!!.txtd3.text = ""
        _binding!!.radiogrup.clearCheck()
        _binding!!.radiogrupRP.clearCheck()
        _binding!!.btnLatLng1.isEnabled = true
        _binding!!.btnLatLng1.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_btn))


        /*  val intent = requireActivity().intent
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or
                  Intent.FLAG_ACTIVITY_NO_ANIMATION)
          requireActivity().overridePendingTransition(0, 0)
          requireActivity().finish()

          requireActivity().overridePendingTransition(0, 0)
          startActivity(intent)*/
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager!!.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onStart() {
        if (!isLocationEnabled()) {
            isLocationEnabled()
        }
        super.onStart()
    }

    /** Print Text */
    fun printMatrixText(farmer_name: String, area: Double, s_id: Int) {

        var farmer_code = farmer_name.split(",".toRegex()).dropLastWhile { it.isEmpty() }
        var village_code_name =
            entitySurvey.Village_Code.split(",".toRegex()).dropLastWhile { it.isEmpty() }
        var verity_code =
            entitySurvey.Verity_Code.split(",".toRegex()).dropLastWhile { it.isEmpty() }

        mSingleThreadExecutor!!.execute {
            var printStatus: Int = printer!!.getPrinterStatus()
            if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                requireActivity().runOnUiThread {
                    DialogUtils.show(activity, getString(R.string.printer_out_of_paper))
                }
            } else {
                val format = PrnStrFormat()
                format.textSize = 30
                format.ali = Layout.Alignment.ALIGN_NORMAL
                format.style = PrnTextStyle.NORMAL
                format.font = PrnTextFont.SANS_SERIF
//                printer!!.setPrintAppendString(resources.getString(R.string.bhageshwar_sugar_chemical), format)
                printer!!.setPrintAppendString(company_name, format)
                format.textSize = 20
                format.ali = Layout.Alignment.ALIGN_NORMAL
                format.style = PrnTextStyle.NORMAL
                format.font = PrnTextFont.MONOSPACE
//                printer!!.setPrintAppendString(resources.getString(R.string.kanchanpur), format)
                //  printer!!.setPrintAppendString("Nepal -------------------------------------------------", format)
                try{ printer!!.setPrintAppendString(resources.getString(R.string.sur_no) + s_id, format)
                    printer!!.setPrintAppendString(resources.getString(R.string.g_code) + farmer_code[1], format)
                    printer!!.setPrintAppendString(resources.getString(R.string.g_name) + farmer_code[2], format)
                    printer!!.setPrintAppendString(resources.getString(R.string.g_father) + farmer_code[3], format)
                    printer!!.setPrintAppendString(resources.getString(R.string.g_v_code) + village_code_name[0], format)
                    printer!!.setPrintAppendString(resources.getString(R.string.g_village) + village_code_name[1], format)
                    printer!!.setPrintAppendString(resources.getString(R.string.variety) + verity_code[1], format)
                    printer!!.setPrintAppendString(resources.getString(R.string.rat_plant) + entitySurvey.RP_Value, format)
                    printer!!.setPrintAppendString(resources.getString(R.string.plot_no) + entitySurvey.Plot_No, format)
                    printer!!.setPrintAppendString(resources.getString(R.string.Ward_no) + entitySurvey.Wardno, format)
                    printer!!.setPrintAppendString(resources.getString(R.string.plot_v_code) + entitySurvey.Plot_Vcode, format)
                    printer!!.setPrintAppendString(resources.getString(R.string.east) + entitySurvey.East, format)
                    printer!!.setPrintAppendString(resources.getString(R.string.west)+ entitySurvey.West, format)
                    printer!!.setPrintAppendString(resources.getString(R.string.north)+ entitySurvey.North, format)
                    printer!!.setPrintAppendString(resources.getString(R.string.south)+ entitySurvey.South, format)
                    printer!!.setPrintAppendString(resources.getString(R.string.coordinates_mtr) + " ", format)
                    printer!!.setPrintAppendString(resources.getString(R.string.area_hc) + AreaDivision.toString(), format)
                    printer!!.setPrintAppendString(resources.getString(R.string.date) + entitySurvey.SurveyDateTime, format)
                    printer!!.setPrintAppendString(resources.getString(R.string.t_code) + " ", format)
                    printer!!.setPrintAppendString(resources.getString(R.string.plot_v_code) + village_code_name[3], format)
                    printer!!.setPrintAppendString("----------------------------------", format)
                    printer!!.setPrintAppendString(" ", format)
                    printer!!.setPrintAppendString(" ", format)
                    printer!!.setPrintAppendString(" ", format)
                }
                catch(e:Exception){}
                printStatus = printer!!.setPrintStart()
                if (printStatus == SdkResult.SDK_PRN_STATUS_PAPEROUT) {
                    requireActivity().runOnUiThread {
                        DialogUtils.show(activity, getString(R.string.printer_out_of_paper))
                    }
                }
            }
        }
    }

    fun saveFile(inputStream: InputStream, fileName: String?) {
        val appDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "fonts"
        )
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val file = fileName?.let { File(appDir, it) }
        try {
            val fos = FileOutputStream(file)
            val bs = ByteArray(1024)
            var len: Int
            while (inputStream.read(bs).also { len = it } != -1) {
                fos.write(bs, 0, len)
            }
//            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionsManager!!.recheckPermissions(requestCode, permissions, grantResults)
    }


}
