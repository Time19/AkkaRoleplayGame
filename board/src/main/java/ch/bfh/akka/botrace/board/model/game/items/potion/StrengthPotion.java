package ch.bfh.akka.botrace.board.model.game.items.potion;

public class StrengthPotion extends Potion {
    private final int effectNumber;
    public StrengthPotion(String name, int weight, int effectNumber) {
        super(name, weight);
        this.effectNumber = effectNumber;
        setItemDescription("Strength Potion: " +
                "\nIncreases max. carry strength / inventory slots by: " + this.effectNumber);
    }


    public boolean use(){
        if(isUsed()){
            System.out.println("Bottle is empty, you already used this potion");
            return false;
        }else{
            // increase carryStrength
            this.getOwner().setCarryStrength(this.getOwner().getCarryStrength() + effectNumber);
            setUsed(true);
            return true;
        }
    }
}
