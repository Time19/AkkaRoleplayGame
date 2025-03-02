package ch.bfh.akka.botrace.board.model.game.items;


import ch.bfh.akka.botrace.board.model.game.figures.Figure;
import ch.bfh.akka.botrace.board.model.game.items.armor.Armor;
import ch.bfh.akka.botrace.board.model.game.items.ring.Ring;
import ch.bfh.akka.botrace.board.model.game.items.weapon.Weapon;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Item implements Collectable{
    private final String name;
    private final int weight;
    private Figure owner;
    private String itemDescription = "No description yet";
    private boolean wearable;

    public Item(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public boolean pickup(Figure figure) {
        if (getOwner() != null) {
            // item already has an owner
            return false;
        }

        int newWCarryStrength = figure.getCarryStrength() - this.getWeight();
        if (newWCarryStrength < 0) {
            System.out.println(figure.getName() + " could not pick up this item");
            return false;
        }else{
            figure.setCarryStrength(newWCarryStrength);
            figure.addItemToInventory(this);
            this.owner = figure;
            return true;
        }
    }

    public void drop(){
        if(this.getOwner() != null){
            // unequip wearable items if they're in an active place
            if(this instanceof Wearable){

                // if this is an Armor
                if(this instanceof Armor){
                    if(this.getOwner().getActiveArmor().equals(this)){
                        ((Armor) this).unwear();
                    }
                }
                // if this is a Weapon
                else if (this instanceof Weapon) {
                    if(this.getOwner().getActiveWeapon().equals(this)){
                        ((Weapon) this).unwear();
                    }
                }
                // if this is a Ring
                else if (this instanceof Ring){
                    if(this.getOwner().getActiveRing().equals(this)){
                        ((Ring) this).unwear();
                    }
                }
            }

            owner.setCarryStrength(owner.getCarryStrength()+this.getWeight());
            owner.dropItem(this);
            this.setOwner(null);
        }
    }


    protected String getBaseDescription(){
        StringBuilder overview = new StringBuilder();
        overview.append("------------- ITEM ----------------\n");
        overview.append(String.format("%-5s | %-12s | %-4s%n", "Type", "Name", "Weight"));
        overview.append("-----------------------------------\n");
        overview.append(String.format("%-6s  %-13s  %-4s%n\n", this.getClass().getSimpleName(), this.getName(), this.getWeight()));
        overview.append("-------------- INFO ---------------\n");
        overview.append(itemDescription);

        return overview.toString();
    }

    public String itemOverview() {
        return "";
    }
}
