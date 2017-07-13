(ns clojure-lein.routes
  (:require [clojure-lein.handlers.metrics :as metrics]
            [clojure-lein.middleware.json :as json]
            [clojure-lein.handlers.launch-darkly :as ld]
            [clojure.pprint :as pprint]
            [compojure.core :refer :all]
            [compojure.handler :refer [api]]
            [compojure.route :as route]
            [ring.logger :as logger]))

(defn home-handler [request]
  (if (ld/show-feature? "test-flag" false)
    "Feature Enabled!"
    "<h1>Hello World</h1>"))

(defn debug [handler]
  (fn [request]
    (handler request)))

(defroutes metrics-routes
  (GET "/healthcheck" [] metrics/healthcheck))

(defroutes all-routes
  (GET "/" [] home-handler)
  (context "/metrics" []
           metrics-routes)
  (route/not-found "<h1>Page not found</h1>"))

(defn build-handler []
  (-> (api all-routes)
      json/wrap-json-response
      json/wrap-content-type
      (logger/wrap-with-logger {:printer :no-color})))
