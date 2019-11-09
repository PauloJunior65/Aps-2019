package br.org.aps.ui.myregistro;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import br.org.aps.R;
import br.org.aps.classe.Local;
import br.org.aps.classe.Servidor;
import br.org.aps.ui.excluir.ExcluirFragment;
import br.org.aps.ui.listar.ListarFragment;

public class MyRegistroAddFragment extends Fragment {

    Spinner spTipo;
    LinearLayout tlTipo;
    EditText txNome, txDesc, txEnd, txTipo;
    ImageButton btEnd, btTipo;
    Button btSalvar;
    FragmentActivity activity;
    Servidor sv;
    Local item = null;
    ArrayList<String> tipos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_myregistro_add, container, false);
        txNome = root.findViewById(R.id.fra_myreg_add_txNome);
        txDesc = root.findViewById(R.id.fra_myreg_add_txDesc);
        txEnd = root.findViewById(R.id.fra_myreg_add_txEnd);
        btEnd = root.findViewById(R.id.fra_myreg_add_btEnd);
        btSalvar = root.findViewById(R.id.fra_myreg_add_btSalvar);
        spTipo = root.findViewById(R.id.fra_myreg_add_spTipo);
        tlTipo = root.findViewById(R.id.fra_myreg_add_tlTipo);
        txTipo = root.findViewById(R.id.fra_myreg_add_txTipo);
        btTipo = root.findViewById(R.id.fra_myreg_add_btTipo);
        activity = getActivity();
        sv = Servidor.getInstance();
        int codigo = getArguments().getInt("codigo", -1);
        if (codigo == 0) {
            item = new Local(sv.getUser().getCodigo());
        } else {
            for (Local obj : sv.getLocals()) {
                if (obj.getCodigo() == codigo) {
                    item = obj;
                    break;
                }
            }
        }
        if (item != null) {
            btEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.setEdit(txNome.getText().toString(), txDesc.getText().toString(),
                            txEnd.getText().toString(), tipos);
                    sv.setLoc(item);
                    Bundle b = new Bundle();
                    b.putBoolean("editar", true);
                    sv.getNav().navigate(R.id.nav_map, b);
                }
            });
            btSalvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    item.setEdit(txNome.getText().toString(), txDesc.getText().toString(),
                            txEnd.getText().toString(), tipos);
                    if (item.getCodigo() == 0) sv.getLocals().add(item);
                    reguestRegistro(item);
                    sv.getNav().navigateUp();
                }
            });
            spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 0) {
                        txTipo.setVisibility(View.VISIBLE);
                    } else {
                        txTipo.setVisibility(View.INVISIBLE);
                        txTipo.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            btTipo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tx;
                    if (txTipo.getText().toString().isEmpty()) {
                        tx = spTipo.getSelectedItem().toString();
                    } else tx = txTipo.getText().toString();
                    if (!tipos.contains(tx)) tipos.add(tx);
                    carregarTipo();
                }
            });
            carregar();
        } else sv.getNav().navigateUp();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sv.isLoc()) item = sv.getLoc();
        carregar();
    }

    private void carregar() {
        txNome.setText(item.getNome());
        txDesc.setText(item.getDesc());
        txEnd.setText(item.getMapa());
        tipos = new ArrayList<>(item.getTipoArray());
        carregarTipo();
    }

    private void carregarTipo() {
        ArrayList<String> list1 = new ArrayList<>();
        for (Local obj : sv.getLocals()) {
            for (String tx : obj.getTipoArray()) {
                if (!list1.contains(tx)) list1.add(tx);
            }
        }
        list1.removeAll(tipos);
        Collections.sort(list1);
        list1.add(0, "TODOS");
        ArrayAdapter<String> list = new ArrayAdapter<>(activity, R.layout.spinner_item, list1);
        list.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spTipo.setAdapter(list);
        Collections.sort(tipos);
        tlTipo.removeAllViews();
        for (final String tx : tipos) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = inflater.inflate(R.layout.obj_myregistro_add, null);
            TextView texto = row.findViewById(R.id.obj_myreg_add_lbTexto);
            ImageButton del = row.findViewById(R.id.obj_myreg_add_btDel);
            texto.setText(tx);
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tipos.remove(tx);
                    carregarTipo();
                }
            });
            tlTipo.addView(row);
        }
    }

    private void reguestRegistro(final Local obj) {
        final String codigo = String.valueOf(obj.getCodigo()),
                usecodigo = String.valueOf(obj.getUsecodigo()),
                nome = obj.getNome(),
                desc = obj.getDesc(),
                tipo = obj.getTipo(),
                mapa = obj.getMapa(),
                latitude = String.valueOf(obj.getLatitude()),
                longitude = String.valueOf(obj.getLongitude());
        StringRequest req = new StringRequest(Request.Method.POST, sv.getUrl()
                .appendPath("addeditlocal.php").build().toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject json = new JSONObject(s);
                            if (json.optBoolean("ok", false)) {
                                obj.setCodigo(json.getInt("codigo"));
                            }
                        } catch (Exception e) {
                            Toast.makeText(activity, s + "\n\n(Erro: " + e.getMessage() + " )", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Toast.makeText(activity, "(Erro: " + e.getMessage() + " )", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("codigo", codigo);
                params.put("usecodigo", usecodigo);
                params.put("nome", nome);
                params.put("desc", desc);
                params.put("tipo", tipo);
                params.put("mapa", mapa);
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                return params;
            }
        };
        sv.addRequest(req, "excluir");
    }
}
