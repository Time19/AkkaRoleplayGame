/*
 * Special Week 2 (BTI5205), © 2024 Berner Fachhochschule
 */
package ch.bfh.akka.botrace.board.model;

import akka.actor.typed.ActorRef;
import ch.bfh.akka.botrace.board.actor.Board;

// figures
import ch.bfh.akka.botrace.board.model.game.figures.*;
// items

import ch.bfh.akka.botrace.common.Message;
import lombok.Getter;

import java.util.*;

/**
 * Model for managing the state of the board. Implements the {@link Board}
 * interface. As called by an actor thread, use {@link javafx.application.Platform#runLater(Runnable)}.
 */
public class BoardModel implements Board {

    @Getter
    private Map<ActorRef<Message>, Figure> figures = new HashMap<>();
    private List<ActorRef<Message>> botRefs = new ArrayList<>();
    private List<ActorRef<Message>> playersAlive = new ArrayList<>();
    @Getter
    private List<String> figureNames = new ArrayList<>();
    private List<BoardUpdateListener> listeners = new ArrayList<>(); // define listeners
    private int nextTurn = 0;
    private int maxPlayers = 0;

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
    public void registerNewBot(String name, String figureType, ActorRef<Message> botRef) {
        // TODO: Create new figure object
        Elf elf = new Elf(figureType, 10, 10, 10);
        figures.put(botRef, elf);
        figureNames.add(name);
        botRefs.add(botRef);
        playersAlive.add(botRef);
        maxPlayers = maxPlayers + 1;
    }

    public ActorRef<Message> getNextTurnFigure(){
        int result = nextTurn % maxPlayers + 1;
        nextTurn++;
        return playersAlive.get(result);
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
