package br.org.aps.ui.listar;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import br.org.aps.R;

public class ListarViewFragment extends Fragment {

    ImageView igImg;
    TextView lbTexto;
    MapView mpView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listar_view, container, false);
        igImg = root.findViewById(R.id.fra_list_view_igImg);
        lbTexto = root.findViewById(R.id.fra_list_view_lbTexto);
        mpView = root.findViewById(R.id.fra_list_view_mpView);
        return root;
    }

}
