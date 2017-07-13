(ns clojure-lein.handlers.launch-darkly
  (:import [com.launchdarkly.client LDConfig$Builder LDClient LDUser$Builder]))


(defn create-client []
  (LDClient. "sdk-196cbe83-740b-4712-aae8-ee07f14a88bc"))

(defn ld-config []
  (-> (LDConfig$Builder.)
      (.connectTimeout 3)
      (.socketTimeout 3)
      .build))

(defn ld-user [uuid]
  (-> (LDUser$Builder. uuid)
      (.anonymous true)
      .build))

(defn show-feature? [feature-flag default-value]
  (let [ld-client (create-client)
        anonymous-user (ld-user "anonymous")]
    (.boolVariation ld-client feature-flag anonymous-user default-value)))
