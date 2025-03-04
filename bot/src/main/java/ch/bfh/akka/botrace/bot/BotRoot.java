/*
 * Special Week 2 (BTI5205), © 2024 Berner Fachhochschule
 */
package ch.bfh.akka.botrace.bot;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractOnMessageBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.TimerScheduler;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.Receptionist.Listing;
import akka.actor.typed.receptionist.ServiceKey;
import ch.bfh.akka.botrace.common.BoardService;
import ch.bfh.akka.botrace.common.Message;
import ch.bfh.akka.botrace.common.boardmessage.*;
import ch.bfh.akka.botrace.common.boardmessage.PingMessage;
import ch.bfh.akka.botrace.common.botmessage.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The root actor of the Bot actor system.
 */



public class BotRoot extends AbstractOnMessageBehavior<Message> { // guardian actor

	public record TimerMessagePlay() implements Message { }
	public record TimerMessageRegistering() implements Message { }
	public record TimerMessageReached() implements Message { }
	static final String TIMER_KEY_PLAY = "PlayTimer";
	static final String TIMER_KEY_REGISTER = "RegisterTimer";
	static final String TIMER_KEY_REACHED = "ReachedTimer";
    private Scanner scanner = new Scanner(System.in);

    private final TimerScheduler<Message> timers;
    private int sleepTime;

    /**
     * The service key instance to lookup for the service name {@link BoardService#SERVICE_NAME} of the board.
     */
    final static ServiceKey<Message> serviceKeyForBoard = ServiceKey
            .create(Message.class, BoardService.SERVICE_NAME);

    /**
     * The response message of the {@link Receptionist}.
     */
    record ListingResponse(Listing listing) implements Message {}

    /**
     * Factory method to create the bot root actor.
     *
     * @return a behavior.
     */
    public static Behavior<Message> create(String botName, int figureType) {
        return Behaviors.setup(ctx -> Behaviors.withTimers(timers -> new BotRoot(ctx, timers, botName, figureType)));
    }

    private enum Phase{
        REGISTERING, READY, PLAYING, PAUSED, TARGET_REACHED
    }
    private Phase currentPhase;
    private int moveCount;
    ArrayList<Integer> recentDistances;
    private int figureType;


    /**
     * Upon creation of the Bot root actor, it tells the {@link Receptionist} its
     * interest in receiving a list of services.
     *
     * @param context the context of this actor system
     */
    private BotRoot(ActorContext<Message> context, TimerScheduler<Message> timers, String botName, int figureType) {
        super(context);
        this.timers = timers;
        this.recentDistances = new ArrayList<>();
        this.figureType = figureType;
        this.currentPhase = Phase.REGISTERING;

        ActorRef<Listing> listingResponseAdapter = context
                .messageAdapter(Listing.class, ListingResponse::new);
        context.getSystem().receptionist().tell(Receptionist.subscribe(serviceKeyForBoard, listingResponseAdapter));
    }

    private ActorRef<Message> boardRef;
    private final String actorName = getContext().getSelf().path().name();
    private final ActorRef<Message> botRef = getContext().getSelf();

    /**
     * Handle incoming messages.
     * @param message a message
     * @return the same behavior
     */
	@Override
	public Behavior<Message> onMessage(Message message) {
		if (currentPhase == Phase.TARGET_REACHED) {
			// Stop further processing of messages once the target is reached
			return this;
		}

        return switch(message){
            case PingMessage ignored                                                   -> onPing();
            case SetupMessage setupMessage                                             -> onSetup(setupMessage);
            case StartMessage ignored                                                  -> onStart();
            case PauseMessage ignored                                                  -> onPause();
            case ResumeMessage ignored                                                 -> onResume();
            case ListingResponse listingResponse                                       -> onListingResponse(listingResponse);
            case UnregisteredMessage ignored                                           -> onUnregister();
            case UnexpectedMessage unexpectedMessage                                   -> onUnexpectedMessage(unexpectedMessage);
            case TimerMessagePlay ignored                                              -> onTimerMessage();
            // game message
            case DeathMessage deathMessage                                             -> onDeathMessage(deathMessage);
            case FigureOverviewResponseMessage figureOverviewResponseMessage           -> onFigureOverviewResponseMessage(figureOverviewResponseMessage);
            case InventoryResponseMessage inventoryResponseMessage                     -> onInventoryResponseMessage(inventoryResponseMessage);
            case ItemResponseMessage itemResponseMessage                               -> onItemResponseMessage(itemResponseMessage);
            case WinMessage winMessage                                                 -> onWinMessage(winMessage);

			default -> throw new IllegalStateException("Unexpected value: " + message);
		};
	}





        private Behavior<Message> onFigureOverviewResponseMessage(FigureOverviewResponseMessage message){
        while(true){
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            System.out.println(message.figureOverview());
            int input = scanner.nextInt();
            // 0 - attack
            if(input == 0) {
                // TODO MULTIPLAYER: send opponentRequest

                int i;
                while (true) {
                    printList(message.opponentList());
                    i = scanner.nextInt();
                    if (i <= message.opponentList().size() && i >= 0) ;
                    break;
                }

                boardRef.tell(new AttackMessage(message.opponentList().get(i), this.botRef));


                // sout opponents turn
                break;
            }
            // 1 - open inventory
            if(input == 1){
                boardRef.tell(new InventoryRequestMessage(this.botRef));
                break;
            }
        }
        return this;
    }

    private void printList(List<String> list){
        for(int i = 0; i < list.size(); i++){
            System.out.println(i + " - " + list.get(i));
        }
        System.out.println("\nTotal: " + list.size());
    }

    private Behavior<Message> onInventoryResponseMessage(InventoryResponseMessage message){
        while(true){
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            System.out.println(message.inventoryInfo());
            int input = scanner.nextInt();
            // 0 - go back to figure overview
            if(input == 0){
                boardRef.tell(new FigureOverviewRequestMessage(this.botRef));
                break;
            }
            // n - get item at index n
            if(input <= message.itemCount() + 1){
                boardRef.tell(new ItemRequestMessage(input, this.botRef));
                break;
            }
        }

        return this;
    }

    private Behavior<Message> onItemResponseMessage(ItemResponseMessage message){
        while(true){
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            System.out.println(message.itemInfo());
            int input = scanner.nextInt();
            // 0 - go back to inventory
            if(input == 0){
                break;
            }

            // 1 - drop
            // 2 - use (usable)
            // 2 - equip (wearable)
            // 3 - unequip (wearable)
            if(input <= 2 || (input == 3 && message.wearable())){
                boardRef.tell(new ItemActionMessage(input, message.itemIndex(), this.botRef));
                break;
            }
        }
        // after action get InventoryRequest
        boardRef.tell(new InventoryRequestMessage(this.botRef));

        return this;
    }




        private Behavior<Message> onDeathMessage(DeathMessage message){
            System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            System.out.println("You lost the fight, you are dead");
        return this;
    }


    private Behavior<Message> onWinMessage(WinMessage message){
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("You win the fight");
        return this;
    }



    private Behavior<Message> onSetup(SetupMessage setupMessage){
        this.currentPhase = Phase.READY;
        sleepTime = setupMessage.sleepTime();
        getContext().getLog().info("Bot {} got setup message", actorName);
        getContext().getLog().info("Bot {} switched to Phase: {}", actorName, this.currentPhase);
        return this;
    }

    private Behavior<Message> onStart(){
        this.currentPhase = Phase.PLAYING;
        getContext().getLog().info("Bot {} got start message", actorName);
        getContext().getLog().info("Bot {} switched to Phase: {}", actorName, this.currentPhase);
        //boardRef.tell(new AvailableDirectionsRequestMessage(botRef));
        return this;
    }

    private Behavior<Message> onPause(){
        this.currentPhase = Phase.PAUSED;
        timers.cancel(TIMER_KEY_PLAY);
        getContext().getLog().info("Timer was stopped");
        getContext().getLog().info("Game was paused");
        getContext().getLog().info("Bot {} switched to Phase: {}", actorName, this.currentPhase);
        return this;
    }

    private Behavior<Message> onResume(){
        this.currentPhase = Phase.PLAYING;
        getContext().getLog().info("Game was resumed");
        getContext().getLog().info("Bot {} switched to Phase: {}", actorName, this.currentPhase);

        //boardRef.tell(new AvailableDirectionsRequestMessage(botRef));
        return this;
    }

    private Behavior<Message> onUnregister(){
        getContext().getLog().info("Bot {} is deregistered ", actorName);
        return Behaviors.stopped();
    }

    private Behavior<Message> onListingResponse(ListingResponse listingResponse) {
        getContext().getLog().info("Received listing from receptionist");
        for (ActorRef<Message> boardRef : listingResponse.listing.getServiceInstances(serviceKeyForBoard)) {
            this.boardRef = boardRef;
			timers.startSingleTimer(TIMER_KEY_PLAY, new TimerMessagePlay(), Duration.ofMillis(5000));
            getContext().getLog().info("StartedRegisteringTimer");
            boardRef.tell(new RegisterMessage(actorName, this.figureType, botRef));
            getContext().getLog().info("Stored board reference from receptionist");
        }
        return this;
    }

    private Behavior<Message> onPing() {
        getContext().getLog().info("Bot {} got pinged", actorName);
        if(boardRef != null){
            boardRef.tell(new PingResponseMessage(actorName, botRef));
            getContext().getLog().info("Bot {} responded to the ping", actorName);
        }
        else{
            getContext().getLog().info("No board reference found");
        }
        return this;
    }

    private Behavior<Message> onUnexpectedMessage(UnexpectedMessage unexpectedMessage) {
        getContext().getLog().error(unexpectedMessage.description());
        return this;
    }

    private Behavior<Message> onTimerMessage() {
        if(this.currentPhase == Phase.PLAYING){
            // TODO: Check
			//boardRef.tell(new AvailableDirectionsRequestMessage(botRef));
            getContext().getLog().info("Timer triggered a request");
        }
        else {
            timers.cancel(TIMER_KEY_PLAY);
            getContext().getLog().info("Timer was stopped");
        }
        return this;
    }

	private Behavior<Message> onTimerMessageRegistering() {
		if(this.currentPhase == Phase.REGISTERING){
			boardRef.tell(new RegisterMessage(actorName,figureType, botRef));
            getContext().getLog().info("Timer triggered a new registermessage");
		}
		else {
			timers.cancel(TIMER_KEY_REGISTER);
            getContext().getLog().info("Register Timer was stopped");
		}
		return this;
	}

	private Behavior<Message> onTimerMessageReached() {
		if(this.currentPhase == Phase.TARGET_REACHED){
			boardRef.tell(new DeregisterMessage("Bot reached target but ran into a timeout", botRef));
            getContext().getLog().info("Timer triggered a new deregister message");
		}
		else {
			timers.cancel(TIMER_KEY_REACHED);
            getContext().getLog().info("Reached Timer was stopped");
		}
		return this;
	}

}