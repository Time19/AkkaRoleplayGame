package ch.bfh.akka.botrace.board.model.game.items.weapon;


public class Club extends MeleeWeapon {
    public Club(String name, int weight, double attackValue, double defenceValue) {
        super(name, weight, attackValue, defenceValue);
        this.setItemDescription("Melee weapon" +
                "\nAttack Strength: " + attackValue +
                "\nDefence Strength: " + defenceValue);
    }
}
