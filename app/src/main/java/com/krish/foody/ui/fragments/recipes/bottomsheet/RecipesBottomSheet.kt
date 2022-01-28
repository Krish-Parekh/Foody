package com.krish.foody.ui.fragments.recipes.bottomsheet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.krish.foody.databinding.RecipesBottomSheetBinding
import com.krish.foody.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.krish.foody.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.krish.foody.viewmodel.RecipesViewModel

private const val TAG = "RecipesBottomSheet"
class RecipesBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: RecipesBottomSheetBinding
    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0
    private lateinit var recipesViewModel : RecipesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = RecipesBottomSheetBinding.inflate(inflater, container, false)

        recipesViewModel.readMealAndDietType.asLiveData()
            .observe(viewLifecycleOwner, Observer { value ->
                mealTypeChip = value.selectedMealType
                dietTypeChip = value.selectedDietType
                updateChip(value.selectedMealTypeId, binding.mealTypeChipGroup)
                updateChip(value.selectedDietTypeId, binding.dietTypeChipGroup)
            })

        binding.mealTypeChipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedMealType = chip.text.toString().lowercase()
            mealTypeChip = selectedMealType
            mealTypeChipId = selectedChipId
        }

        binding.dietTypeChipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedDietType = chip.text.toString().lowercase()
            dietTypeChip = selectedDietType
            dietTypeChipId = selectedChipId

        }

        binding.applyBtn.setOnClickListener {
            recipesViewModel.saveMealAndDietTypeTemp(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId
            )
            val action =
                RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
            findNavController().navigate(action)
        }
        return binding.root
    }

    private fun updateChip(selectedMealTypeId: Int, mealTypeChipGroup: ChipGroup) {
        if (selectedMealTypeId != 0) {
            try {
                val targetView = mealTypeChipGroup.findViewById<Chip>(selectedMealTypeId)
                targetView.isChecked = true
                mealTypeChipGroup.requestChildFocus(targetView,targetView)
            } catch (e: Exception) {
                Log.d(TAG, "updateChip: ${e.message.toString()}")
            }
        }
    }


}