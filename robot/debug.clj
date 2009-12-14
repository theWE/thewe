(ns we
  (:use clojure.contrib.pprint)
  (:use clojure.contrib.duck-streams)
  (:import java.util.Date))

(def *call-log* (atom {}))

(defn macro? [expr]
  (not= (macroexpand-1 expr) expr))

(defmacro attempt [expr]
  `(try ~expr (catch Throwable t# t#)))

(def *log-path* [])
(def *log-counter* (atom 0))
(def *unit-tests* (atom {}))

(defn ppr-str [x]
  (with-out-str (pprint x)))

(defn pprn-str [x]
  (str (ppr-str x) \newline))

(defmacro log-time [expr]
  `(let [start# (. System (nanoTime))
         ret# ~expr]
     (swap! *call-log* assoc-in (conj *log-path* :time) (str (/ (double (- (. System (nanoTime)) start#)) 1000000.0) " msecs"))
     ret#))

(defn log** [result]
  (let [log-path (conj *log-path* :result)]
    (if (instance? Throwable result)
      (do
        (swap! *call-log* assoc-in log-path (str "[Exception] " result))
        (throw result))
      (swap! *call-log* assoc-in log-path (ppr-str result)))
    result))

(defmacro log* [result]
  `(log** (attempt ~result)))

; @todo: remove this stupid string / thing
(defn log-conj-clean [pre new]
  (conj pre (str (swap! *log-counter* inc) "/" new)))

(defn log-conj [pre new]
  (log-conj-clean pre (binding [*print-level* 3] 
                        (pr-str new))))

(def *enable-logging* false)
(def *log-monads* false)

(defn third [l]
  (nth l 2))

(comment
(defn monad-log-form [comp]
  (apply concat (for [name val] (partition 2 comp)
		     [name `(log ~val)])))
)

(defn log-all [expr]
  (for [clause expr]
    `(log ~clause)))

(defn internal-log-form [expr]
  (let [func (first expr)
	standard-log-form `(~func ~@(log-all (rest expr)))]
        
	(cond
	  (#{'do 'if 'and 'or} func)
	  standard-log-form

          (#{'if-let} func)
          `(~func [~(first (second expr)) (log ~(second (second expr)))] ~@(log-all (rest (rest expr))))            
          
          (#{'try} func)
          `(~func (log ~(second expr)) ~@(rest (rest expr))) 
          
	  (#{'let 'for 'clojure.core/let 'clojure.core/for 'doseq 'clojure.core/doseq 'binding 'clojure.core/binding} func)
	  `(~func ~(second expr) ~@(log-all (rest (rest expr))))
      
	  (or (special-symbol? func)
	      (macro? expr))
	  expr
      
	  :else
	  standard-log-form)))

(defmacro log [expr]
  `(if *enable-logging*
     (binding [*log-path* (log-conj *log-path* '~expr)]
	~(if (seq? expr)
	   (let [func (first expr)]
	     (cond
	       (#{'iterate-events} func)
	       `(log ~(macroexpand-1 expr))
	    
	       :else
	       `(log* ~(internal-log-form expr))))
	
	   (cond
	     (symbol? expr)
	     `(log* ~expr)
	  
	     :else 
	     expr)))
     ~expr))

(defn clean-unit-tests! []
  (reset! *unit-tests* {}))

(def *record-unit-tests* false)

(defmacro logify [name args rest]
  `(if *enable-logging*
     (let [result# 
	   (binding [*log-path* (log-conj-clean *log-path* (str "Function call: " '(~name ~@args)))]
	     ~@(for [arg args] `(log ~arg))
             (log* (do ~@(for [expr# rest] `(log ~expr#)))))]
       (if (and *record-unit-tests* (empty? *log-path*))
	 (let [expr# `(~'~name ~@~args)]
	   (swap! *unit-tests* assoc expr# result#)))
       result#)
     (do ~@rest)))

(defmacro fn-log [args & rest]
  `(fn ~args (logify '-anonymous- ~args ~rest)))

(defmacro defn-log [name & fdecl]
  (let [other (if (string? (first fdecl))
	       (rest fdecl)
	       fdecl)
	args (first other)]
    `(defn ~name ~args (logify ~name ~args ~(rest other)))))

(def *tests-file* "/home/avital/swank/tests/tests.clj")

(defn run-tests []
  (for [[test expected] (read-string (str "(" (slurp *tests-file*) ")"))
	:let [actual (eval test)] 
	:when (not= actual expected)]
    `(~test ~actual ~expected)))

(defn approve-tests []
  (seq (for [test @*unit-tests*]
	 (append-spit *tests-file* (prn-str test))))
  (reset! *unit-tests* {}))

(defn current-time []
  (. (new Date) (toString)))

(defn log-exception [t]
  (append-spit "/home/avital/swank/log/exceptions"
	       (str (current-time) \newline 
		    t \newline \newline)))

(defmacro wave-attempt [expr]
  `(try ~expr 
	(catch Throwable t#
	  (log-exception t#)
	  [])))

(defn log-info [title x]
  (append-spit "/home/avital/swank/log/operations"
	       (str (current-time) \newline title \newline (pprn-str x) \newline))
  x)



; tests
  
  
  
(comment

  (defn-mark f [x y])

  (f 2 3)

  
  
  (macroexpand '(defn-log f [x y] (+ x y)))

  (clean-unit-tests!)
  (def *enable-logging* true)
  (def f (fn-log [x y] 2 (+ (inc x) (dec y))))
  (def g (fn-log [x y] (+ (f x y)
			  (f y x))))
  
  (g 2 3)
  (f 1 1)
  @*call-log*

  (eval '(f 2 3))
  
  (run-tests)

  (defn f [& args] `(f ~@args))

  (f 2 3)

  
  (defn-log f []
    (try
     (eval "x/x")
     (catch Throwable t 2)))
  
  
  
  (def y)
  
  (defn-log f [x] 
    (binding [y (+ 2 2)] (+ x y) 3))

  (defn-log add [a b] (+ a b))  
  (def *enable-logging* true)
  (swap! *call-log* empty)
  (def x 2)
  (def y 3)
  (log (if-let [z (:ass {})] (+ x x)))
  @*call-log*
  
  (def x 2)
  
  `(a (b ~x))

  '(b x)
  
  (defmacro iterate-events [x] `(+ ~x ~x))

  (macroexpand-1 '(iterate-events (inc 2)))

  (iterate-events 1)
  
  (log (iterate-events (inc 2)))

  (swap! call-log {})
  @call-log

  (println (json-str @call-log))
  
  (defn-log fact [n]
    (if (zero? n)
      1
      (* n (fact (dec n)))))

  (reset! call-log {})
  (reset! *log-counter* 0)
					;  (macroexpand-1 '(log (for [x [[1 2] [3 4 5]] y x :when (even? y) z (range 1 y)] z)))

  (log (for [x [1 2] :let [y (inc x)]] (+ x y)))
					;  (log (* (inc 1) (inc 2)))
  (println (json-str @call-log))

  (log (if (zero? 2) (inc x) (inc y)))

  (log x)


  (log (aveg 2 3))


  (macroexpand-1 '(log (+ 2 2)))

  (def x 2)
  (def y 3)

  (log (+ x y))

  (defn-log f [x] (* (+ x x) x) 2)

  (macroexpand-1 '(defn-log f [x] (+ x x) x))

  (macroexpand-1 '(log (do x y)))

  (log (do x y))

  (f 2)

  (log (+ 1 1))

  (do
    (reset! call-log {})
    (f 2)
    (println (json-str @call-log)))

  (do
    (reset! call-log {})
    (log (+ (/ 2 1) (/ 2 0)))
    (println (json-str @call-log)))

  )