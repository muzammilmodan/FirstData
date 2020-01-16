package com.googlemapwithdrawpathapp.activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.QuickHelp.AppUtils.LocationNode
import com.QuickHelp.AppUtils.User_Constant
import com.QuickHelpVendor.Utils.Applog
import com.QuickHelpVendor.Utils.ConnectivityDetector
import com.QuickHelpVendor.Utils.KeyboardUtility
import com.QuickHelpVendor.Utils.SnackBar
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.googlemapwithdrawpathapp.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.googlemapwithdrawpathapp.Model.Track_Constant
import com.googlemapwithdrawpathapp.polilineAnimator.MapHttpConnection
import com.googlemapwithdrawpathapp.polilineAnimator.PathJSONParser
import org.json.JSONObject
import java.text.DecimalFormat


class LocationToFromActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks, LocationListener,
    GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnInfoWindowClickListener {

    //Todo: set values
    var mGoogleApiClient: GoogleApiClient? = null
    var mFragment: MapView? = null
    var context: Context? = null
    var snackBarView: View? = null
    private var savedInstanceState: Bundle? = null
    private var lastLocation: Location? = null
    private var mGoogleMap: GoogleMap? = null

    private val mapViewVisible: Boolean = false
    private var locationRequest: LocationRequest? = null

    private val FASTEST_INTERVAL = 900
    private val UPDATE_INTERVAL = 901
    private val MY_PERMISSIONS_REQUEST_ACCOUNTS = 3

    //Set Data
    internal var flag = false
    internal var node: String? = null

    var senderId: String = ""
    var reciverId: String = ""
    var track_node:String = ""

    private var mDatabase: DatabaseReference? = null
    var chatNode: LocationNode? = null
    var user_pref: SharedPreferences? = null
    var chatHistory: ArrayList<LocationNode> = ArrayList()
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    private var currentLat: String? = null
    private var currentLong: String? = null

    var receiverLat: Double = 23.013054
    var receiverLng: Double = 72.562515

    var currentPoint: LatLng? = null
    var receiverPoint: LatLng? = null

    private var mLatLng: LatLng? = null

    var value: String = ""
    var senderid: String = ""
    var receiverid: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_to_from)

        context = LocationToFromActivity@ this
        snackBarView = findViewById(android.R.id.content)

        //Todo: Get FCM Token
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("LocationToFrom", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                val token = task.result?.token
                Log.d("LocationToFrom", token)
                Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })

        mFragment = findViewById(R.id.mgooleMap)
        //Todo: Check internet connection available or not
        if (ConnectivityDetector.isConnectingToInternet(context!!)) {
            if (Build.VERSION.SDK_INT < 23) {
                initilizeMap()
                setUpGoogleApiClient()
            } else {
                if (checkAndRequestPermissions()) {
                    initilizeMap()
                    setUpGoogleApiClient()
                }
            }

        } else {
            SnackBar.showInternetError(context!!, snackBarView!!)
        }

        initMap()
        setData()
    }

//Todo: FCM Storage and get FCM data........................ Start
    private fun setData() {
        var chatmsg: HashMap<String, String>? = null
        val type = "text"

        chatmsg = createTrackNode(type, "")

        val database = FirebaseDatabase.getInstance()
        val databaseReference = database.getReference("Tracking")
        if (chatmsg != null && track_node != "")
            databaseReference.child(track_node).setValue(chatmsg)
    }

    private fun createTrackNode(type: String, sendPath: String): HashMap<String, String> {
        val hashMap = HashMap<String, String>()
        hashMap.put(Track_Constant.sender_lat, chatNode!!.getSenderLat())
        hashMap.put(Track_Constant.sender_lng, chatNode!!.getSenderLng())

        return hashMap
    }


    private fun initMap() {

        chatNode = LocationNode()
        track_node = track_node + ""
        mDatabase = FirebaseDatabase.getInstance().getReference("Tracking")
        chatHistory = ArrayList()
        mDatabase!!.addValueEventListener(valueEventListener())
    }

    internal fun valueEventListener(): ValueEventListener {
        return object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try {

                    if (dataSnapshot.hasChildren()) {

                        var messageChildren: Iterable<DataSnapshot> = dataSnapshot.child(track_node).getChildren()
                        Log.d("firebase", "" + track_node)
                        Log.d("firebase", dataSnapshot.child(track_node).toString())
                        Log.d("firebase", "" + dataSnapshot.child(track_node).childrenCount)

                        Log.d("firebase::: Key", "=====>" + dataSnapshot.child(track_node).key)
                        Log.d("firebase::: vales", "=====>" + dataSnapshot.child(track_node).value)


                        chatHistory.clear()
                        for (message: DataSnapshot in messageChildren) {
                            var senderLat = ""
                            var senderLng = ""

                            val temp = message.value!!.toString()
                            val valuesTitle = message.key!!.toString()

                            Log.d("K---====>", temp + "T---====>" + valuesTitle)

                            val stringArray: Array<String>
                            //stringArray = temp.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            stringArray = temp.split(",").toTypedArray()

                            for (valueS in stringArray) {
                                value = valueS.replace("{", "")
                                value = valueS.replace("}", "")

                                Log.e("values Data key::::", "---->" + valueS)
                                Log.e("values Data value::::", "---->" + value)

                                if (valuesTitle.contains(Track_Constant.sender_lat))
                                    senderLat = value.substring(value.indexOf("=") + 1)
                                if (valuesTitle.contains(Track_Constant.sender_lng))
                                    senderLng = value.substring(value.indexOf("=") + 1)

                            }
                            Log.d("Tracking", temp)
                            Log.d("senderLat---====>", senderLat)
                            Log.d("senderLng---====>", senderLng)
                            chatHistory.add(LocationNode(senderLat, senderLng))
                        }
                        Log.d("chathistory", chatHistory.toString())
                        if (chatHistory.size > 0) {
                            var latC = chatHistory.get(0).getSenderLat()
                            var lngC = chatHistory.get(1).getSenderLng()

                            Applog.E("get lat and lng:::" + latC + "===" + lngC)
                            currentPoint = LatLng(latC.toDouble(), lngC.toDouble())
                            receiverPoint = LatLng(receiverLat!!, receiverLng!!)
                            createRoute(receiverPoint, currentPoint)

                            bindData()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
    }



    private fun bindData() {
        try {
            senderid = chatNode!!.getSenderid()
            receiverid = chatNode!!.getReceiverid()

            receiverPoint =
                LatLng(chatHistory.get(0).getReceiverLat().toDouble(), chatHistory.get(0).getReceiverLng().toDouble())
            currentPoint =
                LatLng(chatHistory.get(0).getSenderLat().toDouble(), chatHistory.get(0).getSenderLng().toDouble())

            if ((user_pref!!.getString(User_Constant.id, "")!! + "-").contains(track_node)) {
            } else {
                //  ((TextView) findViewById(R.id.txt_receiver_name)).setText(chatNode.getReceiver_name());
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

//Todo: FCM Storage and get FCM data........................ End


    //Todo:First of all Initilize map using map view fragment
    fun initilizeMap() {
        mFragment!!.onCreate(savedInstanceState)
        mFragment!!.getMapAsync(this)
    }

    //Todo: Google api client initialized with places api ,Without initialized google map not load
    private fun setUpGoogleApiClient() {
        try {
            if (mGoogleApiClient == null || !mGoogleApiClient!!.isConnected()) {
                mGoogleApiClient = GoogleApiClient.Builder(LocationToFromActivity@ this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .enableAutoManage(LocationToFromActivity@ this, /*0 *//* clientId *//*,*/ this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(LocationServices.API)
                    .build()

                mGoogleApiClient!!.connect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Create rout and draw 

    //Todo: Check run time permissions
    private fun checkAndRequestPermissions(): Boolean {
        val AccessFineLocation =
            ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_FINE_LOCATION)
        val AccessCorasLocation =
            ContextCompat.checkSelfPermission(context!!, Manifest.permission.ACCESS_COARSE_LOCATION)


        val listPermissionsNeeded = ArrayList<String>()
        if (AccessFineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (AccessCorasLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                LocationToFromActivity@ this,
                listPermissionsNeeded.toTypedArray(), MY_PERMISSIONS_REQUEST_ACCOUNTS
            )
            return false
        }
        return true
    }

    override fun onConnected(p0: Bundle?) {
        try {
            getLastKnownLocation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Todo: After connected google map : Get LasKnowLocation
    private fun getLastKnownLocation() {
        try {
            if (checkPermission()) {
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
                if (lastLocation != null) {

                    writeLastLocation()
                    startLocationUpdates()

                    val lat = lastLocation!!.getLatitude()
                    val lng = lastLocation!!.getLongitude()

                    currentLat = lat.toString()
                    currentLong = lng.toString()
                    setInfoWindowData()


                    //Todo: MM set for testing
                    currentPoint = LatLng(lastLocation!!.getLatitude(), lastLocation!!.getLongitude())
                    receiverPoint = LatLng(receiverLat!!, receiverLng!!)
                    createRoute(receiverPoint, currentPoint)

                } else {
                    Log.w("TrackingFragment", "No location retrieved yet")
                }
            } else
                askPermission()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Todo: start location update continue...........

    val REQUEST_LOCATION = 1520
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        try {
            locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL.toLong())
                .setFastestInterval(FASTEST_INTERVAL.toLong())

            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest!!)
            builder.setAlwaysShow(true)

            val result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
            result.setResultCallback(object : ResultCallback<LocationSettingsResult> {
                override fun onResult(result: LocationSettingsResult) {
                    val status = result.status
                    when (status.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                            status.startResolutionForResult(
                                this@LocationToFromActivity,
                                REQUEST_LOCATION
                            )

                            finish()
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }

                    }
                }
            })

            if (checkAndRequestPermissions()) {
                //  LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this)
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun askPermission() {
        try {
            ActivityCompat.requestPermissions(
                LocationToFromActivity@ this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_ACCOUNTS
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            LocationToFromActivity@ this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun writeLastLocation() {
        try {
            writeActualLocation(this.lastLocation!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //Todo: set information google map clicked open information window
    private fun setInfoWindowData() {
        mGoogleMap!!.setInfoWindowAdapter(MarkerInfoWindowAdapter())
        mGoogleMap!!.setOnInfoWindowClickListener(this)
        val ll = LatLng(
            java.lang.Double.parseDouble(currentLat!!),
            java.lang.Double.parseDouble(currentLong!!)
        )
        var marker = mGoogleMap!!.addMarker(
            MarkerOptions().position(ll).title("Title").snippet("Snippet").icon(
                BitmapDescriptorFactory.defaultMarker()
            )
        )
        marker.showInfoWindow();

        val builder = LatLngBounds.Builder()
    }


    inner class MarkerInfoWindowAdapter : GoogleMap.InfoWindowAdapter {
        private var title: String? = null
        private var bath: String? = null

        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

        override fun getInfoContents(marker: Marker): View {
            val v = layoutInflater.inflate(R.layout.row_info_window, null)
            try {

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return v
        }
    }


    //Todo: Actual get Location thru set camera in Google map.
    private fun writeActualLocation(location: Location) {
        try {
            val cameraPosition =
                CameraPosition.Builder().target(LatLng(location.latitude, location.longitude))
                    .bearing(360f).zoom(18f)
                    .tilt(40f).build()
            mGoogleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onConnectionSuspended(p0: Int) {
    }

    //Create marker
    protected fun createMarker(
        latitude: Double,
        longitude: Double,
        title: String,
        snippet: String
    ): Marker {

        return mGoogleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_gry))
        )


    }


    //Todo: on location changed get location(lat and lng)
    override fun onLocationChanged(location: Location?) {
        if (track_node == "blank") {
            //chat_node = createnode()
            receiverPoint = LatLng(receiverLat!!, receiverLng!!)
            createPropertyMarker(receiverPoint!!.latitude, receiverPoint!!.longitude, "Title", "Property")


        }
    }

    private fun createPropertyMarker(latitude: Double, longitude: Double, title: String, snippet: String): Marker? {
        return mGoogleMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_gry))
        )
    }


    private fun createRoute(propertyPoint: LatLng?, vendorPoint: LatLng?) {

        try {
            val url = getMapsApiDirectionsUrl(propertyPoint!!, vendorPoint!!)
//        String url = getMapsApiDirectionsUrl(new LatLng(23.0100678, 72.5606467), new LatLng(23.5316786, 72.3735223));
            val downloadTask = ReadTask()
            // Start downloading json data from Google Directions API
            downloadTask.execute(url)

            Log.e("===========> ", "" + url)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private inner class ReadTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg url: String): String {
            // TODO Auto-generated method stub
            var data = ""
            try {
                val http = MapHttpConnection()
                data = http.readUrl(url[0])

            } catch (e: Exception) {
                // TODO: handle exception
                Log.d("Background Task", e.toString())
            }

            return data
        }

        public override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) {
                ParserTask().execute(result)
            } else {
                Log.e("TAG", "Didn't get response")
            }

        }
    }

    var duration: String = ""
    var duration1: String = ""
    var dist: Double = 0.0

    inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {

        var distance: String = ""
        var timeFormat: String? = null
        private var routeList: MutableList<LatLng>? = null
        var mapZoomLevel: Int = 0
        var statusPickuplocationRout = 0

        override fun doInBackground(
            vararg jsonData: String
        ): List<List<HashMap<String, String>>> {

            // TODO Auto-generated method stub
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {

                if(jsonData.size>0) {
                    jObject = JSONObject(jsonData[0])
                    val parser = PathJSONParser()
                    routes = parser.parse(jObject)

                    duration = parser.getDuration(jObject)!!
                    //                if (distance.equals(""))
                    //   distance = parser.getDistance()
                    var separated: Array<String>? = null
                    if (!duration.isEmpty()) {
                        separated = duration.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                    }
                    // this will contain "Fruit"
                    duration1 = separated!![0]
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return routes!!
        }

        override fun onPostExecute(routes: List<List<HashMap<String, String>>>) {
            //            ArrayList<LatLng> points = null;
            var polyLineOptions: PolylineOptions? = null

            try {
                if (routes.size > 0) {

                    // proDialog.hide();

                    for (i in routes.indices) {
                        routeList = ArrayList()
                        polyLineOptions = PolylineOptions()
                        val path = routes[i]

                        for (j in path.indices) {
                            val point = path[j]
                            //todo: MM
                            var lat: Double = 0.0
                            var lng: Double = 0.0
                            if (point["lat"] != null && point["lat"] != "") {
                                lat = java.lang.Double.parseDouble(point["lat"]!!)
                            } else {
                                lat = 0.0
                            }

                            if (point["lng"] != null && point["lng"] != "") {
                                lng = java.lang.Double.parseDouble(point["lng"]!!)
                            } else {
                                lng = 0.0
                            }
//todo: MM end
                            /* val lng = java.lang.Double.parseDouble(point["lng"])*/
                            val position = LatLng(lat, lng)
                            routeList!!.add(position)
                            Log.e("lattitude:", "=======>$lat")
                            Log.e("longitude:", "=======>$lng")
                        }


                        polyLineOptions.addAll(routeList!!)
                        polyLineOptions.width(3f)
                        polyLineOptions.color(context!!.resources.getColor(R.color.app_bg))
                    }

                    mGoogleMap!!.clear()
                    mGoogleMap!!.setPadding(10, 200, 10, 100)
                    mGoogleMap!!.addPolyline(polyLineOptions)
                    /*createMarker(
                        java.lang.Double.parseDouble(sourceLat),
                        java.lang.Double.parseDouble(sourceLng),
                        "",
                        ""
                    )
                    createMarker(java.lang.Double.parseDouble(destLat), java.lang.Double.parseDouble(destLng), "", "")
*/
                    //                    Toast.makeText(context, "Please wait rout draw few min", Toast.LENGTH_LONG).show();
                    val builder = LatLngBounds.Builder()
                    builder.include(receiverPoint)
                    builder.include(currentPoint)

                    createPropertyMarker(receiverPoint!!.latitude, receiverPoint!!.longitude, "Title", "Property")
                    createMarker(currentPoint!!.latitude, currentPoint!!.longitude, "Title", "Vendor")

                    val bounds = builder.build()

                    val width = context!!.resources.displayMetrics.widthPixels
                    val padding = (width * 0.2).toInt()

                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                    mGoogleMap!!.moveCamera(cameraUpdate)
                    mGoogleMap!!.animateCamera(cameraUpdate)

                    //dist = getDistance(receiverPoint.latitude, receiverPoint.longitude, currentPoint.latitude, currentPoint.longitude);
                    distance = distance.substring(0, distance.indexOf(" "))
                    //todo:: MM start
                    if (distance != null && distance != "") {
                        dist = java.lang.Double.parseDouble(distance)
                    } else {
                        dist = 0.0
                    }
                    //todo:: MM end
                    mGoogleMap!!.getUiSettings().setZoomControlsEnabled(false)

                    /*  distance = distance.trim();
                    if (distance.contains("km")) {
                        distance = distance.replace("km", "");
                        dist = Double.parseDouble(distance);
                    }
                    else of*/
                    //float tripCst = CalculateMile(String.valueOf(dist), durationOnlyMin);
                    //tripCost = Float.parseFloat(new DecimalFormat("##.##").format(Double.valueOf(tripCst)));
/*
                    if (dist > 2 && dist <= 5) {
                        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(13.0f))
                        mapZoomLevel = 12
                    } else if (dist > 5 && dist <= 10) {
                        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(12.0f))
                        mapZoomLevel = 11
                    } else if (dist > 10 && dist <= 20) {
                        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(11.0f))
                        mapZoomLevel = 11
                    } else if (dist > 20 && dist <= 40) {
                        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(10.0f))
                        mapZoomLevel = 10
                    } else if (dist > 40 && dist < 100) {
                        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(9.0f))
                        mapZoomLevel = 9
                    } else if (dist > 100 && dist < 200) {
                        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(8.0f))
                        mapZoomLevel = 8
                    } else if (dist > 200 && dist < 400) {
                        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(7.0f))
                        mapZoomLevel = 7
                    } else if (dist > 400 && dist < 700) {
                        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(6.0f))
                        mapZoomLevel = 7
                    } else if (dist > 700 && dist < 1000) {
                        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(5.0f))
                        mapZoomLevel = 6
                    } else if (dist > 1000) {
                        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(4.0f))
                        mapZoomLevel = 5
                    } else {
                        mGoogleMap!!.animateCamera(CameraUpdateFactory.zoomTo(14.0f))
                        mapZoomLevel = 14
                    }*/

                    val df = DecimalFormat()
                    df.maximumFractionDigits = 2


                } else {
                    //   proDialog.hide();
                    if (statusPickuplocationRout == 1) {
                        //  SnackBar.showError(context, snackbarView, "No route found");
                    } else {

                    }
                    Log.e("TAG", "No route found")
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun onPreExecute() {
            super.onPreExecute()

        }
    }

    private fun getMapsApiDirectionsUrl(origin: LatLng, dest: LatLng): String {
        // Origin of route
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude

        // Destination of route
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude


        // Sensor enabled
        val sensor = "sensor=false"
        val filter = "units=imperial"
        // Building the parameters to the web service
        //sapan
        val mode = "mode=driving"
        val key = "key=" + context!!.resources.getString(R.string.google_map_key)


        //sapan add mode and key
        val parameters = "$str_origin&$str_dest&$sensor&$filter&$mode&$key"
        //        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + filter;
        // Building the parameters to the web service
        // String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        val output = "json"

        // Building the url to the web service


        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"


    }

//Location changed
    override fun onMapReady(googleMap: GoogleMap?) {
        try {
            mGoogleMap = googleMap
            val mapStyle = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json)
            mGoogleMap!!.setMapStyle(mapStyle)
            googleMap!!.setOnCameraChangeListener(GoogleMap.OnCameraChangeListener { cameraPosition ->
                mLatLng = cameraPosition.target
                if (mapViewVisible == true) {
                    try {
                        KeyboardUtility.hideKeyboard(context!!, mFragment)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onInfoWindowClick(p0: Marker?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            mFragment!!.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onLowMemory() {
        super.onLowMemory()
        try {
            mFragment!!.onLowMemory()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        mFragment!!.onResume()
    }

    override fun onPause() {
        super.onPause()

        try {
            stopLocationUpdates()
            mFragment!!.onPause()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    protected fun stopLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this
            )

            Log.d("trackingFragment", "Location update stopped .......................")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onStart() {
        super.onStart()
        try {
            setUpGoogleApiClient()
            mGoogleApiClient!!.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onStop() {
        super.onStop()
        try {
            if (!mGoogleApiClient!!.isConnected()) {
                mGoogleApiClient!!.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
