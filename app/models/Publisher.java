package models;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.contrib.pattern.DistributedPubSubExtension;
import akka.contrib.pattern.DistributedPubSubMediator;

public class Publisher extends UntypedActor {
    // activate the extension
    ActorRef mediator = DistributedPubSubExtension.get(getContext().system()).mediator();

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof String) {
            mediator.tell(new DistributedPubSubMediator.Publish("events", msg), getSelf());
        } else {
            unhandled(msg);
        }
    }
}
