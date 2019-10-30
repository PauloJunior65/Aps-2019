package br.org.aps.ui.listar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import br.org.aps.R;
import br.org.aps.classe.Local;
import br.org.aps.classe.Servidor;

public class ListarFragment extends Fragment {

    Spinner spTipo, spPor;
    LinearLayout tlItem;
    ProgressBar bar;
    ScrollView scroll;
    Servidor sv;
    ArrayList<Local> items = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listar, container, false);
        spTipo = root.findViewById(R.id.fra_list_spTipo);
        spPor = root.findViewById(R.id.fra_list_spPor);
        tlItem = root.findViewById(R.id.fra_list_tlItem);
        bar = root.findViewById(R.id.fra_list_progressBar);
        scroll = root.findViewById(R.id.fra_list_scrollView);
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
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, sv.getUrl().appendPath("?.php").toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            sv.requestLocal(response);
                            ArrayList<String> list1 = new ArrayList<>();
                            ArrayList<String> list2 = new ArrayList<>();
                            for (Local obj : items) {
                                for (String tx : obj.getTipo()) {
                                    if (!list1.contains(tx)) list1.add(tx);
                                }
                            }
                            Collections.sort(list1);
                            list1.add(0, "TODOS");
                            list2.add("ASC");
                            list2.add("DESC");
                            ArrayAdapter<String> list = new ArrayAdapter<>(ListarFragment.this.getActivity(), R.layout.spinner_item, list2);
                            list.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                            spPor.setAdapter(list);
                            list = new ArrayAdapter<>(ListarFragment.this.getActivity(), R.layout.spinner_item, list1);
                            list.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                            spTipo.setAdapter(list);
                            bar.setVisibility(View.GONE);
                            scroll.setVisibility(View.VISIBLE);
                            carregar();
                        } catch (Exception e) {
                            Toast.makeText(ListarFragment.this.getActivity(), "Erro1: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListarFragment.this.getActivity(), "Erro2: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
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
            View row = inflater.inflate(R.layout.obj_listar, null);
            ImageView img = row.findViewById(R.id.obj_list_igImg);
            TextView texto = row.findViewById(R.id.obj_list_lbTexto);
            img.setVisibility(View.GONE);
            String tx = "Nome: " + obj.getNome() +
                    "\n\nTipo: ";
            for (String tx2 : obj.getTipo()) {
                tx += tx2 + ", ";
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