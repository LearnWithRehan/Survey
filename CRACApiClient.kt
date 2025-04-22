package com.bmac.surwayapp.ApiClient

import com.google.gson.GsonBuilder
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class CRACApiClient {

    companion object
    {

       // var BASE_URL= "http://122.176.50.103:8080/cane_mah/"
       // var BASE_URL= "http://122.176.50.103:8080/cane_UAT/"
       //var BASE_URL= "http://122.176.50.103:8080/cane/"
        // var BASE_URL= "http://122.176.50.103:8080/cane_indu/"
       // val BASE_URL = "http://122.176.50.103:8080/cane_UAT/"
       var BASE_URL= "http://122.176.50.103:8080/cane_bagmati/"

        lateinit var retrofit: Retrofit

        fun getapiclient(): Retrofit {

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            /*  val client: OkHttpClient = OkHttpClient.Builder()
                  .addInterceptor(interceptor)
                  .retryOnConnectionFailure(true)
                  .connectTimeout(30, TimeUnit.SECONDS)
                  .build()*/

            val gson = GsonBuilder()
                .setLenient()
                .create()
            //

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
    }

    }
