package mapapp.ashhy.techm.mapapp;

import android.*;
import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,LocationListener{


    GoogleMap gmap;
    double latitude,longitude;
    boolean locationAvailable;
    boolean locationRequired;
    boolean currentMarker;
    private FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    Button locationButton;
    TextView lat,lang;
    Location lastLocation;
    private final int PERMISSION_LIST_REQUEST_CODE=123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationAvailable = false;
        locationRequired = true;
        currentMarker=false;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback=new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                lastLocation=locationResult.getLastLocation();
                for(Location location:locationResult.getLocations()){
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                    lat.setText("Latitude: "+String.valueOf(latitude));
                    lang.setText("Longitude: "+String.valueOf(longitude));
                }

                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();
                if(gmap!=null&&currentMarker==false){
                    gmap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Current Location"));
                    currentMarker=true;
                    CameraUpdate center=
                            CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude));
                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
                    gmap.moveCamera(center);
                    gmap.animateCamera(zoom);
                }
            }
        };

        lat=(TextView)findViewById(R.id.latitude);
        lang=(TextView)findViewById(R.id.longitude);
        locationButton=(Button)findViewById(R.id.locationbutton);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationButton.getText().toString().equals(getString(R.string.viewlocation))){
                    //SHOW LOCATION
                    YoYo.with(Techniques.FadeIn).duration(400).playOn(lat);
                    YoYo.with(Techniques.FadeIn).duration(400).playOn(lang);
                    lat.setVisibility(View.VISIBLE);
                    lang.setVisibility(View.VISIBLE);
                    lat.setText("Latitude: "+String.valueOf(latitude));
                    lang.setText("Longitude: "+String.valueOf(longitude));
                    YoYo.with(Techniques.Tada).duration(500).playOn(locationButton);
                    locationButton.setText(getString(R.string.hidelocation));
                }
                else{
                    //HIDE LOCATION
                    YoYo.with(Techniques.SlideOutUp).duration(400).playOn(lat);
                    YoYo.with(Techniques.SlideOutUp).duration(400).playOn(lang);
                    lat.setVisibility(View.GONE);
                    lang.setVisibility(View.GONE);
                    YoYo.with(Techniques.Tada).duration(500).playOn(locationButton);
                    locationButton.setText(getString(R.string.viewlocation));
                }
            }
        });


        SupportMapFragment mapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setAndCheckPermission();
        }
    }



    public void setAndCheckPermission(){
        int finelocation=ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarselocation=ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> plist=new ArrayList<String>();
        if(finelocation!=PackageManager.PERMISSION_GRANTED)plist.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if(coarselocation!=PackageManager.PERMISSION_GRANTED)plist.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if(!plist.isEmpty()){
            ActivityCompat.requestPermissions(this,plist.toArray
                    (new String[plist.size()]),PERMISSION_LIST_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length<=0)return;
        switch (requestCode) {
            case PERMISSION_LIST_REQUEST_CODE: {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED  &&grantResults[1]==PackageManager.PERMISSION_GRANTED){
                    Snackbar.make(findViewById(R.id.activity_main),"Thank you for granting Permission",Snackbar.LENGTH_LONG).show();

                } else {
                    Snackbar.make(findViewById(R.id.activity_main),"App may not work properly without Permission",Snackbar.LENGTH_LONG).show();
                }
                return;
            }

        }
    }


    public void addMarker(View view){
        if(gmap!=null&&latitude!=0&&longitude!=0){
            Log.d("Adding Marker","Maekrer");
            gmap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("Current Location"));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        LocationRequest locationRequest=setLocation();
        setLocationSettings(locationRequest);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(findViewById(R.id.activity_main), "Unable to Receive Location Permission ", Snackbar.LENGTH_LONG).show();
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,  locationCallback, null);
    }


    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private LocationRequest setLocation(){
        LocationRequest locationRequest=new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(10);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }


    private void setLocationSettings(final LocationRequest locationRequest){
        LocationSettingsRequest.Builder builder=new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient= LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task=settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                locationAvailable=true;
                Log.d("INSIDE SETTINGS",String.valueOf(locationAvailable));
                Log.d("Location Available",String.valueOf(locationAvailable));
                Log.d("LOCATION REQUIRED",String.valueOf(locationRequired));

            }
        });
            task.addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case CommonStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(MainActivity.this,
                                        12);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way
                            // to fix the settings so we won't show the dialog.
                            break;
                    }
                }
            });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Snackbar.make(findViewById(R.id.activity_main),"Map is Ready",Snackbar.LENGTH_LONG).show();
        Log.i("Maps ", "READY");
        gmap=googleMap;

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
