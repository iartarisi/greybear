(defproject greybear "0.1.0-SNAPSHOT"
  :description "Live Go game server using HTML5 canvas and websockets"
  :url "http://github.com/mapleoin/greybear"
  :license {:name "GNU Affero General Public License version 3"
            :url "https://www.gnu.org/licenses/agpl-3.0.html"}
  :dependencies [[com.cemerick/friend "0.1.5"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.3"]
                 [korma "0.4.0"]
                 [midje "1.6.3"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.2"]
                 [org.clojure/java.jdbc "0.3.5"]
                 [org.clojure/tools.logging "0.3.0"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [org.webbitserver/webbit "0.4.14"]
                 [ring/ring-core "1.2.0"]
                 [ring/ring-jetty-adapter "1.1.8"]]
  :plugins [[lein-ring "0.8.3"]
            [lein-midje "3.0.0"]]
  :ring {:handler greybear.server/app}
  :aliases {"setup-db" ["run" "-m" "greybear.model.dbcli/setup"]
            "teardown-db" ["run" "-m" "greybear.model.dbcli/teardown"]
            "websockets" ["run" "-m" "greybear.websocket"]}
  )
