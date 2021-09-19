(ns more-pour
  (:require [clojure.math.combinatorics :as combo]
            [clojure.test :refer [deftest is]]))

(defn transfer [[caps curr goal]]
  (map (fn [[from to]]
         [(str "from " from " to " to)
          [caps (-> curr
                    (assoc from (- (nth curr from) (min (nth curr from) (- (nth caps to) (nth curr to)))))
                    (assoc to (+ (nth curr to) (min (nth curr from) (- (nth caps to) (nth curr to))))))
           goal]])
       (combo/permuted-combinations (range (count caps)) 2)))

(let [[caps curr goal] [[9 4] [5 3] 6]]
  (concat (transfer [caps curr goal])
          (mapcat (fn [idx]
                    [[(str "empty " idx) [caps (assoc curr idx 0) goal]]
                     [(str "fill " idx) [caps (assoc curr idx (nth caps idx)) goal]]])
                  (range (count caps)))))

(defn successors [[caps curr goal]]
  (concat
   (transfer [caps curr goal])
   (mapcat (fn [idx]
             [[(str "empty " idx) [caps (assoc curr idx 0) goal]]
              [(str "fill " idx) [caps (assoc curr idx (nth caps idx)) goal]]])
           (range (count caps)))))

(defn success [[_ curr goal]]
  (some #{goal} curr))

(defn next-paths [successor-fn path]
  (map #(concat (butlast path) %) (successor-fn (last path))))

(defn solve
  ([init-state successor-fn success-fn] (solve [[init-state]] #{} successor-fn success-fn))
  ([frontier explored successor-fn success-fn]
   (let [path (first frontier)
         state (last path)]
     (cond (success-fn state) path
           (explored state) (recur (rest frontier) explored successor-fn success-fn)
           :else (recur (concat frontier (next-paths successor-fn path))
                        (conj explored state)
                        successor-fn success-fn)))))

(defn more-pour-solve [capacities goal]
  (solve [capacities (vec (repeat (count capacities) 0)) goal] successors success))

(more-pour-solve [9 4] 6)



(deftest t
  (is (= '("fill 0" "from 0 to 1" "empty 1" "from 0 to 1" "empty 1" "from 0 to 1" "fill 0" "from 0 to 1" [[9 4] [6 4] 6])
         (more-pour-solve [9 4] 6)))
  (is (= '("fill 3" "from 3 to 0" "from 3 to 1" [[1 2 4 8] [1 2 0 5] 5])
         (more-pour-solve [1 2 4 8] 5))))
