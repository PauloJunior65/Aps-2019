package br.org.aps.classe;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.navigation.NavController;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.org.aps.MainActivity;
import br.org.aps.R;

public class Servidor {
    private static Servidor instance;
    private RequestQueue request;

    private String url = "apptestpj.000webhostapp.com", pasta = "aps";

    private MainActivity context;
    private NavController nav;
    private ArrayList<Local> locals = new ArrayList<>();
    private Local loc;
    private User user = null;


    private Servidor(Context context) {
        if (request == null) {
            this.request = Volley.newRequestQueue(context.getApplicationContext());
        }
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

    public MainActivity getContext() {
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

    public Local getLoc() {
        return loc;
    }

    public boolean isLoc() {
        return loc != null;
    }

    public void setLoc(Local loc) {
        this.loc = loc;
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

    public ArrayList<Local> requestLocal(JSONObject json) throws Exception {
        try {
            ArrayList<Local> list = new ArrayList<>();
            JSONArray array = json.getJSONArray("local");
            for (int i = 0; i<array.length();i++){
                list.add(new Local(array.getJSONObject(i)));
            }
            this.locals = list;
        }catch (Exception e){
            throw new Exception("(requestLocal: "+e.getMessage()+" )");
        }
        return locals;
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

    public String getLocalString(ArrayList<Local> locals){
        JSONArray array = new JSONArray();
        for (Local obj : locals){
            try {
                JSONObject o = new JSONObject();
                o.put("nome",obj.getNome());
                o.put("lat",obj.getLatitude());
                o.put("lng",obj.getLongitude());
                array.put(o);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array.toString();
    }

    public void navMap(Local obj){
        Bundle b = new Bundle();
        ArrayList<Integer> a = new ArrayList<>();
        a.add(obj.getCodigo());
        b.putIntegerArrayList("dado",a);
        getNav().navigate(R.id.nav_map, b);
    }

    public void navMap(ArrayList<Local> list){
        Bundle b = new Bundle();
        ArrayList<Integer> a = new ArrayList<>();
        for (Local obj : list)a.add(obj.getCodigo());
        b.putIntegerArrayList("dado",a);
        getNav().navigate(R.id.nav_map, b);
    }
}
