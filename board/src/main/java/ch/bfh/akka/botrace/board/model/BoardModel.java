/*
 * Special Week 2 (BTI5205), © 2024 Berner Fachhochschule
 */
package ch.bfh.akka.botrace.board.model;

import akka.actor.typed.ActorRef;
import ch.bfh.akka.botrace.board.actor.Board;

// figures
import ch.bfh.akka.botrace.board.model.game.figures.*;
// items

import ch.bfh.akka.botrace.board.model.game.items.ItemShop;
import ch.bfh.akka.botrace.common.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Model for managing the state of the board. Implements the {@link Board}
 * interface. As called by an actor thread, use {@link javafx.application.Platform#runLater(Runnable)}.
 */
public class BoardModel implements Board {

    @Getter
    private Map<ActorRef<Message>, Figure> figures = new HashMap<>();
    private List<ActorRef<Message>> botRefs = new ArrayList<>();
    @Getter
    @Setter
    private List<ActorRef<Message>> playersAlive = new ArrayList<>();
    @Getter
    private List<String> figureNames = new ArrayList<>();
    private List<BoardUpdateListener> listeners = new ArrayList<>(); // define listeners
    private int nextTurn = 1;
    private final ItemShop itemShop = new ItemShop();

    public void addBoardUpdateListener(BoardUpdateListener listener) {
        listeners.add(listener);
    }

    private void notifyUiListeners() {
        for (BoardUpdateListener listener : listeners) {
            listener.boardUpdated();
        }
    }

    public BoardModel() {}

    @Override
    public void registerNewBot(String name, int figureType, ActorRef<Message> botRef) {

        Figure figure = null;

        switch (figureType){
            case 0: figure = new Human(name, 10, 5); break;
            case 1: figure = new Elf(name, 10, 10, 1);
            case 2: figure = new Dwarf(name, 10, 10);
            case 3: figure = new Goblin(name, 10, 10);
            case 4: figure = new Ork(name, 10, 10);
            case 5: figure = new Troll(name, 10, 10);
        }

        figures.put(botRef, figure);
        figureNames.add(name);
        botRefs.add(botRef);
        playersAlive.add(botRef);
    }

    public ActorRef<Message> getNextTurnFigure(){
        if (playersAlive.isEmpty()){
            return null;
        }

        int result = nextTurn % playersAlive.size();
        nextTurn++;
        System.out.println(result);
        return playersAlive.get(result);
    }

    public double[] attack(Figure opponentRef, ActorRef<Message> attackRef) {
        double[] result = new double[1];

        Figure attacker = figures.get(attackRef);
        Figure defender = opponentRef;

        double attackDamage = attacker.attack();
        defender.defend(attackDamage);

        // check if attacker gets item
        if(attacker.getTotalAttacks() % 2 == 0){
            this.itemShop.createRandomItem(attacker);
            System.out.println("new item created");
        }



        return result;
    }

    @Override
    public boolean isAlive(ActorRef<Message> botRef) {
        if(playersAlive.contains(botRef)) {
            return true;
        }
        return false;
    }

    public void deregister(ActorRef<Message> botRef) {
        figureNames.remove(figures.get(botRef).getName());
        botRefs.remove(botRef);
        playersAlive.remove(botRef);
        figures.remove(botRef);
    }

    public void markPlayerDead(ActorRef<Message> botRef) {
        playersAlive.remove(botRef);
    }

    public List<ActorRef<Message>> getBotRefs(){
        return botRefs;
    }
}
