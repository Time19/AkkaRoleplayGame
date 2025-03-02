package ch.bfh.akka.botrace.common.botmessage;

import akka.actor.typed.ActorRef;
import ch.bfh.akka.botrace.common.Message;

public record ItemRequestMessage(int itemIndex, ActorRef<Message> botRef) implements BotMessage {}