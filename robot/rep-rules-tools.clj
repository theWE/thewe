(ns we)

(def *rep-rules-save* @*rep-rules*)

(defn filter-elements-from-partition [partition pred]
  (into #{} 
	 (for [rep-class partition] 
	   (into #{} 
		 (for [el rep-class 
		       :when (pred el)] 
		   el)))))

(defn filter-rep-locs-in-rep-rules! [pred]
  (swap! *rep-rules* 
	 filter-elements-from-partition pred))

(filter-rep-locs-in-rep-rules! #(not= (:wave-id %)
				      "googlewave.com!w+x3-iIo83E"))



(defn rep-rules-with-rep-locs [pred]
  (into #{} 
	 (for [rep-class @*rep-rules* 
	       :when (some pred rep-class)] rep-class))) 

(defn replace-element-in-partition [partition el new-el]
  (into #{} (for [class partition] (into #{} (for [e class] (if (= e el) new-el e))))))

(defn replace-rep-loc-in-rep-rules! [rep-loc new-rep-loc]
  (swap! *rep-rules* replace-element-in-partition rep-loc new-rep-loc))

(def *bkup-rep-rules* @*rep-rules*)
(def @*rep-rules* *bkup-rep-rules*)

(filter-rep-locs-in-rep-rules!  #(= (:wave-id %) "googlewave.com!w+x3-iIo83A"))