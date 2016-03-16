/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dados;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author 66
 */
public class Viagem {
    
    private Lock lock;
    
    // ?? Fundir as Condition "semCondutor" e "semPassageiro" ??
    //    Apenas uma dessas Condition é utilizada em cada viagem...
    //    Ou seja, apenas um (passageiro ou condutor) é que espera pela atribuição do outro
    
    private Condition semCondutor;
    private Condition semPassageiro;
    private Condition condutorNaoChegou;
    private Condition viagemNaoAcabou;

    private String condutor;    
    private String matricula;
    private String modelo;
    private String passageiro;
    private int x, y, x1, y1, x2, y2;
    private boolean condutorChegou; // FALSE até à chegada do motorista, mesmo após a atribuição do mesmo
    private boolean acabada; // FALSE até à chegada ao destino
    
    

    
    // Construtor para condutores
    public Viagem(String condutor, String matricula, String modelo, int x, int y){
        this.condutor = condutor;
        this.matricula = matricula;
        this.modelo = modelo;
        this.passageiro = null;
        this.condutorChegou = false;
        this.acabada = false;
        this.x = x;
        this.y = y;
        this.x1 = -1;
        this.y1 = -1;
        this.x2 = -1;
        this.y2 = -1;    
        this.lock = new ReentrantLock();
        this.semCondutor = this.lock.newCondition();
        this.semPassageiro = this.lock.newCondition();
        this.viagemNaoAcabou = this.lock.newCondition();
        this.condutorNaoChegou = this.lock.newCondition();
    }        
    
    // Construtor para passageiros
    public Viagem(String passageiro, int x1, int y1, int x2, int y2){
        this.condutor = null;
        this.matricula = null;
        this.modelo = null;
        this.passageiro = passageiro;
        this.condutorChegou = false;
        this.acabada = false;
        this.x = -1;
        this.y = -1;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;        
        this.lock = new ReentrantLock();
        this.semCondutor = this.lock.newCondition();
        this.semPassageiro = this.lock.newCondition();
        this.viagemNaoAcabou = this.lock.newCondition();
        this.condutorNaoChegou = this.lock.newCondition();
    }
    
    
    
    public int getX(){
        lock.lock();
        try {
            return this.x;
        } finally {
            lock.unlock();
        }
    }
    
    public int getY(){ 
        lock.lock();
        try {
            return this.y;
        } finally {
            lock.unlock();
        }
    }
    
    public int getX1(){
        lock.lock();
        try {
            return this.x1;
        } finally {
            lock.unlock();
        }
    }
    
    public int getY1(){
        lock.lock();
        try {
            return this.y1;
        } finally {
            lock.unlock();
        }
    }
    
    public int getX2(){ 
        lock.lock();
        try {
            return this.x2;
        } finally {
            lock.unlock();
        }
    }
    
    public int getY2(){ 
        lock.lock();
        try {
            return this.y2;
        } finally {
            lock.unlock();
        }
    }
    
    public String getPassageiro(){ 
        lock.lock();
        try {
            return this.passageiro;
        } finally {
            lock.unlock();
        } 
    }
    
    public String getCondutor(){ 
        lock.lock();
        try {
            return this.condutor;
        } finally {
            lock.unlock();
        }
    }
    
    public String getMatricula(){ 
        lock.lock();
        try {
            return this.matricula;
        } finally {
            lock.unlock();
        }
    }
    
    public String getModelo(){ 
        lock.lock();
        try {
            return this.modelo;
        } finally {
            lock.unlock();
        } 
    }
    
    
    
    
    
    
    
    public void atribuirCondutor(String condutor, String matricula, String modelo, int x, int y){
        this.lock.lock();
        try {
            this.condutor = condutor;
            this.matricula = matricula;
            this.modelo = modelo;
            this.x = x;
            this.y = y;
            semCondutor.signalAll(); // "semCondutor.signal()" TAMBÉM SERVE?
        } finally {
            this.lock.unlock();
        }
    }        
    
    public void atribuirPassageiro(String passageiro, int x1, int y1, int x2, int y2){
        this.lock.lock();
        try {
            this.passageiro = passageiro;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            semPassageiro.signalAll(); // "semPassageiro.signal()" TAMBÉM SERVE?
        } finally {
            this.lock.unlock();
        }
    }
       
    
       

    public void esperarAtribuicaoCondutor() throws InterruptedException {
        this.lock.lock();
        try {
            while (this.condutor==null){
                semCondutor.await();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void esperarAtribuicaoPassageiro() throws InterruptedException {
        this.lock.lock();
        try {
            while (this.passageiro==null){
                semPassageiro.await();
            }
        } finally {
            this.lock.unlock();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    
    public void anunciarChegadaDoCondutor(){
        this.lock.lock();
        try {
            this.condutorChegou = true;
            condutorNaoChegou.signalAll();  // "condutorNaoChegou.signal()" TAMBÉM SERVE?
        } finally {
            this.lock.unlock();
        }
    }
    
    public void anunciarChegadaAoDestino(){
        this.lock.lock();
        try {
            this.acabada = true;
            viagemNaoAcabou.signalAll();  // "viagemNaoAcabou.signal()" TAMBÉM SERVE?
        } finally {
            this.lock.unlock();
        }
    }
    
    
    
    
    public void esperarChegadaDoCondutor() throws InterruptedException{
        this.lock.lock();
        try {
            while (this.condutorChegou==false){
                condutorNaoChegou.await();
            }
        } finally {
            this.lock.unlock();
        }
    }
    
    public void esperarChegadaAoDestino() throws InterruptedException{
        this.lock.lock();
        try {
            while (this.acabada==false){
                viagemNaoAcabou.await();
            }
        } finally {
            this.lock.unlock();
        }
    }
    
    
    
    // Usar o Lock aqui? Nenhuma thread invoca funções sobre o objeto ao mesmo tempo que esta é invocada
    public float preco(){
        return (float) ((Math.abs(this.x1-this.x2) + Math.abs(this.y1-this.y2))*0.33) + 2;
    }
    
    
    // Usar o Lock aqui? Nenhuma thread invoca funções sobre o objeto ao mesmo tempo que esta é invocada
    public String tempoEspera(){
        
        int distancia = Math.abs(this.x-this.x1) + Math.abs(this.y-this.y1);
        int segundos = 4*(distancia), horas = segundos/3600, minutos = (segundos-(horas*3600))/60;
        segundos -= (horas*3600) + (minutos*60);
        
        StringBuilder sb = new StringBuilder();
        
        if (horas>0) { sb.append(horas); sb.append("h "); }
        if (minutos>0 || horas>0) { sb.append(minutos); sb.append("m "); }
        if (segundos>=0) { sb.append(segundos); sb.append("s"); }
        
        return sb.toString();
    }
    
}
