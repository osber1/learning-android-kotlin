package io.osvaldas.agecalculator

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

const val MINUTES_DIVIDER = 60000

class MainActivity : AppCompatActivity() {
    private var tvSelectedDate: TextView? = null
    private var tvMinutes: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnDatePicker: Button = findViewById(R.id.btn_date_picker)
        tvSelectedDate = findViewById(R.id.tv_selected_day)
        tvMinutes = findViewById(R.id.tv_minutes)
        btnDatePicker.setOnClickListener {
            clickDatePicker()
        }
    }

    private fun clickDatePicker() {
        val cal = getInstance()
        val year = cal.get(YEAR)
        val month = cal.get(MONTH)
        val day = cal.get(DAY_OF_MONTH)
        val dpd = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                tvSelectedDate?.text = selectedDate
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val date = sdf.parse(selectedDate)
                date?.let {
                    val selectedDateInMinutes = date.time.div(MINUTES_DIVIDER)
                    val currentDate = sdf.parse(sdf.format(System.currentTimeMillis()))
                    currentDate?.let {
                        val currentDateInMinutes = currentDate.time.div(MINUTES_DIVIDER)
                        val differenceDateInMinutes =
                            currentDateInMinutes.minus(selectedDateInMinutes)
                        tvMinutes?.text = differenceDateInMinutes.toString()
                    }
                }
            },
            year, month, day
        )
        dpd.datePicker.maxDate = System.currentTimeMillis().minus(86400000)
        dpd.show()
    }
}