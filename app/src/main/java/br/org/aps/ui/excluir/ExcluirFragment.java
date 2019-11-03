package br.org.aps.ui.excluir;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
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

public class ExcluirFragment extends Fragment {

    Spinner spTipo, spPor;
    LinearLayout tlItem;
    ProgressBar bar;
    FragmentActivity activity;
    ScrollView scroll;
    Servidor sv;
    ArrayList<Local> items;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_excluir, container, false);
        spTipo = root.findViewById(R.id.fra_excluir_spTipo);
        spPor = root.findViewById(R.id.fra_excluir_spPor);
        tlItem = root.findViewById(R.id.fra_excluir_tlItem);
        bar = root.findViewById(R.id.fra_excluir_progressBar);
        scroll = root.findViewById(R.id.fra_excluir_scrollView);
        activity = getActivity();
        sv = Servidor.getInstance();
        AdapterView.OnItemSelectedListener adapter = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                carregar();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        spTipo.setOnItemSelectedListener(adapter);
        spPor.setOnItemSelectedListener(adapter);
        reguest();
        return root;
    }

    public void reguest() {
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
                                ArrayList<String> list1 = new ArrayList<>();
                                ArrayList<String> list2 = new ArrayList<>();
                                for (Local obj : items) {
                                    for (String tx : obj.getTipoArray()) {
                                        if (!list1.contains(tx)) list1.add(tx);
                                    }
                                }
                                Collections.sort(list1);
                                list1.add(0, "TODOS");
                                list2.add("ASC");
                                list2.add("DESC");
                                ArrayAdapter<String> list = new ArrayAdapter<>(activity, R.layout.spinner_item, list2);
                                list.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                spPor.setAdapter(list);
                                list = new ArrayAdapter<>(ExcluirFragment.this.getActivity(), R.layout.spinner_item, list1);
                                list.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                                spTipo.setAdapter(list);
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
        sv.addRequest(req, "excluir");
    }

    public void carregar() {
        tlItem.removeAllViews();
        items = new ArrayList<>(sv.getLocals());
        String tipo = spTipo.getSelectedItem().toString(),
                por = spPor.getSelectedItem().toString();
        if (!tipo.equals("TODOS")) {
            for (int i = items.size() - 1; i >= 0; i--) {
                if (!items.get(i).isTipo(tipo))items.remove(i);
            }
        }
        switch (por) {
            case "ASC":
                Collections.sort(items, new Comparator<Local>() {
                    @Override
                    public int compare(Local t1, Local t2) {
                        return t1.getNome().compareTo(t2.getNome());
                    }
                });
                break;
            case "DESC":
                Collections.sort(items, new Comparator<Local>() {
                    @Override
                    public int compare(Local t1, Local t2) {
                        return t2.getNome().compareTo(t1.getNome());
                    }
                });
                break;
            default:
                break;
        }
        for (final Local obj : items) {
            LayoutInflater inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = inflater.inflate(R.layout.obj_excluir, null);
            ImageView img = row.findViewById(R.id.obj_excluir_igImg);
            TextView texto = row.findViewById(R.id.obj_excluir_lbTexto);
            Button excluir = row.findViewById(R.id.obj_excluir_btExcluir);
            img.setVisibility(View.GONE);
            String tx = "Nome: " + obj.getNome() +
                    "\n\nTipo: ";
            for (String tx2 : obj.getTipoArray()) {
                tx += tx2 + ", ";
            }
            texto.setText(tx);
            excluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reguestExcluir(obj);
                    items.remove(obj);
                    tlItem.removeView(row);
                }
            });
            tlItem.addView(row);
        }
    }

    private void reguestExcluir(final Local obj) {
        StringRequest req = new StringRequest(Request.Method.POST, sv.getUrl()
                .appendPath("droplocal.php").build().toString(),
                null,null
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("codigo", String.valueOf(obj.getCodigo()));
                return params;
            }
        };
        sv.addRequest(req, "excluir");
    }
}