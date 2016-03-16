/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dados;

/**
 *
 * @author 66
 */
public class Condutor extends Utilizador{
    
    private String matricula;
    private String modelo;
    
    
    public Condutor(String nome, String password, String matricula, String modelo){
        super(nome, password);
        this.matricula = matricula;
        this.modelo = modelo;
    }
    
    
    
    
    public synchronized String getMatricula(){
        return this.matricula;
    }
    public synchronized String getModelo(){
        return this.modelo;
    }
    
    public synchronized void setMatricula(String matricula){
        this.matricula = matricula;
    }
    public synchronized void setModelo(String modelo){
        this.modelo = modelo;
    }
    
}
