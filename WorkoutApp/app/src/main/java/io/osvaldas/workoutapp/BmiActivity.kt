package io.osvaldas.workoutapp

import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.osvaldas.workoutapp.Constants.NORMAL_WEIGHT
import io.osvaldas.workoutapp.Constants.OBESE_I
import io.osvaldas.workoutapp.Constants.OBESE_II
import io.osvaldas.workoutapp.Constants.UNDERWEIGHT
import io.osvaldas.workoutapp.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

class BmiActivity : AppCompatActivity() {

    private var binding: ActivityBmiBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarBmiActivity)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATE BMI"
        }

        binding?.toolbarBmiActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnCalculateUnits?.setOnClickListener {
            if (validateMetricUnits()) {
                displayBmiResults(calculateBmi())
            } else {
                Toast.makeText(this, "Please enter valid values.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayBmiResults(bmi: Float) {
        val bmiLabel: String
        val bmiDescription: String

        when {
            bmi.compareTo(15f) <= 0 -> {
                bmiLabel = "Very severely underweight"
                bmiDescription = UNDERWEIGHT
            }
            bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0 -> {
                bmiLabel = "Severely underweight"
                bmiDescription = UNDERWEIGHT
            }
            bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0 -> {
                bmiLabel = "Underweight"
                bmiDescription = UNDERWEIGHT
            }
            bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0 -> {
                bmiLabel = "Normal"
                bmiDescription = NORMAL_WEIGHT
            }
            bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0 -> {
                bmiLabel = "Overweight"
                bmiDescription = OBESE_I
            }
            bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0 -> {
                bmiLabel = "Obese Class | (Moderately obese)"
                bmiDescription = OBESE_I
            }
            bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0 -> {
                bmiLabel = "Obese Class || (Severely obese)"
                bmiDescription = OBESE_II
            }
            else -> {
                bmiLabel = "Obese Class ||| (Very Severely obese)"
                bmiDescription = OBESE_II
            }
        }
        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, HALF_EVEN).toString()

        binding?.llDisplayBmiResult?.visibility = VISIBLE
        binding?.tvBmiValue?.text = bmiValue
        binding?.tvBmiType?.text = bmiLabel
        binding?.tvBmiDescription?.text = bmiDescription
    }

    private fun calculateBmi(): Float {
        val height: Float = binding?.etHeight?.text.toString().toFloat() / 100
        val weight: Float = binding?.etWeight?.text.toString().toFloat()
        return weight / (height * height)
    }

    private fun validateMetricUnits(): Boolean {
        var isValid = true
        if (binding?.etWeight?.text.toString().isEmpty()
            || binding?.etHeight?.text.toString().isEmpty()
        ) {
            isValid = false
        }
        return isValid
    }
}