package com.example.lab_week_07

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.lab_week_07.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    // Launcher untuk meminta permission dan menangani respon user
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Registrasi hasil permintaan permission
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Jika izin diberikan, jalankan fungsi lokasi
                    getLastLocation()
                } else {
                    // Jika ditolak, tampilkan rationale dialog
                    showPermissionRationale {
                        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                    }
                }
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Mengecek apakah user sudah memberikan izin lokasi
        when {
            hasLocationPermission() -> getLastLocation()

            // Jika user sebelumnya menolak izin, tampilkan rationale dialog
            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                showPermissionRationale {
                    requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                }
            }

            // Jika belum pernah diminta, langsung minta izin
            else -> requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    // Mengecek apakah permission lokasi sudah diberikan
    private fun hasLocationPermission() =
        ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    // Fungsi dummy untuk menandai lokasi terakhir (nanti di Part 2 akan diisi)
    private fun getLastLocation() {
        Log.d("MapsActivity", "getLastLocation() called.")
    }

    // Menampilkan dialog alasan kenapa aplikasi butuh izin lokasi
    private fun showPermissionRationale(positiveAction: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Location permission")
            .setMessage("This app will not work without knowing your current location")
            .setPositiveButton(android.R.string.ok) { _, _ -> positiveAction() }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
