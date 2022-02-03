package io.osvaldas.workoutapp.history

import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import io.osvaldas.workoutapp.WorkoutApp
import io.osvaldas.workoutapp.databinding.ActivityHistoryBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {

    private var binding: ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarHistoryActivity)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "HISTORY"
        }

        binding?.toolbarHistoryActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        val historyDao = (application as WorkoutApp).db.historyDao()
        getAllCompletedTrainings(historyDao)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getAllCompletedTrainings(historyDao: HistoryDao) {
        lifecycleScope.launch {
            historyDao.getAll().collect { allCompletedDatesList ->
                if (allCompletedDatesList.isNotEmpty()) {
                    binding?.tvHistory?.visibility = VISIBLE
                    binding?.rvHistory?.visibility = VISIBLE
                    binding?.tvNoDataAvailable?.visibility = INVISIBLE

                    binding?.rvHistory?.layoutManager = LinearLayoutManager(this@HistoryActivity)
                    binding?.rvHistory?.adapter = HistoryAdapter(addAllDatesToList(allCompletedDatesList))
                } else {
                    binding?.tvHistory?.visibility = INVISIBLE
                    binding?.rvHistory?.visibility = INVISIBLE
                    binding?.tvNoDataAvailable?.visibility = VISIBLE


                }
            }
        }
    }

    private fun addAllDatesToList(allCompletedDatesList: List<HistoryEntity>): ArrayList<String> {
        val dates = ArrayList<String>()
        allCompletedDatesList.forEach {
            dates.add(it.date)
        }
        return dates
    }
}