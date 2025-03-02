package ch.bfh.akka.botrace.common.boardmessage;

import akka.actor.typed.ActorRef;
import ch.bfh.akka.botrace.common.Message;

import java.util.List;

public record FigureOverviewResponseMessage(String figureOverview, List<String> opponentList, ActorRef<Message> botRef) implements Message {
}
