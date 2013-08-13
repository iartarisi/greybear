var ws = new WebSocket("ws://localhost:8080/websocket");

var game_id = document.title.match(/Game #(\d+)/)[1];

ws.onopen = function() {
    console.log("Opened!");
    ws.send("init-game: " + game_id);
};

function draw_callback(x, y) {
    console.log(x, y);
    ws.send("make-move: " + game_id + " " + x + "-" + y);
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
        default:
          console.log("Got command: " + cmd);
    }
};
