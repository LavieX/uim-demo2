(ns clojure-lein.handlers.metrics
  (:require [clojure-lein.healthchecks :as healthchecks]
            [ring.util.response :as response]))

(defn healthcheck [_]
  (let [results (healthchecks/check-all)]
    (if (not (empty? (:unhealthy results)))
      (-> results response/response (response/status 500))
      (-> results response/response (response/status 200)))))
