var PIXEL = 0.5;  // hack to draw pixel-perfect lines
var DOT = 2;  // radius of the 9 board dots (hoshi)
var SHADOW_ALPHA = 0.4;  // transparency of the future stones
var BACKGROUND = "#E8BD68";  // board background color
var MARKINGS = "#444";  // board markings color, such as lines and hoshi
var LASTMOVE = "red";  // color of the last move dot

var shadow = [-1, -1];
var board = {
    "lines": 19,
    "size": 620,
    "offset": 20,
    "space": 28,  // Math.floor((lines + 3) / size)
    "inner": 504,  // (lines - 1) * space
    "stone-radius": 11  // space * 2/5
};

function go_board(element_id, stones, playing, make_move, last_move) {
    board["canvas"] = document.getElementById(element_id);
    board["context"] = document.getElementById(element_id).getContext("2d");
    board["stones"] = stones;
    board["playing"] = playing;
    board["last-move"] = last_move;
    window.make_move = make_move;
    make_board();
};

function mouse_location(event) {
    if (event.layerX || event.layerX == 0) { // Firefox
        var x = event.layerX - board["canvas"].offsetLeft;
        var y = event.layerY - board["canvas"].offsetTop;
    } else { // Opera
        var x = event.offsetX - board["canvas"].offsetLeft;
        var y = event.offsetY - board["canvas"].offsetTop;
    }
    var square = board["space"];
    var half_square = square / 2;  // magic value
    if (x > square - half_square &&
        x < (square + board["inner"] + half_square) &&
        y > square - half_square &&
        y < (square + board["inner"] + half_square)) {
        return [Math.floor((x - half_square) / square),
                Math.floor((y - half_square) / square)];
    } else {
        return null;
    }
};

function get_stone(x, y) {
    return board["stones"][19*y + x];
};

function draw_background() {
    board["context"].fillStyle = BACKGROUND;
    var size = board["inner"] + 2 * board["space"];
    board["context"].fillRect(0, 0, size, size);
};

function draw_lines() {
    board["context"].beginPath();
    var close_edge = board["offset"];
    var far_edge = close_edge + board["inner"];
    for (var i=0; i<19; i++) {
        var x = PIXEL + board["offset"] + i * board["space"];
        board["context"].moveTo(close_edge, x);
        board["context"].lineTo(far_edge, x);
        board["context"].moveTo(x, close_edge);
        board["context"].lineTo(x, far_edge);
    }
    board["context"].strokeStyle = MARKINGS;
    board["context"].stroke();
};

function draw_letters() {
    board["context"].textBaseline = "top";
    board["context"].fillStyle = MARKINGS;
    for (var i=0; i<19; i++) {
        var letter = "abcdefghjklmnopqrst"[i];
        board["context"].fillText(letter,
                                  i * board["space"] + board["offset"],
                                  board["inner"] + 5/4 * board["space"]);
        board["context"].fillText(board["lines"] - i,
                                  board["inner"] + 5/4 * board["space"],
                                  i * board["space"] + board["offset"]);
    }
};

function draw_circle(x, y, radius, fill_color, border_color) {
    board["context"].beginPath();
    board["context"].arc(board["offset"] + PIXEL + x * board["space"],
                         board["offset"] + PIXEL + y * board["space"],
                         radius,
                         0,
                         2 * Math.PI,
                         false);
    board["context"].closePath();
    board["context"].strokeStyle = border_color;
    board["context"].fillStyle = fill_color;
    board["context"].fill();
    board["context"].stroke();
};

function draw_dots() {
    var dot_locations = [3, 9, 15];
    for (var i = 0; i < dot_locations.length; i++) {
        for (var j = 0; j < dot_locations.length; j++) {
            draw_circle(dot_locations[i], dot_locations[j],
                        DOT, MARKINGS, MARKINGS);
        }
    }
};

function draw_stone(color, x, y) {
    if (color == 1) {
        var fill_color = "black";
    } else {
        var fill_color = "white";
    }
    draw_circle(x, y, board["stone-radius"], fill_color, "black");
};

function draw_shadow() {
    board["context"].globalAlpha = SHADOW_ALPHA;
    draw_stone(board["playing"], shadow[0], shadow[1]);
    board["context"].globalAlpha = 1;
};

function draw_last_move() {
    draw_circle(board["last-move"][0], board["last-move"][1],
                DOT, LASTMOVE, LASTMOVE);
};

function draw_board() {
    // Draws the board, together with all the stones on it  
    draw_background();
    draw_lines();
    draw_letters();
    draw_dots();
    for (var i = 0; i < board["stones"].length; i++) {
        var color = board["stones"][i];
        if (color == 1 || color == 2) {
            draw_stone(color,
                       i % board["lines"], Math.floor(i/board["lines"]));
        }
    }
    if (board["last-move"]) {
        draw_last_move();
    }
    if (shadow) {
        draw_shadow();
    }
};

function mouse_move(event) {
    var location = mouse_location(event);
    console.log(location);
    if (location) {
        var x = location[0];
        var y = location[1];
        if (x != shadow[0] || y != shadow[1]) {
            shadow = [x, y];
            draw_board();
        }
    }
};

function mouse_out(event) {
    shadow = [-1, -1];
    draw_board();
};

function mouse_up(event) {
    var location = mouse_location(event);
    if (location) {
        var x = location[0];
        var y = location[1];
        if (get_stone(x, y) == 0) {
            make_move(x, y);            
        }
    }
};

function setup_board() {
    board["canvas"].height = board["size"];
    board["canvas"].width = board["size"];
    if (board["playing"]) {
        board["canvas"].addEventListener('mousemove', mouse_move, false);
        board["canvas"].addEventListener('mouseout', mouse_out, false);
        board["canvas"].addEventListener('mouseup', mouse_up, false);
    }
};

function make_board() {
    setup_board();
    draw_board();    
};