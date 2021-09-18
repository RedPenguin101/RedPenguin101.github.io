(ns bridge
  (:require [clojure.math.combinatorics :as combo]
            [clojure.set :refer [difference union]]
            [clojure.test :refer [deftest is]]))

(defn successors [[here there light-on-left t]]
  (if light-on-left
    (for [movers (set (mapcat #(combo/combinations (vec here) %) [1 2]))]
      [(apply str (vec movers) "->")
       [(difference here movers)
        (set (union there movers))
        false
        (+ t (apply max movers))]])
    (for [movers (set (mapcat #(combo/combinations (vec there) %) [1 2]))]
      [(apply str (vec movers) "<-")
       [(set (union here movers))
        (difference there movers)
        true
        (+ t (apply max movers))]])))

(defn next-paths [path]
  (map #(concat (butlast path) %) (successors (last path))))

(defn bridge-problem
  ([people] (bridge-problem #{} [[[people #{} true 0]]] 0))
  ([explored frontier it]
   (let [path (first frontier)
         [here :as state] (last path)]
     (cond (> it 1000) :fail
           (empty? here) path
           (explored (butlast state)) (recur explored (rest frontier) (inc it))
           :else (recur (conj explored (butlast state))
                        (sort-by #(last (last %)) (concat (rest frontier) (next-paths path)))
                        (inc it))))))

(bridge-problem #{1 2 5 10})
(bridge-problem #{1 1000})


(deftest t
  (is (= #{["[2]->" [#{1 5 10} #{2} false 2]]
           ["[1 5]->" [#{2 10} #{1 5} false 5]]
           ["[10]->" [#{1 2 5} #{10} false 10]]
           ["[5 10]->" [#{1 2} #{5 10} false 10]]
           ["[2 5]->" [#{1 10} #{2 5} false 5]]
           ["[1]->" [#{2 5 10} #{1} false 1]]
           ["[1 10]->" [#{2 5} #{1 10} false 10]]
           ["[2 10]->" [#{1 5} #{2 10} false 10]]
           ["[5]->" [#{1 2 10} #{5} false 5]]
           ["[1 2]->" [#{5 10} #{1 2} false 2]]}
         (set (successors [#{1 2 5 10} #{} true 0]))))
  (is (= #{["[5 10]<-" [#{5 10} #{1 2} true 10]]
           ["[10]<-" [#{10} #{1 2 5} true 10]]
           ["[5]<-" [#{5} #{1 2 10} true 5]]
           ["[2]<-" [#{2} #{1 5 10} true 2]]
           ["[1 5]<-" [#{1 5} #{2 10} true 5]]
           ["[1 10]<-" [#{1 10} #{2 5} true 10]]
           ["[1 2]<-" [#{1 2} #{5 10} true 2]]
           ["[2 5]<-" [#{2 5} #{1 10} true 5]]
           ["[1]<-" [#{1} #{2 5 10} true 1]]
           ["[2 10]<-" [#{2 10} #{1 5} true 10]]}
         (set (successors [#{} #{1 2 5 10} false 0]))))
  (is (= #{'("a" "b" "c" "[5 10]->" [#{1 2} #{5 10} false 10])
           '("a" "b" "c" "[1 10]->" [#{2 5} #{1 10} false 10])
           '("a" "b" "c" "[1 5]->" [#{2 10} #{1 5} false 5])
           '("a" "b" "c" "[1]->" [#{2 5 10} #{1} false 1])
           '("a" "b" "c" "[2]->" [#{1 5 10} #{2} false 2])
           '("a" "b" "c" "[5]->" [#{1 2 10} #{5} false 5])
           '("a" "b" "c" "[2 5]->" [#{1 10} #{2 5} false 5])
           '("a" "b" "c" "[10]->" [#{1 2 5} #{10} false 10])
           '("a" "b" "c" "[2 10]->" [#{1 5} #{2 10} false 10])
           '("a" "b" "c" "[1 2]->" [#{5 10} #{1 2} false 2])}
         (let [state [#{1 2 5 10} #{} true 0]
               path ["a" "b" "c" state]]
           (set (next-paths path)))))
  (is (= '("[1 2]->" "[1]<-" "[5 10]->" "[2]<-" "[1 2]->" [#{} #{1 2 5 10} false 17])
         (bridge-problem #{} [[[#{1 5 10 2} #{} true 0]]] 0)
         (bridge-problem #{1 5 10 2}))))
