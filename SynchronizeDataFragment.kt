package com.bmac.surwayapp.Fragments

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.room.Room
import com.bmac.surwayapp.Adapters.GrowerAdapter
import com.bmac.surwayapp.ApiClient.CRACApiClient
import com.bmac.surwayapp.ApiInterface.ApiService
import com.bmac.surwayapp.Entity.FarmerDataClass
import com.bmac.surwayapp.Entity.VerityCodeClass
import com.bmac.surwayapp.Entity.VillageCodeClass
import com.bmac.surwayapp.Model.GrowerDataListModel
import com.bmac.surwayapp.Model.VerityListModel
import com.bmac.surwayapp.Model.VillageListModel
import com.bmac.surwayapp.R
import com.bmac.surwayapp.RoomDatabase.CRACDatabase
import com.bmac.surwayapp.databinding.FragmentGrowerDataBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SynchronizeDataFragment : Fragment() {
    private var _binding: FragmentGrowerDataBinding? = null
    private val binding get() = _binding!!

    lateinit var apiService: ApiService
    lateinit var db:CRACDatabase

    var growerList:List<String> = ArrayList()
    var verityList:List<String> = ArrayList()
    var villageList:List<String> = ArrayList()

    lateinit var myAdapter:GrowerAdapter
    var verityEntity = VerityCodeClass()
    var verityModel = VerityListModel()
    var verityEntityArray:ArrayList<VerityCodeClass> = ArrayList()

    var growerEntity = FarmerDataClass()
    var growerModel = GrowerDataListModel()

    var villageEntity = VillageCodeClass()
    var villageModel = VillageListModel()
  // var machineID = "1280559c17de400e"
   var machineID = ""
    private lateinit var  sharedPreferences: SharedPreferences

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) { disconnected() }
            else { connected() }
        }
    }
    private fun disconnected()
    {
        _binding!!.imgSyncVerityList.isEnabled = false
        _binding!!.imgSyncVillageList.isEnabled = false
        _binding!!.imgSyncGrowerList.isEnabled = false
        if(!_binding!!.imgSyncVerityList.isEnabled)
        { _binding!!.imgSyncVerityList.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.grey)) }
        if(!_binding!!.imgSyncVillageList.isEnabled)
        { _binding!!.imgSyncVillageList.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.grey))}
        if(!_binding!!.imgSyncGrowerList.isEnabled)
        { _binding!!.imgSyncGrowerList.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.grey))}
        Toast.makeText(requireContext(),"Please Check Your Internet Connection",Toast.LENGTH_LONG).show()
    }

    private fun connected() {
     //   Toast.makeText(requireContext(),"You Are Connected...",Toast.LENGTH_LONG).show()

        _binding!!.imgSyncVerityList.isEnabled = true
        _binding!!.imgSyncVillageList.isEnabled = true
        _binding!!.imgSyncGrowerList.isEnabled = true
        if(_binding!!.imgSyncVerityList.isEnabled) { _binding!!.imgSyncVerityList.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_btn))}
        if(_binding!!.imgSyncVillageList.isEnabled) { _binding!!.imgSyncVillageList.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_btn))}
        if(_binding!!.imgSyncGrowerList.isEnabled) { _binding!!.imgSyncGrowerList.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_btn))}
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGrowerDataBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPreferences = requireActivity().getSharedPreferences("C_NM_M_ID_PREF", Context.MODE_PRIVATE)

        machineID = sharedPreferences.getString("machineID", "").toString()

        Log.d("m_ids","$machineID")
        apiService = CRACApiClient.getapiclient().create(ApiService::class.java)
        db = Room.databaseBuilder(requireActivity(), CRACDatabase::class.java, "FarmerDB").allowMainThreadQueries().build()

        growerList = ArrayList()

        // button clicks
        _binding!!.imgSyncGrowerList.setOnClickListener { SyncGrowerListData() }
        _binding!!.imgSyncVerityList.setOnClickListener { SyncVerityListData() }
        _binding!!.imgSyncVillageList.setOnClickListener { SyncVillageListData() }

        return view
    }

    // sync farmer data list
    private fun SyncGrowerListData() {

       // var call: Call<String> = apiService.viewDataG("93210331476")
       var call: Call<String> = apiService.viewDataG(machineID)
        var dialog = ProgressDialog.show(requireContext(), "", "Loading Grower Data...", true, false)

        call.enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful){

                    var data = db.daoclass().viewFarmerList()
                    growerList = listOf(*response.body()!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())

                    if(data.isEmpty()){
                        for (j in  growerList.indices) {
                            val separated =
                                growerList[j].split(",".toRegex()).dropLastWhile { it.isEmpty() }

                            if (separated.size == 1) {

                                growerEntity.VillageCode = separated[0].trim()
                                growerModel.VillageCode = growerEntity.VillageCode
                                db.daoclass().addFarmer(growerEntity)
                            }
                            if(separated.size == 2){
                                growerEntity.VillageCode = separated[0].trim()
                                growerEntity.FarmerCode = separated[1].trim()

                                growerModel.VillageCode = growerEntity.VillageCode
                                growerModel.FarmerCode = growerEntity.FarmerCode

                                db.daoclass().addFarmer(growerEntity)
                            }
                            if(separated.size == 3){
                                growerEntity.VillageCode = separated[0].trim()
                                growerEntity.FarmerCode = separated[1].trim()
                                growerEntity.First_FarmerName = separated[2].trim()

                                growerModel.VillageCode = growerEntity.VillageCode
                                growerModel.FarmerCode = growerEntity.FarmerCode
                                growerModel.First_FarmerName = growerEntity.First_FarmerName

                                db.daoclass().addFarmer(growerEntity)
                            }
                            if(separated.size == 4){
                                growerEntity.VillageCode = separated[0].trim()
                                growerEntity.FarmerCode = separated[1].trim()
                                growerEntity.First_FarmerName = separated[2].trim()
                                growerEntity.Second_FarmerName = separated[3].trim()

                                growerModel.VillageCode = growerEntity.VillageCode
                                growerModel.FarmerCode = growerEntity.FarmerCode
                                growerModel.First_FarmerName = growerEntity.First_FarmerName
                                growerModel.Second_FarmerName = growerEntity.Second_FarmerName
                                db.daoclass().addFarmer(growerEntity)
                            }
                        }
                        dialog.dismiss()
                    }

                    else{
                        if(data.isNotEmpty()){
                            db.daoclass().deleteGrowerData()

                            for (j in  growerList.indices) {
                                val separated =
                                    growerList[j].split(",".toRegex()).dropLastWhile { it.isEmpty() }

                                if (separated.size == 1) {
                                    growerEntity.VillageCode = separated[0].trim()
                                    growerModel.VillageCode = growerEntity.VillageCode
                                    db.daoclass().addFarmer(growerEntity)
                                }
                                if(separated.size == 2){
                                    growerEntity.VillageCode = separated[0].trim()
                                    growerEntity.FarmerCode = separated[1].trim()

                                    growerModel.VillageCode = growerEntity.VillageCode
                                    growerModel.FarmerCode = growerEntity.FarmerCode

                                    db.daoclass().addFarmer(growerEntity)
                                }
                                if(separated.size == 3){
                                    growerEntity.VillageCode = separated[0].trim()
                                    growerEntity.FarmerCode = separated[1].trim()
                                    growerEntity.First_FarmerName = separated[2].trim()

                                    growerModel.VillageCode = growerEntity.VillageCode
                                    growerModel.FarmerCode = growerEntity.FarmerCode
                                    growerModel.First_FarmerName = growerEntity.First_FarmerName

                                    db.daoclass().addFarmer(growerEntity)
                                }
                                if(separated.size == 4){
                                    growerEntity.VillageCode = separated[0].trim()
                                    growerEntity.FarmerCode = separated[1].trim()
                                    growerEntity.First_FarmerName = separated[2].trim()
                                    growerEntity.Second_FarmerName = separated[3].trim()

                                    growerModel.VillageCode = growerEntity.VillageCode
                                    growerModel.FarmerCode = growerEntity.FarmerCode
                                    growerModel.First_FarmerName = growerEntity.First_FarmerName
                                    growerModel.Second_FarmerName = growerEntity.Second_FarmerName
                                    db.daoclass().addFarmer(growerEntity)
                                }
                            }
                                dialog.dismiss()
                        }
                    }
                }else if(response.code() == 404 || response.code() == 500 || response.body() == null)
                {
                    Toast.makeText(requireContext(), "Data Not Found", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(), "Server is not responding...", Toast.LENGTH_SHORT).show()
                Log.d("error", "${t.message}")
            }
        })

    }

    // sync Verity data list
    private fun SyncVerityListData() {

        //var call:Call<String> = apiService.getVariety("93210331476")
        var call:Call<String> = apiService.getVariety(machineID)
        var dialog = ProgressDialog.show(requireContext(), "", "Loading Verity Data...", true, false)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {

                if(response.isSuccessful){

                    var data = db.daoclass().viewVerityData()
                    verityList = listOf(*response.body()!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                    if(data.isEmpty()) {

                        for (i in verityList.indices)
                        {
                            val separated = verityList[i].split(",".toRegex()).dropLastWhile { it.isEmpty() }

                            verityEntity.VerityId = separated[0]
                            verityEntity.VerityCode = separated[1]

                            db.daoclass().addVerity(verityEntity)

                            verityModel.VerityId = verityEntity.VerityId
                            verityModel.VerityCode = verityEntity.VerityCode
                        }
                        dialog.dismiss()
                    }
                    else{
                            if(data.isNotEmpty()){
                                db.daoclass().deleteVerityData()

                              for (i in verityList.indices) {
                                        val separated = verityList[i].split(",".toRegex()).dropLastWhile { it.isEmpty() }

                                        verityEntity.VerityId = separated[0]
                                        verityEntity.VerityCode = separated[1]

                                        db.daoclass().addVerity(verityEntity)

                                        verityModel.VerityId = verityEntity.VerityId
                                        verityModel.VerityCode = verityEntity.VerityCode
                                        Log.d("entity123", "$verityEntity $verityModel")
                                    }
                                    dialog.dismiss()
                           }
                    }
                }
                else  if(response.code() == 404 || response.code() == 500 || response.body() == null)
                {
                    Toast.makeText(requireContext(), "Data Not Found", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(), "Server is not responding...", Toast.LENGTH_SHORT).show()
                Log.d("error", "${t.message}")
            }
        })

    }


    // sync village data list
    private fun SyncVillageListData() {
       //var call:Call<String> = apiService.getVillage("1280559c17de400e")
        var call:Call<String> = apiService.getVillage(machineID)
        Log.d("m_id","$machineID")
        var dialog = ProgressDialog.show(requireContext(), "", "Loading Village Data...", true, false)


        call.enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful)
                {
                    var data = db.daoclass().viewVillageData()
                    villageList = listOf(*response.body()!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray())

                    if(data.isEmpty()){

                        for(i in villageList.indices){

                            val separated = villageList[i].split(",".toRegex()).dropLastWhile { it.isEmpty() }

                            villageEntity.VillageCode = separated[0]
                            villageEntity.VillageName = separated[1]
                            villageEntity.PlotNo = separated[2]
                            villageEntity.PlotCode = separated[3]

                            db.daoclass().addVillage(villageEntity)

                            villageModel.VillageCode = villageEntity.VillageCode
                            villageModel.VillageName = villageEntity.VillageName
                            villageModel.PlotNo = villageEntity.PlotNo
                            villageModel.PlotCode = villageEntity.PlotCode
                        }
                        dialog.dismiss()
                    }
                    else{
                        if(data.isNotEmpty()){
                            db.daoclass().deleteVillageData()

                                for(i in villageList.indices){

                                    val separated = villageList[i].split(",".toRegex()).dropLastWhile { it.isEmpty() }

                                    villageEntity.VillageCode = separated[0]
                                    villageEntity.VillageName = separated[1]
                                    villageEntity.PlotNo = separated[2]
                                    villageEntity.PlotCode = separated[3]

                                    db.daoclass().addVillage(villageEntity)

                                    villageModel.VillageCode = villageEntity.VillageCode
                                    villageModel.VillageName = villageEntity.VillageName
                                    villageModel.PlotNo = villageEntity.PlotNo
                                    villageModel.PlotCode = villageEntity.PlotCode
                                }
                            dialog.dismiss()

                            }
                        }
                }
                else if(response.code() == 404 || response.code() == 500 || response.body() == null)
                {
                    Toast.makeText(requireContext(), "Data Not Found", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(), "Server is not responding...", Toast.LENGTH_SHORT).show()
                Log.d("error", "${t.message}")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        requireActivity().registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    //    val cm =requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    //    val networkInfo = cm!!.activeNetworkInfo
     //   registerNetworkBroadcastReceiver()
    }
 /*    fun registerNetworkBroadcastReceiver(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requireActivity().registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        }
    }*/
    override fun onResume() {
        super.onResume()
        /*for registering to broadcast receiver*/
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        requireActivity().registerReceiver(broadcastReceiver, filter)
      //  AppController.getInstance().setConnectivityListener(this)
    }
}


