package models;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import play.mvc.WebSocket;

public class Subscriber extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    WebSocket.In<String> in;
    WebSocket.Out<String> out;

    public Subscriber(WebSocket.In<String> in, WebSocket.Out<String> out) {
        this.in = in;
        this.out = out;

        ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();
        // subscribe to the topic named "events"
        mediator.tell(new DistributedPubSubMediator.Subscribe("events", getSelf()), getSelf());
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof String) {
            log.info("Got: {}", msg);
            out.write((String)msg);
        } else if (msg instanceof DistributedPubSubMediator.SubscribeAck) {
            log.info("subscribing");
        } else {
            unhandled(msg);
        }
    }
}
