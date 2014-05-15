/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication16;

import java.util.logging.Level;

/**
 *
 * @author michael
 */
public class MonsterID {
    
    private int ID;
    private String Name;
    private int Level;
    private boolean Weather;
    private int[] Attacks = new int[10];
    private int[] WeatherAttacks = new int[10];
    private int Health;
    
    
    public MonsterID(int ID, String Name, int Level) {
        this.ID = ID;
        this.Name = Name;
        this.Level = Level;
        
        /*
         * TODO Vom Server den Rest Laden
         * https get monster by id
         */  
    } 
    public void debugmonster(){
        Weather = true;
        Attacks[0] = 10;
        Attacks[1] = 10;
        Attacks[2] = 10;
        WeatherAttacks[0] = 30;
        WeatherAttacks[1] = 30;
        WeatherAttacks[2] = 30;
        Health = 100;
        }
    
//GET Methoden
    
    
    public int getID() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public int getLevel() {
        return Level;
    }

    public boolean isWeather() {
        return Weather;
    }

    public int[] getAttacks() {
        return Attacks;
    }

    public int[] getWeatherAttacks() {
        return WeatherAttacks;
    }

    public int getHealth() {
        return Health;
    }

//SET Methoden
    
    public void setID(int ID) {
        this.ID = ID;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setLevel(int Level) {
        this.Level = Level;
    }

    public void setWeather(boolean Weather) {
        this.Weather = Weather;
    }

    public void setAttack(int[] Attack) {
        this.Attacks = Attack;
    }

    public void setWeatherAttack(int[] WeatherAttack) {
        this.WeatherAttacks = WeatherAttack;
    }

    public void setHealth(int Health) {
        this.Health = Health;
    }
    
    
    
        
}
