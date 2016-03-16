/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dados;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author 66
 */
public class Viagens {
    
    private Lock lock;
    private Set<Viagem> viagensCondutores;
    private Set<Viagem> viagensPassageiros;
    
    
    
    
    public Viagens(){
        this.viagensCondutores = new HashSet<>();
        this.viagensPassageiros = new HashSet<>();
        this.lock = new ReentrantLock();
    }
    
    
    public Viagens(Set<Viagem> viagensCondutores, Set<Viagem> viagensPassageiros){
        this.viagensCondutores = viagensCondutores;
        this.viagensPassageiros = viagensPassageiros;
        this.lock = new ReentrantLock();
    }
    
        
    
    
    
    
    public boolean haCondutorDisponivel(){
        
        lock.lock();
        boolean haCondutorDisponivel = false;
        try {
            for (Viagem v : this.viagensCondutores){
                if (v.getPassageiro()==null) haCondutorDisponivel = true;
            }
        } finally {
            lock.unlock();
        }
        return haCondutorDisponivel;
    }
    
    
    public boolean haPassageiroDisponivel(){
        
        lock.lock();
        boolean haPassageiroDisponivel = false;
        try{
            for (Viagem v : this.viagensPassageiros){
                if (v.getCondutor()==null) haPassageiroDisponivel = true;
            }
        } finally {
            lock.unlock();
        }
        return haPassageiroDisponivel;
    }
    
    
    
    
    
    
    public Viagem escolherCondutor(int x1, int y1, int x2, int y2, String nome){
        
        lock.lock();
        
        Viagem viagem = null;
        int aux, menorDistancia = Integer.MAX_VALUE;
        
        try {
            if (this.haCondutorDisponivel()){
                for (Viagem v : this.viagensCondutores){
                    if (v.getPassageiro()==null && (aux = distancia(x1, y1, v.getX(), v.getY())) < menorDistancia){
                        viagem = v;
                        menorDistancia = aux;
                    }
                }
                // Atribuir o passageiro ao condutor escolhido
                if (viagem!=null) viagem.atribuirPassageiro(nome, x1, y1, x2, y2);
            }
        } finally {
            lock.unlock();
        }
        return viagem;        
    }
    
    
    public Viagem escolherPassageiro(int x, int y, String nome, String matricula, String modelo){
        
        lock.lock();
        
        Viagem viagem = null;
        int aux, menorDistancia = Integer.MAX_VALUE;
        try {
            if (this.haPassageiroDisponivel()){
                for (Viagem v : this.viagensPassageiros){
                    if (v.getCondutor()==null && (aux = distancia(x, y, v.getX1(), v.getY1())) < menorDistancia){
                        viagem = v;
                        menorDistancia = aux;
                    }
                }
                // Atribuir o condutor ao passageiro escolhido
                if (viagem!=null) viagem.atribuirCondutor(nome, matricula, modelo, x, y);
            }
        } finally {
            lock.unlock();
        }
        return viagem;
    }
    
    
    
    
    
    
    public void adicionarViagemCondutor(Viagem viagem){
        lock.lock();
        try {
            this.viagensCondutores.add(viagem);
        } finally {
            lock.unlock();
        }
    }
    
    public void adicionarViagemPassageiro(Viagem viagem){
        lock.lock();
        try {
            this.viagensPassageiros.add(viagem);
        } finally {
            lock.unlock();
        }
    }
    
    
    public void removerViagemCondutor(Viagem viagem){
        lock.lock();
        try {
            this.viagensCondutores.remove(viagem);
        } finally {
            lock.unlock();
        }
    }
    
    public void removerViagemPassageiro(Viagem viagem){
        lock.lock();
        try {
            this.viagensPassageiros.remove(viagem);
        } finally {
            lock.unlock();
        }
    }
    
    
    
    
    public static int distancia(int x1, int y1, int x2, int y2){
        return Math.abs(x1-x2) + Math.abs(y1-y2);
    }
    
    
}
