(ns pouring
  (:require [clojure.test :refer [deftest is]]))

(defn successors [X Y x y]
  [[:fill-x [X y]]
   [:fill-y [x Y]]
   [:empty-x [0 y]]
   [:empty-y [x 0]]
   [:trf-x-y [(- x (min x (- Y y))) (+ y (min x (- Y y)))]]
   [:try-y-x [(+ x (min y (- X x))) (- y (min y (- X x)))]]])

(defn pour-problem
  ([X Y goal] (pour-problem X Y goal #{} [[[0 0]]]))
  ([X Y goal explored frontier]
   (let [path (first frontier) ;; path is a series of actions, and the state: :fill-x, :fill-y, :empty-x [3 4]
         [x y] (last path)]
     (cond ((hash-set x y) goal) path
           (empty? frontier) :fail
           (explored [x y]) (recur X Y goal explored (rest frontier))
           :else (recur X Y goal (conj explored [x y])
                        (concat (rest frontier)
                                (map #(apply conj (vec (butlast path)) %) (successors X Y x y))))))))

(deftest t
  (is (= [:fill-x :trf-x-y :empty-y :trf-x-y :empty-y :trf-x-y :fill-x :trf-x-y [6 4]]
         (pour-problem 9 4 6))))


(defn successors2 [[X Y x y goal]]
  [[:fill-x [X Y X y goal]]
   [:fill-y [X Y x Y goal]]
   [:empty-x [X Y 0 y goal]]
   [:empty-y [X Y x 0 goal]]
   [:trf-x-y [X Y (- x (min x (- Y y))) (+ y (min x (- Y y))) goal]]
   [:try-y-x [X Y (+ x (min y (- X x))) (- y (min y (- X x))) goal]]])

(defn success [[_ _ x y goal]]
  ((hash-set x y) goal))

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
(comment
  (solve [9 4 0 0 6] successors2 success))