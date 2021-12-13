package com.developers.shopapp.ui.fragments.maps

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.developers.shopapp.databinding.FragmentMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import dagger.hilt.android.AndroidEntryPoint
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.developers.shopapp.R
import com.developers.shopapp.data.local.DataStoreManager
import com.developers.shopapp.entities.Restaurant
import com.developers.shopapp.entities.UserInfoDB
import com.developers.shopapp.utils.Constants.TAG
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import javax.inject.Inject
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MapsFragment : Fragment() {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var dataStoreManager: DataStoreManager
    private val args:MapsFragmentArgs by navArgs()
    private val callback = OnMapReadyCallback { googleMap ->

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val dataUserInfo = dataStoreManager.glucoseFlow.value
        val currentRestaurant=  args.restaurant

     // setupStyleForMap(googleMap)
     setupMarkerRestaurantAndUserLocation(googleMap,dataUserInfo,currentRestaurant)
    }

    private fun setupMarkerRestaurantAndUserLocation(
        googleMap: GoogleMap,
        dataUserInfo: UserInfoDB?,
        currentRestaurant: Restaurant
    ) {


        val restaurantLocation = LatLng(currentRestaurant.latitude, currentRestaurant.longitude)
        val userLocation=dataUserInfo!!.latLng



        val userCameraPosition=buildCameraPosition(userLocation)
        val restaurantCameraPosition=buildCameraPosition(userLocation)
        googleMap.addMarker(MarkerOptions().position(dataUserInfo.latLng).title("You")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.userlocation)))

             addCircle(googleMap,dataUserInfo.latLng)
        googleMap.addMarker(MarkerOptions().position(restaurantLocation).title("${currentRestaurant.restaurantName}")
            .snippet(currentRestaurant.restaurantType)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurantlocation)))
        addCircle(googleMap,restaurantLocation)
        addPolyLine(googleMap,userLocation,restaurantLocation)
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(userCameraPosition))
    }

    private fun addPolyLine(googleMap: GoogleMap, userLocation: LatLng, restaurantLocation: LatLng) {
       googleMap.addPolyline(PolylineOptions()
           .add(userLocation).add(restaurantLocation)
           .color(R.color.colorPrimaryDark).width(5f))
    }

    private fun addCircle(map: GoogleMap, latLng: LatLng) {
        val circle: Circle = map.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(400.0)
                .strokeColor(Color.BLUE)
                .strokeWidth(5f)
                .fillColor(R.color.blueLight)
        )
    }

    private fun buildCameraPosition(latLng: LatLng) =
      CameraPosition.Builder()
            .target(latLng) // Sets the center of the map to Mountain View
            .zoom(13f) // Sets the zoom
            .bearing(90f) // Sets the orientation of the camera to east
            .tilt(30f) // Sets the tilt of the camera to 30 degrees
            .build()



    private fun setupStyleForMap(googleMap: GoogleMap) {
        lifecycleScope.launch {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireContext(), R.raw.style_map_json
                    )
                )
                if (!success) {
                    Log.e(TAG, "Style parsing failed.")
                }
            } catch (e: Resources.NotFoundException) {
                Log.e(TAG, "Can't find style. Error: ", e)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentMapsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}