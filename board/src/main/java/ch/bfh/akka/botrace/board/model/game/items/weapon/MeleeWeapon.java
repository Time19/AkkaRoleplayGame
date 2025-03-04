package ch.bfh.akka.botrace.board.model.game.items.weapon;

import lombok.Getter;

@Getter
public abstract class MeleeWeapon extends Weapon{
    private final double defenceValue;

    public MeleeWeapon(String name, int weight, double attackValue, double defenceValue) {
        super(name, weight, attackValue);
        this.defenceValue = defenceValue;
    }
}
