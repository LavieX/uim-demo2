(ns clojure-lein.core-test
  (:require [clojure.test :refer :all]
            [clojure-lein.server :as server]
            [clj-http.client :as client]))

(defn with-server [f]
  (let [stopper (server/start-server)]
    (f)
    (stopper)))

(use-fixtures :once with-server)

(def localhost "http://localhost:4000")

(defn http-get
  [path]
  (client/get (str localhost path) {:throw-exceptions? false :as :json}))

(deftest metrics-healthcheck
  (let [healthcheck (http-get "/metrics/healthcheck")]
    (is (= 200 (:status healthcheck)))
    (is (empty? (get-in healthcheck [:body :unhealthy])))
    (is (not (empty? (get-in healthcheck [:body :healthy]))))))
