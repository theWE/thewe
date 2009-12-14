(ns avital
  (:require clojure.contrib.monads)
  (:use clojure.contrib.monads)
  (:require clojure.contrib.accumulators)
  (:use clojure.contrib.accumulators))

; Original function form
(defn aveg [v] (/ (reduce + v) (count v)))

; Logged form with monads (manually written)
(defn aveg-with-logging-1 [v]
  (domonad (writer-m empty-vector)
    [a1 [(reduce + v) [['(reduce + v) (reduce + v)]]]
     a2 [(/ a1 (count v)) [['(/ (reduce + v) (count v)) (/ a1 (count v))]]]]
    a2))

; Logged form with monads (manually written, no duplication of computation)
; Runs faster when computations done are relatively expensive
(defn aveg-with-logging-2 [v]
  (domonad (writer-m empty-vector)
    [:let [a1 (reduce + v)]
     a1-log [nil [['(reduce + v) a1]]]
     :let [a2 (/ a1 (count v))]
     a2-log [nil [['(/ (reduce + v) (count v)) a2]]]]
    a2))



; Help us do timing
(def *num-times* 100000)

(defmacro many-times [expr]
  `(time (dotimes [x# ~'*num-times*] ~expr)))




; cheap computation time comparison (aveg-with-logging-1 is faster)


(many-times (aveg (range 10 20)))
; "Elapsed time: 80.004559 msecs"

(many-times (aveg-with-logging-1 (range 10 20)))
; "Elapsed time: 580.033055 msecs"

(many-times (aveg-with-logging-2 (range 10 20)))
; "Elapsed time: 760.043312 msecs"



; Expensive computation time comparison (aveg-with-logging-2 is faster)

(many-times (aveg (range 10 1000)))
; "Elapsed time: 3204.182596 msecs"

(many-times (aveg-with-logging-1 (range 10 1000)))
; "Elapsed time: 7228.411924 msecs"

(many-times (aveg-with-logging-2 (range 10 1000)))
; "Elapsed time: 4124.235027 msecs"




((domonad (state-t (writer-m empty-vector))
  [a (m-result 3)
   _ (set-val 0 0)
   b (m-result 4)
   _ (set-val 1 2)
   c (m-result (+ a b))] c) {})

((domonad state-m
  [a (m-result 3)
   _ (set-val 0 0)
   b (m-result 4)
   _ (set-val 1 2)
   c (m-result (+ a b))] c) {"a" 2})

(domonad (writer-m empty-vector)
  [a (m-result 3)
   _ (write "hello")
   b (m-result 4)
   _ (write "hi")
   c (m-result (+ a b))] c)
