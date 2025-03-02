package ch.bfh.akka.botrace.common.boardmessage;

import akka.actor.typed.ActorRef;
import ch.bfh.akka.botrace.common.Message;
import ch.bfh.akka.botrace.common.botmessage.BotMessage;

public record DeathMessage(ActorRef<Message> botRef) implements BotMessage {
}
