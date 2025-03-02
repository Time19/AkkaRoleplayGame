package ch.bfh.akka.botrace.board.model.game.items.potion;


public class HealthPotion extends Potion {
    private final int effectNumber;
    public HealthPotion(String name, int weight, int effectNumber) {
        super(name, weight);
        this.effectNumber = effectNumber;
        setItemDescription("Health Potion" +
                "\nHeals Figure for: " + effectNumber + " HP");
    }

    public boolean use(){
        if(this.isUsed()){
            System.out.println("Bottle is empty, you already used this potion");
            return false;
        }else{
            // heal owner
            this.getOwner().setHealthPoints(this.getOwner().getHealthPoints() + effectNumber);
            setUsed(true);
            return true;
        }
    }
}
