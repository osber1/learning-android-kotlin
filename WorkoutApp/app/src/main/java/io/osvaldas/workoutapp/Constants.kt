package io.osvaldas.workoutapp

import io.osvaldas.workoutapp.exercise.ExerciseModel

object Constants {
    const val UNDERWEIGHT = "Oops! You really need to take better care of yourself! Eat more!"
    const val NORMAL_WEIGHT = "Congratulations! You are in a good shape!"
    const val OBESE_I = "Oops! You really need to take care of your yourself! Workout maybe!"
    const val OBESE_II = "OMG! You are in a very dangerous condition! Act now!"

    fun defaultExerciseList(): ArrayList<ExerciseModel> {
        val exerciseList = ArrayList<ExerciseModel>()
        createAndAddExerciseToList(exerciseList, 1, "Jumping Jacks", R.drawable.ic_jumping_jacks)
        createAndAddExerciseToList(exerciseList, 2, "Wall Sit", R.drawable.ic_wall_sit)
        createAndAddExerciseToList(exerciseList, 3, "Push Up", R.drawable.ic_push_up)
        createAndAddExerciseToList(exerciseList, 4, "Abdominal Crunch", R.drawable.ic_abdominal_crunch)
        createAndAddExerciseToList(exerciseList, 5, "Step-Up onto Chair", R.drawable.ic_step_up_onto_chair)
        createAndAddExerciseToList(exerciseList, 6, "Squat", R.drawable.ic_squat)
        createAndAddExerciseToList(exerciseList, 7, "Triceps Dip On Chair", R.drawable.ic_triceps_dip_on_chair)
        createAndAddExerciseToList(exerciseList, 8, "Plank", R.drawable.ic_plank)
        createAndAddExerciseToList(exerciseList, 9, "High Knees Running In Place", R.drawable.ic_high_knees_running_in_place)
        createAndAddExerciseToList(exerciseList, 10, "Lunges", R.drawable.ic_lunge)
        createAndAddExerciseToList(exerciseList, 11, "Push up and Rotation", R.drawable.ic_push_up_and_rotation)
        createAndAddExerciseToList(exerciseList, 12, "Side Plank", R.drawable.ic_side_plank)
        return exerciseList
    }

    private fun createAndAddExerciseToList(
        exerciseList: ArrayList<ExerciseModel>,
        id: Int,
        name: String,
        image: Int
    ) {
        val sidePlank = ExerciseModel(id, name, image)
        exerciseList.add(sidePlank)
    }
}

//return listOf(
//ExerciseModel(1, "Jumping Jacks", R.drawable.ic_jumping_jacks),
//ExerciseModel(2, "Wall Sit", R.drawable.ic_wall_sit),
//ExerciseModel(3, "Push Up", R.drawable.ic_push_up),
//ExerciseModel(4, "Abdominal Crunch", R.drawable.ic_abdominal_crunch),
//ExerciseModel(5, "Step-Up onto Chair", R.drawable.ic_step_up_onto_chair),
//ExerciseModel(6, "Squat", R.drawable.ic_squat),
//ExerciseModel(7, "Triceps Dip On Chair", R.drawable.ic_triceps_dip_on_chair),
//ExerciseModel(8, "Plank", R.drawable.ic_plank),
//ExerciseModel(9,"High Knees Running In Place", R.drawable.ic_high_knees_running_in_place),
//ExerciseModel(10, "Lunges", R.drawable.ic_lunge),
//ExerciseModel(11, "Push up and Rotation", R.drawable.ic_push_up_and_rotation),
//ExerciseModel(12, "Side Plank", R.drawable.ic_side_plank)
//)