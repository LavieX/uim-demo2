(ns clojure-lein.server
  (:require [ring.adapter.jetty :as jetty]
            [clojure-lein.routes :as routes]
            [clojure-lein.healthchecks :as healthchecks])
  (:gen-class))

(defn start-server
  ([]
   (start-server (routes/build-handler) 4000))
  ([handler port]
   (let [server (jetty/run-jetty handler {:port port :join? false})]
     (fn []
       (.stop server)))))

(defn -main [& args]
  (start-server))
