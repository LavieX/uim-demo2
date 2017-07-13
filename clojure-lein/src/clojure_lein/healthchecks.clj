(ns clojure-lein.healthchecks)

(defn check-all []
  {:healthy {:name :hello-check
             :healthy? true
             :message "Hello World!"}
   :unhealthy {}})
