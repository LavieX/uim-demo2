(ns clojure-lein.reporter.report
  (:require [eftest.report :refer [report-to-file]]
            [eftest.report.junit :as junit]
            [eftest.report.progress :as progress]))

(def xml-reporter (report-to-file junit/report "target/test-results.xml"))

(defn report [m]
  (progress/report m)
  (xml-reporter m))
