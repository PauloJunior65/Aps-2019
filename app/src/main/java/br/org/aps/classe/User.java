package br.org.aps.classe;

import org.json.JSONObject;

public class User {
    int codigo;
    String nick,senha;
    boolean admin;

    public User(JSONObject item) throws Exception {
        try {
            this.codigo = item.getInt("codigo");
            this.nick = item.getString("nick");
            this.senha = item.optString("senha","");
            if (item.getInt("admin")==1){
                this.admin = true;
            }else this.admin = false;
        }catch (Exception e){
            throw new Exception("(User: "+e.getMessage()+")");
        }
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNick() {
        return nick;
    }

    public String getSenha() {
        return senha;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
