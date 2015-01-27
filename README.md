# Publish-Subscribe with Play, Akka, and WebSockets

This project is a simple example of publish-subscribe Akka actors within the Play Framework.
An overview of the implementation can be found here:
http://tomcarroll.net/Clustered-Publish-Subscribe-with-Play-Akka-and-WebSockets

To run the project, install Activator by following the instructions here:

https://www.playframework.com/download
https://www.playframework.com/documentation/2.3.x/Installing

After you have added activator to your path, run the following command in the project root to start the server:
```
activator run
```

The following URLs should be available after activator is finished starting the app:

http://localhost:9000
http://localhost:9000/send
![Published Event](https://raw.githubusercontent.com/TomAlanCarroll/event-subscriber/master/screenshot3.png)
