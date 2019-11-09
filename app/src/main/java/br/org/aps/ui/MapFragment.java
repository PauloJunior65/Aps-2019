package br.org.aps.ui;


import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.org.aps.R;
import br.org.aps.classe.Local;
import br.org.aps.classe.Servidor;
import br.org.aps.ui.myregistro.MyRegistroAddFragment;

public class MapFragment extends Fragment {
    private GoogleMap mMap;
    private FragmentActivity activity;
    private Servidor sv;
    private boolean editar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fra_map);
        activity = getActivity();
        sv = Servidor.getInstance();
        editar = getArguments().getBoolean("editar",false);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMinZoomPreference(12);
                mMap.setIndoorEnabled(true);
                UiSettings uiSettings = mMap.getUiSettings();
                uiSettings.setIndoorLevelPickerEnabled(true);
                uiSettings.setMyLocationButtonEnabled(true);
                uiSettings.setMapToolbarEnabled(true);
                uiSettings.setCompassEnabled(true);
                uiSettings.setZoomControlsEnabled(true);

                if (editar){
                    if (sv.isLoc()){
                        Local loc = sv.getLoc();
                        LatLng sydney = new LatLng(loc.getLatitude(), loc.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(sydney).title("Local"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                mMap.clear();
                                sv.getLoc().setLocal(latLng);
                                Geocoder geocoder = new Geocoder(Servidor.getInstance().getContext(), Locale.getDefault());
                                try {
                                    List<Address> address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                    Servidor.getInstance().getLoc().setMapa(address.get(0).getAddressLine(0));
                                    Toast.makeText(activity,address.get(0).getAddressLine(0),Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                }
                                mMap.addMarker(new MarkerOptions().position(latLng).title("Local"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            }
                        });
                    }
                }else {
                    ArrayList<Integer> codigos = MapFragment.this.getArguments().getIntegerArrayList("dado");
                    for (Local o : sv.getLocals()){
                        for (int i=codigos.size()-1;i>=0;i--){
                            if (codigos.get(i) == o.getCodigo()){
                                LatLng sydney = new LatLng(o.getLatitude(), o.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(sydney).title(o.getNome()));
                                if (i == 0) mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                                codigos.remove(i);
                            }
                        }
                    }
                }
            }
        });
        return root;
    }
}
