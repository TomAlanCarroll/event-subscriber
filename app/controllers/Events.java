package controllers;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.AskTimeoutException;
import models.Publisher;
import models.Subscriber;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Akka;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import views.html.index;
import views.html.send;

import java.util.UUID;

import static akka.pattern.Patterns.gracefulStop;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Events Controller. This is a generalized controller for handling requests and managing
 * the domain object models--in this case, {@link Publisher} and {@link Subscriber}.
 */
public class Events extends Controller {
    /**
     * The instance of the publisher. This publishes to all active subscriber actors.
     */
    private static final ActorRef publisher = Akka.system().actorOf(Props.create(Publisher.class), "publisher");

    /**
     * Setup an instance of a {@code Subscriber} actor and return a new {@code WebSocket}
     * @return A new {@code WebSocket} for the user
     */
    public static WebSocket<String> eventWs() {
        return new WebSocket<String>() {
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                final ActorRef subscriber = Akka.system().actorOf(Props.create(Subscriber.class, in, out),
                        "subscriber-" + UUID.randomUUID());

                in.onClose(new Callback0() {
                    @Override
                    public void invoke() throws Throwable {
                        try {
                            // Stop this subscriber when the websocket closes
                            Future<Boolean> stopped = gracefulStop(subscriber, Duration.create(10, SECONDS));
                            Await.result(stopped, Duration.create(11, SECONDS));
                            // the actor has been stopped
                        } catch (AskTimeoutException e) {
                            // the actor wasn't stopped within 10 seconds
                        }
                    }
                });
            }

        };
    }

    /**
     * Action that renders event JS
     * @return The rendering of the event JS
     */
    public static Result eventJs() {
        return ok(views.js.event.render());
    }

    /**
     * Action that renders index.scala.html
     * @return The rendering of index
     */
    public static Result index() {
        return ok(index.render());
    }

    /**
     * Action that renders send.scala.html
     * @return The rendering of send
     */
    public static Result send() {
        return ok(send.render());
    }

    /**
     * Sends an event to the {@code publisher} given a {@link DynamicForm} in the request.
     * @return A status code 200 OK response
     */
    public static Result sendEvent() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String eventData = requestData.get("eventData");
        publisher.tell(eventData, null);
        return ok();
    }

}
