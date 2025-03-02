package ch.bfh.akka.botrace.common.boardmessage;

import akka.actor.typed.ActorRef;
import ch.bfh.akka.botrace.common.Message;

public record WinMessage(ActorRef<Message> botRef) implements Message {
}
