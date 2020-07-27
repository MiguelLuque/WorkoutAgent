package com.tfg.workoutagent.presentation.ui.routines.trainer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.tfg.workoutagent.R
import com.tfg.workoutagent.data.repositoriesImpl.ExerciseRepositoryImpl
import com.tfg.workoutagent.data.repositoriesImpl.RoutineRepositoryImpl
import com.tfg.workoutagent.databinding.FragmentAddDayEditRoutineBinding
import com.tfg.workoutagent.domain.routineUseCases.ManageRoutineUseCaseImpl
import com.tfg.workoutagent.models.RoutineActivity
import com.tfg.workoutagent.presentation.ui.routines.trainer.adapters.ActivityListAdapter
import com.tfg.workoutagent.presentation.ui.routines.trainer.viewModels.EditRoutineViewModel
import com.tfg.workoutagent.presentation.ui.routines.trainer.viewModels.EditRoutineViewModelFactory
import com.tfg.workoutagent.vo.createAlertDialog
import kotlinx.android.synthetic.main.fragment_add_day_edit_routine.*

class AddDayEditRoutineFragment : Fragment() {

    private val routineId by lazy { AddDayEditRoutineFragmentArgs.fromBundle(arguments!!).routineId }

    private lateinit var adapter: ActivityListAdapter

    private val viewModel by lazy {
        ViewModelProvider(
            activity!!,
            EditRoutineViewModelFactory(
                routineId,
                ManageRoutineUseCaseImpl(RoutineRepositoryImpl(), ExerciseRepositoryImpl())
            )
        ).get(EditRoutineViewModel::class.java)
    }

    private lateinit var binding: FragmentAddDayEditRoutineBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_day_edit_routine,
            container,
            false
        )
        this.binding.viewModel = viewModel
        this.binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ActivityListAdapter(this.context!!, { routineActivity: RoutineActivity ->
            viewModel.onEditActivity(routineActivity)
            findNavController().navigate(
                AddDayEditRoutineFragmentDirections.actionAddDayEditRoutineFragmentToEditActivityEditRoutineFragment(
                    routineId = routineId
                )
            )
        }, { routineActivity: RoutineActivity ->
            createAlertDialog(
                requireContext(),
                "Delete day",
                "Are you sure you want to delete this day?",
                {
                    viewModel.removeActivity(routineActivity)
                },
                {})
        })

        recycler_add_day_activities.layoutManager = LinearLayoutManager(this.context!!)
        recycler_add_day_activities.adapter = adapter

        // val itemTouchHelper = setUpItemTouchHelper()
        // itemTouchHelper.attachToRecyclerView(recycler_add_day_activities)

        // TODO
        viewModel.adapter = adapter

        onBackPressed()
        observeData()
        observeErrors()
        setupButtons()
    }

    /*private fun setUpItemTouchHelper(): ItemTouchHelper {
        val simpleItemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewHolder as ActivityListAdapter.ActivityListViewHolder
                val activity = viewHolder.activity
                viewModel.removeActivity(activity)
            }
        }

        return ItemTouchHelper(simpleItemTouchCallback)
    }*/

    private fun onBackPressed() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                findNavController().navigate(
                    AddDayEditRoutineFragmentDirections.actionAddDayEditRoutineFragmentToEditRoutineFragment(
                        routineId = routineId,
                        clearData = 2
                    )
                )
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun observeData() {
        viewModel.activities.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.setListData(it)
                adapter.notifyDataSetChanged()
                // findNavController().navigate(CreateRoutineFragmentDirections.actionCreateRoutineToAddDayFragment())
            }
        })

        viewModel.dayCreated.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    findNavController().navigate(
                        AddDayEditRoutineFragmentDirections.actionAddDayEditRoutineFragmentToEditRoutineFragment(
                            routineId = routineId,
                            clearData = 2
                        )
                    )
                }
            }
        })
    }

    private fun observeErrors() {
        viewModel.dayNameError.observe(viewLifecycleOwner, Observer {
            binding.routineDayNameInputEdit.error =
                if (it != "") it else null
        })

        viewModel.activitiesError.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it == "") {
                    binding.dayActivitiesError.visibility = View.INVISIBLE
                } else {
                    binding.dayActivitiesError.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupButtons() {
        save_create_day_button.setOnClickListener {
            viewModel.onSaveDay()
        }

        add_day_activity_button.setOnClickListener {
            findNavController().navigate(
                AddDayEditRoutineFragmentDirections.actionAddDayEditRoutineFragmentToAddActivityEditRoutineFragment(
                    routineId = routineId
                )
            )
        }

        cancel_create_day_button.setOnClickListener {
            findNavController().navigate(
                AddDayEditRoutineFragmentDirections.actionAddDayEditRoutineFragmentToEditRoutineFragment(
                    routineId = routineId,
                    clearData = 2
                )
            )
        }
    }
}
