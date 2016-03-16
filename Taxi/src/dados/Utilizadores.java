/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dados;

import exceptions.*;
import java.util.Collection;
import java.util.TreeMap;

/**
 *
 * @author 66
 */
public class Utilizadores {
    
    private TreeMap<String, Utilizador> utilizadores;
    
    
    public Utilizadores(){
        this.utilizadores = new TreeMap<>();
    }
    
    public Utilizadores(TreeMap<String, Utilizador> utilizadores){
        this.utilizadores = utilizadores;
    }
    
    
    
    
    /*  
        ! SITUAÇÃO EM QUE VÁRIAS THREADS TENTAM ADICIONAR UM UTILIZADOR COM O MESMO NOME !
    
        1. Imaginemos que uma thread verifica a condição inicial e é desescalonada
        2. De seguida, outra thread faz o mesmo (verifica a condição inicial e é desescalonada)
        3. A thread inicial volta a executar, adicionando um utilizador
        4. A seguir, a outra thread é escalonada e, assumindo que a condição ainda se verifica,
        adiciona um utilizador com o mesmo nome, fazendo overwrite do anterior
    
        SOLUÇÃO: obter o lock do objeto Utilizadores antes de adicionar um utilizador
        IMPLEMENTAÇÃO: obtem-se o lock implícito (usando synchronized) do 
                       objeto Utilizadores antes de adicionar um utilizador
    */
    
    
    // Método para adicionar passageiros
    public synchronized void adicionarUtilizador(String nome, String password) throws UtilizadorJaExisteException{
        if (!utilizadores.containsKey(nome))
            utilizadores.put(nome, new Utilizador(nome, password));
        else throw new UtilizadorJaExisteException();
    }
    
    // Método para adicionar condutores
    public synchronized void adicionarUtilizador(String nome, String password, String matricula, String modelo) throws UtilizadorJaExisteException{
        if (!utilizadores.containsKey(nome))
            utilizadores.put(nome, new Condutor(nome, password, matricula, modelo));
        else throw new UtilizadorJaExisteException();
    }
    
    
    
    
    /*  
        ! SITUAÇÃO EM QUE VÁRIAS THREADS TENTAM EFETUAR LOGIN(/LOGOUT) COM O MESMO NOME !
    
        1. Imaginemos que uma thread verifica que o utilizador esta offline e é desescalonada
        2. De seguida, outra thread faz o mesmo (verifica que o utilizador esta offline e é desescalonada)
        3. A thread inicial volta a executar, efetuando login nesse utilizador
        4. A seguir, a outra thread é escalonada e, assumindo que o utilizador esta offline,
        efetua login com o mesmo nome, associando-se à mesma sessao de utilizador
    
        SOLUÇÃO: obter o lock do utilizador antes de efetuar login(/logout)
        IMPLEMENTAÇÂO: métodos "logIn" e "logOut" da classe Utilizador são synchronized
    */
    
    
    
    // Ninguém pode efetuar logIn no mesmo utilizador em simultâneo,
    // já que o método chamado sobre o utilizador é synchronized
    public Utilizador logIn(String nome, String password) throws UtilizadorNaoExisteException, UtilizadorOnlineException, PasswordIncorretaException{
        Utilizador u;
        if (utilizadores.containsKey(nome)){
            u = utilizadores.get(nome);
            // Método "logIn" da classe Utilizador é synchronized
            u.logIn(password);
        }
        else throw new UtilizadorNaoExisteException();
        return u;
    }
    
    // Ninguém pode efetuar logOut no mesmo utilizador em simultâneo,
    // já que o método chamado sobre o utilizador é synchronized    
    public void logOut(String nome) throws UtilizadorNaoExisteException, UtilizadorOfflineException{
        if (utilizadores.containsKey(nome)){
            Utilizador u = utilizadores.get(nome);
            // Método "logOut" da classe Utilizador é synchronized
            u.logOut();
        }
        else throw new UtilizadorNaoExisteException();
    }
    
    
    
        
    
    
    
    
    
    
    
    
    /*  -----------------------------------------------------------------
        --- Invocações simultâneas de "logIn" e "adicionarUtilizador" ---
        -----------------------------------------------------------------
    
        a) Imaginemos que já existe o utilizador:
            Caso 1: "logIn" verifica que existe; "adicionarUtilizador" verifica que existe; "logIn" faz login; "adicionarUtilizador" pára;
            Caso 2: "adicionarUtilizador" verifica que existe; "logIn" verifica que existe; "adicionarUtilizador" pára; "logIn" faz login; 
        b) Imaginemos que não existe o utilizador:
            Caso 1: "logIn" verifica que não existe; "adicionarUtilizador" verifica que não existe; "logIn" pára; "adicionarUtilizador" adiciona
            Caso 2: "adicionarUtilizador" verifica que não existe; "logIn" verifica que não existe; "adicionarUtilizador" adiciona; "logIn" pára; 
    
    
        É possível invocar "logIn"/"logOut" e "adicionarUtilizador" em simultâneo, já que um é synchronized e os outros não.
    
    */
    
    
    
}
