/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dados;

import exceptions.PasswordIncorretaException;
import exceptions.UtilizadorOfflineException;
import exceptions.UtilizadorOnlineException;

/**
 *
 * @author 66
 */
public class Utilizador {
    
    private String nome;
    private String password;
    private boolean online;
    
    
    public Utilizador(String nome, String password){
        this.nome = nome;
        this.password = password;
        this.online = false;
    }
    
    public Utilizador(Utilizador u){
        this.nome = u.nome;
        this.password = u.password;
        this.online = u.online;
    }
    
    
    
    
    public synchronized void logIn(String password) throws UtilizadorOnlineException, PasswordIncorretaException{
        if (this.online) throw new UtilizadorOnlineException();
        else if (!this.password.equals(password)) throw new PasswordIncorretaException();
        else this.online = true;
    }
    
    public synchronized void logOut() throws UtilizadorOfflineException{
        if (!this.online) throw new UtilizadorOfflineException();
        else this.online = false;
    }
    
    
}
