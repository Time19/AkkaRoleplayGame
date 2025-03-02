package ch.bfh.akka.botrace.board.model.game.items.weapon;

public class Sword extends MeleeWeapon {
    public Sword(String name, int weight, int attackValue, int defenceValue) {
        super(name, weight, attackValue, defenceValue);
        this.setItemDescription("Melee weapon" +
                "\nAttack Strength: " + attackValue +
                "\nDefence Strength: " + defenceValue);
    }
}
