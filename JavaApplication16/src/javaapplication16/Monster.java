/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication16;

/**
 *
 * @author michael
 */
public class Monster {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MonsterID looser = null;
        
        MonsterID player1 = new MonsterID(2,"Michi", 5);
        MonsterID player2 = new MonsterID(3,"Kevin", 6);
        
        Kampfsystem kampf = new Kampfsystem(player1, player2);
        
        while(looser == null){
        looser = kampf.fight(1, 1);
        }
        
        if(looser == player1)
            System.out.println(player1.getName() +" hat verloren");
        if(looser == player2)
            System.out.println(player2.getName() +" hat verloren");
        
    }
}
