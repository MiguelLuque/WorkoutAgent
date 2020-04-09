package com.tfg.workoutagent.presentation.ui.routines.trainer.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg.workoutagent.domain.routineUseCases.ManageRoutineUseCase
import com.tfg.workoutagent.models.Routine
import kotlinx.coroutines.launch
import java.util.*

class CreateRoutineViewModel(private val manageRoutineUseCase: ManageRoutineUseCase) :
    ViewModel() {

    val title = MutableLiveData<String>()
    var startDate = MutableLiveData<String>()

    private val _routineCreated = MutableLiveData<Boolean?>(null)
    val routineCreated: LiveData<Boolean?>
        get() = _routineCreated

    private val _backToCreate = MutableLiveData<Boolean?>(null)
    val backToCreate: LiveData<Boolean?>
        get() = _backToCreate

    private val _addDay = MutableLiveData<Boolean?>(null)
    val addDay: LiveData<Boolean?>
        get() = _addDay

    fun onSubmit() {
        if (checkData()) {
            createRoutine()
        }
    }

    fun onAddDay() {
        _addDay.value = true
    }

    private fun createRoutine() {
        viewModelScope.launch {
            try {
                manageRoutineUseCase.createRoutine(
                    Routine(
                        title = title.value.toString(),
                        startDate = Date()
                    )
                )
                _routineCreated.value = true
            } catch (e: Exception) {
                _routineCreated.value = false
            }
            _routineCreated.value = null
        }
    }

    private fun checkData(): Boolean {
        return true
    }

    fun addDayNavigationCompleted() {
        _addDay.value = null
    }

    fun clearData() {
        title.value = ""
        startDate.value = ""
    }

    fun onBackToCreate() {
        _backToCreate.value = true
    }

    fun backToCreateComplete() {
        _backToCreate.value = null
    }
}
