package br.org.aps.ui.listar;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.org.aps.R;
import br.org.aps.classe.Local;
import br.org.aps.classe.Servidor;

public class ListarViewFragment extends Fragment {

    ImageView igImg;
    TextView lbTexto;
    Button btMap;
    FragmentActivity activity;
    Servidor sv;
    Local item = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_listar_view, container, false);
        igImg = root.findViewById(R.id.fra_list_view_igImg);
        lbTexto = root.findViewById(R.id.fra_list_view_lbTexto);
        btMap = root.findViewById(R.id.fra_list_view_btMap);
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
            carregar();
        } else sv.getNav().navigateUp();
        return root;
    }

    private void carregar() {
        String tx = "Nome: " + item.getNome() +
                "\nDescrisão: " + item.getDesc()+
                "\nTipo: ";
        for (String t : item.getTipoArray())tx+=t+"; ";
        tx += "\nEnd.: " + item.getMapa() +
                "\nLatitude: " + item.getLatitude() +
                "\nLongitude: " + item.getLongitude();
        lbTexto.setText(tx);
        btMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sv.navMap(item);
            }
        });
    }

}
