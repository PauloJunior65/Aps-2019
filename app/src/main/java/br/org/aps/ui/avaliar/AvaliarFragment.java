package br.org.aps.ui.avaliar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import br.org.aps.R;
import br.org.aps.classe.Local;
import br.org.aps.classe.Servidor;

public class AvaliarFragment extends Fragment {

    LinearLayout tlItem;
    ProgressBar bar;
    ScrollView scroll;
    FragmentActivity activity;
    Servidor sv;
    ArrayList<Local> items;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_avaliar, container, false);
        tlItem = root.findViewById(R.id.fra_avaliar_tlItem);
        bar = root.findViewById(R.id.fra_avaliar_progressBar);
        scroll = root.findViewById(R.id.fra_avaliar_scrollView);
        activity =getActivity();
        sv = Servidor.getInstance();
        reguest();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        carregar();
    }

    private void reguest() {
        bar.setVisibility(View.VISIBLE);
        scroll.setVisibility(View.GONE);
        StringRequest req = new StringRequest(Request.Method.POST, sv.getUrl()
                .appendPath("alllocal.php").build().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject json = new JSONObject(s);
                            if (json.has("local")){
                                sv.requestLocal(json);
                                carregar();
                            }
                        } catch (Exception e) {
                            Toast.makeText(activity, s+"\n\n(Erro: " + e.getMessage() + " )", Toast.LENGTH_LONG).show();
                        }
                        bar.setVisibility(View.GONE);
                        scroll.setVisibility(View.VISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Toast.makeText(activity, "(Erro: " + e.getMessage() + " )", Toast.LENGTH_LONG).show();
                        bar.setVisibility(View.GONE);
                        scroll.setVisibility(View.VISIBLE);
                    }
                }
        );
        sv.addRequest(req, "login");
    }

    public void carregar(){
        items = new ArrayList<>(sv.getLocals());
        for (int i= 0;i<items.size();i++){
            if (items.get(i).isAvaliar())items.remove(i);
        }
        Collections.sort(items, new Comparator<Local>() {
            @Override
            public int compare(Local t1, Local t2) {
                return t1.getCodigo() - t2.getCodigo();
            }
        });
        for (final Local obj : items){
            LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = inflater.inflate(R.layout.obj_excluir, null);
            ImageView img = row.findViewById(R.id.obj_excluir_igImg);
            TextView texto = row.findViewById(R.id.obj_excluir_lbTexto);
            Button aprovar = row.findViewById(R.id.obj_excluir_btExcluir);
            img.setVisibility(View.GONE);
            String tx = "Nome: " + obj.getNome() +
                    "\n\nTipo: ";
            for (String tipo : obj.getTipoArray()) {
                tx += tipo + ", ";
            }
            texto.setText(tx);
            aprovar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reguestAvaliar(obj);
                    obj.setAvaliar(false);
                    tlItem.removeView(row);
                }
            });
        }
    }

    private void reguestAvaliar(final Local obj) {
        StringRequest req = new StringRequest(Request.Method.POST, sv.getUrl()
                .appendPath("droplocal.php").build().toString(),
                null,null
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("codigo", String.valueOf(obj.getCodigo()));
                params.put("avaliar", String.valueOf(0));
                return params;
            }
        };
        sv.addRequest(req, "avaliar");
    }
}