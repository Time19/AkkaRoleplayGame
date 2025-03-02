package ch.bfh.akka.botrace.board.model.game.items.ring;


import ch.bfh.akka.botrace.board.model.game.items.Item;
import ch.bfh.akka.botrace.board.model.game.items.Wearable;

public abstract class Ring extends Item implements Wearable {

    public Ring(String name, int weight) {
        super(name, weight);
        this.setWearable(true);
    }

    @Override
    public String itemOverview(){
        String actions = "0 - Go back to inventory\n" +
                "1 - Drop item\n" +
                "2 - Equip item\n" +
                "3 - Uneqip item";

        StringBuilder overview = new StringBuilder();
        overview.append(this.getBaseDescription());
        overview.append("\n\n------------- ACTIONS -------------\n");
        overview.append(actions);
        overview.append("\n-----------------------------------");

        return overview.toString();
    }
}
