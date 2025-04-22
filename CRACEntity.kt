package com.bmac.surwayapp.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FarmerData")
data class FarmerDataClass(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "FarmerId")
    var FarmerId: Int = 0,

    @ColumnInfo(name = "VillageCode")
    var VillageCode: String = "",

    @ColumnInfo(name = "FarmerCode")
    var FarmerCode: String = "",

    @ColumnInfo(name = "First_FarmerName")
    var First_FarmerName :String = "",

    @ColumnInfo(name = "Second_FarmerName")
    var Second_FarmerName :String = "",
)

@Entity(tableName = "VillageData")
data class VillageCodeClass(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VillageId")
    var VillageId: Int = 0,

    @ColumnInfo(name = "VillageCode")
    var VillageCode: String = "",

    @ColumnInfo(name = "VillageName")
    var VillageName: String = "",

    @ColumnInfo(name = "PlotNo")
    var PlotNo: String = "",

    @ColumnInfo(name = "PlotCode")
    var PlotCode: String = ""
)

@Entity(tableName = "VerityData")
data class VerityCodeClass(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "VId")
    var VId: Int = 0,

    @ColumnInfo(name = "VerityId")
    var VerityId: String = "",

    @ColumnInfo(name = "VerityCode")
    var VerityCode: String = "",

)

@Entity(tableName = "CRACSurvayDetail")
data class CRACSurvayDetailClass(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "SurveyId")
    var SurveyId: Int = 0 ,

    @ColumnInfo(name = "MachineId")
    var MachineId: String = "",

    @ColumnInfo(name = "UniqueID")
    var UniqueID: String = "",

    @ColumnInfo(name = "Village_Code")
    var Village_Code: String = "",

    @ColumnInfo(name = "Farmer_Code")
    var Farmer_Code: String = "",

    @ColumnInfo(name = "Verity_Code")
    var Verity_Code: String = "",

    @ColumnInfo(name = "Farmer_Transfer")
    var Farmer_Transfer: String = "",

    @ColumnInfo(name = "Plot_No")
    var Plot_No: String = "",



    @ColumnInfo(name = "Plot_Vcode")
    var Plot_Vcode: String = "",


    @ColumnInfo(name = "Kitta")
    var Kitta: String = "",

    @ColumnInfo(name = "East")
    var East: String = "",


    @ColumnInfo(name = "West")
    var West: String = "",


    @ColumnInfo(name = "North")
    var North: String = "",


    @ColumnInfo(name = "South")
    var South: String = "",

    @ColumnInfo(name = "RP_Value")
    var RP_Value: String = "",

    @ColumnInfo(name = "ShareHolderCode")
    var ShareHolderCode: String = "",

    @ColumnInfo(name = "AreaPoint1")
    var areaPoint1: String = "",

    @ColumnInfo(name = "AreaPoint2")
    var areaPoint2: String = "",

    @ColumnInfo(name = "AreaPoint3")
    var areaPoint3: String = "",

    @ColumnInfo(name = "AreaPoint4")
    var areaPoint4: String = "",

    @ColumnInfo(name = "TotalArea")
    var totalArea: Double = 0.00,

    @ColumnInfo(name = "DistancePoint12")
    var distancePoint12: Double = 0.00,

    @ColumnInfo(name = "DistancePoint23")
    var distancePoint23: Double = 0.00,

    @ColumnInfo(name = "DistancePoint34")
    var distancePoint34: Double = 0.00,

    @ColumnInfo(name = "DistancePoint41")
    var distancePoint41: Double = 0.00,

    @ColumnInfo(name = "SurveyDateTime")
    var SurveyDateTime: String = "",

    @ColumnInfo(name = "Wardno")
    var Wardno: String = "",

    )

