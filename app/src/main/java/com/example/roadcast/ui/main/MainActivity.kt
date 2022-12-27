package com.example.roadcast.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roadcast.base.BaseActivity
import com.example.roadcast.databinding.ActivityMainBinding
import com.example.roadcast.ui.location.LocationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    companion object {
        val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MainAdapter

    private val viewModel by viewModel<MainActivityViewModel>()

    private lateinit var permission: ActivityResultLauncher<Array<String>>

    override fun initView(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initData() {
        adapter = MainAdapter()

        binding.rcvEntries.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.rcvEntries.adapter = adapter

        viewModel.getEntries()

        viewModel.response.observe(this) { onResponse(it) }
        viewModel.loading.observe(this) { onLoading(it) }
        viewModel.error.observe(this) { updateUi(it) }

        permission = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                goToLocationActivity()
            } else {
                Toast.makeText(this, "Location Permission in Required", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    override fun initListener() {
        binding.btLocation.setOnClickListener {
            permissionCheck()
        }
    }

    private fun onResponse(baseResponse: BaseResponse) {
        if (baseResponse.entries.isEmpty()) {
            updateUi("No Entry Found")
        } else {
            adapter.submitList(baseResponse.entries)
            updateUi(null)
        }
    }

    private fun onLoading(it: Boolean) {
        if (it) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun updateUi(it: String?) {
        if (it == null) {
            binding.rcvEntries.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        } else {
            binding.rcvEntries.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun permissionCheck() {
        if (checkForPermission()) {
            goToLocationActivity()
        } else {
            requestMandatoryPermission()
        }
    }

    private fun checkForPermission(): Boolean {
        for (per in PERMISSIONS) {
            if (!permissionGranted(per)) {
                return false
            }
        }
        return true
    }

    private fun permissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestMandatoryPermission() {
        permission.launch(PERMISSIONS)
    }

    private fun goToLocationActivity() {
        moveToNext(LocationActivity::class.java)
    }
}