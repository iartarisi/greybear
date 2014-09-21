# greybear

A [Go (the board game)](http://en.wikipedia.org/wiki/Go_%28game%29) server written in Clojure.

This is a WIP project to build a Go server where players can play live against one another on a HTML5 board.

## Usage

To setup the database. Create a `greybear` database with a user
`greybear` and a password `greybear`. You can also change these values
in `src/greybear/model.clj`.

Setup the database with the provided lein plugin:

```bash
$ lein db setup
```

Start the websocket server with:

```
$ lein run
```

Start the webserver with:

```
$ lein ring server
```


## Testing

Create the 'greybear-test' database, user and password.

Run tests continuously with:
```
$ lein midje :autotest
```

You'll also need a freshly compiled greybear.js file from the goboard.js project: https://github.com/mapleoin/goboard.js . Clone it to ~/goboard and then run:
`lein cljsbuild auto`.

Note. jquery 2.x does not work on IE6,7,8

## License

Copyright © 2013-2014 Ionuț Arțăriși <ionut@artarisi.eu>

Distributed under the terms of the GNU Affero General Public License.
