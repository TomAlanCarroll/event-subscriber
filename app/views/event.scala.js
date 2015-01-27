if (window.location.pathname === "/") {
    // Setup the websocket connection and event
    $(function () {
        var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
        var dateSocket = new WS("@routes.Events.eventWs().webSocketURL(request)")

        var receiveEvent = function (event) {
            $("#event-data").html("Event data: " + event.data);
        };

        dateSocket.onmessage = receiveEvent;
    });
}

if (window.location.pathname === "/send") {
    // Add send form submit event
    $("#event-form").submit(function (event) {
        // Stop form from submitting normally
        event.preventDefault();

        $("#form-status").html("Sending&hellip;");

        // Get the event-data and url
        var $form = $(this),
            postEventData = $form.find("input[name='event-data']").val(),
            url = $form.attr("action");

        // Send the data using post
        $.post(url, {eventData: postEventData})
        .done(function (data) {
            $("#form-status").html("The event has been sent!");
        })
        .fail(function() {
            $("#form-status").html("The event failed to send :(");
        });
    });
}