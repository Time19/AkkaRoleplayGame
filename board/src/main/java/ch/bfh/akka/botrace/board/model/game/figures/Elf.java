package ch.bfh.akka.botrace.board.model.game.figures;
import ch.bfh.akka.botrace.board.model.game.items.weapon.Bow;


import lombok.Getter;

@Getter
public class Elf extends Figure implements Attacks, Defends {
    private final double magicStrength;

    public Elf(String name, int carryStrength, int healthPoints, int magicStrength) {
        super(name, carryStrength, healthPoints, 1.0);
        this.magicStrength = magicStrength;
    }


    @Override
    public double attack() {
        // base attack strength from figure
        double attack = this.getBaseAttackStrength();

        // for elfs: add half of magic strength
        attack += (this.magicStrength / 2);

        // for elfs: if using bow -> +50% damage
        if(this.getActiveWeapon()!=null){
            if(this.getActiveWeapon() instanceof Bow){
                attack = attack * 1.5;
            }
        }

        return attack;
    }
}