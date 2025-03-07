/*
 * Special Week 2 (BTI5205), © 2024 Berner Fachhochschule
 */
package ch.bfh.akka.botrace.board.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractOnMessageBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.TimerScheduler;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import ch.bfh.akka.botrace.board.model.BoardModel;
import ch.bfh.akka.botrace.board.model.game.figures.Figure;
import ch.bfh.akka.botrace.board.model.game.items.Item;
import ch.bfh.akka.botrace.board.model.game.items.Usable;
import ch.bfh.akka.botrace.board.model.game.items.Wearable;
import ch.bfh.akka.botrace.common.BoardService;
import ch.bfh.akka.botrace.common.Message;
import ch.bfh.akka.botrace.common.boardmessage.*;
import ch.bfh.akka.botrace.common.botmessage.*;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


/**
 * The root actor of the Board actor system. It registers itself at the {@link Receptionist}
 * under the service name {@link BoardService#SERVICE_NAME}.
 */
public class BoardRoot extends AbstractOnMessageBehavior<Message> { // root actor

	public record TimerMessage() implements Message { }

	private final TimerScheduler<Message> timers;

	/** The service key for the board service {@link BoardService#SERVICE_NAME} actor system. */
	public final static ServiceKey<Message> SERVICE_KEY = ServiceKey.create(Message.class, BoardService.SERVICE_NAME);

	BoardModel boardModel;
	Scanner scanner = new Scanner(System.in);

	/**
	 * Factory method which creates the root actor of the board.
	 * @param board a board adapter
	 */
	// TODO adjust parameters
	public static Behavior<Message> create(BoardModel board) {
		return Behaviors.setup(ctx -> Behaviors.withTimers(timers -> new BoardRoot(ctx, timers, board)));
	}

	/**
	 * Upon creation, it registers its actor reference under the service name
	 * {@value BoardService#SERVICE_NAME} at the {@link Receptionist}.
	 *
	 * @param context context of the actor system.
	 * @param board adapter for the board model.
	 */

	private BoardRoot(ActorContext<Message> context, TimerScheduler<Message> timers, BoardModel board) {
		super(context);
		boardModel = board;
		this.timers = timers;

		context.getLog().info(getClass().getSimpleName() + " created: " + this.getContext().getSelf());

		// Register the board root actor at the receptionist
		context
				.getSystem()
				.receptionist()
				.tell(Receptionist.register(SERVICE_KEY, this.getContext().getSelf().narrow()));
		context.getLog().info(getClass().getSimpleName() + ": Registered at the receptionist");

		timers.startTimerAtFixedRate(new TimerMessage(), Duration.ofSeconds(5));
	}

	/**
	 * Handles the received messages.
	 * @param message a message.
	 * @return the same behavior.
	 */
	@Override
	public Behavior<Message> onMessage(Message message) {

		switch (message){
			case PingResponseMessage pingResponseMessage								-> onPingResponseMessage(pingResponseMessage);
			case DeregisterMessage deregisterMessage 									-> onDeregisterMessage(deregisterMessage);
			case RegisterMessage registerMessage 										-> onRegisterMessage(registerMessage);
			case StartRaceMessage startRaceMessage										-> onStartRaceMessage(startRaceMessage);

			// Events coming from user input sending to all bots
			case PauseMessage pauseMessage 												-> onPauseMessage(pauseMessage);
			case ResumeMessage resumeMessage											-> onResumeMessage(resumeMessage);
			case TimerMessage ignored                                                  -> onTimerMessage();
			case TimeoutMessage timeoutMessage 										   -> onTimeout(timeoutMessage);

			// Events from active game
			case AttackMessage attackMessage -> onAttackMessage(attackMessage);
			case FigureOverviewRequestMessage figureOverviewRequest -> onFigureOverviewRequestMessage(figureOverviewRequest);
			case InventoryRequestMessage inventoryRequestMessage -> onInventoryRequestMessage(inventoryRequestMessage);
			case ItemRequestMessage itemRequestMessage -> onItemRequestMessage(itemRequestMessage);
			case ItemActionMessage itemActionMessage -> onItemActionMessage(itemActionMessage);

			default -> throw new IllegalStateException("Message not handled: " + message);
        };

		return Behaviors.same();
	}

	private Behavior<Message> onItemActionMessage(ItemActionMessage message) {
		// TODO: LOG

		Figure figure = boardModel.getFigures().get(message.botRef());
		Item item = figure.getItemsCarrying().get(message.itemIndex());

		// drop item
		if(message.action() == 1){
			item.drop();
		}

		// wear / unwear
		if(item instanceof Wearable){
			switch (message.action()){
				case 2: ((Wearable) item).wear(); break;
				case 3: ((Wearable) item).unwear(); break;
			}
		}

		// use
		if(item instanceof Usable){
			if(message.action() == 2){
				((Usable) item).use();
			}
		}
		return this;
	}

	private Behavior<Message> onAttackMessage(AttackMessage message) {
		// get attack value of attacker


		Figure defender = null;
		ActorRef<Message> defenderRef = null;
		// find figure to attack
		for(ActorRef<Message> actorRef: boardModel.getBotRefs()){
			if(boardModel.getFigures().get(actorRef).getName().equals(message.opponentRef())){
				defenderRef = actorRef;
				defender = boardModel.getFigures().get(actorRef);
			}
		}

		double[] values = boardModel.attack(defender, message.botRef());

		// TODO: LOGGING of attack (values in array)


		// check if player still alive:
		if(defender.getHealthPoints() <= 0){
			defenderRef.tell(new DeathMessage(defenderRef));
			boardModel.getPlayersAlive().remove(defender);

			// if attacker = last survivor -> winner
			if(boardModel.getPlayersAlive().size() == 1){
				message.botRef().tell(new WinMessage(message.botRef()));
			}
		}

		// send out new attackMessage to next actor
		ActorRef<Message> actorRef = boardModel.getNextTurnFigure();
		String figureOverview = boardModel.getFigures().get(actorRef).getFigureOverview();
		List<String> nameListe = getOpponentsList(actorRef);
		actorRef.tell(new FigureOverviewResponseMessage(figureOverview, nameListe,actorRef));

		return this;
	}

	private Behavior<Message> onFigureOverviewRequestMessage(FigureOverviewRequestMessage message) {
		// TODO: LOG
		getContext().getLog().info("Figure overview request: {}", boardModel.getFigures().get(message.botRef()).getName());

		// get figureOverview from boarModel
		String figureOverview = boardModel.getFigures().get(message.botRef()).getFigureOverview();

		List<String> nameListe = getOpponentsList(message.botRef());
		message.botRef().tell(new FigureOverviewResponseMessage(figureOverview, nameListe, message.botRef()));

		return this;
	}

	private List<String> getOpponentsList(ActorRef<Message> actorRef){

		List<String> nameListe = boardModel.getFigures().values().stream()
				.map(Figure::getName)
				.collect(Collectors.toList());

		nameListe.remove(boardModel.getFigures().get(actorRef).getName());
		return nameListe;
	}

	private Behavior<Message> onInventoryRequestMessage(InventoryRequestMessage message) {
		// TODO: LOG
		getContext().getLog().info("Inventory request: {}", boardModel.getFigures().get(message.botRef()).getName());

		// get inventory
		String inventory = boardModel.getFigures().get(message.botRef()).getInventoryOverview();
		int itemsCount = boardModel.getFigures().get(message.botRef()).getItemsCarrying().size();
		message.botRef().tell(new InventoryResponseMessage(inventory, itemsCount, message.botRef()));
		return this;
	}

	private Behavior<Message> onItemRequestMessage(ItemRequestMessage message) {
		getContext().getLog().info("Item request: {}", message.itemIndex());

		String itemOverview = boardModel.getFigures().get(message.botRef()).getItemsCarrying().get(message.itemIndex()).itemOverview();
		boolean wearable = boardModel.getFigures().get(message.botRef()).getItemsCarrying().get(message.itemIndex()).isWearable();
		message.botRef().tell(new ItemResponseMessage(itemOverview, wearable, message.itemIndex(), message.botRef()));
		return this;
	}


	Behavior<Message> onStartRaceMessage(StartRaceMessage startRaceMessage) {
		getContext().getLog().info("Starting fight");

		// get first actor to start the fight
		ActorRef<Message> actorRef = boardModel.getBotRefs().getFirst();
		String figureOverview = boardModel.getFigures().get(actorRef).getFigureOverview();
		List<String> nameListe = getOpponentsList(actorRef);

		actorRef.tell(new FigureOverviewResponseMessage(figureOverview, nameListe, actorRef));

		return this;
	}

	// start?
	Behavior<Message> onRegisterMessage(RegisterMessage message) {
		getContext().getLog().info("Registering: {}", message.name());

		// adding to boardModel
		boardModel.registerNewBot(message.name(), message.type(), message.botRef());

		// ping the bot
		message.botRef().tell(new PingMessage());

		// sending SetupMessage back to bot
		message.botRef().tell(new SetupMessage(600));

		while(true){
			System.out.println("\n\n\n\nNew fighter connected:" +
					"\n0 - start game" +
					"\n1 - wait for another enemy" +
					"\n\ncurrent amount of fighter: " + boardModel.getBotRefs().size());
			int input = scanner.nextInt();
			if(input == 0){
				System.out.println("\n\nFight has started");
				// send one person figureOverview
				ActorRef<Message> actorRef = boardModel.getBotRefs().getFirst();

				List<String> nameList = getOpponentsList(actorRef);

				actorRef.tell(new FigureOverviewResponseMessage(boardModel.getFigures().get(actorRef).getFigureOverview(),nameList, actorRef));
				break;
			}else if(input == 1){
				break;
			}
		}

		return this;
	}

	private Behavior<Message> onPauseMessage(PauseMessage pauseMessage) {
		getContext().getLog().info("Pausing Race");

		for(ActorRef<Message> ref : boardModel.getBotRefs()){
			ref.tell(new PauseMessage());
		}

		return this;
	}

	private Behavior<Message> onTimerMessage() {

		for (ActorRef<Message> ref : boardModel.getBotRefs()) {
			getContext().getLog().info("Sending ping to " + ref.path().name());
			ref.tell(new PingMessage());
			String key = "timeout_" + ref.path().name(); //key for each bot
			getContext().getLog().info("Setting timeout for " + ref.path().name());
			timers.startSingleTimer(key, new TimeoutMessage(ref), Duration.ofSeconds(3));
		}
		return this;
	}

	private Behavior<Message> onResumeMessage(ResumeMessage resumeMessage) {
		getContext().getLog().info("Starting race");

		for(ActorRef<Message> ref : boardModel.getBotRefs()){
			ref.tell(new ResumeMessage());
		}
		return this;
	}

	Behavior<Message> onPingResponseMessage(PingResponseMessage pingResponseMessage) {
		getContext().getLog().info("Ping response received from " + pingResponseMessage.name());

		// cancel the timeout when a response is received
		String key = "timeout_" + pingResponseMessage.name();
		timers.cancel(key);
		return this;
	}

	Behavior<Message> onDeregisterMessage(DeregisterMessage message) {
		getContext().getLog().info("Deregistering because: {}", message.reason());
		// unregistering from boardModel
		boardModel.deregister(message.botRef());
		message.botRef().tell(new UnregisteredMessage());

		return this;
	}

	private Behavior<Message> onTimeout(TimeoutMessage timeoutMessage) {
		getContext().getLog().info("Timeout reached for " + timeoutMessage.botRef().path().name());
		getContext().getLog().info("Deregistering bot " + timeoutMessage.botRef().path().name());
		//boardModel.deregister(timeoutMessage.botRef());
		return this;
	}
}
