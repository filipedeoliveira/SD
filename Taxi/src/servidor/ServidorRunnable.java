/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;


import dados.Utilizador;
import dados.Utilizadores;
import dados.Viagem;
import dados.Viagens;
import exceptions.PasswordIncorretaException;
import exceptions.UtilizadorJaExisteException;
import exceptions.UtilizadorNaoExisteException;
import exceptions.UtilizadorOfflineException;
import exceptions.UtilizadorOnlineException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author 66
 */
public class ServidorRunnable implements Runnable{
    
    private String nome;
    private Socket cliente;
    private Utilizador utilizador;
    private PrintWriter outputServidor;
    private BufferedReader inputCliente;
    
    private Utilizadores utilizadores;
    private Viagens viagens;
              
    
    
    
    public ServidorRunnable(Socket cliente, Utilizadores utilizadores, Viagens viagens) throws IOException{   
        this.nome = null;
        this.utilizador = null;
        this.cliente = cliente;
        this.utilizadores = utilizadores;
        this.viagens = viagens;
        this.outputServidor = new PrintWriter(cliente.getOutputStream());
        this.inputCliente = new BufferedReader(new InputStreamReader(cliente.getInputStream()));             
    }
    
    

    @Override
    public void run() {
        
        try {
            String mensagem;
            this.outputServidor = new PrintWriter(cliente.getOutputStream());
            this.inputCliente = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

            while ((mensagem = inputCliente.readLine()) != null){
                System.out.println("[" + cliente.getPort() + "] " + mensagem);
                this.executarComando(mensagem);
            }   
            
        } catch (IOException ex) { // Caso o cliente se desligue espontaneamente, com sessao ativa, efetua-se o logout
            System.out.println(cliente.getPort() + " desligou-se!");
            if (this.nome!=null) try {
                utilizadores.logOut(this.nome);
            } catch (UtilizadorNaoExisteException | UtilizadorOfflineException ex1) {}
                
            
        } finally {
            outputServidor.close();
        }
        
    }
    
    
    
    
    public void executarComando(String mensagem){
        
        String[] args = mensagem.split("\\s+");
        String comando = args[0].toLowerCase();
        int nArgs = args.length;
        
        switch (comando){
            
            
            // Formato do registo: "registar nome password"
            case "registar": {
                if (nArgs!=3 && nArgs!=5){
                    outputServidor.println("Numero de argumentos invalido!");
                    outputServidor.flush();
                    break;
                }
                try {
                    if (nArgs==3) utilizadores.adicionarUtilizador(args[1], args[2]);
                    else utilizadores.adicionarUtilizador(args[1], args[2], args[3], args[4]);
                    outputServidor.println("Utilizador adicionado");
                    outputServidor.flush();
                } catch (UtilizadorJaExisteException ex) {
                    outputServidor.println("O utilizador \"" + args[1] + "\" ja existe!");
                    outputServidor.flush();
                }
                break;
            }
            
            
            
            
            // Formato do login: "login nome password"
            case "login": {
                if (this.nome!=null) {
                    outputServidor.println("Ja se encontra online!");
                    outputServidor.flush();
                    break;
                }
                if (nArgs<3){
                    outputServidor.println("Numero de argumentos invalido!");
                    outputServidor.flush();
                    break;
                }
                try {
                    this.utilizador = utilizadores.logIn(args[1], args[2]);
                    this.nome = args[1];
                    outputServidor.println("Login efetuado com sucesso");
                    outputServidor.flush();
                } catch (UtilizadorNaoExisteException ex) {
                    outputServidor.println("O utilizador \"" + args[1] + "\" nao existe!");
                    outputServidor.flush();
                } catch (UtilizadorOnlineException ex) {
                    outputServidor.println("O utilizador \"" + args[1] + "\" ja esta online!");
                    outputServidor.flush();
                } catch (PasswordIncorretaException ex) {
                    outputServidor.println("A password esta incorreta!");
                    outputServidor.flush();
                }
                break;
            }

            
            
            
            // Formato do logout: "logout"
            case "logout": {
                if (this.nome==null){
                    outputServidor.println("Nao se encontra online!");
                    outputServidor.flush();
                    break;
                }
                else try {
                    utilizadores.logOut(this.nome);
                    this.nome = null;
                    this.utilizador = null;
                    outputServidor.println("Logout efetuado com sucesso");
                    outputServidor.flush();
                } catch (UtilizadorOfflineException ex) {
                    outputServidor.println("Nao se encontra online!");
                    outputServidor.flush();
                } catch (UtilizadorNaoExisteException ex) {}
                break;
            }
            
            
            
            
            // Formato da solicitação de um táxi: "solicitar x1 y1 x2 y2"
            case "solicitar": {
                
                if (this.nome==null){
                    outputServidor.println("Nao se encontra online!");
                    outputServidor.flush();
                    break;
                }
                if (nArgs<5){
                    outputServidor.println("Numero de argumentos invalido!");
                    outputServidor.flush();
                    break;
                }               
                // VERIFICAR SE É PASSAGEIRO
                
                int x1 = Integer.parseInt(args[1]), y1 = Integer.parseInt(args[2]);
                int x2 = Integer.parseInt(args[3]), y2 = Integer.parseInt(args[4]);
                
                // Verificar se há algum condutor disponível
                // Escolher o condutor disponível mais próximo
                Viagem viagem = this.viagens.escolherCondutor(x1, y1, x2, y2, this.nome);  
                
                if (viagem!=null){  
                    // Simular a viagem
                    simularViagemPassageiro(viagem);
                    // Neste caso, quem elimina o registo da viagem é o condutor
                }
                
                else{
                    // Caso nao haja condutores disponíveis, cria um registo e espera
                    viagem = new Viagem(this.nome, x1, y1, x2, y2);
                    this.viagens.adicionarViagemPassageiro(viagem);
                    
                    try {
                        viagem.esperarAtribuicaoCondutor();
                    } catch (InterruptedException ex) {}
                    // Simular a viagem
                    simularViagemPassageiro(viagem);
                    // Elimina o registo da viagem anteriormente criada
                    this.viagens.removerViagemPassageiro(viagem);
                }
            
                break;              
            }
            
            
            
            
            // Formato do anúncio da disponibilidade para conduzir: "anunciar x y"
            case "anunciar": {
                
                if (this.nome==null){
                    outputServidor.println("Nao se encontra online!");
                    outputServidor.flush();
                    break;
                }                
                if (nArgs<3){
                    outputServidor.println("Numero de argumentos invalido!");
                    outputServidor.flush();
                    break;
                }
                // VERIFICAR SE É MOTORISTA
                if (!(this.utilizador instanceof dados.Condutor)){
                    outputServidor.println("Nao se encontra autenticado como condutor!");
                    outputServidor.flush();
                    break;
                }
                
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                
                // Verificar se há passageiros disponíveis
                // Escolher o passageiro em espera mais próximo
                Viagem viagem = this.viagens.escolherPassageiro(x, y, this.nome, ((dados.Condutor)this.utilizador).getMatricula(), ((dados.Condutor)this.utilizador).getModelo()); 
                
                if (viagem!=null){                                   
                    // Simular a viagem 
                    simularViagemCondutor(viagem);
                    // Neste caso, quem elimina o registo da viagem é o passageiro
                }
                
                else{
                    // Caso nao haja passageiros em espera, cria um registo e espera
                    viagem = new Viagem(this.nome, ((dados.Condutor)this.utilizador).getMatricula(), ((dados.Condutor)this.utilizador).getModelo(), x, y);                
                    this.viagens.adicionarViagemCondutor(viagem);

                    try{
                        viagem.esperarAtribuicaoPassageiro();
                        // Simular a viagem
                        simularViagemCondutor(viagem);
                        // Elimina o registo da viagem anteriormente criada
                        this.viagens.removerViagemCondutor(viagem);
                    } catch (InterruptedException ex) {
                        outputServidor.println("Ocorreu um erro!");
                        outputServidor.flush();
                    }  
                }
                
                break;
            }
            
            
            
            
            // Caso o cliente não tenha pedido o logout, o servidor tem que faze-lo
            case "sair": {
                try {
                    if (this.nome!=null) {
                        utilizadores.logOut(this.nome);
                        nome = null;
                    }
                } catch (UtilizadorOfflineException | UtilizadorNaoExisteException ex) {}
                break;
            }
                        
            
            
            
            // Comando inexistente
            default : {
                outputServidor.println("Comando inexistente!");
                outputServidor.flush();
            }            
            
        }        
        
    }
    
    
    
    
        
    
    
    
    
    public void simularViagemCondutor(Viagem viagem){
        
        // Informar o processo do condutor que uma viagem começou
        outputServidor.println("CONDUTOR");
        outputServidor.flush();
        
        outputServidor.println(viagem.getPassageiro());                        
        outputServidor.println(viagem.getX1());                        
        outputServidor.println(viagem.getY1());                        
        outputServidor.println(viagem.getX2()); 
        outputServidor.println(viagem.getY2());
        outputServidor.flush();
        
        try {
            String aux = null;
            while (!(aux = inputCliente.readLine()).equals("fim")){
                if (aux.equals("ok")){
                    viagem.anunciarChegadaDoCondutor();
                    System.out.println("[" + cliente.getPort() + "]  (Condutor chegou)");
                }
            }
            viagem.anunciarChegadaAoDestino();
            System.out.println("[" + cliente.getPort() + "]  (Viagem acabou)");
            
        } catch (IOException ex) {}
        
    }
    
    
    
    public void simularViagemPassageiro(Viagem viagem){
    
        try {
            // Informar o processo do passageiro que uma viagem começou
            outputServidor.println("PASSAGEIRO");
            outputServidor.flush();
            
            outputServidor.println(viagem.getCondutor());
            outputServidor.println(viagem.getMatricula());
            outputServidor.println(viagem.getModelo());
            outputServidor.println(viagem.tempoEspera());
            outputServidor.flush();

            viagem.esperarChegadaDoCondutor();
            outputServidor.println("");
            outputServidor.flush();

            viagem.esperarChegadaAoDestino();
            outputServidor.println(viagem.preco());
            outputServidor.flush();
            
        } catch (InterruptedException ex) {}
        
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*
    public void simularViagemCondutor2(Viagem viagem){
        
        // Informar o processo do condutor que uma viagem começou
        outputServidor.println("CONDUTOR");
        outputServidor.flush();
        
        outputServidor.println("-------------------------------------");
        outputServidor.println("Passageiro: \"" + viagem.getPassageiro() + "\"");                        
        outputServidor.println("Origem: " + viagem.getX1() + "," + viagem.getY1());                        
        outputServidor.println("Destino: " + viagem.getX2() + "," + viagem.getY2()); 
        outputServidor.println("-------------------------------------");
        
        outputServidor.println("FIM");  
        outputServidor.flush();
        
        try {
            String aux = null;
            while (!(aux = inputCliente.readLine()).equals("fim")){
                if (aux.equals("ok")){
                    viagem.anunciarChegadaDoCondutor();
                    System.out.println("[" + cliente.getPort() + "]  ( Condutor chegou )");
                }
            }
            viagem.anunciarChegadaAoDestino();
            System.out.println("[" + cliente.getPort() + "]  ( Viagem acabou )");
            
        } catch (IOException ex) {}
        
    }
    
    
    
    
    
    public void simularViagemPassageiro2(Viagem viagem){
    
        try {
            // Informar o processo do passageiro que uma viagem começou
            outputServidor.println("PASSAGEIRO");
            outputServidor.flush();
            
            outputServidor.println("-------------------------------------");
            outputServidor.println("Condutor: \"" + viagem.getCondutor() + "\"");
            outputServidor.println("Matricula: \"" + viagem.getMatricula() + "\"");
            outputServidor.println("Modelo: \"" + viagem.getModelo() + "\"");
            outputServidor.println("Tempo de espera estimado: " + viagem.tempoEspera());
            outputServidor.flush();

            viagem.esperarChegadaDoCondutor();
            outputServidor.println("-------------------------------------");
            outputServidor.println("A viatura chegou ao local de partida!");
            outputServidor.flush();

            viagem.esperarChegadaAoDestino();
            outputServidor.println("-------------------------------------");
            outputServidor.println("Chegou ao seu destino! Preço: " + viagem.preco());
            outputServidor.println("-------------------------------------");
            
            outputServidor.println("FIM");
            outputServidor.flush();
            
        } catch (InterruptedException ex) {}
        
    }
    */
    
    
    
}
