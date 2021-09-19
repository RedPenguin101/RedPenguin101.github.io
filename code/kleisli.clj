(ns kleisli (:require [clojure.string :as str]))

(defn >=> [m1 m2]
  (fn [x]
    (let [[y s1] (m1 x)
          [z s2] (m2 y)]
      [z (str s1 s2)])))

(defn upCase [string] [(str/upper-case string) "upCase "])

(defn toWords [string] [(str/split string #" ") "toWords "])

(def process (>=> upCase toWords))

(process "hello world")
;; => [["HELLO" "WORLD"] "upCase toWords "]

;; option monad

(defn option-compose [o1 o2]
  (fn [x]
    (if-let [y (o1 x)]
      (o2 y)
      nil)))

(defn safe-reciprocal [x] (when (not (zero? x)) (/ 1 x)))
(safe-reciprocal 5) ;; => 1/5
(safe-reciprocal 0) ;; => nil

(defn safe-root [x] (when (not (neg? x)) (Math/sqrt x)))
(safe-root 5) ;; => 2.23606797749979
(safe-root -3) ;; => nil

(def safe-root-reciprocal (option-compose safe-reciprocal safe-root))
(safe-root-reciprocal 5) ;; => 0.4472135954999579
(safe-root-reciprocal 0) ;; => nil
(safe-root-reciprocal -4) ;; => nil
