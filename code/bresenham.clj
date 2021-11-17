(ns bresenham)

;; https://www.geeksforgeeks.org/bresenhams-circle-drawing-algorithm/
;; 1. Divide the circle into octants of 45 degrees
;; 2. Draw a pixel in each of the octants:
;;    [y,x] [x,y] [x,-y] etc. for every combo of x,y,-x,y
;; 3. Calculate the next pixel from the previous one
;;    from x,y you can either go to x+1,y, (east) or x+1, y-1 (south east)
;;    the decision is based on the 'decision parameter' d.
;;    if d>0, then go south east. Else, go 

(defn point-add [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn octal-points [[x y]]
  (set (let [y- (- y) x- (- x)]
         [[x,y], [x y-] [x- y] [x- y-]
          [y x], [y- x] [y x-] [y- x-]])))

(defn next-point [[x y] d]
  (if (> d 0)
    [(inc x) (dec y)]
    [(inc x) y]))

(defn d-make [a]
  (fn [d x y]
    (if (< d 0) (+ d (* a (inc x)) a)
        (+ d (* a (- (inc x) y)) a))))

(defn bresenham-circle* [[x y] d points d-fn]
  (if (>= x y)
    (conj points [x y])
    (recur (next-point [x y] d)
           (d-fn d x y)
           (conj points [x y])
           d-fn)))

(defn bresenham-circle [[x-center y-center] rad d-fn]
  (->> (bresenham-circle* [0 rad] (- 2 (* 2 rad)) [] d-fn)
       (mapcat octal-points)
       (map #(point-add [x-center y-center] %))
       (sort)))

(defn draw-points [points]
  (let [[xs ys] (apply map vector points)
        x-max (apply max xs) x-min (apply min xs)
        y-max (apply max ys) y-min (apply min ys)
        grid (vec (repeat (- (inc y-max) y-min) (vec (repeat (- (inc x-max) x-min) "."))))]
    (doall (map println
                (reduce (fn [g [x y]]
                          (assoc-in g [(- y y-min) (- x x-min)] "#"))
                        grid
                        points)))
    points))
(comment
  (draw-points (bresenham-circle [5 5] 4 (d-make 7)))
  (draw-points (bresenham-circle [5 5] 5 (d-make 7))))

(defn bresenham-line [[x-start y-start] [x-end y-end]]
  (let [dx (Math/abs (- x-end x-start)) dy (- (Math/abs (- y-end y-start)))
        x-move (if (< x-start x-end) inc dec)
        y-move (if (< y-start y-end) inc dec)]
    (loop [e (+ dx dy) x' x-start y' y-start points [] i 0]
      (if (and (= x' x-end) (= y' y-end))  (conj points [x' y'])
          (let [x-change (>= (* 2 e) dy)  y-change (<= (* 2 e) dx)]
            (recur (cond-> e x-change (+ dy) y-change (+ dx))
                   (cond-> x' x-change x-move) (cond-> y' y-change y-move)
                   (conj points [x' y']) (inc i)))))))

(comment
  (draw-points (bresenham-line [0 0] [7 5]))
  (draw-points (bresenham-line [6 3] [3 -5])))

(defn flip-x [[x y]] [(- x) y])
(defn flip-y [[x y]] [x (- y)])
(defn line [slope] (fn [x] (Math/round (* slope x))))

(defn coord- [[x1 y1] [x2 y2]]
  [(- x1 x2) (- y1 y2)])

(defn coord+ [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn b-line
  ([p1 p2] (map #(coord+ p1 %) (b-line (coord- p2 p1))))
  ([[xf yf]]
   (let [flip-x? (< xf 0) flip-y? (< yf 0)
         xf (if flip-x? (- xf) xf)
         yf (if flip-y? (- yf) yf)
         slope (float (/ yf xf))]
     (cond->> (if (<= slope 1)
                (map (juxt identity (line slope)) (range (inc xf)))
                (map (juxt (line (/ 1 slope)) identity) (range (inc yf))))
       flip-x? (map flip-x)
       flip-y? (map flip-y)))))

(draw-points (b-line [10 4])) ;; oct1
(draw-points (b-line [4 10])) ;; oct2
(draw-points (b-line [10 -4])) ;; oct3
(draw-points (b-line [4 -10])) ;; oct4

(draw-points (b-line [-10 4])) ;; oct5  
(draw-points (b-line [-4 10])) ;; oct6  
(draw-points (b-line [-10 -4])) ;; oct 7
(draw-points (b-line [-4 -10])) ;; oct 8

(draw-points (b-line [-5 -5] [15 -12]))