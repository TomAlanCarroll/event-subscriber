if (window.location.pathname === "/") {
    // Setup the websocket connection and event
    $(function () {
        var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
        var dateSocket = new WS("@routes.Soccer.soccerWs().webSocketURL(request)")

        var receiveEvent = function (event) {
            $("#event-data").html("Event data: " + event.data);
        };

        dateSocket.onmessage = receiveEvent;
    });
}

if (window.location.pathname === "/send") {
    // Add send form submit goals
    $("#goals-form").submit(function (event) {
        // Stop form from submitting normally
        event.preventDefault();

        $("#form-status").html("Sending&hellip;");

        // Get the goals-data and url
        var $form = $(this),
            postGoalsData = [{
                name: "A",
                goals: $form.find("input[name='a-goals']").val()
            }, {
                name: "B",
                goals: $form.find("input[name='b-goals']").val()
            }],
            url = $form.attr("action");

        // Send the data using post
        $.post(url, {goalsData: postGoalsData})
        .done(function (data) {
            $("#form-status").html("The goals have been sent!");
        })
        .fail(function() {
            $("#form-status").html("The goals failed to send :(");
        });
    });

    // Add reset timer form submit
    $("#reset-timer-form").submit(function (event) {
        // Stop form from submitting normally
        event.preventDefault();

        $("#form-status").html("Resetting timer&hellip;");

        // Get the goals-data and url
        var $form = $(this), url = $form.attr("action");

        // Send the data using post
        $.post(url, {goalsData: postGoalsData})
        .done(function (data) {
            $("#form-status").html("The goals have been sent!");
        })
        .fail(function() {
            $("#form-status").html("The goals failed to send :(");
        });
    });
}