package com.bmac.surwayapp.DaoClass

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bmac.surwayapp.Entity.CRACSurvayDetailClass
import com.bmac.surwayapp.Entity.FarmerDataClass
import com.bmac.surwayapp.Entity.VerityCodeClass
import com.bmac.surwayapp.Entity.VillageCodeClass
import com.bmac.surwayapp.Model.CRACSaveSurveyDetailsModel
import com.bmac.surwayapp.Model.GrowerDataListModel
import com.bmac.surwayapp.Model.VerityListModel
import com.bmac.surwayapp.Model.VillageListModel


@Dao
interface CRACDaoClas {

    // ----------- operations of Grower data insert, select -------------
    @Insert
    fun addFarmer(user: FarmerDataClass)

    @Query("SELECT * FROM  FarmerData Where VillageCode = :village_Code")
    fun viewFarmerData(village_Code:String):List<GrowerDataListModel>

    @Query("SELECT * FROM  FarmerData ")
    fun viewFarmerList():List<GrowerDataListModel>

    @Query("DELETE FROM FarmerData")
    fun deleteGrowerData()
    // -------------------------------------------------

    // ----------- operations of Village data insert, select -------------

    @Insert
    fun addVillage(user: VillageCodeClass)

    @Query("SELECT * FROM  VillageData")
    fun viewVillageData():List<VillageListModel>

    @Query("DELETE FROM VillageData")
    fun deleteVillageData()
    // -------------------------------------------------

    // ----------- operations of Verity data insert, select -------------

    @Insert
    fun addVerity(user: VerityCodeClass)

    @Query("SELECT * FROM  VerityData")
    fun viewVerityData() : List<VerityListModel>

    @Query("DELETE FROM VerityData")
    fun deleteVerityData()
    // -------------------------------------------------

    // ----------- operations of Survey Details data insert, select -------------

    @Insert
    fun addSurway(user: CRACSurvayDetailClass)

    @Query("SELECT * FROM  CRACSurvayDetail")
    fun viewSavedSurveyDetails():MutableList<CRACSaveSurveyDetailsModel>

    @Query("SELECT SurveyId FROM CRACSurvayDetail ORDER BY SurveyId DESC LIMIT 2")
    fun getLastSurvay(): MutableList<Int>



    @Delete
    fun deleteSurveyData(surveyData: CRACSurvayDetailClass)
    // -------------------------------------------------



}