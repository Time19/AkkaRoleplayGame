package ch.bfh.akka.botrace.board.model.game.figures;

public class Ork extends Figure implements Attacks, Defends {
    private boolean rageMode = false;

    public Ork(String name, int carryStrength, int healthPoints) {
        super(name, carryStrength, healthPoints, 1.0);
    }


    /**
     * If ork in rageMode -> attack damage triples
     * @return
     */
    @Override
    public double attack(){
        if(this.rageMode){
            return this.getBaseAttackStrength() * 3;
        }else{
            return this.getBaseAttackStrength();
        }
    }

    /**
     * if ork in rageMode -> only takes half the damage
     * @param attackStrength
     * @return
     */
    @Override
    public double defend(double attackStrength){
        double attack = this.getBaseDefence(attackStrength);
        this.setHealthPoints(this.getHealthPoints()-attack);
        checkRageMode();

        return attack;
    }


    /**
     * updates rageMode
     * TODO: call checkRageMode after turn
     */
    public void checkRageMode(){
        if(this.getHealthPoints() < getMaxHealth() * 0.25){
            this.rageMode = true;
        }else{
            this.rageMode = false;
        }
    }
}