/*
 * Special Week 2 (BTI5205), © 2024 Berner Fachhochschule
 */
package ch.bfh.akka.botrace.board.actor;

import akka.actor.typed.ActorRef;
import ch.bfh.akka.botrace.common.Message;

import java.util.List;

/**
 * Abstraction of the board, the playground. To be used by the actors. To
 * be implemented by, say, the model of the board.
 */
public interface Board {

    void registerNewBot(String name, String figureType, ActorRef<Message> botRef);
    boolean isAlive(ActorRef<Message> botRef);
}
