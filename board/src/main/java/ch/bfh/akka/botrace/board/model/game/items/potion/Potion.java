package ch.bfh.akka.botrace.board.model.game.items.potion;

import ch.bfh.akka.botrace.board.model.game.items.Item;
import ch.bfh.akka.botrace.board.model.game.items.Usable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Potion extends Item implements Usable {
    private boolean used;

    public Potion(String name, int weight) {
        super(name, weight);
        used = false;
    }

    @Override
    public String itemOverview(){
        String actions = "0 - Go back to inventory\n" +
                "1 - Drop item\n" +
                "2 - Use item";
        StringBuilder overview = new StringBuilder();
        overview.append(this.getBaseDescription());
        overview.append("\n\n------------- ACTIONS -------------\n");
        overview.append(actions);
        overview.append("\n-----------------------------------");

        return overview.toString();
    }
}
