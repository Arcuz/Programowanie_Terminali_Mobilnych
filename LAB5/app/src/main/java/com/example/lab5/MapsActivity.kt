package com.example.lab5

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import com.example.lab5.databinding.ActivityMapsBinding
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLoadedCallback,
    GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {
    private lateinit var layoutBinding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val MY_PERMISSION_REQUEST_ACCESS_FINELOCATION = 101
    var gpsMarker: Marker? = null
    private val markerList: ArrayList<Marker> = ArrayList()
    var totalDistance = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(layoutBinding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),MY_PERMISSION_REQUEST_ACCESS_FINELOCATION)
            return
        }
        layoutBinding.clearButton.setOnClickListener{
            markerList.clear()
            mMap.clear()
            totalDistance = 0f
            Snackbar.make(layoutBinding.root,getString(R.string.map_clear), Snackbar.LENGTH_LONG).show()
            showLastLocationMarker()
            mMap.moveCamera(CameraUpdateFactory.zoomTo(2f))
            layoutBinding.distanceInfo.text = String.format(getString(R.string.distance_info_format),totalDistance)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLoadedCallback(this)
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMapLongClickListener(this)
        mMap.uiSettings.isMapToolbarEnabled = false
    }

    fun zoomInClick(view: View) {
        mMap.moveCamera(CameraUpdateFactory.zoomIn())
    }
    fun zoomOutClick(view: View) {
        mMap.moveCamera(CameraUpdateFactory.zoomOut())
    }
    fun moveToMyLocation(view: View) {
        gpsMarker ?: return
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(gpsMarker?.position,12f))
    }

    private fun createLocationRequest(){
        mLocationRequest = LocationRequest()
        with(mLocationRequest){
            setInterval(10000)
            setFastestInterval(5000)
            setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        }
    }

    private fun createLocationCallback(){
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                gpsMarker ?.remove()
                gpsMarker = mMap.addMarker(with(MarkerOptions()){
                    position(LatLng(locationResult.lastLocation.latitude!!,locationResult.lastLocation.longitude!!))
                    icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    alpha((0.8f))
                    title(getString(R.string.my_location_msg))
                })
            }
        }
    }

    private fun startLocationUpdates(){

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) fusedLocationProviderClient.requestLocationUpdates(mLocationRequest,locationCallback,null)
    }

    override fun onResume() {
        super.onResume()
        createLocationCallback()
        createLocationRequest()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == MY_PERMISSION_REQUEST_ACCESS_FINELOCATION){
            val indexOf = permissions.indexOf(Manifest.permission.ACCESS_FINE_LOCATION)
            if(indexOf != -1 && grantResults[indexOf] != PackageManager.PERMISSION_GRANTED){
                Snackbar.make(layoutBinding.root, getString(R.string.permission_msg), Snackbar.LENGTH_LONG).setAction(getString(R.string.retry_msg)){
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),MY_PERMISSION_REQUEST_ACCESS_FINELOCATION)
                }.show()
            }
        }
    }
    override fun onMapLoaded() {
        showLastLocationMarker()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.normalMap -> mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybridMap -> mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satelliteMap -> mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrainMap -> mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLastLocationMarker() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                location ?: return@addOnSuccessListener

                mMap.addMarker(with(MarkerOptions()) {
                    position(LatLng(location!!.latitude, location!!.longitude))
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    title(getString(R.string.last_location_msg))
                })

            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        if(mMap.cameraPosition.zoom < 14f){
            mMap.moveCamera(CameraUpdateFactory.zoomTo(14f))
        }

        return false
    }

    override fun onMapLongClick(latLng: LatLng) {
        var distance = 0f
        if(markerList.size > 0){
            val lastMarker = markerList.get(markerList.size - 1)
            val results = FloatArray(3)
            Location.distanceBetween(lastMarker.position.latitude,lastMarker.position.longitude,latLng.latitude,latLng.longitude,results)
            distance = results[0]
            totalDistance += distance/1000f
            val rectOptions = with(PolylineOptions()){
                add(lastMarker.position)
                add(latLng)
                width(10f)
                color(Color.CYAN)
            }
            mMap.addPolyline((rectOptions))
        }
        val Marker = mMap.addMarker(with(MarkerOptions()){
            position(LatLng(latLng.latitude!!,latLng.longitude!!))
            icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2))
            alpha((0.8f))
            title(String.format(getString(R.string.marker_info_format),latLng.latitude,latLng.longitude,distance))
        })
        markerList.add(Marker)
        layoutBinding.distanceInfo.text = String.format(getString(R.string.distance_info_format), totalDistance)
    }
}