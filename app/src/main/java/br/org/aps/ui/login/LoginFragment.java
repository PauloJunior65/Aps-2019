package br.org.aps.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import br.org.aps.R;
import br.org.aps.classe.Servidor;
import br.org.aps.classe.User;

public class LoginFragment extends Fragment {

    EditText txNick, txSenha;
    Button btLogin, btRegistro;
    FragmentActivity activity;
    Servidor sv;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        txNick = root.findViewById(R.id.fra_login_txNick);
        txSenha = root.findViewById(R.id.fra_login_txSenha);
        btLogin = root.findViewById(R.id.fra_login_btLogin);
        btRegistro = root.findViewById(R.id.fra_login_btRegistro);
        activity = getActivity();
        sv = Servidor.getInstance();
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registro();
            }
        });
        return root;
    }

    private void login() {
        if (!txNick.getText().toString().isEmpty() && !txSenha.getText().toString().isEmpty()){
            StringRequest req = new StringRequest(Request.Method.POST, sv.getUrl()
                    .appendPath("login.php").build().toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject json = new JSONObject(s);
                                if (json.optBoolean("ok",false)){
                                    json.put("nick",txNick.getText().toString());
                                    json.put("senha",txSenha.getText().toString());
                                    sv.setUser(new User(json));
                                    sv.getContext().upMenu();
                                    sv.getNav().popBackStack(R.id.nav_listar,false);
                                }else Toast.makeText(activity, "Login Invalido\n\n"+json.toString(), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                    Toast.makeText(activity, s+"\n\n(Erro: " + e.getMessage() + " )", Toast.LENGTH_LONG).show();
                            }
                            lipar();
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
                    params.put("nick", txNick.getText().toString());
                    params.put("senha", txSenha.getText().toString());
                    return params;
                }
            };
            sv.addRequest(req, "login");
        }else Toast.makeText(activity,"Campo Vazio!!!",Toast.LENGTH_LONG).show();
    }

    private void registro() {
        if (!txNick.getText().toString().isEmpty() && !txSenha.getText().toString().isEmpty()){
            StringRequest req = new StringRequest(Request.Method.POST, sv.getUrl()
                    .appendPath("addedituser.php").build().toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONObject json = new JSONObject(s);
                                if (json.optBoolean("ok",false)){
                                    json.put("nick",txNick.getText().toString());
                                    json.put("senha",txSenha.getText().toString());
                                    json.put("admin",0);
                                    sv.setUser(new User(json));
                                    sv.getContext().upMenu();
                                    sv.getNav().popBackStack(R.id.nav_listar,false);
                                }else Toast.makeText(activity, "Já exitir", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(activity, s+"\n\n(Erro: " + e.getMessage() + " )", Toast.LENGTH_LONG).show();
                            }
                            lipar();
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
                    params.put("nick", txNick.getText().toString());
                    params.put("senha", txSenha.getText().toString());
                    return params;
                }
            };
            sv.addRequest(req, "login");
        }else Toast.makeText(activity,"Campo Vazio!!!",Toast.LENGTH_LONG).show();
    }

    private void lipar(){
        txNick.setText("");
        txSenha.setText("");
    }

}