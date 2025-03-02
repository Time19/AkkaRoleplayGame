package ch.bfh.akka.botrace.board.model.game.items.ring;

import lombok.Getter;

@Getter
public class DefenceRing extends Ring {
    private final double ringStrength; // must be decimal bsp 0.1 -> 10% damage reduction
    public DefenceRing(String name, int weight, double ringStrength) {
        super(name, weight);
        this.ringStrength = ringStrength;
        setItemDescription("DefenseRing:" +
                "Reduces taken damage by " + ringStrength*100 + "%");
    }

    public boolean wear(){
        getOwner().setActiveRing(this);
        return true;
    }

    public void unwear(){
        getOwner().setActiveRing(null);
    }
}
