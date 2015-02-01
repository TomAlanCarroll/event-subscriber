package controllers;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.pattern.AskTimeoutException;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.plugin.RedisPlugin;
import models.SoccerData;
import models.SoccerGame;
import play.Play;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Akka;
import play.libs.F.Callback0;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import views.html.index;
import views.html.send;

import java.util.UUID;

import static akka.pattern.Patterns.gracefulStop;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Soccer Controller. This is a generalized controller for handling requests and managing
 * the domain object models--in this case, {@link models.SoccerGame} and {@link models.SoccerData}.
 */
public class Soccer extends Controller {
    /**
     * The instance of the publisher. This publishes to all active subscriber actors.
     */
    private static final ActorRef publisher = Akka.system().actorOf(Props.create(SoccerGame.class), "publisher");

    /**
     * Setup an instance of a {@code SoccerData} actor and return a new {@code WebSocket}
     *
     * @return A new {@code WebSocket} for the user
     */
    public static WebSocket<String> soccerWs() {
        return new WebSocket<String>() {
            public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
                final ActorRef subscriber = Akka.system().actorOf(Props.create(SoccerData.class, in, out),
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
     * Action that renders soccer JS
     *
     * @return The rendering of the soccer JS
     */
    public static Result soccerJs() {
        return ok(views.js.soccer.render());
    }

    /**
     * Action that renders index.scala.html
     *
     * @return The rendering of index
     */
    public static Result index() {
        JedisPool p = Play.application().plugin(RedisPlugin.class).jedisPool();
        Jedis jedis = p.getResource();
        String r = jedis.get("foo") + " - foo2:" + jedis.get("foo2");
        p.returnResource(jedis);
        //return ok(index.render("foo3:"+ Cache.get("foo3")+" foo2:"+Cache.get("foo2").toString() +" - redis:" + r ));
        return ok(index.render());
    }

    /**
     * Action that renders send.scala.html
     *
     * @return The rendering of send
     */
    public static Result send() {
        return ok(send.render());
    }

    /**
     * Sends an event to the {@code publisher} given a {@link DynamicForm} in the request.
     *
     * @return A status code 200 OK response
     */
    public static Result sendEvent() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String eventData = requestData.get("eventData");
        publisher.tell(eventData, null);
        return ok();
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result addDisplay() {
        JsonNode json = request().body().asJson();
        String name = json.findPath("name").textValue();
        if (name == null) {
            return badRequest("Missing parameter [name]");
        } else {

            return ok();
        }
    }
}
