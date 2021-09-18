(ns subway
  (:require [clojure.test :refer [deftest is]]))

(def boston {:blue [:bowdain :government :state :aquarium :maverick :airport :suffolk :revere :wonderland]
             :orange [:oakgrove :sullivan :haymarket :state :downtown :chinatown :tufts :backbay :foresthills]
             :green [:lechmore :science :north :haymarket :government :park :caplet :kenmore :newton :riverside]
             :red [:alewife :davis :porter :harvard :central :mit :charles :park :downtown :south :umas :mattopan]})

"concepts
 
 Line
 station
 journey
 
 
State is just a station and the goal. A path is 'blue bowdian' - taking the blue train to bowdain
"

(defn successors [subway]
  (fn [[station goal]]
    (set (apply concat
                (for [[line-name line] subway]
                  (keep (fn [[a b]]
                          (cond (= a station) [(str (name line-name) " to " (name b)) [b goal]]
                                (= b station) [(str (name line-name) " to " (name a)) [a goal]]
                                :else nil))
                        (partition 2 1 line)))))))

(defn success [[station goal]]
  (= station goal))

(deftest t
  (is (= ((successors boston) [:government :downtown])
         #{["green to haymarket" [:haymarket :downtown]] ["blue to state" [:state :downtown]]
           ["green to park" [:park :downtown]] ["blue to bowdain" [:bowdain :downtown]]})))


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

(solve [:wonderland :mattopan] (successors boston) success)