(ns linear-algebra
  (:require [clojure2d.core :as c2d]
            [clojure2d.color :as col]))

(def dino-vectors [[6 4] [3 1] [1 2] [-1 5] [-2 5] [-3 4] [ -4 4]])

(defn degree->radian [a] (/ (* a Math/PI) 180))
(defn radian->degree [a] (/ (* a 180) Math/PI))

(defn v+ [& vs] (apply map + vs))
(defn v* [s v]  (map #(* s %) v))
(defn v- [v1 v2] (v+ v1 (v* -1 v2)))

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

(defn normalized-dot [v1 v2]
  (/ (dot-product v1 v2) (* (vector-length v1) (vector-length v2))))

(dot-product [3 0 3] [1 2 -1])
(dot-product [1 0] [0 2])

(defn angle [v1 v2]
  (Math/acos
    (/ (dot-product v1 v2)
       (* (vector-length v1) (vector-length v2)))))

(angle [1 2 2] [2 2 1]) ; 0.47588224966041665
(radian->degree (angle [1 2 2] [2 2 1])) ; 27.266044450732828

(defn cross-product [[ux uy uz] [vx vy vz]]
  [(- (* uy vz) (* uz vy))
   (- (* uz vx) (* ux vz))
   (- (* ux vy) (* uy vx))])

(defn unit [v] (v* (/ 1 (vector-length v)) v))

(defn normal [[v1 v2 v3]]
  (cross-product (v- v2 v1) (v- v3 v1)))

(defn component [v dir]
  (/ (dot-product v dir) (vector-length dir)))

(defn project-2d [v, v-up, v-right]
  [(component v v-up) (component v v-right)])

(defn project-face [face v-up v-right v-light]
  (when (> (last (unit (normal face))) 0)
    {:vertices    (map #(project-2d % v-up v-right) face)
     :light-align (max 0 (normalized-dot (normal face) v-light))}))

(def octahedron [[[ 1 0 0]  [0,1 ,0] [0,0,1]]
                 [[ 1 0 0]  [0,0,-1] [0,1,0]]
                 [[ 1 0 0]  [0,0 ,1] [0,-1,0]]
                 [[ 1 0 0]  [0,-1,0] [0,0,-1]]
                 [[-1 0 0]  [0,0,1] [0,1,0]]
                 [[-1 0 0]  [0,1,0] [0,0,-1]]
                 [[-1 0 0]  [0,-1,0] [0,0,1]]
                 [[-1 0 0]  [0,0,-1] [0,-1,0]]])

;; light-align in shapes is value between -1 and 1. 0 means there is no
;; light falling on the shape. 1 means it's directly facing the light source
;; Strategy here is to convert the light-align to alpha,
;; with 1 being 255 and 0 being 0
(defn color [light-align]
  (col/color :blue (+ 20 (* (- 1 light-align) 235))))

(defn s [p] (+ 500 (* 500 p)))
(defn flip-y [v] (update v 1 * -1))

(defn draw-triangle [canvas {:keys [vertices light-align]}]
  (c2d/with-canvas [c canvas]
    (c2d/set-color c (color light-align))
    (let [[[x1 y1] [x2 y2] [x3 y3]] (map flip-y vertices)]
      (c2d/triangle c (s x1) (s y1) (s x2)
                    (s y2) (s x3) (s y3)))))

(defn render-triangles [triangles]
  (let [canvas (c2d/canvas 1000 1000)]
    (doseq [t triangles] (draw-triangle canvas t))
    (c2d/show-window canvas "Drawing")))

(comment
  (render-triangles (keep #(project-face % [1 0 0] [0 1 0] [1 2 3]) octahedron))
  (render-triangles (keep #(project-face % [1 0 0] [0 1 0] [3 3 3]) octahedron))
  (render-triangles (keep #(project-face % [1 0 0] [0 1 0] [0 0 1]) octahedron))
  (render-triangles (keep #(project-face % [1 0 0] [0 1 0] [0 0 -1]) octahedron))
  (render-triangles (keep #(project-face % [0 1 0] [1 0 0] [1 2 3]) octahedron))
  (render-triangles (keep #(project-face % [0.5 0.5 0] [0 0.5 0.5] [1 2 3]) octahedron)))

