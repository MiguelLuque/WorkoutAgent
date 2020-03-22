package com.tfg.workoutagent.presentation.ui.exercises.trainer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.tfg.workoutagent.domain.exerciseUseCases.ListExercisesUseCase
import com.tfg.workoutagent.vo.Resource
import kotlinx.coroutines.Dispatchers

class ListExerciseViewModel(listExerciseUseCase: ListExercisesUseCase): ViewModel() {

    val exerciseList = liveData(Dispatchers.IO) {
        emit(Resource.Loading())
        try{
            val exerciseList = listExerciseUseCase.getExercises()
            emit(exerciseList)
        }catch (e: Exception){
            emit(Resource.Failure(e))
        }
    }

}