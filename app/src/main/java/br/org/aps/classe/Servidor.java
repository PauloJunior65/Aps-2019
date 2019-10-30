package br.org.aps.classe;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.navigation.NavController;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

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
    private ArrayList<User> users;
    private User user;


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

    public void setLocals(ArrayList<Local> locals) {
        this.locals = locals;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
