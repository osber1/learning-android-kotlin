package io.osvaldas.workoutapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.osvaldas.workoutapp.databinding.ActivityFinishBinding
import io.osvaldas.workoutapp.history.HistoryDao
import io.osvaldas.workoutapp.history.HistoryEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FinishActivity : AppCompatActivity() {

    private var binding: ActivityFinishBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarFinishActivity)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarFinishActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnFinish?.setOnClickListener {
            finish()
        }

        val historyDao = (application as WorkoutApp).db.historyDao()
        addToDatabase(historyDao)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun addToDatabase(historyDao: HistoryDao) {
        lifecycleScope.launch {
            historyDao.insert(HistoryEntity(getDate()))
        }
    }

    private fun getDate(): String {
        val sdf = SimpleDateFormat("yyyy MM dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Calendar.getInstance().time)
    }
}