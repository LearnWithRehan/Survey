package com.bmac.surwayapp.Adapters

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bmac.surwayapp.ApiClient.CRACApiClient
import com.bmac.surwayapp.ApiInterface.ApiService
import com.bmac.surwayapp.Entity.CRACSurvayDetailClass
import com.bmac.surwayapp.Model.ApiResultModel
import com.bmac.surwayapp.Model.CRACSaveSurveyDetailsModel
import com.bmac.surwayapp.Model.PostApiModel
import com.bmac.surwayapp.RoomDatabase.CRACDatabase
import com.bmac.surwayapp.databinding.DesignSavedSurvayListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class SavedSurveyViewAdapter(var context: Context, var savedSurvey:MutableList<CRACSaveSurveyDetailsModel>):
    RecyclerView.Adapter<mySavedSurveyHolder>()
{
    lateinit var apiService: ApiService
    var db: CRACDatabase? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mySavedSurveyHolder {

        val binding = DesignSavedSurvayListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        apiService = CRACApiClient.getapiclient().create(ApiService::class.java)
        db = Room.databaseBuilder(context, CRACDatabase::class.java, "FarmerDB").allowMainThreadQueries().build()
        return mySavedSurveyHolder(binding)
    }

    override fun onBindViewHolder(holder: mySavedSurveyHolder, @SuppressLint("RecyclerView") position: Int) {
        val dFormat = DecimalFormat("##.######")
        var s_id = savedSurvey[position].SurveyId
        var machine_id = savedSurvey[position].MachineId
        var areaPoint1 = savedSurvey[position].AreaPoint1
        var areaPoint2 = savedSurvey[position].AreaPoint2
        var areaPoint3 = savedSurvey[position].AreaPoint3
        var areaPoint4 = savedSurvey[position].AreaPoint4

        var total_area  = String.format("%.6f", savedSurvey[position].TotalArea)
        Log.d("areaformat", "$total_area    ${savedSurvey[position].TotalArea}")
        holder.binding.txtVillageCode.text = savedSurvey[position].Village_Code
        holder.binding.txtGrowerName.text = savedSurvey[position].Farmer_Code
        holder.binding.txtVerityCode.text = savedSurvey[position].Verity_Code
        holder.binding.txtTotalArea.text = total_area
        holder.binding.txtShareHolder.text = savedSurvey[position].ShareHolderCode
        holder.binding.txtFarmerTransfer.text = savedSurvey[position].Farmer_Transfer
        holder.binding.txtRPValue.text = savedSurvey[position].RP_Value
        holder.binding.txtPlotNo.text = savedSurvey[position].Plot_No
        holder.binding.txtWardno.text = savedSurvey[position].Wardno
      //  holder.binding.txtPlotcode.text = savedSurvey[position].Plot_Vcode
        holder.binding.txtKitta.text = savedSurvey[position].Kitta
        holder.binding.txtEast.text = savedSurvey[position].East
        holder.binding.txtWest.text = savedSurvey[position].West
        holder.binding.txtNorth.text = savedSurvey[position].North
        holder.binding.txtSouth.text = savedSurvey[position].South
        holder.binding.txtSurveyDateTime.text = savedSurvey[position].SurveyDateTime

        var distance12 = java.lang.Double.valueOf(dFormat.format(savedSurvey[position].DistancePoint12))
        var distance23 = java.lang.Double.valueOf(dFormat.format(savedSurvey[position].DistancePoint23))
        var distance34 = java.lang.Double.valueOf(dFormat.format(savedSurvey[position].DistancePoint34))
        var distance41 = java.lang.Double.valueOf(dFormat.format(savedSurvey[position].DistancePoint41))
        Log.d("distadapter", "${savedSurvey[position].DistancePoint12}   $distance12")

        var grower_code= ""
        var first_farmername= ""
        var second_farmername= ""
        var verity_code= ""
        var village_code= ""
        var village_name= ""
        var plot_vcode= ""

        var P1lat1 = ""
        var P1lng1 = ""
        var P2lat1 = ""
        var P2lng1 = ""
        var P3lat1 = ""
        var P3lng1 = ""
        var P4lat1 = ""
        var P4lng1 = ""
        try{
            var txtFarmer =
                savedSurvey[position].Farmer_Code.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                grower_code = txtFarmer[1].trim()
                first_farmername = txtFarmer[2].trim()
                 second_farmername = txtFarmer[3].trim()

            var txtVerity =
                savedSurvey[position].Verity_Code.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                verity_code = txtVerity[1].trim()
            Log.d("issueHandle","${txtFarmer[1].trim()}")

            var txtVillage =
                savedSurvey[position].Village_Code.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            village_code = txtVillage[0].trim()
            village_name = txtVillage[1].trim()
            //plot_vcode = txtVillage[3].trim()

            var txtP1LatLng =
                savedSurvey[position].AreaPoint1.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            P1lat1 = txtP1LatLng[0].trim()
            P1lng1 = txtP1LatLng[1].trim()

            var txtP2LatLng =
                savedSurvey[position].AreaPoint2.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            P2lat1 = txtP2LatLng[0].trim()
            P2lng1 = txtP2LatLng[1].trim()

            var txtP3LatLng =
                savedSurvey[position].AreaPoint3.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            P3lat1 = txtP3LatLng[0].trim()
            P3lng1 = txtP3LatLng[1].trim()

            var txtP4LatLng =
                savedSurvey[position].AreaPoint4.split(",".toRegex()).dropLastWhile { it.isEmpty() }
            P4lat1 = txtP4LatLng[0].trim()
             P4lng1 = txtP4LatLng[1].trim()
        }
        catch (e:Exception)
        {Log.d("issueSave","${e.message}")}
            holder.binding.btnUploadSurvey.setOnClickListener {
                try {
                  //  Toast.makeText(context, "1", Toast.LENGTH_SHORT).show()
                    val call: Call<ApiResultModel?>? = apiService.postSurvayDetails(PostApiModel(
                        s_id,
                        machine_id,
                        village_code,
                        grower_code,
                        village_name,
                        first_farmername,
                        second_farmername,
                        verity_code,
                        savedSurvey[position].RP_Value,
                        savedSurvey[position].Plot_No,
                        savedSurvey[position].Wardno,
                      //  savedSurvey[position].Plot_Vcode,
                        savedSurvey[position].Kitta,
                        savedSurvey[position].East,
                        savedSurvey[position].West,
                        savedSurvey[position].North,
                        savedSurvey[position].South,
                        total_area,
                        savedSurvey[position].SurveyDateTime,
                        P1lat1,
                        P2lat1,
                        P3lat1,
                        P4lat1,
                        0.000000.toString(),
                        0.000000.toString(),
                        0.000000.toString(),
                        0.000000.toString(),
                        P1lng1,
                        P2lng1,
                        P3lng1,
                        P4lng1,
                        0.000000.toString(),
                        0.000000.toString(),
                        0.000000.toString(),
                        0.000000.toString(),
                       // plot_vcode,
                        distance12,
                        distance23,
                        distance34,
                        distance41
                    )
                    )
                   // Toast.makeText(context, "2", Toast.LENGTH_SHORT).show()

                    call!!.enqueue(object : Callback<ApiResultModel?> {
                        override fun onResponse(call: Call<ApiResultModel?>, response: Response<ApiResultModel?>) {
                       //     Toast.makeText(context, "3", Toast.LENGTH_SHORT).show()
                            if(response.isSuccessful){
                                var msg = response.body()!!.message
                                var status = response.body()!!.status
                             //   Toast.makeText(context, "4"+status, Toast.LENGTH_SHORT).show()
                                var dialog =
                                    ProgressDialog.show(context, "", "Uploading Survey...", true, false)
                              //  Toast.makeText(context, "5", Toast.LENGTH_SHORT).show()
                                if(status == true){
                                //    Toast.makeText(context, "6", Toast.LENGTH_SHORT).show()
                                    Toast.makeText(context, "$msg", Toast.LENGTH_SHORT).show()
                                    deleteSurveyData(savedSurvey[position].SurveyId)
                                    savedSurvey.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position, savedSurvey.size)
                                    dialog.dismiss()
                                }
                                else
                                {
                                  //  Toast.makeText(context, "7", Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                    Toast.makeText(context, "$msg", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else if(response.code() == 404 || response.code() == 500 || response.body() == null)
                            {
                               // Toast.makeText(context, "8", Toast.LENGTH_SHORT).show()
                                Toast.makeText(context, "${response.body()!!.status}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<ApiResultModel?>, t: Throwable) {
                            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show()
                        }
                    })
                } catch (e: java.lang.Exception) { e.printStackTrace()
                    Log.d("catchh","${e.message}")}
            }
            Log.d("record", "Status: done")
    }
    override fun getItemCount(): Int {

        return savedSurvey.size
    }

    fun deleteSurveyData(id: Int)
    {
        val survey_id = CRACSurvayDetailClass()
        survey_id.SurveyId = id.toInt()
        db!!.daoclass().deleteSurveyData(survey_id)

    }
}

class mySavedSurveyHolder(val binding: DesignSavedSurvayListBinding): RecyclerView.ViewHolder(binding.root) {

}
