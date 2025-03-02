package ch.bfh.akka.botrace.common.botmessage;

import akka.actor.typed.ActorRef;
import ch.bfh.akka.botrace.common.Message;

// TODO: multiplayer - give
public record AttackMessage(String opponentRef,ActorRef<Message> botRef) implements BotMessage {}
