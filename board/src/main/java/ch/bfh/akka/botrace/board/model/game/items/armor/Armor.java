package ch.bfh.akka.botrace.board.model.game.items.armor;


import ch.bfh.akka.botrace.board.model.game.items.Item;
import ch.bfh.akka.botrace.board.model.game.items.Wearable;
import lombok.Getter;

@Getter
public abstract class Armor extends Item implements Wearable {

    private final ArmorType armorType;
    private final double armorStrength;     // percentage damage reduction in decimal (0.1 = 10%)
    private final double preventAttack;     // chance of ignoring attack completely in % (10%)
    int initiativeValueReduction;

    public Armor(String name, int weight, double armorStrength, ArmorType armorType) {
        super(name, weight);
        this.armorStrength = armorStrength;
        this.armorType = armorType;
        this.setWearable(true);

        if(this.armorType == ArmorType.LIGHT){
            this.preventAttack = 10;
            this.initiativeValueReduction = 1;
        }else{
            this.preventAttack = 20;
            this.initiativeValueReduction = 2;
        }
        setItemDescription("Armor\nType: " + this.armorType.name() + "\nDamage reduction: " + this.armorStrength*100 + "%\nChance of ignoring damage: " + preventAttack + "%");
    }

    @Override
    public boolean wear() {
        this.getOwner().setActiveArmor(this);
        return true;

        // TODO: Change initiative value
        // TODO: Check if not troll (cnat wear heavy armor)
        // TODO: Check if not Dwarf (cant wear heavy armor)
    }

    @Override
    public void unwear() {
        this.getOwner().setActiveArmor(null);
        // TODO: Change initiative value
    }

    public String getItemOverview(){
        String actions = "0 - Go back to inventory\n" +
                "1 - Drop item\n" +
                "2 - Equip item\n" +
                "3 - Uneqip item";

        StringBuilder overview = new StringBuilder();
        overview.append(this.itemOverview());
        overview.append("--------- ITEM INFORMATION ---------\n");
        overview.append(this.getItemDescription());
        overview.append("\n\n------------- ACTIONS -------------\n");
        overview.append(actions);
        overview.append("\n-----------------------------------");

        return overview.toString();
    }
}