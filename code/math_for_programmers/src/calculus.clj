(ns calculus
  (:require [draw :refer [draw]]))

(defn volume [t]
  (+ (/ (Math/pow (- t 4) 3) 64) 3.3))
;; ((t-4)^3/64)+3.3

(defn flow [t]
  (* 3 (/ (Math/pow (- t 4) 2) 64)))

(defn decreasing-volume [t]
  (if (< t 5) (- 10 (/ (Math/pow t 2) 5))
      (* 0.2 (Math/pow (- 10 t) 2))))

(volume 4) ; 3.3
(volume 9) ; 5.253125

(comment
  (draw [:function volume 0 10]
        [:function decreasing-volume 0 10]))

(defn average-flow-rate [v-fn, t1, t2]
  (/ (- (v-fn t2) (v-fn t1)) (- t2 t1)))

(average-flow-rate volume 4 9) ; 0.390625

(comment
  (draw/function decreasing-volume 0 10 1000 1000))

(average-flow-rate decreasing-volume 0 4) ;; => -0.8

(defn average-flow-line [f x1 x2]
  (fn [x]
    (+ (f x1)
       (/ (* (- x x1)
             (- (f x2) (f x1)))
          (- x2 x1)))))

((average-flow-line volume 4 9) 4)

(comment
  (draw [:function volume 0 10]
        [:function (average-flow-line volume 4 9) 4 9])

  (draw [:function decreasing-volume 0 10]
        [:function (average-flow-line decreasing-volume 0 4) 0 4]))

(comment
  (map #(vector % (average-flow-rate volume % (inc %))) (range 0 10))

;; => ([0 0.578125]
;;     [1 0.296875]
;;     [2 0.109375]
;;     [3 0.015625]
;;     [4 0.015625]
;;     [5 0.109375]
;;     [6 0.296875]
;;     [7 0.578125]
;;     [8 0.953125]
;;     [9 1.421875])

  (draw [:scatter (map #(vector % (average-flow-rate volume % (inc %))) (range 0 10))])
  (draw [:scatter (map #(vector % (average-flow-rate volume % (inc %))) (range 0 10 0.1))]))

(defn ten-to-the [x] (Math/pow 10 x))

(defn instantaneous-flow-rate [f t]
  (->> (range 0 -7 -1)
       (map ten-to-the)
       (map #(average-flow-rate f (- t %) (+ t %)))
       (partition 2 1)
       (remove #(> (Math/abs (apply - %)) (Math/pow 10 -6)))
       first last))

(comment
  (instantaneous-flow-rate volume 1)

;; => 0.42187500015393936

  (draw [:scatter (map #(vector % (instantaneous-flow-rate volume %)) (range 0 10 0.1))]))

(defn flow-rate [f]
  (fn [t] (instantaneous-flow-rate f t)))

(comment
  (draw [:function (flow-rate volume) 0 10]))

(defn estimated-volume-change [flow-fn, t1, t2]
  (/ (* (+ (flow-fn t2) (flow-fn t1)) (- t2 t1)) 2))

(estimated-volume-change (fn [_] 60) 0 1)
(estimated-volume-change (fn [x] (if (zero? x) 50 100)) 0 1)

(defn volume-change [f]
  (fn [t1 t2 step]
    (->> (range t1 t2 step)
         (partition 2 1)
         (map #(apply estimated-volume-change f %))
         (apply +))))

(time ((volume-change flow) 0 10 0.0001))
;; => 4.375000002326983

(- (volume 10) (volume 0))
;; => 4.375

(comment
  (map (juxt identity volume) (range 0 10))
  ;; => ([0 2.3] [1 2.878125] [2 3.175] [3 3.284375] [4 3.3] [5 3.315625] [6 3.425] [7 3.721875] [8 4.3] [9 5.253125])

  (reductions + 0 (map #((volume-change flow) %1 %2 0.0001) (range 0 10) (drop 1 (range 0 10))))

  (draw [:function volume 0 10]
        [:scatter (map vector (range 0 10 0.1)
                       (reductions + 0 (map #((volume-change flow) %1 %2 0.0001) (range 0 11 0.1) (drop 1 (range 0 11 0.1)))))]))

(defn volume-from-flow [f c]
  (fn [t]
    (reduce + c (map #((volume-change f) %1 %2 0.0001)
                     (range 0 t 0.1)
                     (drop 1 (range 0 t 0.1))))))

(comment
  ((volume-from-flow flow 2.3) 10)
;; => 6.674812911913465
  (volume 10)

;; => 6.675
  (draw [:function volume 0 10]
        [:scatter (map (juxt identity (volume-from-flow flow 0)) (range 0 11 0.5))]))