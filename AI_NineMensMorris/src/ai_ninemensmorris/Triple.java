/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai_ninemensmorris;

/**
 *
 * @author Caio e Mathias
 */
public class Triple {
    public int x,y,z;
    
    Triple(){
        x=0;
        y=0;
        z=0;
    }
    
    Triple(int a, int b, int c){
        x=a;
        y=b;
        z=c;
    }
    
    public void setFirst(int value){
        x = value;
    }
    
    public void setSecond(int value){
        x = value;
    }
    
    public void setThird(int value){
        z = value;
    }
    
    public int getFirst(){
        return x;
    }
    
    public int getSecond(){
        return y;
    }
    
    public int getThird(){
        return z;
    }
}