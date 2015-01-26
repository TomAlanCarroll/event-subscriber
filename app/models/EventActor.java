package models;

import play.*;
import play.mvc.*;
import play.libs.*;

import scala.concurrent.duration.Duration;
import java.util.concurrent.TimeUnit;
import akka.actor.UntypedActor;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class EventActor extends UntypedActor {
    WebSocket.In<String> in;
    WebSocket.Out<String> out;

    public EventActor(WebSocket.In<String> in, WebSocket.Out<String> out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public void onReceive(Object message) {
        if (message.equals("Event")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            out.write(sdf.format(cal.getTime()));
        } else {
            unhandled(message);
        }
    }
}