package com.tfg.workoutagent.presentation.ui.routines.customer.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import com.tfg.workoutagent.R
import com.tfg.workoutagent.base.BaseFragment
import com.tfg.workoutagent.data.repositoriesImpl.RoutineRepositoryImpl
import com.tfg.workoutagent.domain.routineUseCases.ManageRoutineCustomerUseCaseImpl
import com.tfg.workoutagent.models.ActivitySet
import com.tfg.workoutagent.models.RoutineActivity
import com.tfg.workoutagent.presentation.ui.routines.customer.adapters.TodayActivitiesCustomerListAdapter
import com.tfg.workoutagent.presentation.ui.routines.customer.viewModels.TodayActivitiesCustomerViewModel
import com.tfg.workoutagent.presentation.ui.routines.customer.viewModels.TodayActivitiesCustomerViewModelFactory
import com.tfg.workoutagent.vo.Resource
import kotlinx.android.synthetic.main.fragment_today_activities_customer.*

class TodayActivitiesCustomerFragment : BaseFragment() {

    private lateinit var adapterActivities : TodayActivitiesCustomerListAdapter
    private val viewModel by lazy {
        ViewModelProvider(
            this, TodayActivitiesCustomerViewModelFactory(
                ManageRoutineCustomerUseCaseImpl(
                    RoutineRepositoryImpl()
                )
            )).get(TodayActivitiesCustomerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_today_activities_customer, container, false)
    }

    class ActionListeners(val deleteClickListener : (activityPos: Int, position: Int) -> Unit,
        val finishedClickListener : (activityPos: Int, setPosition: Int, repetitions: Int?, weight:Double?) -> String,
                          val updateElement : (activityPos: Int) -> Unit)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterActivities = TodayActivitiesCustomerListAdapter(this.requireContext(),
            completedClickListener@{position: Int -> this.viewModel.completedActivity(position) },
            ActionListeners(
            deletedClickListener@{activityPos: Int, position: Int -> this.viewModel.removeSetActivity(activityPos, position)},
            finishedClickListener@{activityPos: Int, setPosition: Int, repetition:Int?, weight: Double? ->
                this.viewModel.updateSetActivityToday(activityPos, setPosition, repetition, weight)},
            updatedElement@{activityPos: Int -> adapterActivities.notifyItemChanged(activityPos)})
            )
        recycler_today_activities.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recycler_today_activities.adapter = adapterActivities
        observeData()
    }


    fun observeData(){
        viewModel.routine.observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading -> {
                    sv_today.startShimmer()
                }
                is Resource.Failure -> {
                    sv_today.stopShimmer()
                    sv_today.visibility = View.GONE
                    Log.i("it", it.toString())
                }
                is Resource.Success -> {sv_today.stopShimmer()
                    sv_today.visibility = View.GONE
                    var activities = mutableListOf<RoutineActivity>()
                    for (day in it.data.days) {
                        if (!day.completed) {
                            activities = day.activities
                            break
                        }
                        Log.i("day", day.toString())
                    }
                    if(activities.size==0){
                        //TODO: Mensaje tipo "You don't have any goals yet"
                    }
                    adapterActivities.setDataList(activities)
                    adapterActivities.notifyDataSetChanged()
                    this.viewModel.dayLoaded()
                }
            }
        })
        viewModel.dayLoaded.observe(viewLifecycleOwner, Observer {
            when(it){
                true -> {
                    viewModel.dayLoaded()
                }
                false -> {
                    viewModel.dayLoaded()
                }
            }
        })

    }
}
