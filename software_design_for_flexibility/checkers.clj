(ns checkers
  (:require [clojure.test :refer [deftest is]]))

;; coord fns

(defn coords+ [[x y] [x' y']] (vector (+ x x') (+ y y')))

(defn current-pieces [board] (:pieces board))
(defn is-position-on-board? [[x y] board] (and (<= 0 x 7) (<= 0 y 7)))
(defn board-get [coord board] (get (:pieces board) coord false))
(defn position-info [coord board] (get (board-get coord board) :owner :unoccupied))
(defn is-position-unoccupied? [coord board] (= :unoccupied (position-info coord board)))
(defn is-position-occupied-by-self? [coord board] (= :occupied-by-self (position-info coord board)))
(defn is-position-occupied-by-opponent? [coord board] (= :occupied-by-opponent (position-info coord board)))


(defn piece-coords [piece] (:coord piece))

(defn should-be-crowned? [piece] (or (and (= 7 (piece-coords piece))
                                          (= (:owner piece) :occupied-by-self))
                                     (and (= 0 (piece-coords piece))
                                          (= (:owner piece) :occupied-by-opponent))))

(defn crown-piece [piece] (assoc piece :crowned true))

(defn possible-directions [piece]
  (if (= (:owner piece) :occupied-by-self)
    [[0 1] [-1 1] [1 1]]
    [[0 -1] [-1 -1] [1 -1]]))

(position-info [1 1] {:pieces {[1 1] {:owner :occupied-by-opponent}}})
(position-info [1 1] {:pieces {[1 2] {:owner :occupied-by-opponent}}})
(is-position-unoccupied? [1 1] {:pieces {[1 2] {:owner :occupied-by-opponent}}})
(is-position-unoccupied? [1 1] {:pieces {[1 2] {:owner :occupied-by-opponent}}})
(is-position-occupied-by-opponent? [1 1] {:pieces {[1 1] {:owner :occupied-by-opponent}}})

;; referee

(defn step-to [step] (:post step))
(defn step-board [step] (:board step))

(defn make-simple-move [new-coord piece board]
  (let [new-piece (assoc piece :coord new-coord)]
    {:prior piece
     :jump? false
     :post new-piece
     :board (->  board
                 (update :pieces #(dissoc % (piece-coords piece)))
                 (assoc-in [:pieces new-coord] new-piece))}))

(make-simple-move [2 1] {:owner :occupied-by-self :coord [1 1]} {:pieces {[1 1] {:owner :occupied-by-self :coord [1 1]}}})

(defn make-jump [new-coord jumped-coord piece board]
  (let [new-piece (assoc piece :coord new-coord)]
    {:prior piece
     :jump? true
     :post new-piece
     :board (->  board
                 (update :pieces #(dissoc % (piece-coords piece)))
                 (update :pieces #(dissoc % jumped-coord))
                 (assoc-in [:pieces new-coord] new-piece))}))

(defn replace-piece [new-piece old-piece board])

(defn path-contains-jumps? [path]
  (some :jump? path))

(defn try-step [piece board direction path]
  (let [new-coord (coords+ (piece-coords piece) direction)]
    (and (is-position-on-board? new-coord board)
         (case (position-info new-coord board)
           :unoccupied (and (not (path-contains-jumps? path))
                            (cons (make-simple-move new-coord piece board) path))

           :occupied-by-opponent
           (let [landing (coords+ new-coord direction)]
             (and (is-position-on-board? landing board)
                  (is-position-unoccupied? landing board)
                  (cons (make-jump landing new-coord piece board) path)))

           :occupied-by-self false
           (throw (ex-info "Unknown position info" {}))))))

(try-step {:owner :occupied-by-self :coord [1 1]}
          {:pieces {[1 1] {:owner :occupied-by-self :coord [1 1]}}}
          [1 0]
          [])

(try-step {:owner :occupied-by-self :coord [1 1]}
          {:pieces {[1 1] {:owner :occupied-by-self :coord [1 1]}
                    [2 2] {:owner :occupied-by-opponent :coord [2 2]}}}
          [1 1]
          [])

(defn compute-next-steps [piece board path]
  (remove false? (map (fn [direction] (try-step piece board direction path)) (possible-directions piece))))

(compute-next-steps {:owner :occupied-by-self :coord [1 1]}
                    {:pieces {[1 1] {:owner :occupied-by-self :coord [1 1]}
                              [2 2] {:owner :occupied-by-opponent :coord [2 2]}}}
                    [])

(compute-next-steps {:owner :occupied-by-self :coord [1 1]}
                    {:pieces {[0 2] {:owner :occupied-by-self :coord [1 1]}
                              [1 1] {:owner :occupied-by-self :coord [1 1]}
                              [2 2] {:owner :occupied-by-opponent :coord [2 2]}}}
                    [])

(defn evolve-jumps [paths]
  (mapcat (fn [path]
            (let [step (first path)
                  new-paths (compute-next-steps (step-to step) (step-board step) path)]
              (if (some seq? new-paths) (evolve-jumps new-paths)
                  (list path))))
          paths))

(compute-next-steps {:owner :occupied-by-self, :coord [1 1]}
                    {:pieces {[1 1] {:owner :occupied-by-self, :coord [1 1]}
                              [2 2] {:owner :occupied-by-opponent, :coord [2 2]}
                              [4 4] {:owner :occupied-by-opponent, :coord [4 4]}}}
                    [])

(evolve-jumps '(({:prior {:owner :occupied-by-self, :coord [1 1]}
                  :jump? true
                  :post {:owner :occupied-by-self, :coord [3 3]}
                  :board {:pieces {[4 4] {:owner :occupied-by-opponent, :coord [4 4]}
                                   [3 3] {:owner :occupied-by-self, :coord [3 3]}}}})))

(defn evolve-paths [piece board]
  (let [paths (compute-next-steps piece board '())
        jumps (filter path-contains-jumps? paths)]
    (if (empty? jumps)
      paths
      (evolve-jumps jumps))))

(evolve-paths {:owner :occupied-by-self, :coord [1 1]}
              {:pieces {[7 0] {:owner :occupied-by-self, :coord [7 0]}
                        [1 1] {:owner :occupied-by-self, :coord [1 1]}
                        [2 2] {:owner :occupied-by-opponent, :coord [2 2]}
                        [4 4] {:owner :occupied-by-opponent, :coord [4 4]}}})

(evolve-paths {:owner :occupied-by-self, :coord [7 0]}
              {:pieces {[7 0] {:owner :occupied-by-self, :coord [7 0]}
                        [1 1] {:owner :occupied-by-self, :coord [1 1]}
                        [2 2] {:owner :occupied-by-opponent, :coord [2 2]}
                        [4 4] {:owner :occupied-by-opponent, :coord [4 4]}}})