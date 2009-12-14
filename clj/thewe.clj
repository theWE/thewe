(ns we
  (:use clojure.contrib.json.read)
  (:use clojure.contrib.json.write)
  (:use clojure.set)
  (:use clojure.contrib.duck-streams)
  (:use clojure.contrib.str-utils)
  (:import java.util.Date))

; =====================
; ======= Atoms =======
; =====================

; Atom File DB

(def atom-base-dir)

(defn atom-filename [name]
  (str atom-base-dir name))


; Reading Atoms

(defn file-exists?
  "Check to see if a file exists"
  [file]
  (.exists (java.io.File. file)))

(defn load-atom-from-file [name]
  (let [file (atom-filename name)]
    (if (file-exists? file)
      (read-string (slurp file)))))

(defmacro load-atom [atom]
  `(load-atom-from-file ~(.replace (str atom) "*" "")))

(def *db-atoms* {})


; Actual atoms

(defmacro init-atoms 
  ([] nil)
  ([name initial-val & rest]
     `(do
        (defonce ~name nil)
        (if-not ~name
          (if-let [db-val# (load-atom ~name)]
            (def ~name (atom db-val#))
            (def ~name (atom ~initial-val))))
        (alter-var-root #'*db-atoms* assoc '~name ~name)
        ~`(init-atoms ~@rest))))



; Saving Atoms

(defn save-atom-to-file [name atom]
  (spit (atom-filename name) @atom))

(defn save-atom [name atom]
  (save-atom-to-file (.replace (str name) "*" "") atom))

(defn save-atoms []
  (doseq [[name atom] *db-atoms*]
    (save-atom name atom)))

; Thanks to Jonathan Smith
(defn periodicly [fun time]
  "starts a thread that calls function every time ms"
  (let [thread (new Thread (fn [] (loop [] (fun) (Thread/sleep time)
(recur))))]
    (.start thread)
    thread)) 

(defonce save-periodically false)

(defn start-atom-db [base-dir]
  (def atom-base-dir base-dir)
  (init-atoms *rep-rules* #{}
	      *clipboard* nil
	      *last-clipboard* nil
	      *other-wave* nil
	      *mixin-db* {})
  (when-not save-periodically
    (periodicly save-atoms 25000)
    (def save-periodically true)))



; Some global vars
(def *ctx*)

; =========================
; ======= Utilities =======
; =========================

; (dig {:a {:b 3}} :a :b) returns 3
(defn dig [map & rest]
  (get-in map rest))

(def log-list (atom []))

; From clojure.contrib.core. Not sure why I can't just use it.
(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
nested structure. keys is a sequence of keys. Any empty maps that result
will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
	    (assoc m k newmap)
	    (dissoc m k)))
      m)
    (dissoc m k)))

(defn assoc-in-x 
  ([map] map) 
  ([map path val & rest]
     (apply assoc-in-x `[~(assoc-in map path val) ~@rest])))


; =========================================
; ======= Logical Replication Layer =======
; =========================================

; Data structures:
; ----------------
;
; rep-loc:    Either {:type "blip" :wave-id wave-id :wavelet-id wavelet-id :blip-id blip-id} or
;                    {:type "gadget" :wave-id wave-id :wavelet-id wavelet-id :blip-id blip-id :key key} or
;
; rep-op:     {:rep-loc rep-loc :content content :action action :loc-type: gadget} or
;             {:rep-loc rep-loc :content content :action action :loc-type: blip}
;
; rep-rules:  A set of sets (a partition) of rep-locs

(defn-log add-to-class [partition class el] 
  (conj (disj partition class) (conj class el)))

(defn-log containing-rep-class [rep-loc]
  (first (for [rep-class @*rep-rules* :when (some #{rep-loc} rep-class)] rep-class)))

(defn-log replicate-replocs! [r1 r2]
  (let [rc1 (containing-rep-class r1) rc2 (containing-rep-class r2)]
    (cond
      (and (not rc1) (not rc2))  ; when both are not in rep-classes
      (swap! *rep-rules* conj #{r1 r2})

      (and rc1 (not rc2))
      (swap! *rep-rules* add-to-class rc1 r2)

      (and rc2 (not rc1))
      (swap! *rep-rules* add-to-class rc2 r1)

      (and (and rc1 rc2) (not= rc1 rc2))
      (swap! *rep-rules* #(conj (disj % rc1 rc2) (union rc1 rc2))))))

(defn-log equal-rep-loc [r1 r2]
  (let [rep-loc-keys [:wave-id :wavelet-id :blip-id]] 
    (= (select-keys r1 rep-loc-keys) (select-keys r2 rep-loc-keys))))

(defmulti update-rep-loc-ops
  (fn-log [rep-loc content] (:type rep-loc)))

(defn-log has-annotation [rep-op name val]
  (some #(and (= (% "name") name) (= (% "value") val)) (:annotations rep-op)))

(defn-log transformation-function-if-match-rep-loc [rep-op rep-loc]
  (let [rep-op-key (dig rep-op :rep-loc :key)
	rep-loc-key (:key rep-loc)]
    (cond 
      
      (and (:blip-id rep-loc) 
	   (= (:rep-loc rep-op) rep-loc))
      identity
    
      (and (:subcontent rep-loc)	; replication is by subcontent
	   (and
	    (= (dissoc (:rep-loc rep-op) :blip-id) (dissoc rep-loc :blip-id :subcontent))
	    (.contains (:content rep-op) (:subcontent rep-loc))))
      identity
    
      (and (:annotation-name rep-loc)	; replication is by annotation
	   (has-annotation rep-op (:annotation-name rep-loc) (:annotation-value rep-loc)))   
      identity
    
    
      (and
       rep-op-key
       (equal-rep-loc rep-loc (:rep-loc rep-op))
       (or
	(= rep-op-key rep-loc-key) ; normal gadget-gadget replication
	(and ; hyper replication
	 (.startsWith rep-op-key (str rep-loc-key "."))
	 (not= rep-op-key rep-loc-key))))
      (fn [other-rep-loc] 
	(assoc other-rep-loc :key 
	       (.replaceFirst rep-op-key rep-loc-key (:key other-rep-loc))))

      :else
      nil)))


(defn-log do-replication 
  "Receives rep-rules and incoming rep-ops and returns rep-ops to be acted upon"
  [rep-rules rep-ops]
  (apply concat 
	 (for [rep-op rep-ops
	       rep-class @*rep-rules*
	       rep-loc rep-class
	       :let [trans-func (transformation-function-if-match-rep-loc rep-op rep-loc)]
	       :when trans-func]
	   (apply concat 
		  (for [other-rep-loc (disj rep-class rep-loc)] 
		    (update-rep-loc-ops (trans-func other-rep-loc) (:content rep-op)))))))



; ===============================================
; ======= Google Wave Incoming JSON Layer =======
; ===============================================

; Data structures:
; ----------------
;
; incoming-map:       Some crazy Google format of a map that contains information on which
;                     blips were modified, their content (including gadgets inside)
;                     and their parent blip contents
;
; blip-data:          (dig incoming-wave-map "blips" "map" blip-id)



(defn-log blip-data-to-rep-ops [blip-data]
  (let [basic-rep-loc {:wave-id (blip-data "waveId"), :wavelet-id (blip-data "waveletId"), :blip-id (blip-data "blipId")}]
    (if-let [gadget-map (first (dig blip-data "elements"))]
					; there is a gadget here
      (let [gadget-state (dig (val gadget-map) "properties")]
        (for [[k v] gadget-state]
          {:rep-loc (assoc basic-rep-loc :type "gadget" :key k) :content v}))

					; there is no gadget

      (if-let [dnr (first (filter #(= ( % "name") "we/DNR") (:annotations *ctx*)))]
	[{:rep-loc (assoc basic-rep-loc :type "blip") :content 
	  (.replace (blip-data "content") (subs (blip-data "content") (dig dnr "range" "start") (dig dnr "range" "end")) "") 
	  :annotations (:annotations *ctx*)}]
	[{:rep-loc (assoc basic-rep-loc :type "blip") :content (blip-data "content") :annotations (:annotations *ctx*)}]))))



; ===============================================
; ======= Google Wave Outgoing JSON Layer =======
; ===============================================

; Data structures:
; ----------------
;
; outgoing-map:  Some crazy Google format of a map that contains information on which
;                operations the robot will do

(defn kill-nil [m]
  (into {} (for [[key val] m :when val] [key (if (map? val) (kill-nil val) val)])))

(defn-log params [rep-loc] 
  (kill-nil 
   {"waveId" (:wave-id rep-loc)
    "waveletId" (:wavelet-id rep-loc)
    "blipId" (:blip-id rep-loc)}))

(defn-log op-skeleton [rep-loc] 
  {"params" (params rep-loc)
   "id" (str (rand))})

(defn-log modify-how-json [modify-how values elements]
  (kill-nil
   {"modifyHow" modify-how 
    "values" values
    "elements" elements}))

(defn-log modify-query-json [restrictions max-res element-match]
  {"restrictions" restrictions
    "maxRes" max-res 
    "elementMatch" element-match})

(defn-log document-modify-json 
  [rep-loc range modify-query modify-how]
  (kill-nil 
   (assoc-in-x (op-skeleton rep-loc) 
               ["method"] "document.modify"
               ["params" "modifyAction"] modify-how
               ["params" "range"] range
               ["params" "modifyQuery"] modify-query)))

(defn-log range-json [start end] 
  {"end" end, "start" start})

(defn-log document-insert-ops [rep-loc cursor content]
  [(document-modify-json rep-loc 
                        (range-json cursor (+ cursor (count content))) 
                        nil
                        (modify-how-json "INSERT" [content] nil))])

(defn-log document-append-ops [rep-loc content]
  [(document-modify-json rep-loc nil nil
                         (modify-how-json "INSERT_AFTER" [content] nil))])

(defn-log document-delete-ops [rep-loc]
  [(document-modify-json rep-loc nil nil
                         (modify-how-json "DELETE" nil nil))])

(defn-log document-delete-append-ops [rep-loc content]
  ; if there is no blip id this must be a one-way replication so just ignor it
  (if (:blip-id rep-loc)
    (concat (document-delete-ops rep-loc) (document-append-ops rep-loc content))
    []))

(defn-log add-annotation-ops [rep-loc name range value]
  [(document-modify-json rep-loc range nil
                         (assoc 
                             (modify-how-json
                              "ANNOTATE" [value] nil) :annotationKey name))])

(defn-log delete-annotation-ops [rep-loc name range]
  [(document-modify-json rep-loc range nil
                         (assoc 
                             (modify-how-json
                              "CLEAR_ANNOTATION" nil nil) :annotationKey name))])

(defn-log add-annotation-norange-ops [rep-loc name value]
  [(document-modify-json rep-loc nil nil
                         (assoc 
                             (modify-how-json
                              "ANNOTATE" [value] nil) :annotationKey name))])

; @TODO  - this is horrible, should talk about why this is here.
(defn-log gadget-state-rep-loc [rep-loc]
  (into {} 
        (map #(vec `(~(.replace (.replace (str (key %)) "-" "") ":" "") ~(val %))) rep-loc)))

(defn-log gadget-op-json [rep-loc gadget-state]
  {"properties" (merge gadget-state (gadget-state-rep-loc rep-loc))
   "type" "GADGET"})

(defn-log append-gadget-ops [rep-loc gadget-state]
  [(document-modify-json rep-loc nil nil
                        (modify-how-json "INSERT_AFTER" nil 
                                         [(gadget-op-json rep-loc gadget-state)]))])

; @TODO - should check if we should re-do the escaping workaround with the " "
(defn-log gadget-submit-delta-ops [rep-loc state]
  [(document-modify-json rep-loc nil 
                        (modify-query-json {"url" (state "url")} 1 "GADGET")
                        (modify-how-json "UPDATE_ELEMENT" nil 
                                         [(gadget-op-json rep-loc (dissoc state "url"))]))])

(defn-log blip-create-child-ops [rep-loc content new-id]
  [(assoc-in-x 
     (op-skeleton (assoc rep-loc :blip-id nil))
     ["method"] "wavelet.appendBlip"
     ["params" "blipData"]
     (assoc (params (assoc rep-loc :blip-id new-id))
       "content" content))])


(defmethod update-rep-loc-ops "gadget" [rep-loc content]
  (log (gadget-submit-delta-ops rep-loc 
				{(:key rep-loc) content
				 "url" "http://wave.thewe.net/gadgets/thewe-ggg/thewe-ggg.xml"})))

(defmethod update-rep-loc-ops "blip" [rep-loc content]
  (log (document-delete-append-ops rep-loc content)))

(defn-log add-string-and-annotate-ops [rep-loc str annotate-until annotation-name]
  (concat
   (document-insert-ops rep-loc 0 str)
   (add-annotation-ops rep-loc annotation-name (range-json 0 annotate-until) "nothing")))

(defn-log add-string-and-eval-ops [rep-loc str]
  (add-string-and-annotate-ops rep-loc 0 str (count str) "we/eval"))


; =============================
; ======= Harness Layer =======
; =============================

;;; Utilities

;;; Helper "API"

(defmacro iterate-events [events listen-to for-args]
  `(let [~'modified-blip-ids
	 (for [~'event (dig ~events "events")
	       :when (not (.endsWith (~'event "modifiedBy") "@a.gwave.com"))
	       :when (= (~'event "type") ~listen-to)]
	   (dig ~'event "properties" "blipId"))]
     (for [~'blip-id ~'modified-blip-ids
	   :let [~'blip-data (dig ~events "blips" ~'blip-id)
		 ~'content (~'blip-data "content")		 
		 ~'blip-annotations (dig ~'blip-data "annotations")		 
		 ~'rep-loc {:type "blip"  :wave-id (~'blip-data "waveId") :wavelet-id (~'blip-data "waveletId") :blip-id (~'blip-data "blipId")}
		 ~'first-gadget-map (first (dig ~'blip-data "elements"))
		 ~'gadget-state (if ~'first-gadget-map (dig (val ~'first-gadget-map) "properties") {})]] 
       (binding [~'*ctx* (we/log {:rep-loc ~'rep-loc :content ~'content :annotations ~'blip-annotations :gadget-state ~'gadget-state})] ~for-args))))



;;; Functions that can be called with an we/eval

(defn-log delete-annotation [annotation]
  (delete-annotation-ops (:rep-loc *ctx*) 
                         (annotation "name")
                         (range-json (dig annotation "range" "start")
                                     (dig annotation "range" "end"))))

(defn-log echo [s]
  (document-insert-ops (:rep-loc *ctx*) (:cursor *ctx*) (str \newline s)))

(defn-log echo-pp [s]
  (echo (pprn-str s)))

(defn-log identify-this-blip []
  (echo-pp (:rep-loc *ctx*)))

(defn-log create-child-blip [] 
  (blip-create-child-ops (:rep-loc *ctx*) "" (str (rand))))

(defn-log modify-ggg [state]
  (gadget-submit-delta-ops (:rep-loc *ctx*) (assoc state "url" "http://wave.thewe.net/gadgets/thewe-ggg/thewe-ggg.xml")))

(defn-log current-rep-class []
  (containing-rep-class (*ctx* :rep-loc)))

(defn-log gadget-rep-class [key]
  (containing-rep-class (assoc (*ctx* :rep-loc)
			   :type "gadget" :key key)))


;;; Clipboard stuff

(defn-log remember-wave! []
  (reset! *other-wave* *ctx*)
  (echo "Remembered"))

(defmacro on-other-wave [expr]
  `(binding [*ctx* @*other-wave*]
     ~expr))

(defn-log remember-gadget-key! [rep-key]
  (reset! *clipboard* 
	  {:source-key rep-key 
	   :rep-loc (:rep-loc *ctx*) 
	   :subkeys (for [[key val] (:gadget-state *ctx*) 
			  :when (or (= key rep-key) (.startsWith key (str rep-key ".")))]
		      [(.replaceFirst key rep-key "") val])})
 ; this never happened
  (reset! *last-clipboard* @*clipboard*)
  (echo "ok!"))

(defn submit-replication-delta [rep-key]
  (we/gadget-submit-delta-ops (:rep-loc we/*ctx*) (into {"url" "http://wave.thewe.net/gadgets/thewe-ggg/thewe-ggg.xml"} (for [[subkey val] (:subkeys @we/*last-clipboard*)] [(str rep-key subkey) val]))))

(defn-log replicate-gadget-key! [rep-key]
  (doseq [:let [{subkeys :subkeys source-key :source-key source-rep-loc :rep-loc} @*clipboard*] [subkey _] subkeys] 
    (replicate-replocs!     
     (assoc source-rep-loc :type "gadget" :key (str source-key subkey))
     (assoc (:rep-loc *ctx*) :type "gadget" :key (str rep-key subkey))))
  (add-string-and-eval-ops (:rep-loc *ctx*) (str `(we/submit-replication-delta ~rep-key))))


;;; Utilities for gadget-initiated replication

(defn-log handle-to-key []
  (if-let [to-key ((:gadget-state *ctx*) "to-key")]
   (when (not= to-key "*")
      (reset! *clipboard* {:rep-loc (:rep-loc *ctx*) :to-key to-key})
      (gadget-submit-delta-ops (:rep-loc *ctx*) {"to-key" "*" "url" ((:gadget-state *ctx*) "url")}))))

(defn-log handle-from-key []
  (if-let [from-key ((:gadget-state *ctx*) "from-key")]  
    (if (not= from-key "*")
      (when-let [{to-key :to-key source-rep-loc :rep-loc} @*clipboard*] ;hyper: to-key: f1._mixins [from-key: ggg._mixins key: ggg._mixins.2345345....]
	(replicate-replocs! (assoc source-rep-loc :type "gadget" :key to-key)
			      (assoc (:rep-loc *ctx*) :type "gadget" :key from-key))
	(gadget-submit-delta-ops (:rep-loc *ctx*) {"from-key" "*" "url" ((:gadget-state *ctx*) "url")})))))



(defn-log handle-rep-keys []
  (if-let [rep-keys ((:gadget-state *ctx*) "blip-rep-keys")]
    (if (not= rep-keys "*")
      (apply concat 
	     (for [rep-key (.split rep-keys ",")]
	       (let [rep-loc (:rep-loc *ctx*) 
		     child-rep-loc (assoc rep-loc :blip-id "new-blip")
		     annotate-str (str "\nThis blip will be replicated to the gadget key " rep-key 
				       ". Anything within this highlighted segment will be ignored during replication.")
		     key-val (dig *ctx* :gadget-state rep-key)]
		 
		 (replicate-replocs! (dissoc (assoc rep-loc :type "blip" :annotation-name "we/rep" :annotation-value rep-key) :blip-id) ;
				     (assoc rep-loc :type "gadget" :key rep-key))
		 (concat
		  
		  (blip-create-child-ops rep-loc "" "new-blip")
					; set an annotation on the whole blip that holds as a value the key we want to replicate to
		  (add-annotation-norange-ops  child-rep-loc "we/rep" rep-key)
					; insert an annotation that should NOT be replicated to let the user know this blip is replicated to a specific gadget key
		  (add-string-and-annotate-ops 
		   child-rep-loc
		   (str annotate-str "\n")
		   (count annotate-str)
		   "we/DNR")
     					; mark previous annotation with another one to make sure the user notices it (a color annotation)
		  (add-annotation-ops child-rep-loc "style/backgroundColor" (range-json 0 (count annotate-str)) "rgb(255, 153, 0)")
					; insert current value to blip (if exists)
		  (if key-val
		    (document-insert-ops child-rep-loc
					 (inc (count annotate-str)) key-val))
					; change the rep-key value to * so we won't repeat this function over and over
		  (gadget-submit-delta-ops rep-loc {"blip-rep-keys" "*" "url" ((:gadget-state *ctx*) "url")}))))))))

(defn-log handle-gadget-rep "TODO" []
  (concat
   (handle-to-key)
   (handle-from-key)))

(defn-log store-mixins 
  "This stores gadget keys called mixins as they contain code we might want to use by name later"
  []
  (if-let [gadget-state (:gadget-state *ctx*)]
    (swap! 
     *mixin-db* 
     into (for [[key val] gadget-state 
		:when (and 
		       (.startsWith key "_mixins.")
		       (.endsWith key "._name"))
		:let [code-key (str-join "." (assoc (vec (.split key "\\.")) 2 "_code"))]]
	    [val {:rep-loc (assoc (:rep-loc *ctx*) :type "gadget" :key code-key) 			      
		  :name-key key 
		  :code (gadget-state code-key) 
		  :code-key code-key}]))))

(defn-log handle-mixin-rep-key
  "This checks whether we got a key called 'mixin-rep-key' which will indicate we want to replicate to the current gadget a mixin we store in *mixin-db*"
  []
  (if-let [gadget-state (:gadget-state *ctx*)]
    (if-let [cmd (gadget-state "mixin-rep-key")]
      (if (not= cmd "*")
	(let [mixin-rep (read-json cmd) 
	      mixin (@*mixin-db* (mixin-rep "mixinName"))
	      target-rep-loc (assoc (:rep-loc *ctx*) :type "gadget" :key (mixin-rep "key"))] 	
	  (replicate-replocs!
	   (:rep-loc mixin) 
	   target-rep-loc)
	  (concat
	   (update-rep-loc-ops target-rep-loc (:code mixin))
	   (update-rep-loc-ops (assoc target-rep-loc :key "mixin-rep-key") "*")))))))


;;; "Subrobots" to be used with thewe-0+...@appspot.com

(defn sfirst [x]
  (if-let [result (first x)]
    result
    []))

(defn-log run-function-do-operations [events-map] ; this is the signature of a function that can be called by adding + to a robot's address
  (sfirst ; this is the solution for now as there is probably no more than one evaluated expression in each event sent to us
   (iterate-events events-map "ANNOTATED_TEXT_CHANGED"     
		   (apply concat 
			  (for [annotation (:annotations *ctx*) 		  
				:when (not= -1 (dig annotation "range" "start"))
				:when (= "we/eval" (annotation "name"))
				:let [start (dig annotation "range" "start") 
				      end (dig annotation "range" "end")]] 
			    (binding [*ctx* (assoc *ctx* :cursor end)]
			      (concat
			       (delete-annotation annotation)
			       (try (eval (read-string (subs (:content *ctx*) start end)))
				    (catch Throwable t 
				      (log-exception t) (echo t))))))))))

(defn-log allow-gadget-replication [events-map]
  (apply concat (iterate-events events-map "BLIP_SUBMITTED" (handle-gadget-rep))))

(defn concat-apply [fns arg]
  (apply concat 
         (map #(% arg) fns)))

(defn-log mother-shit [events-map]
  (concat 
   (first ; this is the solution for now as there is probably no more than one evaluated expression in each event sent to us     
    (iterate-events events-map "BLIP_SUBMITTED" 
		    (do (store-mixins)
			(concat 
			 (handle-gadget-rep) 
			 (handle-rep-keys) 
			 (do-replication @*rep-rules* 
					 (blip-data-to-rep-ops blip-data))
			 (handle-mixin-rep-key)))))))



; ============================
; ======= Server Layer =======
; ============================

(defn answer-wave [events-map]
  (json-str
   (log-info "Operations" (log (wave-attempt
				((ns-resolve 'we
					     (read-string
					      ((read-json (events-map "proxyingFor")) "action"))) 
				 events-map))))))



