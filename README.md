# greybear

A Clojure library designed to ... well, that part is up to you.

## Usage

To setup the database. Create a `greybear` database with a user
`greybear` and a password `greybear`. You can also change these values
in `src/greybear/model.clj`.

Then just start a `lein repl` and create the tables and add some mock data:

```clojure
(require '[clojure.java.jdbc :as jdbc])
(use 'greybear.model)
; creates tables
(jdbc/with-connection psql (setup))
(create-user "swede" "death")
(create-user "colin" "guns")
; 1, 2 are the respective `id`s of the users created above
(new-game 1 2)
```

Start the websocket server with:

```
$ lein run
```

Start the webserver with:

```
$ lein ring server
```

Run tests continuously with:
```
$ lein midje :autotest
```

You'll also need a freshly compiled greybear.js file from the goboard.js project: https://github.com/mapleoin/goboard.js . Clone it to ~/goboard and then run:
`lein cljsbuild auto`.

Note. jquery 2.x does not work on IE6,7,8

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
