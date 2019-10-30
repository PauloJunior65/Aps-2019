package br.org.aps.ui.excluir;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import br.org.aps.R;
import br.org.aps.classe.Servidor;

public class ExcluirViewFragment extends Fragment {

    ImageView igImg;
    TextView lbTexto;
    MapView mpView;
    Servidor sv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_excluir_view, container, false);
        igImg = root.findViewById(R.id.fra_excluir_view_igImg);
        lbTexto = root.findViewById(R.id.fra_excluir_view_lbTexto);
        mpView = root.findViewById(R.id.fra_excluir_view_mpView);
        sv = Servidor.getInstance();
        return root;
    }

}
