package ch.bfh.akka.botrace.board.model.game.items.weapon;


import ch.bfh.akka.botrace.board.model.game.items.Item;
import ch.bfh.akka.botrace.board.model.game.items.Wearable;
import lombok.Getter;

@Getter
public abstract class Weapon extends Item implements Wearable {
    private final int attackValue;

    public Weapon(String name, int weight, int attackValue) {
        super(name, weight);
        this.attackValue = attackValue;
        this.setWearable(true);
    }

    public boolean wear(){
        getOwner().setActiveWeapon(this);
        return true;
        // TODO: Check if wearing heavy armor if u want to equip bow
    }

    public void unwear(){
        getOwner().setActiveWeapon(null);
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