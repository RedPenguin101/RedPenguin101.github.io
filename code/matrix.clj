(ns matrix)

(defn matrix [[m n] elements]
  (if (not= (* m n) (count elements))
    (throw (ex-info "Incorrect number of elements" {}))
    [[m n] elements]))

(defn elements [m] (second m))
(defn dims [m] (first m))
(defn m-dim [m] (ffirst m)) ;; number of rows
(defn n-dim [m] (second (first m))) ;; number of columns
(defn rows [m] (vec (partition (n-dim m) (elements m))))
(defn columns [m] (apply map vector (rows m)))

(defn m+ [m1 m2]
  (if (not= (dims m1) (dims m2))
    (throw (ex-info "Dimensions do not match" {}))
    (matrix (dims m1) (apply + (elements m1) (elements m2)))))

(defn dot-product [v1 v2] (apply + (map * v1 v2)))

(defn m* [m1 m2]
  (if (not= (n-dim m1) (m-dim m2))
    (throw (ex-info "Neighbouring dimensions do not match" {}))
    (matrix [(m-dim m1) (n-dim m2)]
            (for [row (rows m1) col (columns m2)]
              (dot-product row col)))))

(comment
  (m* (matrix [2 3] [1 2 3 3 2 1]) (matrix [3 2] [0 2 1 -1 0 1]))
  ;; => [[2 2] (2 3 2 5)]

  (m* (matrix [3 2] [0 2 1 -1 0 1]) (matrix [2 3] [1 2 3 3 2 1]))
  ;; => [[3 3] (6 4 2 -2 0 2 3 2 1)]
  )

(defn id-matrix [n]
  (matrix [n n] (mapcat #(assoc (vec (repeat n 0)) % 1) (range n))))

(defn transpose [m]
  (matrix (reverse (dims m)) (apply concat (columns m))))

(rows (matrix [2 3] [1 2 3 3 2 1]))
;; => ((1 2 3) 
;;     (3 2 1))

(rows (transpose (matrix [2 3] [1 2 3 3 2 1])))
;; => ((1 3) 
;;     (2 2) 
;;     (3 1))


(defn vector-scalar* [vector scalar]
  (map * vector (repeat scalar)))

(defn scalar* [s m]
  (matrix (dims m) (map #(* s %) (elements m))))

(comment
  (m* (matrix [2 4] [1 0 8 -4 0 1 2 12])
      (matrix [4 1] [42 8 0 0]))
  ;; => [[2 1] (42 8)]
  )

(defn swap-rows [m r1 r2]
  (matrix (dims m) (apply concat (let [rs (rows m)] (-> rs (assoc r1 (rs r2)) (assoc r2 (rs r1)))))))

(defn row-mult [m r s]
  (matrix (dims m) (apply concat (update (rows m) r vector-scalar* s))))

(defn row-addition [m change-row scalar with-row]
  (let [r (vector-scalar* ((rows m) with-row) scalar)]
    (matrix (dims m) (apply concat (update (rows m) change-row (partial map +) r)))))

(comment
  (row-addition (matrix [4 6] [1 -2 1 -1 1 0
                               4 -8 3 -3 1 2
                               -2 4 -2 -1 4 -3
                               1 -2 0 -3 4 1])
                1 -4 0)


  (-> (matrix [4 6] [-2 4 -2 -1 4 -3
                     4 -8 3 -3 1 2
                     1 -2 1 -1 1 0
                     1 -2 0 -3 4 -1])
      (swap-rows 0 2)
      (row-addition 1 -4 0)
      (row-addition 2 2 0)
      (row-addition 3 -1 0)
      (row-addition 3 -1 1)
      (row-addition 3 -1 2)
      (row-mult 1 -1)
      (row-mult 2 -1/3))
  ;; => [(1 -2 1 -1 1 0) 
  ;;     (0 0 1 -1 3 -2) 
  ;;     (0 0 0 1 -2 1) 
  ;;     (0 0 0 0 0 0)]

  (-> (matrix [4 6] '(1 -2 1 -1 1 0
                        0 0 1 -1 3 -2
                        0 0 0 1 -2 1
                        0 0 0 0 0 0))
      (row-addition 0 1 2)
      (row-addition 1 1 2)
      (row-addition 0 -1 1)
      rows)
  ;; => [(1 -2 0 0 -2  2) 
  ;;     (0  0 1 0  1 -1) 
  ;;     (0  0 0 1 -2  1) 
  ;;     (0  0 0 0  0  0)]
  )
