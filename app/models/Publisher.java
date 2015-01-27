package models;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;

/**
 * A generalized model of a Publisher actor
 */
public class Publisher extends UntypedActor {
    /**
     * The mediator for this distributed publish subscribe extension
     */
    private final ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();

    /**
     * Processes incoming messages that have been sent to this {@code Actor}
     * @param msg An {@code Object} or {@code String} to publish to the {@link Subscriber}s
     */
    @Override
    public void onReceive(Object msg) {
        if (msg instanceof String) {
            mediator.tell(new DistributedPubSubMediator.Publish("events", msg), getSelf());
        } else {
            unhandled(msg);
        }
    }
}
