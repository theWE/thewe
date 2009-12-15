(ns we
  (:use clojure.contrib.core)
  (:use compojure))

; @todo - directory structure?
(def json-tree-html (slurp "/home/avital/swank/assets/json-tree.html"))

(defroutes server
  (ANY "/wave"
       (answer-wave (read-json (params :events)))))



