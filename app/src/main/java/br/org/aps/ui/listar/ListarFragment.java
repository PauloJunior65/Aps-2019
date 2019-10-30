package br.org.aps.ui.listar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;

import br.org.aps.R;
import br.org.aps.classe.Local;
import br.org.aps.classe.Servidor;

public class ListarFragment extends Fragment {

    Spinner spTipo, spPor;
    LinearLayout tlItem;
    Servidor sv;
    ArrayList<Local> items;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listar, container, false);
        spTipo = root.findViewById(R.id.fra_list_spTipo);
        spPor = root.findViewById(R.id.fra_list_spPor);
        tlItem = root.findViewById(R.id.fra_list_tlItem);
        sv = Servidor.getInstance();
        return root;
    }

    public void carregar() {
        tlItem.removeAllViews();
        for (final Local obj : items) {
            LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.obj_listar, null);
            ImageView img = row.findViewById(R.id.obj_list_igImg);
            TextView texto = row.findViewById(R.id.obj_list_lbTexto);
            img.setVisibility(View.GONE);
            String tx = "Nome: " + obj.getNome() +
                    "\n\nTipo: ";
            for (String tipo : obj.getTipo()) {
                tx += tipo + ", ";
            }
            texto.setText(tx);
            texto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putInt("codigo", obj.getCodigo());
                    sv.getNav().getGraph().findNode(R.id.nav_listar_view).setLabel(obj.getNome());
                    sv.getNav().navigate(R.id.nav_listar_view);
                }
            });
            tlItem.addView(row);
        }
    }
}