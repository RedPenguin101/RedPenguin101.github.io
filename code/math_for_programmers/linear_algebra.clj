(ns linear-algebra)

(def dino-vectors [[6 4] [3 1] [1 2] [-1 5] [-2 5] [-3 4] [ -4 4]])

(defn degree->radian [a] (/ (* a Math/PI) 180))
(defn radian->degree [a] (/ (* a 180) Math/PI))

(defn v+ [& vs] (apply map + vs))
(defn v* [s v]  (map #(* s %) v))

(defn square [x] (* x x))

(defn vector-length [v]
  (Math/sqrt (apply + (map square v))))

(Math/sqrt (apply + (map square [3 4 12])))

(defn polar->cartesian [[r a]]
  [(* r (Math/cos a)) (* r (Math/sin a))])

(defn cartesian->polar [[x y]]
  [(vector-length [x y]) (Math/atan2 y x)])

(cartesian->polar [4 3])

(defn v-rotate [a v]
  (-> v cartesian->polar (v+ [0 a]) polar->cartesian))

(defn xform-vectors [xform]
  (fn [vs x] (map #(xform x %) vs)))

(def translate (xform-vectors v+))
(def rescale   (xform-vectors v*))
(def rotate    (xform-vectors v-rotate))

(-> dino-vectors
    (rotate (* 5 (/ Math/PI 3)))
    (translate [8 8]))

;; 3 dimensions

(v+ [1 2 3] [4 5 6]) ; (5 7 9)
(v* 10 [1 2 3]) ; (10 20 30)

(defn dot-product [v1 v2]
   (apply + (map * v1 v2)))

(dot-product [3 0 3] [1 2 -1])
(dot-product [1 0] [0 2])

(defn angle [v1 v2]
  (Math/acos
    (/ (dot-product v1 v2)
       (* (vector-length v1) (vector-length v2)))))

(angle [1 2 2] [2 2 1]) ; 0.47588224966041665
(radian->degree (angle [1 2 2] [2 2 1])) ; 27.266044450732828
