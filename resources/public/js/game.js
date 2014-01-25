var ws = new WebSocket("ws://localhost:8080/websocket");

var game_id = parseInt(document.title.match(/Game #(\d+)/)[1]);
var user_id = window.user_id;

ws.onopen = function() {
    console.log("Opened!");
    var msg = {
        cmd: "init-game",
        game_id: game_id,
        user_id: user_id
    };
    ws.send(JSON.stringify(msg));
};

function draw_callback(x, y) {
    console.log(x, y);
    var msg = {
        cmd: "make-move",
        game_id: game_id,
        user_id: user_id,
        cookie: $.cookie("ring-session"),
        x: x,
        y: y
    };
    ws.send(JSON.stringify(msg));
}

function set_turn(turn) {
    var elem = $("#turn span");
    if (turn == "me") {
        elem.text("Your turn!");
        elem.attr("class", "label label-danger");
    } else if (turn == "opponent"){
        elem.text("Opponent's turn");
        elem.attr("class", "label label-warning");
    } else {
        elem.text("Watching");
        elem.attr("class", "label label-default");
    }
}

ws.onmessage = function(message) {
    var data = angular.fromJson(message.data);
    var cmd = data["cmd"];
    console.log(data["stones"]);
    switch (cmd) {
    case "board":
        go_board("goBoard", angular.fromJson(data["stones"]),
                 data["playing"], draw_callback,
                 data["last-x"], data["last-y"]);
        set_turn(data["turn"]);
    default:
        console.log("Got command: " + cmd);
    }
};
