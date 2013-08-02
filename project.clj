(defproject greybear "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1586"]
                 [korma "0.3.0-RC5"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [org.clojure/java.jdbc "0.3.0-alpha4"]
                 [org.webbitserver/webbit "0.4.14"]
                 [ring/ring-core "1.2.0"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [compojure "1.1.5"]
                 [hiccup "1.0.3"]
                 [com.cemerick/friend "0.1.5"]
                 [sandbar/sandbar "0.4.0-SNAPSHOT"]
                 [midje "1.5.1"]]
  :plugins [[lein-cljsbuild "0.3.0"]
            [lein-ring "0.8.3"]
            [lein-midje "3.0.0"]]
  :main greybear.websocket
  :ring {:handler greybear.server/app}
  :cljsbuild {:builds
              [{:source-paths ["src/cljs" "/home/mapleoin/goboard/src"]
                :compiler {:output-to "resources/public/js/greybear.js"
                           :optimizations :whitespace
                           :pretty-print true}}]})
