package br.org.aps.ui.avaliar;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import br.org.aps.R;
import br.org.aps.classe.Local;
import br.org.aps.classe.Servidor;

public class AvaliarViewFragment extends Fragment {
    TextView lbTexto;
    Button btAprovar,btMap;
    FragmentActivity activity;
    Servidor sv;
    Local item = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_avaliar_view, container, false);
        lbTexto = root.findViewById(R.id.fra_avaliar_view_lbTexto);
        btMap = root.findViewById(R.id.fra_avaliar_view_btMap);
        btAprovar = root.findViewById(R.id.fra_avaliar_view_btAprova);
        activity = getActivity();
        sv = Servidor.getInstance();
        int codigo = getArguments().getInt("codigo", -1);
        for (Local obj : sv.getLocals()) {
            if (obj.getCodigo() == codigo) {
                item = obj;
                break;
            }
        }
        if (item != null) {
            btAprovar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringRequest req = new StringRequest(Request.Method.POST, sv.getUrl()
                            .appendPath("addeditlocal.php").build().toString(),
                            null,null
                    ) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("codigo", String.valueOf(item.getCodigo()));
                            params.put("avaliar", String.valueOf(0));
                            return params;
                        }
                    };
                    sv.addRequest(req, "avaliar");
                    item.setAvaliar(false);
                }
            });
            btMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sv.navMap(item);
                }
            });
            carregar();
        } else sv.getNav().navigateUp();
        return root;
    }

    private void carregar() {
        String tx = "Nome: " + item.getNome() +
                "\n\nDescrisão: " + item.getDesc()+
                "\n\nTipo: ";
        for (String t : item.getTipoArray())tx+=t+"; ";
        tx += "\n\nEnd.: " + item.getMapa() +
                "\n\nLatitude: " + item.getLatitude() +
                "\n\nLongitude: " + item.getLongitude();
        lbTexto.setText(tx);
    }

}
