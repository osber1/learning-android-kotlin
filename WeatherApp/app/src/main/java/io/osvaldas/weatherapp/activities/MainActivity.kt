package io.osvaldas.weatherapp.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import io.osvaldas.weatherapp.R
import io.osvaldas.weatherapp.databinding.ActivityMainBinding
import io.osvaldas.weatherapp.databinding.DialogCustomProgressBinding
import io.osvaldas.weatherapp.models.WeatherResponse
import io.osvaldas.weatherapp.network.WeatherService
import io.osvaldas.weatherapp.utils.Constants.BASE_URL
import io.osvaldas.weatherapp.utils.Constants.PREFERENCE_NAME
import io.osvaldas.weatherapp.utils.Constants.WEATHER_RESPONSE_DATA
import io.osvaldas.weatherapp.utils.Constants.isNetworkEnable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private lateinit var mSharedPreferences: SharedPreferences

    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

        setupUi()

        if (!isLocationEnabled()) {
            Toast.makeText(this, "Please turn on location.", Toast.LENGTH_SHORT).show()
        } else {
            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            requestLocationData()
                        }

                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@MainActivity,
                                "Location permissions denied.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread().check()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                requestLocationData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(GPS_PROVIDER)
                || locationManager.isProviderEnabled(NETWORK_PROVIDER)
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It looks like you have turned off permissions required for this feature. It can be enabled under Application Settings.")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()!!
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            val mLatitude = mLastLocation.latitude
            val mLongitude = mLastLocation.longitude

            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            val location = geocoder.getFromLocation(mLatitude, mLongitude, 1)[0]
            getLocationWeatherDetails(location)
        }
    }

    private fun getLocationWeatherDetails(location: Address) {
        if (isNetworkEnable(this)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherService = retrofit.create(WeatherService::class.java)
            val response: Call<WeatherResponse> = service.getWeather(location.locality)

            showCustomProgressDialog()

            response.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        hideProgressDialog()

                        val weatherResponseJsonString = Gson().toJson(response.body()!!)
                        val editor = mSharedPreferences.edit()
                        editor.putString(WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                        editor.apply()
                        setupUi()
                    } else {
                        hideProgressDialog()
                        when (response.code()) {
                            400 -> Log.e("Error 400", "Bad connection.")
                            404 -> Log.e("Error 404", "Not found.")
                            else -> Log.e("Error", "Generic error.")
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("Error", t.message.toString())
                }
            })

        } else {
            Toast.makeText(this@MainActivity, "No internet connection.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)
        val dialogBinding = DialogCustomProgressBinding.inflate(layoutInflater)
        mProgressDialog!!.setContentView(dialogBinding.root)
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    private fun setupUi() {
        val weatherString = mSharedPreferences.getString(WEATHER_RESPONSE_DATA, "")

        if (!weatherString.isNullOrEmpty()) {
            val weatherResponse = Gson().fromJson(weatherString, WeatherResponse::class.java)
            val forecast = weatherResponse.forecastTimestamps[0]
            val place = weatherResponse.place
            binding?.tvMain?.text = "${
                forecast.conditionCode.substring(0, 1).uppercase(Locale.getDefault())
            }${forecast.conditionCode.substring(1)}"
            binding?.tvTemp?.text = forecast.relativeHumidity.toString()
            binding?.tvMin?.text = forecast.airTemperature.toString()
            binding?.tvSpeed?.text = forecast.windSpeed.toString()
            binding?.tvName?.text = place.name
            binding?.tvCountry?.text = place.country
        }
    }
}