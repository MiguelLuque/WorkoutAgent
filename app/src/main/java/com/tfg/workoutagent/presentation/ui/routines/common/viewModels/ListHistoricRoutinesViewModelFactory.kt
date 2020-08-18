package com.tfg.workoutagent.presentation.ui.routines.common.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tfg.workoutagent.domain.routineUseCases.HistoricRoutinesUseCase
import com.tfg.workoutagent.domain.routineUseCases.ManageRoutineCustomerUseCase

class ListHistoricRoutinesViewModelFactory(private val customerId:String, private val historicRoutinesUseCase: HistoricRoutinesUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ListHistoricRoutinesViewModel(customerId, historicRoutinesUseCase) as T
    }
}