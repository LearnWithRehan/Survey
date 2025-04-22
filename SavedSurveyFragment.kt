package com.bmac.surwayapp.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bmac.surwayapp.Adapters.GrowerAdapter
import com.bmac.surwayapp.Adapters.SavedSurveyViewAdapter
import com.bmac.surwayapp.ApiClient.CRACApiClient
import com.bmac.surwayapp.ApiInterface.ApiService
import com.bmac.surwayapp.Entity.CRACSurvayDetailClass
import com.bmac.surwayapp.Entity.VerityCodeClass
import com.bmac.surwayapp.Model.CRACSaveSurveyDetailsModel
import com.bmac.surwayapp.Model.VerityListModel
import com.bmac.surwayapp.R
import com.bmac.surwayapp.RoomDatabase.CRACDatabase
import com.bmac.surwayapp.databinding.FragmentSavedSurveyBinding
import com.bmac.surwayapp.databinding.FragmentStartSurwayBinding

class SavedSurveyFragment : Fragment() {
    private var _binding: FragmentSavedSurveyBinding? = null
    private val binding get() = _binding!!

    lateinit var savedSurveyList:MutableList<CRACSaveSurveyDetailsModel>
    lateinit var myAdapter: SavedSurveyViewAdapter
    lateinit var apiService: ApiService
    var entity = CRACSurvayDetailClass()
    lateinit var db: CRACDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSavedSurveyBinding.inflate(inflater, container, false)
        val view = binding.root

        apiService = CRACApiClient.getapiclient().create(ApiService::class.java)

        db = Room.databaseBuilder(requireActivity(), CRACDatabase::class.java, "FarmerDB").allowMainThreadQueries().build()

        savedSurveyList = ArrayList()
        savedSurveyList =  db.daoclass().viewSavedSurveyDetails()


        if(savedSurveyList.size > 0){

            var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireActivity())
            _binding!!.recycleSavedSurvey.layoutManager=layoutManager
            myAdapter = SavedSurveyViewAdapter(requireActivity(), savedSurveyList)
            _binding!!.recycleSavedSurvey.adapter = myAdapter
            _binding!!.txtNoDataFound.text = ""
        }
        else{
                _binding!!.txtNoDataFound.text = "No Data Found"
        }

       /* if(savedSurveyList.isEmpty()){
            _binding!!.linearEmptyDesign.visibility = View.VISIBLE
        }else{
            _binding!!.linearEmptyDesign.visibility = View.GONE
        }*/
        return view
    }


}