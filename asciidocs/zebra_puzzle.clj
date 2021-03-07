(ns zebra-puzzle
  (:require [clojure.math.combinatorics :as combo]
            [clojure.set :refer [difference union]]
            [clojure.test :refer [deftest is]]))

[[:nationality :english :house-color :red]
 [:nationality :spanish :pet :dog]
 [:house-color :green :drink :coffee]
 [:nationality :ukraine :drink :tea]
 [:smokes :old-gold :pet :snails]
 [:house-color :yellow :smokes :kools]
 [:house-position 3 :drink :milk]
 [:nationality :norway :house-position 1]
 [:smokes :luckies :drink :orange-juice]
 [:nationality :japan :smokes :parliaments]]

{:nationality #{:english :spanish :ukrainian :norwegian :japanese}
 :house-number #{1 2 3 4 5}
 :house-color #{:red :green :yellow}
 :pet #{:horse :zebra :dog :snails}
 :smokes #{:old-gold :parliaments :luckies :kools}
 :drinks #{:orange-juice :milk :water :tea :coffee}}

"
The Englishman lives in the red house.
The Spaniard owns the dog.
Coffee is drunk in the green house.
The Ukrainian drinks tea.
The green house is immediately to the right of the ivory house.
The Old Gold smoker owns snails.
Kools are smoked in the yellow house.
Milk is drunk in the middle house.
The Norwegian lives in the first house.
The man who smokes Chesterfields lives in the house next to the man with the fox.
Kools are smoked in the house next to the house where the horse is kept.
The Lucky Strike smoker drinks orange juice.
The Japanese smokes Parliaments.
The Norwegian lives next to the blue house.
"

(def rules [[[:nationality :english] :same-house [:house-color :red]]
            [[:nationality :spanish] :same-house [:pet :dog]]
            [[:drink :coffee] :same-house [:house-color :green]]
            [[:nationality :ukrainian] [:drinks :tea]]
            [[:house-color :green] :right-of [:house-color :ivory]]
            [[:smokes :old-gold] :same-house [:pet :snails]]
            [[:smokes :kools] :same-house [:house-color :yellow]]
            [[:drinks :milk] :same-house [:house-number 3]]
            [[:nationality :norwegian] :same-house [:house-number 1]]
            [[:smokes :chesterfields] :next-to [:pet :fox]]
            [[:smokes :kools] :next-to [:pet :horse]]
            [[:smokes :luckies] :same-house [:drinks :orange-juice]]
            [[:nationality :japanese] :same-house [:smokes :parliaments]]
            [[:nationality :norwegian] :next-to [:house-color :blue]]])

;; possible combinations

(Math/pow 5 6)
;; => 15625.0

(Math/pow 120 5)

(count (combo/permutations [:red :green :yellow :ivory :blue]))


(def house-possibilities [[:english :spanish :ukrainian :norwegian :japanese]
                          [1 2 3 4 5]
                          [:red :green :yellow :ivory :blue]
                          [:horse :zebra :dog :snails :fox]
                          [:old-gold :parliaments :luckies :kools :chesterfields]
                          [:orange-juice :milk :water :tea :coffee]])

(ffirst (map vector (map combo/permutations house-possibilities)))



(count (apply combo/cartesian-product house-possibilities))
(first (apply combo/cartesian-product house-possibilities))
;; => (:english 1 :red :horse :old-gold :orange-juice)

(def positions {:nationality 0
                :house-number 1
                :house-color 2
                :pet 3
                :smokes 4
                :drinks 5})

(defn quality [q house]
  (nth house (q positions)))

(defn right-of [houses [p1 v1] [p2 v2]]
  (some (fn [[h1 h2]] (and (= v1 (quality p1 h1))
                           (= v2 (quality p2 h2))))
        (partition 2 1 (sort-by second houses))))

(defn same-house [houses [p1 v1] [p2 v2]]
  (some #(and (= v1 (quality p1 %)) (= v2 (quality p2 %))) houses))


(defn next-to [houses [p1 v1] [p2 v2]]
  (some (fn [[h1 h2]] (or (and (= v1 (quality p1 h1))
                               (= v2 (quality p2 h2)))
                          (and (= v1 (quality p1 h2))
                               (= v2 (quality p2 h1)))))
        (partition 2 1 (sort-by second houses))))

(defn rule-holds? [houses [[p1 v1] typ [p2 v2]]]
  (case typ
    :same-house (same-house houses [p1 v1] [p2 v2])
    :right-of (right-of houses [p1 v1] [p2 v2])
    :next-to (next-to houses [p1 v1] [p2 v2])
    :default false))

(defn valid-combination [houses rules]
  (every? #(rule-holds? houses %) rules))

(deftest tests
  (is (right-of [[:spanish 2 :ivory :dog :old-gold :orange-juice]
                 [:english 1 :green :horse :old-gold :orange-juice]
                 [:english 3 :yellow :horse :old-gold :orange-juice]]
                [:house-color :green]
                [:house-color :ivory]))

  (is (same-house [[:spanish 2 :ivory :dog :old-gold :orange-juice]
                   [:english 1 :green :horse :old-gold :orange-juice]
                   [:english 3 :yellow :horse :old-gold :orange-juice]]
                  [:nationality :english]
                  [:pet :horse]))


  (is (next-to [[:spanish 2 :ivory :dog :old-gold :orange-juice]
                [:english 1 :green :horse :old-gold :orange-juice]
                [:english 3 :yellow :horse :old-gold :orange-juice]]
               [:house-color :green]
               [:house-color :ivory]))

  (is (not (next-to [[:spanish 3 :ivory :dog :old-gold :orange-juice]
                     [:english 1 :green :horse :old-gold :orange-juice]
                     [:english 2 :yellow :horse :old-gold :orange-juice]]
                    [:house-color :green]
                    [:house-color :ivory])))

  (is (rule-holds? [[:english 1 :red :horse :old-gold :orange-juice]]
                   [[:nationality :english]
                    :same-house
                    [:house-color :red]]))

  (is (valid-combination [[:english 1 :red :horse :old-gold :orange-juice]
                          [:spanish 1 :red :dog :old-gold :orange-juice]]
                         [[[:nationality :english] :same-house [:house-color :red]]
                          [[:nationality :spanish] :same-house [:pet :dog]]]))

  (is (not (valid-combination [[:english 1 :red :horse :old-gold :orange-juice]
                               [:spanish 1 :red :zebra :old-gold :orange-juice]]
                              [[[:nationality :english] :same-house [:house-color :red]]
                               [[:nationality :spanish] :same-house [:pet :dog]]]))))


(let [houses [1 2 3 4 5]
      orderings (combo/permutations houses)]
  (filter true? (for [[red green ivory yellow blue] orderings
                      [english spanish ukrainian norwegian japanese] orderings
                      [horse zebra dog snails fox] orderings
                      [old-gold parliaments luckies kools chesterfields] orderings
                      [orange-juice milk water tea coffee] orderings]
                  (when (= red english)))))