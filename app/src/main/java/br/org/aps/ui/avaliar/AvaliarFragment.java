package br.org.aps.ui.avaliar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.org.aps.R;
import br.org.aps.classe.Local;
import br.org.aps.classe.Servidor;

public class AvaliarFragment extends Fragment {

    LinearLayout tlItem;
    Servidor sv;
    ArrayList<Local> items;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_avaliar, container, false);
        tlItem = root.findViewById(R.id.fra_avaliar_tlItem);
        sv = Servidor.getInstance();
        return root;
    }

    public void carregar(){
        Collections.sort(items, new Comparator<Local>() {
            @Override
            public int compare(Local t1, Local t2) {
                return t1.getCodigo() - t2.getCodigo();
            }
        });
        for (final Local obj : items){
            LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = inflater.inflate(R.layout.obj_excluir, null);
            ImageView img = row.findViewById(R.id.obj_avaliar_igImg);
            TextView texto = row.findViewById(R.id.obj_avaliar_lbTexto);
            Button ver = row.findViewById(R.id.obj_avaliar_btVer);
            Button aprovar = row.findViewById(R.id.obj_avaliar_btExcluir);
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
                    sv.getNav().getGraph().findNode(R.id.nav_avaliar_view).setLabel(obj.getNome());
                    sv.getNav().navigate(R.id.nav_avaliar_view);
                }
            });
            aprovar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    obj.setAvaliar(false);
                    tlItem.removeView(row);
                }
            });
        }
    }
}