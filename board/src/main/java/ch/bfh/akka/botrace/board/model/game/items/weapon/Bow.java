package ch.bfh.akka.botrace.board.model.game.items.weapon;

public class Bow extends RangedWeapon {


    public Bow(String name, int weight, int attackValue) {
        super(name, weight, attackValue);
        this.setItemDescription("Ranged weapon" +
                "\nAttack Strength: " + attackValue);
    }
}