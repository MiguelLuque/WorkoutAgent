package com.tfg.workoutagent.data.repositories

import com.tfg.workoutagent.models.Customer
import com.tfg.workoutagent.models.Trainer
import com.tfg.workoutagent.vo.Resource

interface UserRepository {

    suspend fun getOwnCustomers(): Resource<MutableList<Customer>>

    suspend fun getCustomer(id: String) : Resource<Customer>

    suspend fun createCustomer(customer: Customer) : Resource<Boolean>

    suspend fun updateCustomer(customer: Customer) : Resource<Boolean>

    suspend fun deleteCustomer(id : String): Resource<Boolean>

    suspend fun canDeleteCustomer(id : String) : Resource<Boolean>

    suspend fun getCustomersAdmin() : Resource<MutableList<Customer>>

    suspend fun getTrainersAdmin() : Resource<MutableList<Trainer>>

    suspend fun getTrainer(id: String) : Resource<Trainer>

    suspend fun createTrainer(trainer : Trainer) : Resource<Boolean>
}