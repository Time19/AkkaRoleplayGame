package ch.bfh.akka.botrace.board.model.game.items.ring;

import lombok.Getter;

@Getter
public class PowerRing extends Ring {
    private final double ringStrength;
    public PowerRing(String name, int weight, double ringStrength) {
        super(name, weight);
        this.ringStrength = ringStrength;
        setItemDescription("PowerRing:" +
                "Increases attack strength by " + ringStrength*100 + "%");
    }

    public boolean wear(){
        getOwner().setActiveRing(this);
        return true;
    }

    public void unwear(){
        getOwner().setActiveRing(null);
    }
}
