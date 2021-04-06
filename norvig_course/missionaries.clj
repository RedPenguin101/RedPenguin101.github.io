(ns missionaries
  (:require [clojure.test :refer [deftest is]]
            [clojure.string :as str]
            [clojure.math.combinatorics :as combo]))

"Concepts
 river
 boat
 3 missionaries
 3 cannibals
 <= 2 in the boat
 If we leave more cannibals than missionaries on one side, the cannibals will eat the cannibals"

"state representation:
 [left-missionaries, left-cannibals, left-boats, right-missionaries, right-cannibals, right-boats]"

(defn valid-state? [[lm lc _lb rm rc _rb :as state]]
  (not (or (> lc lm 0)
           (> rc rm 0)
           (some #(< % 0) state))))

(valid-state? [1 2 0 0 0 0])

(defn successors [[lm lc lb rm rc rb]]
  (let [mvrs (for [a [0 1 2] b [0 1 2]
                   :when (<= 1 (+ a b) 2)]
               [a b])]
    (->> (mapcat (fn [[m c]]
                   [[(str/join " " [m "m" c "c" "->"]) [(- lm m) (- lc c) (- lb 1) (+ rm m) (+ rc c) (+ rb 1)]]
                    [(str/join " " [m "m" c "c" "<-"]) [(+ lm m) (+ lc c) (+ lb 1) (- rm m) (- rc c) (- rb 1)]]])
                 mvrs)
         (filter (comp valid-state? second))
         set)))

(defn success [[lm lc]]
  (= 0 lm lc))

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

(deftest t
  (is (= #{["1 m 0 c ->" [0 0 0 1 0 1]]}
         (successors [1 0 1 0 0 0])))
  (is (= #{["1 m 1 c ->" [0 0 0 1 1 1]]
           ["1 m 0 c ->" [0 1 0 1 0 1]]
           ["0 m 1 c ->" [1 0 0 0 1 1]]}
         (successors [1 1 1 0 0 0])))
  (is (= #{["1 m 0 c <-" [2 1 2 0 1 0]]
           ["1 m 1 c ->" [0 0 0 2 2 2]]
           ["1 m 0 c ->" [0 1 0 2 1 2]]
           ["1 m 1 c <-" [2 2 2 0 0 0]]}
         (successors [1 1 1 1 1 1])))
  (is (=  (solve [3 3 1 0 0 0] successors success)
          '("1 m 1 c ->"
            "1 m 0 c <-"
            "0 m 2 c ->"
            "0 m 1 c <-"
            "2 m 0 c ->"
            "1 m 1 c <-"
            "2 m 0 c ->"
            "0 m 1 c <-"
            "0 m 2 c ->"
            "0 m 1 c <-"
            "0 m 2 c ->"
            [0 0 0 3 3 1]))))
