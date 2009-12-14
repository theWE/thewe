(ns we
  (:use clojure.contrib.core)
  (:use compojure))

; @todo - directory structure?
(def json-tree-html (slurp "/home/avital/swank/assets/json-tree.html"))

(defroutes server
  (GET "/tests/start"
    (def *record-unit-tests* true)
    "Ok")
  (GET "/tests/stop"
    (def *record-unit-tests* false)
    (approve-tests)
    "Done")
  (GET "/log/start"
    (reset! *call-log* {})
    (def *enable-logging* true)
    "Logging is currently activated. Do some stuff in wave and when you are done, <a href='../log/stop'>stop logging and see results</a>")
  (GET "/log/stop" 
    [(comment {:headers {"Content-Type" "text/plain"}})
     (do
       (def *enable-logging* false)
       (.replace json-tree-html "@@@result@@@" (escape-html (json-str @*call-log*))))])
  (ANY "/wave"
       (answer-wave (read-json (params :events)))))


