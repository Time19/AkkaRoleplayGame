package ch.bfh.akka.botrace.board.model.game.figures;
import ch.bfh.akka.botrace.board.model.game.items.weapon.Bow;

public class Goblin extends Figure implements Attacks, Defends {


    public Goblin(String name, int carryStrength, int healthPoints) {
        super(name, carryStrength, healthPoints, 1.0);
    }

    @Override
    public double attack() {

        // base attack strength from figure (weapon + attackStrength)
        double attack = this.getBaseAttackStrength();

        // for goblins: if using bow -> +50% damage
        if (this.getActiveWeapon() != null) {
            if (this.getActiveWeapon() instanceof Bow) {
                attack = attack * 1.5;
            }
        }
        return attack;
    }
}