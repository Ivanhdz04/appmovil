package com.uttab.aplicacionbien

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.uttab.aplicacionbien.databinding.ActivitySeleccionarUbicacionBinding

class SeleccionarUbicacion : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var bindig : ActivitySeleccionarUbicacionBinding

    private companion object{
        private const val DEFAULT_ZOOM = 15
    }

    private var mMap : GoogleMap?=null

    private var mPlaceClient : PlacesClient?=null
    private var mFusedLocationProviderClient : FusedLocationProviderClient?=null

    private var mLastKnowLocation : Location?=null
    private var selectedLatitude : Double?=null
    private var selectedLongitude : Double?=null
    private var  direccion = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindig = ActivitySeleccionarUbicacionBinding.inflate(layoutInflater)
        setContentView(bindig.root)

        bindig.listLl.visibility = View.GONE

        val mapFragment = supportFragmentManager.findFragmentById(R.id.MapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        Places.initialize(this, getString(R.string.mi_google_maps_api_key))

        mPlaceClient = Places.createClient(this )
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val autoCompletSupportMapFragment = supportFragmentManager.findFragmentById(R.id.autocompletar_fragment)
        as AutocompleteSupportFragment

        val placeList = arrayOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG)

        autoCompletSupportMapFragment.setPlaceFields(listOf(*placeList))

        autoCompletSupportMapFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener{
            override fun onPlaceSelected(place: Place) {
                val id = place.id
                val name = place.name
                val latlng = place.latLng

                selectedLatitude = latlng?.latitude
                selectedLongitude = latlng?.longitude
                direccion = place.address?: ""

                agregarMarcador(latlng, name, direccion)
            }
            override fun onError(p0: Status) {
                Toast.makeText(applicationContext, "Busqueda cancelada", Toast.LENGTH_SHORT).show()
            }

        })
        bindig.IbGps.setOnClickListener{
            if (esGpsActivado()){
                solicitarPermisoLocacion.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }else{
                Toast.makeText(this, "!La ubicacion no esta activada¡. Activalo para mostrara la ubicacion actual", Toast.LENGTH_SHORT).show()
            }
        }
        bindig.BtnListo.setOnClickListener {
            val intent = Intent()
            intent.putExtra("latitud", selectedLatitude)
            intent.putExtra("longitud", selectedLongitude)
            intent.putExtra("direccion", direccion)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun elegirLugarActual(){
        if (mMap == null){
            return
        }
        detectAndShowDeviceLocationMap()
    }

    @SuppressLint("MissingPermission")
    private fun detectAndShowDeviceLocationMap(){
        try {
            val locationResult = mFusedLocationProviderClient!!.lastLocation
            locationResult.addOnSuccessListener {location->
                if(location!=null){
                    mLastKnowLocation = location

                    selectedLatitude =location.latitude
                    selectedLongitude = location.longitude

                    val latLng = LatLng(selectedLatitude!!, selectedLongitude!!)
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM.toFloat()))
                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM.toFloat()))

                    direccionLatLgn(latLng)
                }

            }.addOnSuccessListener {e->

            }
        }catch (e:Exception){

        }
    }

    private fun esGpsActivado(): Boolean {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnable = false
        var networkEnable = false
        try {
            gpsEnable = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }catch (e:Exception){

        }
        try {
            networkEnable = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }catch (e:Exception){

        }

        return !(!gpsEnable && !networkEnable)

    }
    @SuppressLint("MissingPermission")
    private val solicitarPermisoLocacion : ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){seConcede->
            if(seConcede){
                mMap!!.isMyLocationEnabled =true
                elegirLugarActual()
            }else{
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()

            }

        }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        solicitarPermisoLocacion.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        mMap!!.setOnMapClickListener {latlng->
            selectedLatitude = latlng.latitude
            selectedLongitude = latlng.longitude

            direccionLatLgn(latlng)

        }
    }

    private fun direccionLatLgn(latlng: LatLng) {
        val geoCoder = Geocoder(this)
        try {
            val addresList = geoCoder.getFromLocation(latlng.latitude, latlng.longitude, 1)
            val address = addresList!![0]
            val addressLine = address.getAddressLine(0)
            val subLocality = address.subLocality
            direccion= "$addressLine"
            agregarMarcador(latlng, "$subLocality", "$addressLine")
        }catch (e:Exception){

        }
    }
    private fun agregarMarcador(latlng: LatLng, titulo : String, direccion : String){
         mMap!!.clear()
        try {
            val markerOptions = MarkerOptions()
            markerOptions.position(latlng)
            markerOptions.title("$titulo")
            markerOptions.snippet("$direccion")
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

            mMap!!.addMarker(markerOptions)
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, DEFAULT_ZOOM.toFloat()))


            bindig.listLl.visibility = View.VISIBLE
            bindig.lugarSelecTv.text = direccion
        }catch (e:Exception){

        }
    }
}