package ch.bfh.akka.botrace.board.model.game.items;

import ch.bfh.akka.botrace.board.model.game.figures.Figure;
import ch.bfh.akka.botrace.board.model.game.items.armor.Armor;
import ch.bfh.akka.botrace.board.model.game.items.armor.ArmorType;
import ch.bfh.akka.botrace.board.model.game.items.potion.HealthPotion;
import ch.bfh.akka.botrace.board.model.game.items.potion.StrengthPotion;
import ch.bfh.akka.botrace.board.model.game.items.ring.DefenceRing;
import ch.bfh.akka.botrace.board.model.game.items.ring.PowerRing;
import ch.bfh.akka.botrace.board.model.game.items.weapon.Bow;
import ch.bfh.akka.botrace.board.model.game.items.weapon.Club;
import ch.bfh.akka.botrace.board.model.game.items.weapon.Sword;
import ch.bfh.akka.botrace.board.model.game.items.weapon.ThrowingKnive;

import java.util.List;
import java.util.Random;

public class ItemShop {
    private int itemCount;
    List<Item> totalItemsInGame;
    Random random = new Random();


    public ItemShop() {
    }

    public Item createRandomItem(Figure figure){
        int randomInt = random.nextInt(10);
        Item item = null;

        // TODO: maby make item values also random in a certain range / epsilon

        // create item
        switch (randomInt) {

            // potions
            case 0: item = new StrengthPotion("Strength Potion", 2, 4); break; // +4 inventory space
            case 1: item = new HealthPotion("Health Potion", 2, 4); break; // +4 hp

            // rings
            case 2: item = new PowerRing("Power Ring", 3, 0.1); break; // +10% attackDamage
            case 3: item = new DefenceRing("Defence Ring", 3, 0.1); break; // -10% takenDamage

            // armor
            case 4: item = new Armor("Light Armor", 3, 0.05, ArmorType.LIGHT); break; // -5% takenDamage
            case 5: item = new Armor("Light Armor", 3, 0.1, ArmorType.HEAVY); break; // -10% takenDamage

            // weapons
            case 6: item = new Club("Club", 4, 1, 0.2); break; // defence value (0.2) gets fully reduced from attack
            case 7: item = new Sword("Sword", 4, 1, 0.2); break; // defence value (0.2) gets fully reduced from attack
            case 8: item = new Bow("Bow", 4, 1); break;
            case 9: item = new ThrowingKnive("Throwing Knive", 4, 1); break;
        }
        itemCount++;
        totalItemsInGame.add(item);
        item.pickup(figure);
        return item;
    }
}
