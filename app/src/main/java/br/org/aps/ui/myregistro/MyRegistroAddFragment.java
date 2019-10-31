package br.org.aps.ui.myregistro;


import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.org.aps.R;

public class MyRegistroAddFragment extends Fragment {

    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    MapView mapView;
    GoogleMap map;
    LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_myregistro_add, container, false);
        mapView = root.findViewById(R.id.fra_myreg_add_mpView);
        Bundle mapbundle = null;
        if (savedInstanceState != null){
            mapbundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapbundle);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                map = googleMap;
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                LatLng sydney = new LatLng(-2.988195, -60.002744);
                map.addMarker(new MarkerOptions().position(sydney).title("Casa"));
                map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Casa"));
                        Toast.makeText(MyRegistroAddFragment.this.getActivity(),"("+latLng.latitude+") ("+latLng.longitude+")",Toast.LENGTH_LONG).show();
                    }
                });
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            }
        });
        return root;
    }

}
