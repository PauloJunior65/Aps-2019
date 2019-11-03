package br.org.aps.ui.myregistro;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.org.aps.R;
import br.org.aps.classe.Local;
import br.org.aps.classe.Servidor;

public class MyRegistroFragment extends Fragment {

    LinearLayout tlItem;
    FloatingActionButton btAdd;
    FragmentActivity activity;
    Servidor sv;
    ArrayList<Local> items;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_myregistro, container, false);
        tlItem = root.findViewById(R.id.fra_myreg_tlItem);
        btAdd = root.findViewById(R.id.fra_myreg_btAdd);
        activity = getActivity();
        sv = Servidor.getInstance();
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle b = new Bundle();
                b.putInt("codigo", 0);
                sv.getNav().navigate(R.id.nav_myregistro_add,b);
            }
        });
        reguest();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        carregar();
    }

    public void reguest() {
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
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Toast.makeText(activity, "(Erro: " + e.getMessage() + " )", Toast.LENGTH_LONG).show();
                    }
                }
        );
        sv.addRequest(req, "login");
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
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View row = inflater.inflate(R.layout.obj_myregistro, null);
                ImageView img = row.findViewById(R.id.obj_myreg_igImg);
                TextView texto = row.findViewById(R.id.obj_myreg_lbTexto);
                Button editar = row.findViewById(R.id.obj_myreg_btEditar);
                img.setVisibility(View.GONE);
                String tx = "Nome: " + obj.getNome() +
                        "\n\nTipo: ";
                for (String tx2 : obj.getTipoArray()) {
                    tx += tx2 + ", ";
                }
                if (obj.isAvaliar())tx+="\n\nAvaliar";
                texto.setText(tx);
                editar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle b = new Bundle();
                        b.putInt("codigo", obj.getCodigo());
                        sv.getNav().getGraph().findNode(R.id.nav_myregistro_add).setLabel(obj.getNome());
                        sv.getNav().navigate(R.id.nav_myregistro_add,b);
                    }
                });
                tlItem.addView(row);
            }
        }
    }
}