package br.org.aps.classe;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Local {
    int codigo, usecodigo;
    String nome, desc, mapa, tipo;
    double latitude, longitude;
    boolean avaliar;

    public Local(JSONObject item) throws Exception {
        try {
            this.codigo = item.getInt("codigo");
            this.usecodigo = item.getInt("usecodigo");
            this.nome = item.getString("nome");
            this.desc = item.getString("desc");
            this.tipo = item.getString("tipo");
            this.mapa = item.getString("mapa");
            this.latitude = item.getDouble("latitude");
            this.longitude = item.getDouble("longitude");
            if (item.getInt("avaliar") == 1) {
                this.avaliar = true;
            } else this.avaliar = false;
        } catch (Exception e) {
            throw new Exception("(Local: " + e.getMessage() + ")");
        }
    }

    public Local(int usecodigo) {
        this.codigo = 0;
        this.usecodigo = usecodigo;
        this.nome = "";
        this.desc = "";
        this.tipo = "";
        this.mapa = "";
        this.latitude = -2.988153;
        this.longitude = -60.002770;
        this.avaliar = true;
    }

    public void setAll(Local obj) {
        this.nome = obj.getNome();
        this.desc = obj.getDesc();
        this.mapa = obj.getMapa();
        this.tipo = obj.getDesc();
        this.avaliar = obj.isAvaliar();
    }

    public int getCodigo() {
        return codigo;
    }

    public int getUsecodigo() {
        return usecodigo;
    }

    public String getNome() {
        return nome;
    }

    public String getDesc() {
        return desc;
    }

    public String getMapa() {
        return mapa;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLocal(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }

    public String getTipo() {
        return tipo;
    }

    public ArrayList<String> getTipoArray() {
        try {
            ArrayList<String> list = new ArrayList<>();
            JSONArray array = new JSONArray(tipo);
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getString(i));
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public boolean isTipo(String nome) {
        for (String tx : getTipoArray()) {
            if (tx.equalsIgnoreCase(nome)) return true;
        }
        return false;
    }

    public boolean isAvaliar() {
        return avaliar;
    }

    public void setAvaliar(boolean avaliar) {
        this.avaliar = avaliar;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public void setEdit(String n,String d,String m,ArrayList<String> tipos) {
        this.nome = n;
        this.desc = d;
        this.mapa = m;
        Collections.sort(tipos);
        JSONArray array = new JSONArray();
        for (String tx : tipos)array.put(tx);
        this.tipo = array.toString();
    }

    public void setMapa(String mapa) {
        this.mapa = mapa;
    }
}
