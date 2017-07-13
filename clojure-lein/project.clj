(defproject clojure-lein "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.launchdarkly/launchdarkly-client "2.2.3"]
                 [ring "1.6.1"]
                 [ring-logger "0.7.7"]
                 [cheshire "5.7.1"]
                 [compojure "1.6.0" :exclusions [ring/ring-core]]]
  :main ^:skip-aot clojure-lein.server
  :target-path "target/%s"
  :plugins [[lein-eftest "0.3.1"]]
  :eftest {:report clojure-lein.reporter.report/report}
  :profiles {:uberjar {:aot :all}
             :test {:jvm-opts ["-DLOGGING_LEVEL=WARN"]
                    :dependencies [[eftest "0.3.1"]]}
             :dev {:dependencies   [[org.clojure/tools.namespace "0.2.11"]
                                    [clj-http "3.5.0"]]
                   :source-paths   ["dev"]
                   :jvm-opts       ["-DDEV_MODE=true"]}})
