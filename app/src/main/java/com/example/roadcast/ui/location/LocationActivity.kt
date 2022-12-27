package com.example.roadcast.ui.location

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.example.roadcast.R
import com.example.roadcast.base.BaseActivity
import com.example.roadcast.databinding.ActivityLocationBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng

class LocationActivity : BaseActivity() {

    companion object {
        const val LOCATION_UPDATE_INTERVAL = 2000L
    }

    private lateinit var binding: ActivityLocationBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val location = MutableLiveData<LatLng?>()

    override fun initView(savedInstanceState: Bundle?) {
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initData() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        location.value = null

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        ).build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            onBackPress()
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        location.observe(this) {
            if (it != null) {
                binding.tvUpdate.visibility = View.GONE
                binding.tvLatitude.visibility = View.VISIBLE
                binding.tvLongitude.visibility = View.VISIBLE
                binding.tvLatitude.text = getString(R.string.latitude, it.latitude)
                binding.tvLongitude.text =
                    getString(R.string.longitude, it.longitude)
            } else {
                binding.tvUpdate.visibility = View.VISIBLE
                binding.tvLatitude.visibility = View.GONE
                binding.tvLongitude.visibility = View.GONE
            }
        }
    }

    override fun initListener() {

        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            this.registerReceiver(null, filter)
        }

        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.lastLocation?.let {
                location.value = LatLng(it.latitude, it.longitude)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}