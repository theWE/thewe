(ns we)

(def *rep-rules-save* @*rep-rules*)

(defn filter-rep-locs-in-rep-rules! [pred]
  (swap! *rep-rules* #(into #{} 
			    (for [rep-class %] (into #{} 
						     (for [rep-loc rep-class 
							   :when (pred rep-loc)] 
						       rep-loc))))))

(filter-rep-locs-in-rep-rules! #(not (and (= (:blip-id %)
					 "b+GG4sdys7A")
				     (or (.contains (:key %)
						     "_prototype")
					 (.contains (:key %)
						    
						    "_defaultView"))
				   )))