(ns we
  (:use clojure.contrib.core)
  (:use compojure))

(defroutes server
  (ANY "/wave"
       (answer-wave (read-json (params :events)))))



