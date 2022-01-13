(ns draw
  (:require [clojure2d.core :as c2d]))

(def colors [:red :blue :orange])

(defn- draw-axis-lines [canvas {:keys [h-pixels v-pixels xform x-min x-max y-min y-max]}]
  (c2d/with-canvas-> canvas
    (c2d/set-font-attributes 20)
    (c2d/set-color 0 0 0)
    (c2d/set-stroke 2.0)
    (c2d/line (int (* 0.1 h-pixels)) (int (* 0.9 v-pixels))
              (int (* 0.9 h-pixels)) (int (* 0.9 v-pixels)))
    (c2d/line (int (* 0.1 h-pixels)) (int (* 0.1 v-pixels))
              (int (* 0.1 h-pixels)) (int (* 0.9 v-pixels)))
    (c2d/text (int y-min) (- (first (xform [x-min 0])) (* 0.05 h-pixels)) (second (xform [0 y-min])))
    (c2d/text (int y-max) (- (first (xform [x-min 0])) (* 0.05 h-pixels)) (second (xform [0 y-max])))
    (c2d/text (int x-min) (first (xform [x-min 0])) (+ (* 0.05 v-pixels) (second (xform [0 y-min]))))
    (c2d/text (int x-max) (first (xform [x-max 0])) (+ (* 0.05 v-pixels) (second (xform [0 y-min]))))))

(defn- canvas-properties [points h-pixels v-pixels]
  (let [[xs ys] (apply map vector points)
        x-min (apply min xs) x-max (apply max xs)
        y-min (apply min ys) y-max (apply max ys)
        x-start (* 0.1 h-pixels) x-end (* 0.9 h-pixels)
        x-factor (/ (- x-end x-start) (- x-max x-min))
        y-start (* 0.1 v-pixels) y-end (* 0.9 v-pixels)
        y-factor (/ (- y-end y-start) (- y-max y-min))]
    {:x-max x-max
     :x-min x-min
     :y-max y-max
     :y-min y-min
     :h-pixels h-pixels
     :v-pixels v-pixels
     :xform (fn [[x y]]
              [(int (+ x-start (* x-factor (- x x-min))))
               (int (- v-pixels (+ y-start (* y-factor (- y y-min)))))])}))

(defn- draw-points [canvas points canvas-props color]
  (c2d/with-canvas [c canvas]
    (c2d/set-color c color)
    (doseq [[x y] (map (:xform canvas-props) points)]
      (c2d/rect c x y 5 5))))

(defn- function->points [[_type function start end]]
  (map (juxt identity function) (range start end (/ (- end start) 1000))))

(defn- graph->points [graph]
  (case (first graph)
    :scatter (second graph)
    :function (function->points graph)))

(defn- scatter [graph-points x-pix y-pix]
  (let [cp (canvas-properties (apply concat graph-points) x-pix y-pix)]
    (let [canvas (c2d/canvas x-pix y-pix)]
      (draw-axis-lines canvas cp)
      (doseq [[points color] (map vector graph-points colors)]
        (draw-points canvas points cp color))
      (c2d/show-window canvas "Graph"))))

(defn- volume [t]
  (+ (/ (Math/pow (- t 4) 3) 64) 3.3))

(defn- decreasing-volume [t]
  (if (< t 5) (- 10 (/ (Math/pow t 2) 5))
      (* 0.2 (Math/pow (- 10 t) 2))))

(defn draw [& graphs]
  (scatter (map graph->points graphs) 1000 1000))

(comment
  (draw [:function volume 0 10]
        [:function decreasing-volume 0 10]))