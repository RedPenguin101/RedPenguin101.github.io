(ns poker
  (:require [clojure.set :refer [map-invert]]
            [clojure.test :refer [deftest is]]
            [clojure.math.combinatorics :as combo]))

(defn straight? [ranks]
  (every? (fn [[a b]] (= a (dec b))) (partition 2 1 (sort ranks))))

(defn flush? [suits]
  (apply = suits))

(defn kind? [kind ranks]
  ((map-invert (frequencies ranks)) kind))

(defn two-pair? [ranks]
  (let [pairs (map first (filter #(= 2 (second %)) (frequencies ranks)))]
    (when (= 2 (count pairs))
      (vec (reverse (sort pairs))))))

(defn rank-hand [hand]
  (let [[ranks suits] (apply map vector hand)]
    (cond
      (and (straight? ranks) (flush? suits)) [8 (apply max ranks) 0]
      (kind? 4 ranks)                        [7 (kind? 4 ranks) (kind? 1 ranks)]
      (and (kind? 3 ranks) (kind? 2 ranks))  [6 (kind? 3 ranks) (kind? 2 ranks)]
      (flush? suits)                         [5 (vec (reverse (sort ranks))) 0]
      (straight? ranks)                      [4 (apply max ranks) 0]
      (kind? 3 ranks)                        [3 (kind? 3 ranks) 0]
      (two-pair? ranks)                      [2 (two-pair? ranks) 0]
      (kind? 2 ranks)                        [1 (kind? 2 ranks) (reverse (sort ranks))]
      :else                                  [0 (vec (reverse (sort ranks))) 0])))

(let [flush [[2 :C] [3 :C] [7 :C] [5 :C] [6 :C]]]
  (rank-hand flush))

(defn poker [hands]
  (if (empty? hands)
    (throw (ex-info "hands cannot be empty" hands))
    (last (sort-by rank-hand hands))))

(deftest test-hands
  (let [sf [[6 :C] [7 :C] [8 :C] [9 :C] [10 :C]]
        fk [[9 :D] [9 :H] [9 :S] [9 :C] [7 :D]]
        fh [[10 :D] [10 :C] [10 :H] [7 :C] [7 :D]]
        flush [[2 :C] [3 :C] [7 :C] [5 :C] [6 :C]]
        straight [[6 :C] [7 :H] [8 :C] [9 :C] [10 :C]]
        three-kind [[9 :D] [9 :H] [9 :S] [8 :C] [7 :D]]
        two-pair [[10 :D] [10 :C] [7 :H] [7 :C] [6 :D]]
        pair [[10 :D] [10 :C] [7 :H] [8 :C] [6 :D]]
        high-card [[2 :H] [7 :C] [8 :D] [9 :S] [10 :C]]
        hands [sf fk fh flush straight two-pair three-kind pair high-card]]

    (is (= sf (poker [sf fk fh flush straight two-pair three-kind pair high-card])))
    (is (= fk (poker [fk fh flush straight two-pair three-kind pair high-card])))
    (is (= fh (poker [fh flush straight two-pair three-kind pair high-card])))
    (is (= flush (poker [flush straight two-pair three-kind pair high-card])))
    (is (= straight (poker [straight two-pair three-kind pair high-card])))
    (is (= three-kind (poker [two-pair three-kind pair high-card])))
    (is (= two-pair (poker [two-pair pair high-card])))

    (is (= hands (map poker (map vector hands))))
    (is (= sf (poker (cons sf (repeat 99 fk)))))
    (is (thrown? Exception (poker [])))

    (is (= [8 10 0] (rank-hand sf)))
    (is (= [7 9 7] (rank-hand fk)))
    (is (= [6 10 7] (rank-hand fh)))
    (is (= [5 [7 6 5 3 2] 0] (rank-hand flush)))
    (is (= [4 10 0] (rank-hand straight)))
    (is (= [2 [10 7] 0] (rank-hand two-pair)))
    (is (= [1 10 [10 10 8 7 6]] (rank-hand pair)))
    (is (= [0 [10 9 8 7 2] 0] (rank-hand high-card)))))

(def deck
  (for [rank (range 2 15)
        suit [:C :S :D :H]]
    [rank suit]))

deck

(defn deal [num-hands deck]
  (if (> num-hands (quot (count deck) 5))
    (throw (ex-info (str "Max number of hands with " (count deck) " cards is " (quot (count deck) 5))
                    {:num-hands num-hands :deck deck}))
    (take num-hands (partition 5 (shuffle deck)))))

(deal 2 deck)

(defn random-hand [deck]
  (take 5 (shuffle deck)))

(random-hand deck)

(defn run-probs [times]
  (for [i (range 0 times)]
    (random-hand deck)))

(def ranks {0 :high
            1 :pair
            2 :2-pair
            3 :3-kind
            4 :straight
            5 :flush
            6 :full-house
            7 :4-kind
            8 :straight-flush})

(defn verify-probs [n]
  (sort-by second (into {} (map (fn [[k v]] [(ranks k) (apply str (take 5 (str (* 100 (float (/ v n))))))]) (frequencies (map (comp first rank-hand) (run-probs n)))))))

(comment
  (verify-probs 100000))

;; 7-card stud homework

(defn best-hand [hand]
  (sort (poker (combo/combinations hand 5))))

(deftest seven-card
  (is (= [[6 :C] [7 :C] [8 :C] [9 :C] [10 :C]]
         (best-hand [[6 :C] [7 :C] [8 :C] [9 :C] [10 :C] [5 :C] [11 :S]])))
  (is (= [[8 :C] [8 :S] [10 :C] [10 :D] [10 :H]]
         (best-hand [[10 :D] [10 :C] [10 :H] [7 :C] [7 :D] [8 :C] [8 :S]])))
  (is (= [[7 :C] [7 :D] [7 :H] [7 :S] [10 :H]]
         (best-hand [[10 :D] [10 :C] [10 :H] [7 :C] [7 :D] [7 :S] [7 :H]]))))
