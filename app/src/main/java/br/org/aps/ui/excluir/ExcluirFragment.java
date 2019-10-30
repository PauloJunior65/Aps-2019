package br.org.aps.ui.excluir;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import br.org.aps.R;
import br.org.aps.classe.Local;
import br.org.aps.classe.Servidor;

public class ExcluirFragment extends Fragment {

    Spinner spTipo, spPor;
    LinearLayout tlItem;
    Servidor sv;
    ArrayList<Local> items;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_excluir, container, false);
        spTipo = root.findViewById(R.id.fra_excluir_spTipo);
        spPor = root.findViewById(R.id.fra_excluir_spPor);
        tlItem = root.findViewById(R.id.fra_excluir_tlItem);
        sv = Servidor.getInstance();
        return root;
    }

    public void carregar() {
        tlItem.removeAllViews();
        for (final Local obj : items) {
            LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = inflater.inflate(R.layout.obj_excluir, null);
            ImageView img = row.findViewById(R.id.obj_excluir_igImg);
            TextView texto = row.findViewById(R.id.obj_excluir_lbTexto);
            Button ver = row.findViewById(R.id.obj_excluir_btVer);
            Button excluir = row.findViewById(R.id.obj_excluir_btExcluir);
            img.setVisibility(View.GONE);
            String tx = "Nome: " + obj.getNome() +
                    "\n\nTipo: ";
            for (String tipo : obj.getTipo()) {
                tx += tipo + ", ";
            }
            texto.setText(tx);
            ver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putInt("codigo", obj.getCodigo());
                    sv.getNav().getGraph().findNode(R.id.nav_excluir_view).setLabel(obj.getNome());
                    sv.getNav().navigate(R.id.nav_excluir_view);
                }
            });
            excluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    items.remove(obj);
                    tlItem.removeView(row);
                }
            });
            tlItem.addView(row);
        }
    }
}