/*
 * Special Week 2 (BTI5205), © 2024 Berner Fachhochschule
 */
package ch.bfh.akka.botrace.bot;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

import java.util.Scanner;

/**
 * The root actor of the Bot actor system.
 */
public class BotMain {

    /**
     * Entry point for the Bot actor system.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        // Create an Akka system
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name:");
        String name = scanner.nextLine();
        int number = 0;
        while (true) {
            System.out.println("Choose your character:" +
                    "\n0 - Human" +
                    "\n1 - Elf" +
                    "\n2 - Dwarf" +
                    "\n3 - Goblin" +
                    "\n4 - Ork" +
                    "\n5 - Troll");

            int input = scanner.nextInt();
            if (input <= 5 && input >= 0) {
                break;
            }
        }
        String figure = null;

        switch (number) {
            case 0:
                figure = "Human";
                break;
            case 1:
                figure = "Elf";
                break;
            case 2:
                figure = "Dwarf";
                break;
            case 3:
                figure = "Goblin";
                break;
            case 4:
                figure = "Ork";
                break;
            case 5:
                figure = "Troll";
                break;
            default:
                figure = "Human";
                break;
        }

        ActorSystem<Void> bot = ActorSystem.create(rootBehavior(name,figure), "ClusterSystem");
        bot.log().info("Player Actor System created");
    }

    /**
     * Creates the two actors {@link ClusterListener} and {@link BotRoot}.
     *
     * @return a void behavior
     */
    private static Behavior<Void> rootBehavior(String name, String figureType) {
        return Behaviors.setup(context -> {

            // Create an actor that handles cluster domain events
            context.spawn(ClusterListener.create(), "ClusterListener");


            context.spawn(BotRoot.create(name, figureType), name);

            // For the competition, only one bot actor will be allowed
            //context.spawn(BotRoot.create("aName..."), "aBot");

            return Behaviors.empty();
        });
    }
}
