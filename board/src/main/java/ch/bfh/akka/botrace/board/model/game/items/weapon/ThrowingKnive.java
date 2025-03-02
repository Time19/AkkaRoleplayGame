package ch.bfh.akka.botrace.board.model.game.items.weapon;

public class ThrowingKnive extends RangedWeapon {
    public ThrowingKnive(String name, int weight, int attackValue) {
        super(name, weight, attackValue);
        this.setItemDescription("Ranged weapon" +
                "\nAttack Strength: " + attackValue);
    }
}
