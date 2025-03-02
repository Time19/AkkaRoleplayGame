package ch.bfh.akka.botrace.board.model.game.figures;

import ch.bfh.akka.botrace.board.model.game.items.weapon.Club;

public class Troll extends Figure implements Attacks, Defends {


    public Troll(String name, int carryStrength, int healthPoints) {
        super(name, carryStrength, healthPoints,1.0);
    }

    @Override
    public double attack() {

        // base attack strength from figure (weapon + attackStrength)
        double attack = this.getBaseAttackStrength();

        // for troll: if using club -> +50% damage
        if (this.getActiveWeapon() != null) {
            if (this.getActiveWeapon() instanceof Club) {
                attack = attack * 2;
            }
        }
        return attack;
    }


}