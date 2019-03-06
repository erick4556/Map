package com.example.googlemap.activities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.googlemap.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //Para obtener el mapa asyncronamente por defecto
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       // mMap.setMinZoomPreference(10);
       // mMap.setMaxZoomPreference(15);

        // Add a marker in Sydney and move the camera
        LatLng dinam = new LatLng(55.66606154521146, 12.556189656061179);
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(dinam).title("Hola desde Dinamarca").snippet("Dinamarca").icon(BitmapDescriptorFactory.fromResource(android.R.drawable.star_on)).draggable(true));//.draggable(true) digo que es arrastrable

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(dinam)
                .zoom(10) //El límite puede ser 21, el zoom es igual al setMinZoom
                .bearing(90) //Orientación de la cámara hacia el este
                .tilt(45)//Un efecto 3d, el límite es 90
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));//Animar la cámara

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(dinam));

        //Eventos
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this,"Click on: \n"+"Lat "+latLng.latitude+"\n"+"Lon "
                        +latLng.longitude, Toast.LENGTH_SHORT).show();
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Toast.makeText(MapsActivity.this,"Long click : \n"+"Lat "+latLng.latitude+"\n"+"Lon "
                        +latLng.longitude, Toast.LENGTH_SHORT).show();
            }
        });


        //Arrastrar el marcador
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Toast.makeText(MapsActivity.this,"Market dragged to: \n"+"Lat "+marker.getPosition().latitude+"\n"+"Lon "
                        +marker.getPosition().longitude, Toast.LENGTH_SHORT).show();

            }
        });

    }
}
