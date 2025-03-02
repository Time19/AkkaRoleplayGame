package ch.bfh.akka.botrace.common.botmessage;

import akka.actor.typed.ActorRef;
import ch.bfh.akka.botrace.common.Message;

public record InventoryRequestMessage(ActorRef<Message> botRef) implements BotMessage {}