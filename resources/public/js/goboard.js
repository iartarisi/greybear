var PIXEL = 0.5;  // hack to draw pixel-perfect lines
var DOT = 2; // radius of the 9 board dots (hoshi)
var SHADOW_ALPHA = 0.4; // transparency of the future stones


function draw(element_id, stones, playing, make_move, last_move) {
    var board = {
        "canvas": document.getElementById(element_id),
        "context": canvas.getContext("2d"),
        "lines": 19,
        "size": 620,
        "offset": 20,
        "space": 28,  // Math.floor((lines + 3) / size)
        "inner": 504,  // (lines - 1) * space
        "stone-radius": 11,  // space * 2/5
        "background": "#E8BD68",
        "markings": "#444",
        "stones": stones,
        "playing": playing,
        "last-move": last_move
    };
};

function mouse_location(event, board) {
    var x = event.offsetX - board["canvas"].offsetLeft;
    var y = event.offsetY - board["canvas"].offsetTop;
    var square = board["space"];
    var half_square = square / 2;
    if (x > half_square &&
        x < (square + board["inner"] + half_square) &&
        y > half_square &&
        y < (square + board["inner"] + half_square)) {
        return [Math.floor((x - half_square) / size),
                Math.floor((y - half_square) / size)];
    } else {
        return null;
    }
};

function get_stone(board, x, y) {
    return board["stones"][19*y + x];
};

function draw_background(board) {
    board["context"].fillStyle = board["background"];
    var size = board["inner"] + 2 * board["space"];
    board["context"].fillRect(0, 0, size, size);
}

function draw_lines(board) {
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
    board["context"].strokeStyle = board["markings"];
    board["context"].stroke();
}

function draw_letters(board) {
    board["context"].textBaseline = "top";
    board["context"].fillStyle = board["markings"];
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

function draw_circle(board, x, y, radius, fill_color, border_color) {
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

function draw_dots(board) {
    for (var x in [3, 9, 15]) {
        for (var y in [3, 9, 15]) {
            draw_circle(board, x, y, DOT,
                        board["markings"], board["markings"]);
        }
    }
};

function draw_stone(board, color, x, y) {
    if (color == 1) {
        var fill_color = "black";
    } else {
        var fill_color = "white";
    }
    draw_circle(board, x, y, board["stone-radius"], fill_color, "black");
}

function draw_shadow(board, x, y) {
    board["context"].globalAlpha = SHADOW_ALPHA;
    draw_stone(board, board["playing"], x, y);
    board["context"].globalAlpha = 1;
}