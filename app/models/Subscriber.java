package models;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import play.mvc.WebSocket;

/**
 * A generalized model of a Subscriber actor
 */
public class Subscriber extends UntypedActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * The {@link WebSocket} instances for this Subscriber
     */
    WebSocket.In<String> in;
    WebSocket.Out<String> out;

    /**
     * Constructor
     * @param in The input {@link WebSocket} for the client
     * @param out The output {@link WebSocket} for the client
     */
    public Subscriber(WebSocket.In<String> in, WebSocket.Out<String> out) {
        this.in = in;
        this.out = out;

        ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();
        // subscribe to the topic named "events"
        mediator.tell(new DistributedPubSubMediator.Subscribe("events", getSelf()), getSelf());
    }

    /**
     * Processes incoming messages that have been sent to this {@code Actor} from the {@link Publisher}
     * @param msg A {@code SubscribeAck} or a {@code String} that will be sent to the client through out
     */
    @Override
    public void onReceive(Object msg) {
        if (msg instanceof String) {
            log.info("Got: {}", msg);
            out.write((String) msg);
        } else if (msg instanceof DistributedPubSubMediator.SubscribeAck) {
            log.info("subscribing");
        } else {
            unhandled(msg);
        }
    }
}
