package controllers;

import static java.util.concurrent.TimeUnit.SECONDS;
import models.Publisher;
import models.Subscriber;
import play.libs.Akka;
import play.libs.F.Callback0;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import scala.concurrent.duration.Duration;
import views.html.index;
import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;

import com.fasterxml.jackson.databind.JsonNode;

import views.html.*;

import java.util.UUID;

public class Application extends Controller {
	public static ActorRef publisher =  Akka.system().actorOf(Props.create(Publisher.class), "publisher");
	
	public static WebSocket<String> eventWs() {
		return new WebSocket<String>() {
			public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {
				Akka.system().actorOf(Props.create(Subscriber.class, in, out), "subscriber-" + UUID.randomUUID());
				final Cancellable cancellable = Akka.system().scheduler().schedule(Duration.create(1, SECONDS),
						Duration.create(1, SECONDS),
						publisher,
						String.valueOf(Math.random() * 10),
						Akka.system().dispatcher(),
						null
				);
				in.onClose(new Callback0() {
					@Override
					public void invoke() throws Throwable {
						cancellable.cancel();
					}
				});
			}

		};
	}

	public static Result eventJs() {
		return ok(views.js.event.render());
	}

	public static Result index() {
		return ok(index.render());
	}

	public static Result send() {
		return ok(send.render());
	}

	public static Result sendEvent() {
		return ok();
	}

}
