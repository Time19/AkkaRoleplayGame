package ch.bfh.akka.botrace.board.model.game.figures;


import ch.bfh.akka.botrace.board.model.game.items.Item;
import ch.bfh.akka.botrace.board.model.game.items.armor.Armor;
import ch.bfh.akka.botrace.board.model.game.items.ring.DefenceRing;
import ch.bfh.akka.botrace.board.model.game.items.ring.PowerRing;
import ch.bfh.akka.botrace.board.model.game.items.ring.Ring;
import ch.bfh.akka.botrace.board.model.game.items.weapon.MeleeWeapon;
import ch.bfh.akka.botrace.board.model.game.items.weapon.Weapon;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Figure implements Attacks, Defends{
    @Getter
    private final String name;
    @Getter
    @Setter
    private final int maxHealth;

    @Getter
    @Setter
    private double healthPoints;
    @Getter
    @Setter
    private int carryStrength;
    private int maxCarryStrength;

    @Getter
    @Setter
    private int initiativeValue = 5;

    // base attack strength is 1 (addition value)
    @Getter
    @Setter
    private double attackStrength = 1;

    // base defence strength is 1 (received damage multiplicator)

    /**
     *  For each wearable item (weapon, ring, armor) there is one slot
     */
    @Getter
    @Setter
    private Weapon activeWeapon = null;
    @Getter
    @Setter
    private Ring activeRing = null;
    @Getter
    @Setter
    private Armor activeArmor = null;

    @Getter
    // inventory of figure with all the items in it
    List<Item> itemsCarrying = new ArrayList<>();

    // used for rand double
    Random random = new Random();

    public Figure(String name, int carryStrength, int healthPoints, double attackStrength) {
        this.name = name;
        this.carryStrength = carryStrength;
        this.maxCarryStrength = carryStrength;
        this.healthPoints = healthPoints;
        this.maxHealth = healthPoints;
        this.attackStrength = attackStrength;
    }

    public void addItemToInventory(Item item){
        itemsCarrying.add(item);
    }

    public void removeItemFromInventory(Item item){
        itemsCarrying.remove(item);
    }

    /**
     * baseAttackStrength used for attack() method of the figures
     * Adds attackStrength (default = 1) and the weapons attackValue
     * Uses a random multiplier between 0.9 and 1.1
     *
     * @return the attack strength (figures can boost this number depending on weapon specialisation)
     */
    public double getBaseAttackStrength(){
        double attackStrength = this.attackStrength;

        // consider weapon
        if(activeWeapon!=null){
            attackStrength += activeWeapon.getAttackValue();
        }

        // consider ring of power
        if(activeRing!=null){
            if(activeRing instanceof PowerRing powerRing) {
                attackStrength = attackStrength * (1 + powerRing.getRingStrength());
            }
        }

        double randomMultiplier = random.nextDouble(0.9, 1.1);

        return (Math.floor(attackStrength * randomMultiplier * 100) / 100);
    }

    /**
     * Default method for figures without specialisations in attack
     * @return damage of the attack
     */
    @Override
    public double attack() {
        return getBaseAttackStrength();
    }


    /**
     * Method calculates initiative value which decides the Attack order of the fight
     * Armors and Melee weapons reduce the initiative value
     */
    public int getInitiativeValue(){
        int value = initiativeValue;
        if(activeArmor!=null){
            initiativeValue -= activeArmor.getInitiativeValueReduction();
        }
        if(activeWeapon != null){
            if(activeWeapon instanceof MeleeWeapon){
                // melee weapons = -1
                value -= 1;
            }
        }

        return value;
    }


    /**
     * Method used to calculate actual damage taken based on attackers damage
     * If figure uses melee weapon -> subtract defenders melee-weapons defenceValue
     * Multiply damage ArmorStrength
     *
     */

    public double getBaseDefence(double attackerDamage){
        double damage = attackerDamage;

        // melee weapons have a defenseStrength
        if(this.activeWeapon instanceof MeleeWeapon){
            damage -= ((MeleeWeapon) this.activeWeapon).getDefenceValue();
        }

        // if somehow a defence weapon has bigger Value than attackers weapon -> set damage to 1
        if(damage < 1){
            damage = 1;
        }

        // consider armor stats
        if(this.activeArmor != null){
            int nextDouble = random.nextInt(0, 100);

            if(nextDouble <= this.activeArmor.getPreventAttack()){
                // damage ignored
                return 0;
            }
            damage = damage * (1-activeArmor.getArmorStrength());
        }

        // consider ring of defence
        if(this.activeRing != null){
            if(this.activeRing instanceof DefenceRing defenceRing){
                damage = damage * (1 - defenceRing.getRingStrength());
            }
        }
        // round damage to 2 Nachkommastellen lol
        return Math.floor(damage * 100) /100;
    }

    /**
     * Default method to calculate the received damage.
     *
     */
    @Override
    public double defend(double attackerDamage) {
        double takenDamage = getBaseDefence(attackerDamage);

        // reduce the taken damage from hp
        this.healthPoints -= takenDamage;

        return takenDamage;
    }

    public void dropItem(Item item){
        for(Item i : itemsCarrying){
            if(i.equals(item)){
                itemsCarrying.remove(i);
                item.drop();
            }
        }
    }

    public boolean isAlive(){
        if(this.getHealthPoints()> 0){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Methods for prints to CLI
     * - Figure overview
     * - Inventory
     */
    public String getFigureOverview(){
        String actions = "0 - Attack\n" +
                "1 - Open Inventory\n";

        StringBuilder figureOverview = new StringBuilder();
        figureOverview.append("-------------------------\n     FIGURE OVERVIEW     \n-------------------------\n");
        figureOverview.append(this.getFigureStats());
        figureOverview.append("\n---- ACTIVE WEARABLES----\n");
        figureOverview.append(this.getWearablesString());
        figureOverview.append("\n\n-------- ACTIONS --------\n");
        figureOverview.append(actions);
        figureOverview.append("\n-------------------------");

        return figureOverview.toString();
    }

    public String getInventoryOverview(){
        String actions = "0 - Go back to figure overview\n" +
                "n - Inspect item";

        StringBuilder inventoryOverview = new StringBuilder();
        inventoryOverview.append("------------- INVENTORY ----------------\n");
        inventoryOverview.append(String.format("%-4s | %-5s | %-12s | %-4s%n\n", "Index", "Type", "Name", "Weight"));
        //inventoryOverview.append("----------------------------------------\n");

        for(int i = 0; i < itemsCarrying.size(); i++){
            Item item = itemsCarrying.get(i);
            inventoryOverview.append(String.format("%-6s  %-6s  %-13s  %-4s%n", i+1, item.getClass().getSimpleName(), item.getName(), item.getWeight()));
        }
        inventoryOverview.append("Total items: " + itemsCarrying.size());

        inventoryOverview.append("\n\n------------ ACTIVE WEARABLES ----------\n");
        inventoryOverview.append(getWearablesString());
        inventoryOverview.append("\n---------------- ACTIONS ---------------\n");
        inventoryOverview.append(actions);
        inventoryOverview.append("\n----------------------------------------\n");

        return inventoryOverview.toString();
    }

    private String getWearablesString(){
        StringBuilder wearablesString = new StringBuilder();
        // add weapon
        wearablesString.append("Weapon: ");
        if(this.activeWeapon !=null){
            wearablesString.append(this.activeWeapon.getName());
        }else{
            wearablesString.append("-");
        }

        // add armor
        wearablesString.append("\nArmor:  ");
        if(this.activeArmor !=null){
            wearablesString.append(this.activeArmor.getName());
        }else{
            wearablesString.append("-");
        }

        // add ring
        wearablesString.append("\nRing:   ");
        if(this.activeRing !=null){
            wearablesString.append(this.activeRing.getName());
        }else{
            wearablesString.append("-");
        }

        return wearablesString.toString();
    }

    public String getFigureStats() {
        StringBuilder inventoryString = new StringBuilder();
        for(int i = 1; i <= this.carryStrength; i++){
            //inventoryString.append("□");
            inventoryString.append("-");
        }

        for(int i = 1; i <= (this.maxCarryStrength-this.carryStrength) ; i++){
            //inventoryString.insert (0,"■");
            inventoryString.append("+");
        }

        StringBuilder hpString = new StringBuilder();
        for(int i = 1; i <= this.healthPoints; i++){
            //hpString.append("♥");
            hpString.append("+");
        }

        for(int i = 1; i <= (this.maxHealth-this.healthPoints) ; i++){
            //hpString.append("♡");
            hpString.append("-");

        }



        return String.format("%-11s %-20s%nInventory:  %-20s%n", this.getName(), hpString, inventoryString);
    }
}