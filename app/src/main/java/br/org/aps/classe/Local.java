package br.org.aps.classe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Local {
    int codigo,usecodigo;
    String nome,desc,mapa,tipo;
    boolean avaliar;

    public Local(JSONObject item) throws Exception {
        try {
            this.codigo = item.getInt("codigo");
            this.usecodigo = item.getInt("usetipo");
            this.nome = item.getString("nome");
            this.desc = item.getString("desc");
            this.mapa = item.getString("mapa");
            this.tipo = item.getString("tipo");
            if (item.getInt("avaliar") == 1){
                this.avaliar = true;
            }else this.avaliar = false;
        }catch (Exception e){
            throw new Exception("(Local: "+e.getMessage()+")");
        }
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

    public ArrayList<String> getTipo() {
        try {
            ArrayList<String> list = new ArrayList<>();
            JSONArray array = new JSONArray(tipo);
            for (int i = 0; i<array.length();i++){
                list.add(array.getString(i));
            }
            return list;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public boolean isTipo(String nome){
        for (String tx : getTipo()){
            if (tx.equalsIgnoreCase(nome))return true;
        }
        return false;
    }

    public boolean isAvaliar() {
        return avaliar;
    }

    public void setAvaliar(boolean avaliar) {
        this.avaliar = avaliar;
    }
}
