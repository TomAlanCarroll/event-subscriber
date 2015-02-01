package models;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.WebSocket;

/**
 * A generalized model of a SoccerData actor
 */
public class SoccerData extends UntypedActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * The {@link WebSocket} instances for this SoccerData
     */
    WebSocket.In<String> in;
    WebSocket.Out<String> out;

    /**
     * Constructor
     * @param in The input {@link WebSocket} for the client
     * @param out The output {@link WebSocket} for the client
     */
    public SoccerData(WebSocket.In<String> in, WebSocket.Out<String> out) {
        this.in = in;
        this.out = out;

        ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();
        // subscribe to the topic named "events"
        mediator.tell(new DistributedPubSubMediator.Subscribe("events", getSelf()), getSelf());
    }

    public double getGoalsTeamA() {
        return (Game.STATE.getScore("A") != null) ? Game.STATE.getScore("A") : 0;
    }

    public double getGoalsTeamB() {
        return (Game.STATE.getScore("B") != null) ? Game.STATE.getScore("B") : 0;
    }

    public long getCurrentMinutes () {
        return Game.STATE.getCurrentMinute();
    }

    /**
     * This method gets called whenever new
     * data is available (e.g. a new goal,
     * new minute, etc...)
     */
    public void measurementsChanged() {
        ObjectNode result = Json.newObject();
        result.put("A", getGoalsTeamA());
        result.put("B", getGoalsTeamB());
        result.put("MINUTE", getCurrentMinutes());
        out.write(result.toString());
    }

    /**
     * Processes incoming messages that have been sent to this {@code Actor} from the {@link SoccerGame}
     * @param msg A {@code SubscribeAck} or a {@code String} that will be sent to the client through out
     */
    @Override
    public void onReceive(Object msg) {
        if (msg instanceof String) {
            log.info("Got: {}", msg);

            // Notify
            measurementsChanged();
        } else if (msg instanceof DistributedPubSubMediator.SubscribeAck) {
            log.info("subscribing");
        } else {
            unhandled(msg);
        }
    }
}
