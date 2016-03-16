/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
package cliente;

import java.io.*;
import java.net.Socket;

/**
 *
 * @author 66
 */
public class Cliente {
    
    private static Socket cliente;
    private static PrintWriter out;  
    private static BufferedReader in;
    private static BufferedReader stdIn;
    
    
    
    
    // Durante uma viagem, o passageiro nao fornece input, apenas lê o output do servidor
    private static void simularViagemPassageiro() throws IOException{
        
        // Lê e apresenta as informações sobre o passageiro e a viagem 
        System.out.println("[Viagem] -------------------------------------");
        System.out.println("[Viagem] Condutor: \"" + in.readLine() + "\"");
        System.out.println("[Viagem] Matricula: \"" + in.readLine() + "\"");
        System.out.println("[Viagem] Modelo: \"" + in.readLine() + "\"");
        System.out.println("[Viagem] Tempo de espera estimado: " + in.readLine());
        
        //Para saber que a viatura chegou
        in.readLine();
        System.out.println("[Viagem] -------------------------------------");
        System.out.println("[Viagem] A viatura chegou ao local de partida!");
        System.out.println("[Viagem] -------------------------------------");
        
        System.out.println("[Viagem] Chegou ao seu destino! Preço: " + in.readLine());
        System.out.println("[Viagem] -------------------------------------");
            
    }
    
    
   
    
    
    
    private static void simularViagemCondutor() throws IOException{
                
        // Lê e apresenta as informações sobre o passageiro e a viagem        
        System.out.println("[Viagem] -------------------------------------");
        System.out.println("[Viagem] Passageiro: \"" + in.readLine() + "\"");                        
        System.out.println("[Viagem] Origem: " + in.readLine() + "," + in.readLine());                        
        System.out.println("[Viagem] Destino: " + in.readLine() + "," + in.readLine()); 
        System.out.println("[Viagem] -------------------------------------");
          
        System.out.print("[Viagem] > ");
        
        String comando;
        boolean ok = false;
        while (!(comando = stdIn.readLine()).equals("fim") || !ok){
            if (comando.equals("ok")) {
                if (!ok){ 
                    // Envia o comando ao servidor
                    out.println(comando);
                    out.flush();
                }
                ok = true;
            }
            System.out.print("[Viagem] > ");
        }
        
        System.out.println("[Viagem] -------------------------------------");
        out.println(comando); // Envia o comando "fim" ao servidor
        out.flush();   
        
    }
    
    
    
    
    public static void main(String[] args) throws Exception{
                        
        try {
            String comando, resposta;
            cliente = new Socket("localhost", 2048);            
            out = new PrintWriter(cliente.getOutputStream());
            in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.print("> ");        
            
            // Lê um comando do teclado
            while (!(comando = stdIn.readLine()).equals("sair")){ 
                
                // Envia o comando ao servidor
                out.println(comando);
                out.flush();
                
                // Lê a resposta do servidor
                switch (resposta = in.readLine()){
                    case "PASSAGEIRO": {
                        simularViagemPassageiro(); 
                        break;
                    }        
                    case "CONDUTOR": {
                        simularViagemCondutor(); 
                        break;
                    }
                    default: {
                        // Mostra ao cliente a resposta do servidor
                        System.out.println(resposta);
                    }
                }
                System.out.print("> ");
            }
            
            // Envia a mensagem "sair" ao servidor para efetuar o logout do utilizador
            out.println(comando);
            out.flush();            
            
            cliente.shutdownInput();
            cliente.shutdownOutput();
            cliente.close();
            
        } catch (Exception e){
            System.out.println("Servidor offline!");
        }
                
    }
    
    
    
    
    
    
    
    
    /*
    private static void simularViagemPassageiro2() throws IOException{
        String resposta;
        while (!(resposta = in.readLine()).equals("FIM")){
            System.out.println("[Viagem] " + resposta);
        } 
    }
    
    private static void simularViagemCondutor2() throws IOException{
        
        String comando, resposta;
        
        while (!(resposta = in.readLine()).equals("FIM")){  // Lê as informações sobre o passageiro e a viagem
            System.out.println("[Viagem] " + resposta);
        }
        System.out.print("[Viagem] > ");
        boolean ok = false;
        while (!(comando = stdIn.readLine()).equals("fim") || !ok){
            if (comando.equals("ok")) {
                if (!ok){ // Envia o comando ao servidor
                    out.println(comando);
                    out.flush();
                }
                ok = true;
            }
            System.out.print("[Viagem] > ");
        }
        System.out.println("[Viagem] -------------------------------------");
        out.println(comando); // Envia o comando "fim" ao servidor
        out.flush();   
        
    }
    */
}
