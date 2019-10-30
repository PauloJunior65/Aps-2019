package br.org.aps.classe;

import org.json.JSONObject;

public class User {
    int codigo;
    String nick,email,senha;
    boolean admin;

    public User(JSONObject item) throws Exception {
        try {
            this.codigo = item.getInt("codigo");
            this.nick = item.getString("nick");
            this.email = item.getString("email");
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

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public boolean isAdmin() {
        return admin;
    }
}
