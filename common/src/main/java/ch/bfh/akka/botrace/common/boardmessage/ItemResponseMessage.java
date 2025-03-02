package ch.bfh.akka.botrace.common.boardmessage;

import akka.actor.typed.ActorRef;
import ch.bfh.akka.botrace.common.Message;

public record ItemResponseMessage(String itemInfo, boolean wearable, ActorRef<Message> botRef) implements Message { // type: wearable / usable
}
