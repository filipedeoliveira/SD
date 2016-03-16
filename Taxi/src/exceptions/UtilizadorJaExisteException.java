/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

/**
 *
 * @author 66
 */
public class UtilizadorJaExisteException extends Exception{
    
    public UtilizadorJaExisteException(){
        super();
    }
    
    public UtilizadorJaExisteException(String s){
        super(s);
    }
    
}