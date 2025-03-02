package ch.bfh.akka.botrace.board.model.game.items.weapon;

import lombok.Getter;

@Getter
public abstract class MeleeWeapon extends Weapon{
    private final int defenceValue;

    public MeleeWeapon(String name, int weight, int attackValue, int defenceValue) {
        super(name, weight, attackValue);
        this.defenceValue = defenceValue;
    }
}
