package com.example.googlemap.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, View.OnClickListener {

    private View rootView;
    private MapView mapView;//el que captura el layout
    private GoogleMap gMap;

    private List<Address> addresses;
    private Geocoder geocoder;

    private MarkerOptions marker;

    private FloatingActionButton fab;

    public MapFragment() {

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

        LatLng colom = new LatLng(4.5980772, -74.0761028);

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

        marker = new MarkerOptions();
        marker.position(colom);
        marker.title("Mi marcador");
        marker.draggable(true);
        marker.snippet("Esto es una caja de texto donde modificar los datos");
        marker.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.star_on));//Cambiar icono

        //gMap.addMarker(new MarkerOptions().position(colom).title("Colombia").draggable(true));//dragabble Para agarrar el marcador
        gMap.addMarker(marker);//Agregar el marcador
        gMap.moveCamera(CameraUpdateFactory.newLatLng(colom));
        gMap.animateCamera(zoom);

        gMap.setOnMarkerDragListener(this);

       //Recoger la información de la direccion de donde se suelta, geocoder es el que se encarga de eso
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        //Comprobar que tiene el gps

    }

    //Método para saber si el gps esta habilitado
    private void checkGPSisEnabled(){
        try {
            int gpsSignal = Settings.Secure.getInt(getActivity().getContentResolver(),Settings.Secure.LOCATION_MODE);

            if(gpsSignal==0){
                //El GPS no esta activado
                showInfoAlert();
            }else
                Toast.makeText(getContext(),"GPS is enabled",Toast.LENGTH_SHORT).show();

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
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
    public void onMarkerDragStart(Marker marker) { //El evento que se lanza cuando se empieza a arrastrar
        marker.hideInfoWindow();//Cierro cuando agarro el marcador

    }

    @Override
    public void onMarkerDrag(Marker marker) {//Evento que se lanza cada vez que se va moviendo

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        double latitude = marker.getPosition().latitude;
        double longitude = marker.getPosition().longitude;

        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);//1 representa el número de localizaciones, el máximo es 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); //Se accede a 1
        String city = addresses.get(0).getLocality(); //Para la ciudad
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalcode = addresses.get(0).getPostalCode();

        marker.setSnippet("adress" + address +"\n"+
                "city" + city +"\n"+
                "state" + state +"\n"+
                "country" + country +"\n"+
                "postalCode" + postalcode);

        marker.setSnippet(city +" , "+ country+" ("+postalcode+")");
        marker.showInfoWindow(); //Abro cuando suelto

        /*Toast.makeText(getContext(),"adress" + address +"\n"+
                "city" + city +"\n"+
                "state" + state +"\n"+
                "country" + country +"\n"+
                "postalCode" + postalcode,Toast.LENGTH_LONG).show();*/

    }

    @Override
    public void onClick(View view) {
        checkGPSisEnabled();
    }
}
