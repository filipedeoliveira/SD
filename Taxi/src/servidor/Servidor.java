/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import dados.Utilizadores;
import dados.Viagens;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author 66
 */
public class Servidor {
    
    private static Utilizadores utilizadores;
    private static Viagens viagens;
    
    
    public static void main(String[] args) {
                
        try {            
            ServerSocket servidor = new ServerSocket(2048);
            utilizadores = new Utilizadores();           
            viagens = new Viagens();
                    
            while (true){                            
                Socket cliente = servidor.accept();
                ServidorRunnable clientHandler = new ServidorRunnable(cliente, utilizadores, viagens);
                Thread t = new Thread(clientHandler);
                t.start();
            }      
            
        } catch (IOException ex) {
            System.out.println("Conexao nao permitida!");
        }
        
    }
    
}
