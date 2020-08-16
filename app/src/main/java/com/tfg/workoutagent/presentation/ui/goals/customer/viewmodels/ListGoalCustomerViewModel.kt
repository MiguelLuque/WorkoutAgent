package com.tfg.workoutagent.presentation.ui.goals.customer.viewmodels

import android.widget.Toast
import androidx.lifecycle.*
import com.tfg.workoutagent.domain.goalUseCases.ListGoalCustomerUseCase
import com.tfg.workoutagent.models.Goal
import com.tfg.workoutagent.vo.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListGoalCustomerViewModel(private val listGoalCustomerUseCase: ListGoalCustomerUseCase) : ViewModel() {
    val goalsList = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try {
            val goals = listGoalCustomerUseCase.getGoalsCustomer()
            loadGoals(goals)
            emit(goals)
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

    val listGoalsLoaded = mutableListOf<Goal>()
    private fun loadGoals(goals : Resource<MutableList<Goal>>) {
        if(goals is Resource.Success){
            goals.data.forEach { goal -> listGoalsLoaded.add(goal) }
        }
    }

    private val _goalDeleted = MutableLiveData<Boolean?>(null)
    val goalDeleted: LiveData<Boolean?>
        get() = _goalDeleted

    private val _goalFinished = MutableLiveData<Boolean?>(null)
    val goalFinished: LiveData<Boolean?>
        get() = _goalFinished

    fun removeGoal(position : Int) {
        viewModelScope.launch {
            try {
                listGoalCustomerUseCase.removeGoal(position)
                listGoalsLoaded.removeAt(position)
                _goalDeleted.value = true
            }catch (e: Exception){
                _goalDeleted.value = false
            }
        }
    }

    fun finishGoal(position : Int) {
        viewModelScope.launch {
            try {
                listGoalCustomerUseCase.finishGoal(position)
                _goalFinished.value = true
            }catch (e: Exception){
                _goalFinished.value = false
            }
        }
    }
}
