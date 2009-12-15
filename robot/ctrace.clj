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

(defn ppr-str [x]
  (with-out-str (pprint x)))

(defn pprn-str [x]
  (str (ppr-str x) \newline))

(defn log** [result]
  (let [log-path (conj *log-path* :result)]
    (if (instance? Throwable result)
      (do
        (swap! *call-log* assoc-in log-path (str "[Exception] " result))
        (throw result))
      (swap! *call-log* assoc-in log-path result))
    result))

(defmacro log* [result]
  `(log** (attempt ~result)))

(defn log-conj [pre new]
  (conj pre [(swap! *log-counter* inc) new]))

(def *enable-ctrace* false)
(def *ctrace-monads* false)

(defn third [l]
  (nth l 2))

(defn start-complete-tracing []
  (def *enable-ctrace* true)
  (swap! *call-log* empty))

(defn str-copies [unit num]
  (apply str (for [_ (range num)] unit)))

(defn end-complete-tracing-and-print-org-mode
  ([] 
     (def *enable-logging* false)
     (end-complete-tracing-and-print-org-mode 0 nil @*call-log*))
  ([depth what m]
     (when what
       (println (str-copies "*" depth) what)
       (print (str (str-copies " " depth) " "))
       (pprint (:result m)))
     (doseq [[[_ k] v] (sort-by #(first (key %)) (dissoc m :result))]
       (end-complete-tracing-and-print-org-mode (inc depth) k v))))

     
(comment
(defn monad-log-form [comp]
  (apply concat (for [name val] (partition 2 comp)
		     [name `(log ~val)])))
)

(defn ctrace-all [expr]
  (for [clause expr]
    `(ctrace ~clause)))

(defn internal-ctrace-form [expr]
  (let [func (first expr)
	standard-ctrace-form `(~func ~@(ctrace-all (rest expr)))]
        
	(cond
	  (#{'do 'if 'and 'or} func)
	  standard-ctrace-form

          (#{'cond} func)
          `(~func ~@(for [clause (rest expr)] 
                      (if (= clause :else) clause `(ctrace ~clause))))
          
          (#{'if-let} func)
          `(~func [~(first (second expr)) (ctrace ~(second (second expr)))] ~@(ctrace-all (rest (rest expr))))            
          
          (#{'try} func)
          `(~func (ctrace ~(second expr)) ~@(rest (rest expr))) 
          
	  (#{'let 'for 'clojure.core/let 'clojure.core/for 'doseq 'clojure.core/doseq 'binding 'clojure.core/binding} func)
	  `(~func ~(second expr) ~@(ctrace-all (rest (rest expr))))
      
	  (or (special-symbol? func)
	      (macro? expr))
	  expr
      
	  :else
	  standard-ctrace-form)))

(defmacro ctrace [expr]
  `(if *enable-ctrace*
     (binding [*log-path* (log-conj *log-path* '~expr)]
	~(if (seq? expr)
	   (let [func (first expr)]
	     (cond
	       (#{'iterate-events} func)
	       `(ctrace ~(macroexpand-1 expr))
	    
	       :else
	       `(log* ~(internal-ctrace-form expr))))
	
	   (cond
	     (symbol? expr)
	     `(log* ~expr)
	  
	     :else 
	     expr)))
     ~expr))

(defmacro ctraceify [name args rest]
  `(if *enable-ctrace*
     (let [result# 
	   (binding [*log-path* (log-conj *log-path* (str "Function call: " '(~name ~@args)))]
	     ~@(for [arg args] `(ctrace ~arg))
             (log* (do ~@(for [expr# rest] `(ctrace ~expr#)))))]
       result#)
     (do ~@rest)))

(defmacro fn-ctrace [args & rest]
  `(fn ~args (ctraceify '-anonymous- ~args ~rest)))

(defmacro defn-ctrace [name & fdecl]
  (let [other (if (string? (first fdecl))
	       (rest fdecl)
	       fdecl)
	args (first other)]
    `(defn ~name ~args (ctraceify ~name ~args ~(rest other)))))






; tests
    
  
(comment

  (defn-mark f [x y])

  (f 2 3)

  
  
  (macroexpand '(defn-ctrace f [x y] (+ x y)))

  (clean-unit-tests!)
  (def *enable-ctraceging* true)
  (def f (fn-ctrace [x y] 2 (+ (inc x) (dec y))))
  (def g (fn-ctrace [x y] (+ (f x y)
			  (f y x))))
  
  (g 2 3)
  (f 1 1)
  @*call-ctrace*

  (eval '(f 2 3))
  
  (run-tests)

  (defn f [& args] `(f ~@args))

  (f 2 3)

  
  (defn-ctrace f []
    (try
     (eval "x/x")
     (catch Throwable t 2)))
  
  
  
  (def y)
  
  (defn-ctrace f [x] 
    (binding [y (+ 2 2)] (+ x y) 3))

  (defn-ctrace add [a b] (+ a b))  
  (def *enable-ctraceging* true)
  (swap! *call-ctrace* empty)
  (def x 2)
  (def y 3)
  (ctrace (if-let [z (:ass {})] (+ x x)))
  @*call-ctrace*
  
  (def x 2)
  
  `(a (b ~x))

  '(b x)
  
  (defmacro iterate-events [x] `(+ ~x ~x))

  (macroexpand-1 '(iterate-events (inc 2)))

  (iterate-events 1)
  
  (ctrace (iterate-events (inc 2)))

  (swap! call-ctrace {})
  @call-ctrace

  (println (json-str @call-ctrace))
  
  (defn-ctrace fact [n]
    (if (zero? n)
      1
      (* n (fact (dec n)))))

  (reset! call-ctrace {})
  (reset! *ctrace-counter* 0)
					;  (macroexpand-1 '(ctrace (for [x [[1 2] [3 4 5]] y x :when (even? y) z (range 1 y)] z)))

  (ctrace (for [x [1 2] :let [y (inc x)]] (+ x y)))
					;  (ctrace (* (inc 1) (inc 2)))
  (println (json-str @call-ctrace))

  (ctrace (if (zero? 2) (inc x) (inc y)))

  (ctrace x)


  (ctrace (aveg 2 3))


  (macroexpand-1 '(ctrace (+ 2 2)))

  (def x 2)
  (def y 3)

  (ctrace (+ x y))

  (defn-ctrace f [x] (* (+ x x) x) 2)

  (macroexpand-1 '(defn-ctrace f [x] (+ x x) x))

  (macroexpand-1 '(ctrace (do x y)))

  (ctrace (do x y))

  (f 2)

  (ctrace (+ 1 1))

  (do
    (reset! call-ctrace {})
    (f 2)
    (println (json-str @call-ctrace)))

  (do
    (reset! call-ctrace {})
    (ctrace (+ (/ 2 1) (/ 2 0)))
    (println (json-str @call-ctrace)))

  )
