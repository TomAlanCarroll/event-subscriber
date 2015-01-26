$(function() {
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var dateSocket = new WS("@routes.Application.eventWs().webSocketURL(request)")

    var receiveEvent = function(event) {
        $("#event-data").html("Event data: "+event.data);
    };

    dateSocket.onmessage = receiveEvent
});