(ns zebra-puzzle2
  (:require [clojure.math.combinatorics :as combo]))

(defn imright [x y]
  (= (inc y) x))

(defn next-to [x y]
  (= 1 (Math/abs (- x y))))

(time (let [houses [1 2 3 4 5]
            orderings (combo/permutations houses)]
        (for [[red green ivory yellow blue] orderings
              :when (imright green ivory)
              [Englishman Spaniard Ukrainian Japanese Norwegian] orderings
              :when (= Englishman red)
              :when (= 1 Norwegian)
              :when (next-to Norwegian blue)
              [coffee tea milk oj water] orderings
              :when (= green coffee)
              :when (= Ukrainian tea)
              :when (= milk 3)
              [old-gold kools chesterfields luckies parliaments] orderings
              :when (= kools yellow)
              :when (= luckies oj)
              :when (= Japanese parliaments)
              [dog snails horse zebra fox] orderings
              :when (= Spaniard dog)
              :when (= old-gold snails)
              :when (next-to chesterfields fox)
              :when (next-to horse kools)]
          [water zebra])))
(+ 999 999)