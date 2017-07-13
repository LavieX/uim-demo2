(ns clojure-lein.middleware.json
  (:require [cheshire.core :as json]))

;;; Content-type middleware
(def known-content-types
  {:json "application/json"
   :html "text/html"
	 :csv  "text/csv" })

(defn- content-type [type]
  (or (get known-content-types type)
      (name type)))

(defn wrap-content-type
  "Set the content-type response header if the repsonse contains
  a :content-type key. The value can be a keyword such as :json if it
  is in the map `known-content-types`."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (if-let [type (:content-type response)]
        (assoc-in response [:headers "content-type"] (content-type type))
        response))))

(defn wrap-json-response
  "Serialize the response body as JSON if it is a map or sequential."
  [handler]
  (fn [request]
    (let [response (handler request)
          body     (:body response)]
      (merge response
             (if (or (map? body) (sequential? body))
               {:body         (json/generate-string body)
                :content-type :json})))))
