/*
 * Special Week 2 (BTI5205), © 2024 Berner Fachhochschule
 */
package ch.bfh.akka.botrace.board.cli;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import ch.bfh.akka.botrace.board.actor.Board;
import ch.bfh.akka.botrace.board.actor.BoardRoot;
import ch.bfh.akka.botrace.board.actor.ClusterListener;
import ch.bfh.akka.botrace.board.model.BoardUpdateListener;
import ch.bfh.akka.botrace.common.Message;
import ch.bfh.akka.botrace.board.model.BoardModel;
import ch.bfh.akka.botrace.common.boardmessage.StartRaceMessage;
import ch.bfh.akka.botrace.common.boardmessage.PauseMessage;
import ch.bfh.akka.botrace.common.boardmessage.ResumeMessage;
import javafx.application.Platform;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class BoardMain implements BoardUpdateListener {
    /**
     * Entry point for the Board actor system.
     *
     * @param args not used
     */
    private static ActorRef<Message> boardRef;
    private static ActorSystem<Void> board;
    private static Scanner scanner = new Scanner(System.in);

    private static BoardModel boardModel;

    private static boolean gameRunning = false;


    public static void main(String[] args) throws IOException {

        boardModel = new BoardModel();
        boardModel.addBoardUpdateListener(new BoardMain()); // subscribe cli board to BoardModel

        board = ActorSystem.create(rootBehavior(), "ClusterSystem");
        System.out.println("Board Actor System created");
    }




    /**
     * Creates the two actors {@link ClusterListener} and {@link BoardRoot}.
     *
     * @return a void behavior
     */
    private static Behavior<Void> rootBehavior() {
        return Behaviors.setup(context -> {

            context.spawn(ClusterListener.create(),"ClusterListener");

            boardRef = context.spawn(BoardRoot.create(boardModel), "BoardRoot");

            context.getLog().info("BoardRoot with BoardModel created");

            return Behaviors.empty();
        });
    }




    @Override
    public void boardUpdated() {
        // TODO
    }

    @Override
    public void notifyTargetReached(String name) {
        System.out.println("Target reached by bot: " + name);
        gameRunning = false;
        if (board != null) {
            board.terminate();
        }
        Platform.exit();
    }
}
