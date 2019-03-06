package com.example.googlemap.fragments;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.googlemap.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment2 extends Fragment implements OnMapReadyCallback, View.OnClickListener, LocationListener  {


    private View rootView;
    private MapView mapView;//el que captura el layout
    private GoogleMap gMap;
    private FloatingActionButton fab;

    private LocationManager locationManager;
    private Location currentlocation;

    private Marker marker;
    private CameraPosition camera; //para que cuando se actualice se haga el zoom

    public MapFragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        fab = (FloatingActionButton)rootView.findViewById(R.id.fab);

        fab.setOnClickListener(this);

        return rootView;
    }

    //Aqui es donde se carga la vista
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = (MapView)rootView.findViewById(R.id.map);

        if(mapView != null){
            mapView.onCreate(null);//Crear manualmente el map view
            mapView.onResume();//Se llama cuando el activity o fragment esta visible
            mapView.getMapAsync(this);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);//Se agarra el location Manager




        //Permiso de acceso al gps
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //Buscar mi dirección gps
        //gMap.setMyLocationEnabled(true);
        //gMap.getUiSettings().setMyLocationButtonEnabled(false); //desactviar el boton de la interfaz de usuario

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10,this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,this);
        //Pide las actualizaciones de la localización, el segundo parametro el tiempo que se quiere que se de las actualizaciones
        //segundo parametro es el minimo de distancia, tercer parámetro es un location listener

        //En el locationManager primero tiene que pasar como mínimo 1 segundo y 10 metros para que lance la actualización

    }

    //Método para saber si el gps esta habilitado
    private boolean isGPSEnabled(){
        try {
            int gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver(),Settings.Secure.LOCATION_MODE);

            if(gpsSignal==0){
               return false;
            }else{
               return true;
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showInfoAlert(){
        new AlertDialog.Builder(getContext())
                .setTitle("GPS Signal")
                .setMessage("You don't have GPS signal enabled. Would you like to enable the GPS signal")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("CANCEL",null)
                .show();

    }

    @Override
    public void onClick(View view) {
        if(!isGPSEnabled()) { //si no esta habilitado
            showInfoAlert();
        }else{
            //Permiso de acceso al gps
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){//Si el provider no da la localización de gps bueno se escoge del network
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            currentlocation = location;

            if(currentlocation!=null){
                createOrUpdateMarkerByLocation(location);//se actualiza el marcador
                zoomToLocation(location);
            }
        }

    }

    private void createOrUpdateMarkerByLocation(Location location){
        if(marker==null){
            marker = gMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).draggable(true));
        }else{
            marker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        }
    }

    private void zoomToLocation(Location location){
        camera = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(),location.getLongitude()))
                .zoom(10) //El límite puede ser 21, el zoom es igual al setMinZoom
                .bearing(90) //Orientación de la cámara hacia el este
                .tilt(45)//Un efecto 3d, el límite es 90
                .build();

        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera));//Animar la cámara
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getContext(),"Changed ->" + location.getProvider(),Toast.LENGTH_SHORT).show();
        createOrUpdateMarkerByLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
