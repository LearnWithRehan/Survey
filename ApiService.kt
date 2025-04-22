package com.bmac.surwayapp.ApiInterface

import com.bmac.surwayapp.Model.ApiResultModel
import com.bmac.surwayapp.Model.PostApiModel
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {


    @GET("grower.php?")
    fun viewDataG(
        @Query("V_HHT_CODE") V_HHT_CODE: String
    ):retrofit2.Call<String>


  @GET("village.php?")
      fun getVillage(
          @Query("V_HHT_CODE") V_HHT_CODE: String,
      ):retrofit2.Call<String>

      @GET("variety.php?")
      fun getVariety(
          @Query("V_HHT_CODE") V_HHT_CODE: String,
          ):retrofit2.Call<String>


    @POST("Surveydata.php")
    fun postSurvayDetails(@Body body: PostApiModel?): Call<ApiResultModel?>?

    @GET("control.php")
    fun getCompanyName(@Query("V_HHT_CODE") V_HHT_CODE: String, ):retrofit2.Call<String>
}

