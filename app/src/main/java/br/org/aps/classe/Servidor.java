package br.org.aps.classe;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.navigation.NavController;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import br.org.aps.MainActivity;

public class Servidor {
    private static Servidor instance;
    private RequestQueue request;

    private String url = "tccunip2019.000webhostapp.com", pasta = "AvE";
    //private String url = "10.0.0.6", pasta = "AvE";

    private Context context;
    private NavController nav;
    private ArrayList<Local> locals;
    private User user = null;


    private Servidor(Context context) {
        if (request == null) {
            this.request = Volley.newRequestQueue(context.getApplicationContext());
        }
        this.context = context;
    }

    public static synchronized Servidor getInstance(MainActivity context) {
        if (instance == null) {
            instance = new Servidor(context);
        }
        instance.context = context;
        instance.setNav(context.getNav());
        return instance;
    }

    public static synchronized Servidor getInstance() {
        return instance;
    }

    private RequestQueue getRequestQueue() {
        if (request == null) this.request = Volley.newRequestQueue(context.getApplicationContext());
        return request;
    }

    public <T> void addRequest(Request<T> req, String TAG) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    private void stopRequest(String TAG) {
        if (request != null) if (!TAG.isEmpty()) request.cancelAll(TAG);
    }

    public void cancelAllRequests() {
        if (getRequestQueue() != null)
            getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
    }

    public Uri.Builder getUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority(url).appendPath(pasta);
        return builder;
    }

    public Context getContext() {
        return context;
    }

    public NavController getNav() {
        return nav;
    }

    private void setNav(NavController nav) {
        this.nav = nav;
    }

    public boolean isRede() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public ArrayList<Local> getLocals() {
        return locals;
    }

    public User getUser() {
        return user;
    }

    public boolean isUser() {
        return user != null;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void requestLocal(JSONObject json) throws Exception {
        try {
            ArrayList<Local> list = new ArrayList<>();
            JSONArray array = json.getJSONArray("local");
            for (int i = 0; i<array.length();i++){
                list.add(new Local(array.getJSONObject(i)));
            }
            for (int i = locals.size() - 1; i >= 0; i--) {
                boolean pass = true;
                for (int j = list.size() - 1; j >= 0; j--) {
                    Local obj = locals.get(i), obj2 = list.get(j);
                    if (obj.getCodigo() == obj2.getCodigo()) {
                        pass = false;
                        obj.setAll(obj2);
                        list.remove(j);
                        break;
                    }
                }
                if (pass) locals.remove(i);
            }
            if (list.size() > 0) locals.addAll(list);
        }catch (Exception e){
            throw new Exception("(requestLocal: "+e.getMessage()+" )");
        }
    }

    public ArrayList<User> requestUser(JSONObject json) throws Exception {
        try {
            ArrayList<User> list = new ArrayList<>();
            JSONArray array = json.getJSONArray("user");
            for (int i = 0; i<array.length();i++){
                list.add(new User(array.getJSONObject(i)));
            }
            return list;
        }catch (Exception e){
            throw new Exception("(requestLocal: "+e.getMessage()+" )");
        }
    }
}
