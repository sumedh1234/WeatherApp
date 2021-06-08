package com.example.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {

    private lateinit var mfusedlocation:FusedLocationProviderClient
    private var myRequestCode = 1100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        mfusedlocation = LocationServices.getFusedLocationProviderClient(this)

        getLastLocation()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermission()){
            if (locationEnable()){
                mfusedlocation.lastLocation.addOnCompleteListener{
                    task->
                    val location: Location?=task.result
                        if (location == null){
                            newLocation()
                        }else{

                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = Intent(this,MainActivity::class.java)
                                intent.putExtra("lat",location.latitude.toString())
                                intent.putExtra("long",location.longitude.toString())
                                startActivity(intent)
                            finish()
                            }, 2000)
                        }
                }
            }else{
                Toast.makeText(this, "Turn on location and restart the app!", Toast.LENGTH_LONG).show()
            }

        }else{
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun newLocation() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        mfusedlocation = LocationServices.getFusedLocationProviderClient(this)
        mfusedlocation.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(p0 : LocationResult){
            var lastLocation: Location = p0.lastLocation
        }
    }

    private fun locationEnable(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),myRequestCode)
    }

    private fun checkPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == myRequestCode){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getLastLocation()
            }
        }
    }
}