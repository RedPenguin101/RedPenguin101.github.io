(ns scratch)

(def symmetrize identity)

(defn hit
  [asym-body-parts]
  (let [sym-parts (symmetrize asym-body-parts)
        body-part-size-sum (reduce + (map :size sym-parts))
        target (rand body-part-size-sum)]
    (loop [[part & remaining] sym-parts
           accumulated-size (:size part)]
      (if (> accumulated-size target)
        part
        (recur remaining (+ accumulated-size (:size (first remaining))))))))

(defn hit4 [monster]
  (rand-nth (mapcat (fn [part] (repeat (:size part) part)) monster)))

(let [parts [{:name :head :size 100} {:name :left-leg :size 150}
             {:name :right-leg :size 250} {:name :torso :size 300}]]
  (hit4 (symmetrize parts)))






