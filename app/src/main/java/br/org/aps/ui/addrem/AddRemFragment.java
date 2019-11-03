package br.org.aps.ui.addrem;

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

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import br.org.aps.classe.User;
import br.org.aps.ui.listar.ListarFragment;

public class AddRemFragment extends Fragment {

    Spinner spTipo, spPor;
    LinearLayout tlItem;
    ProgressBar bar;
    ScrollView scroll;
    FragmentActivity activity;
    Servidor sv;
    ArrayList<User> items = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addrem, container, false);
        spTipo = root.findViewById(R.id.fra_addrem_spTipo);
        spPor = root.findViewById(R.id.fra_addrem_spPor);
        tlItem = root.findViewById(R.id.fra_addrem_tlItem);
        bar = root.findViewById(R.id.fra_addrem_progressBar);
        scroll = root.findViewById(R.id.fra_addrem_scrollView);
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

    private void reguest() {
        bar.setVisibility(View.VISIBLE);
        scroll.setVisibility(View.GONE);
        StringRequest req = new StringRequest(Request.Method.POST, sv.getUrl()
                .appendPath("alluser.php").build().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject json = new JSONObject(s);
                            items = sv.requestUser(json);
                            ArrayList<String> list1 = new ArrayList<>();
                            ArrayList<String> list2 = new ArrayList<>();
                            list1.add("TODOS");
                            list1.add("ADMIN");
                            list1.add("NORMAL");
                            list2.add("ASC");
                            list2.add("DESC");
                            ArrayAdapter<String> list = new ArrayAdapter<>(activity, R.layout.spinner_item, list2);
                            list.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                            spPor.setAdapter(list);
                            list = new ArrayAdapter<>(activity, R.layout.spinner_item, list1);
                            list.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                            spTipo.setAdapter(list);
                            carregar();
                        } catch (Exception e) {
                            Toast.makeText(activity, s + "\n\n(Erro: " + e.getMessage() + " )", Toast.LENGTH_LONG).show();
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
        sv.addRequest(req, "admin");
    }

    private void carregar() {
        tlItem.removeAllViews();
        String tipo = spTipo.getSelectedItem().toString(),
                por = spPor.getSelectedItem().toString();
        if (!tipo.equals("TODOS")) {
            for (int i = items.size() - 1; i >= 0; i--) {
                User o = items.get(i);
                if (tipo.equals("ADMIN") && !o.isAdmin())items.remove(i);
                if (tipo.equals("NORMAL") && o.isAdmin())items.remove(i);
            }
        }
        switch (por) {
            case "ASC":
                Collections.sort(items, new Comparator<User>() {
                    @Override
                    public int compare(User t1, User t2) {
                        return t1.getNick().compareTo(t2.getNick());
                    }
                });
                break;
            case "DESC":
                Collections.sort(items, new Comparator<User>() {
                    @Override
                    public int compare(User t1, User t2) {
                        return t2.getNick().compareTo(t1.getNick());
                    }
                });
                break;
            default:
                break;
        }
        for (final User obj : items) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.obj_addrem, null);
            TextView texto = row.findViewById(R.id.obj_addrem_lbTexto);
            final Button bt = row.findViewById(R.id.obj_addrem_btExcluir);
            String tx = "Nome: " + obj.getNick()+"\nTipo: ";
            if (obj.isAdmin()){
                tx+="ADMIN";
                bt.setText("MUDAR PRA ADMIN");
            }else {
                tx+="NORMAL";
                bt.setText("MUDAR PRA NORMAL");
            }
            texto.setText(tx);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reguestAddRem(obj);
                    if (obj.isAdmin()){
                        bt.setText("MUDAR PRA NORMAL");
                        obj.setAdmin(false);
                    }else {
                        bt.setText("MUDAR PRA ADMIN");
                        obj.setAdmin(true);
                    }
                }
            });
            tlItem.addView(row);
        }
    }

    private void reguestAddRem(final User obj) {
        StringRequest req = new StringRequest(Request.Method.POST, sv.getUrl()
                .appendPath("addedituser.php").build().toString(),
                null,null
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("codigo", String.valueOf(obj.getCodigo()));
                if (obj.isAdmin()){
                    params.put("admin", String.valueOf(0));
                }else params.put("admin", String.valueOf(1));
                return params;
            }
        };
        sv.addRequest(req, "addrem");
    }
}