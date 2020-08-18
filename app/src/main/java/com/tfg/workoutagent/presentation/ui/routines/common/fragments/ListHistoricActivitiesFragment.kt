package com.tfg.workoutagent.presentation.ui.routines.common.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.tfg.workoutagent.R
import com.tfg.workoutagent.data.repositoriesImpl.RoutineRepositoryImpl
import com.tfg.workoutagent.domain.routineUseCases.HistoricRoutinesUseCaseImpl
import com.tfg.workoutagent.presentation.ui.routines.common.adapters.ListHistoricActivitiesAdapter
import com.tfg.workoutagent.presentation.ui.routines.common.viewModels.ListHistoricActivitiesViewModel
import com.tfg.workoutagent.presentation.ui.routines.common.viewModels.ListHistoricActivitiesViewModelFactory
import com.tfg.workoutagent.vo.Resource
import kotlinx.android.synthetic.main.fragment_list_historic_activities.*

class ListHistoricActivitiesFragment : Fragment() {

    private val routineId by lazy { ListHistoricActivitiesFragmentArgs.fromBundle(requireArguments()).routineId}
    private val numDay by lazy { ListHistoricActivitiesFragmentArgs.fromBundle(requireArguments()).numDay}
    private val nameDay by lazy { ListHistoricActivitiesFragmentArgs.fromBundle(requireArguments()).nameDay}

    private lateinit var adapter : ListHistoricActivitiesAdapter

    private val viewModel by lazy { ViewModelProvider(
        this,
        ListHistoricActivitiesViewModelFactory(
            routineId,
            numDay,
            HistoricRoutinesUseCaseImpl(RoutineRepositoryImpl())
        )
    ).get(ListHistoricActivitiesViewModel::class.java) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_historic_activities, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ListHistoricActivitiesAdapter(requireContext())
        rcv_historic_activities.layoutManager =  LinearLayoutManager(this.requireContext())
        rcv_historic_activities.adapter = adapter
        observeData()
    }
    fun observeData(){
        viewModel.activityList.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                    sv_my_historic_activities.startShimmer()
                }
                is Resource.Success -> {
                    sv_my_historic_activities.visibility = View.GONE
                    sv_my_historic_activities.stopShimmer()
                    if(it.data.days[numDay].activities.size == 0){
                        //TODO: Show a message like --> "You have no activities! Talk with your trainer to start training!"
                    }else{
                        adapter.setListData(it.data.days[numDay].activities)
                        adapter.notifyDataSetChanged()
                    }
                }
                is Resource.Failure-> {
                    sv_my_historic_activities.visibility = View.GONE
                    sv_my_historic_activities.stopShimmer()
                    Toast.makeText(this.requireContext(),"Ocurrió un error ${it.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
