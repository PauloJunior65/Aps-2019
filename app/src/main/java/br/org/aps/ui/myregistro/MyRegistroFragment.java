package br.org.aps.ui.myregistro;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.org.aps.R;
import br.org.aps.classe.Local;
import br.org.aps.classe.Servidor;
import br.org.aps.ui.excluir.ExcluirFragment;

public class MyRegistroFragment extends Fragment {

    LinearLayout tlItem;
    FloatingActionButton btAdd;
    ProgressBar bar;
    ScrollView scroll;
    Servidor sv;
    ArrayList<Local> items = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_myregistro, container, false);
        tlItem = root.findViewById(R.id.fra_myreg_tlItem);
        btAdd = root.findViewById(R.id.fra_myreg_btAdd);
        bar = root.findViewById(R.id.fra_excluir_progressBar);
        scroll = root.findViewById(R.id.fra_excluir_scrollView);
        sv = Servidor.getInstance();
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sv.getNav().navigate(R.id.nav_myregistro_add);
            }
        });
        //reguest();
        return root;
    }

    public void reguest() {
        bar.setVisibility(View.VISIBLE);
        scroll.setVisibility(View.GONE);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, sv.getUrl().appendPath("?.php").toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            sv.requestLocal(response);
                            carregar();
                        } catch (Exception e) {
                            Toast.makeText(MyRegistroFragment.this.getActivity(), "Erro1: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        bar.setVisibility(View.GONE);
                        scroll.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        bar.setVisibility(View.GONE);
                        scroll.setVisibility(View.VISIBLE);
                        Toast.makeText(MyRegistroFragment.this.getActivity(), "Erro2: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void carregar(){
        tlItem.removeAllViews();
        items = new ArrayList<>(sv.getLocals());
        Collections.sort(items, new Comparator<Local>() {
            @Override
            public int compare(Local t1, Local t2) {
                return t1.getCodigo() - t2.getCodigo();
            }
        });
        for (final Local obj : items){
            if (sv.getUser().getCodigo() == obj.getUsecodigo()){
                LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View row = inflater.inflate(R.layout.obj_myregistro, null);
                ImageView img = row.findViewById(R.id.obj_myreg_igImg);
                TextView texto = row.findViewById(R.id.obj_myreg_lbTexto);
                Button editar = row.findViewById(R.id.obj_myreg_btEditar);
                img.setVisibility(View.GONE);
                String tx = "Nome: " + obj.getNome() +
                        "\n\nTipo: ";
                for (String tx2 : obj.getTipo()) {
                    tx += tx2 + ", ";
                }
                if (obj.isAvaliar())tx+="\n\nAvaliar";
                texto.setText(tx);
                editar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle b = new Bundle();
                        b.putInt("codigo", obj.getCodigo());
                        sv.getNav().getGraph().findNode(R.id.nav_avaliar_view).setLabel(obj.getNome());
                        sv.getNav().navigate(R.id.nav_avaliar_view);
                    }
                });
                tlItem.addView(row);
            }
        }
    }
}