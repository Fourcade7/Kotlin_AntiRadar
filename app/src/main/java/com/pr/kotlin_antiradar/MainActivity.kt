package com.pr.kotlin_antiradar

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(),OnMapReadyCallback {
    //only last loocation
    lateinit var fusedLocationClient: FusedLocationProviderClient
    //location updates
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    lateinit var locationCallback: LocationCallback
    val REQUEST_CHECK_SETTINGS = 10001;
    lateinit var googleMap: GoogleMap
    val arraylist=ArrayList<MyLocatioin>()

    var lat: Double? = 40.0
    var lon: Double? = 60.0

    var lat2: Double? = 40.0
    var lon2: Double? = 60.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.googlemap) as SupportMapFragment
        mapFragment.getMapAsync(this)

        lastlocation()
        buildLocationRequest()
        startLocationUpdates()

        arraylist.add(MyLocatioin(41.5073230,60.4802440))
        arraylist.add(MyLocatioin(41.5193550,60.5510650))
        arraylist.add(MyLocatioin(41.5229400,60.5628200))
        arraylist.add(MyLocatioin(41.5329430,60.5955920))
        arraylist.add(MyLocatioin(41.5420680,60.6054800))
        arraylist.add(MyLocatioin(41.5581680,60.5974010))
        arraylist.add(MyLocatioin(41.5780330,60.5739930))


    }



    fun lastlocation() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                Toast.makeText(
                    this@MainActivity,
                    "${it.latitude} / ${it.longitude}",
                    Toast.LENGTH_LONG
                ).show()

//                lat=it.latitude
//                lon=it.longitude


            }
        }
    }

    //location updates
    fun buildLocationRequest() {
        locationRequest = com.google.android.gms.location.LocationRequest.create()
        locationRequest.priority =
            com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 5000
    }

    //location updates
    fun startLocationUpdates() {

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return

                for (location in p0.locations) {
                    lat = location.latitude
                    lon = location.longitude
                    onMapReady(googleMap)
                    // Log.d("Pr", "${location.latitude} / ${location.longitude}")
                      Toast.makeText(this@MainActivity, " ${location.latitude} / ${location.longitude}", Toast.LENGTH_SHORT).show()
                }

            }
        }
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap=p0
        googleMap.clear()

        var lastlocation = LatLng(lat!!, lon!!) //Beruniy
        var markerOptions = MarkerOptions()
                    .position(lastlocation)
                    .title("Traffic Light")
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.qizil2))
        googleMap.addMarker(markerOptions)
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastlocation, 15f))

        for (i in 0..arraylist.size-1){
            var lastlocation2 = readlatlang(i)
            var markerOptions2 = MarkerOptions()
                .position(lastlocation2)
                .title("Radar $i")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.radarformap))
            googleMap.addMarker(markerOptions2)
        }



    }

    fun readlatlang(i:Int):LatLng{
        lat2=arraylist.get(i).lat
        lon2=arraylist.get(i).lon
        return LatLng(lat2!!,lon2!!)
    }
}