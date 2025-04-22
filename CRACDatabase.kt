package com.bmac.surwayapp.RoomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bmac.surwayapp.DaoClass.CRACDaoClas
import com.bmac.surwayapp.Entity.CRACSurvayDetailClass
import com.bmac.surwayapp.Entity.FarmerDataClass
import com.bmac.surwayapp.Entity.VerityCodeClass
import com.bmac.surwayapp.Entity.VillageCodeClass


@Database (entities = [FarmerDataClass::class, VillageCodeClass::class, VerityCodeClass::class, CRACSurvayDetailClass::class], version = 1)

abstract class CRACDatabase :RoomDatabase() {

    abstract fun daoclass(): CRACDaoClas
}