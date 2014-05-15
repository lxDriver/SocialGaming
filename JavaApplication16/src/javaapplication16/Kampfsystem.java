/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication16;

import java.util.HashSet;

/**
 *
 * @author michael
 */
public class Kampfsystem {
    
    private MonsterID player1, player2;
    
    private int[] player1Attacks;
    private int[] player2Attacks;
    
    int lastplayer;
    
    public Kampfsystem(MonsterID player1, MonsterID player2){
        this.player1 = player1;
        this.player2 = player2;
        this.player1Attacks = player1.getAttacks();
        this.player2Attacks = player2.getAttacks();
    }
    
    public MonsterID fight(int player, int Attack){//spieler 1 oder 2, attacke zwischen 1-10 returnt den spieler der verloren hat oder null
        
        //wird auch wirklich abwechselnd gespielt
        
        if(lastplayer == player && player == 1) {
            return player1;
        }
        if(lastplayer == player && player == 2) {
            return player2;
        }
        
        int tmphealth = 100;
        // player 1 attacks player 2 with attack nr
        if(player == 1){
            
            tmphealth = player2.getHealth();
            tmphealth -= player1Attacks[Attack];
            player2.setHealth(tmphealth);
            
            if(tmphealth <=0 ){
                return player2;
            }
        }
        if(player == 2){
            
            
            tmphealth = player1.getHealth();
            tmphealth -= player2Attacks[Attack];
            player1.setHealth(tmphealth);
            
            
            if(tmphealth <=0 ){
                return player1;
            }
        }
        
        
        return null;
    }
            
    
}
