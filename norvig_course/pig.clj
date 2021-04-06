(ns pig
  (:require [clojure.test :refer [deftest is]]))

"Concept Inventory
 
 High level: play-pig :: player-a, player-b -> winner, strategy :: state -> action
 Mid level: state(p me you pending), actions(roll :: state, dice -> state, hold :: state -> state)
 Low level: die, scores, player = strategy, to-move, goal"

(def goal 50)

(defn hold [[p this-player other-player pending]]
  [(mod (+ p 1) 2) other-player (+ pending this-player) 0])

(defn roll [[p this-player other-player pending], d]
  (if (= 1 d)
    (hold [p this-player other-player 1])
    [p this-player other-player (+ d pending)]))

(defn clueless [_state]
  (rand-nth [:roll :hold]))

(symbol (str "hold-at-" 10))

(defn hold-at [n]
  (fn [[_ this-player _ pending]]
    (if (or (>= pending n)
            (>= (+ this-player pending) goal))
      :hold
      :roll)))

(defn win? [[_ this other _]]
  (or (>= this goal) (>= other goal)))

(win? [0 0 56 1])

(def action {:roll roll
             :hold hold})

(defn play-pig [a b rolls]
  (let [strats [a b]]
    (loop [state [0 0 0 0]
           rolls rolls]
      (if (win? state) [(if (zero? (first state)) :b :a) state]
          (recur (case ((nth strats (first state)) state)
                   :roll (roll state (first rolls))
                   :hold (hold state))
                 (rest rolls))))))

(deftest game
  (is (= [:a [1 0 50 0]]
         (play-pig (hold-at 50) clueless (cycle [6 6 6 6 6 6 6 6 2])))))

(deftest hold-at-strat
  (is (= :roll ((hold-at 30) [1 29 15 20])))
  (is (= :hold ((hold-at 30) [1 29 15 21])))
  (is (= :roll ((hold-at 15) [0 2 30 10])))
  (is (= :hold ((hold-at 15) [0 2 30 15]))))

(deftest actions
  (let [s [0 10 20 30]]
    (is (= [1 20 40 0] (hold s)))
    (is (= [0 10 20 36] (roll s 6)))
    (is (= [1 20 11 0] (roll s 1))))
  (is (= [1 10 20 0] (hold [0 10 10 10])))
  (is (= [0 10 20 0] (hold [1 10 10 10])))
  (is (= [1 10 10 15] (roll [1 10 10 10] 5)))
  (is (= [0 10 11 0] (roll [1 10 10 10] 1))))


;; Game Theory

(defn quality
  "The expected value of taking action in state, according to utility U"
  [state action utility]
  (if (= :hold action)
    (utility (+ state 1000000))
    (* 0.5 (+ (utility (+ state 3000000)) (utility state)))))

(defn actions [_state]
  [:hold :gamble])

(defn best-action
  "Return the optimal action for a state, given U"
  [state actions quality utility]
  (let [expected-utility #(quality state % utility)]
    (apply max-key #(expected-utility %) (actions state))))

(best-action 100 actions quality identity)

(best-action 10000000 actions quality #(Math/log %))

(map (fn [start] [start (best-action start actions quality #(Math/log10 %))]) (range 0 10000000 100000))

(defn q-pig [state action pwin]
  (if (= action :hold)
    (- 1 (pwin (hold state)))
    (/ (+ (- 1 (pwin (roll state 1))) (reduce + #(pwin (roll state %)) (range 1 7))) 6)))

(defn pig-actions [[_ _ _ pending]]
  (if (pos? pending) [:roll :hold]
      [:roll]))

(defn pwin [[p me you pending :as state]]
  (cond (>= (+ me pending) goal) 1
        (>= you goal) 0
        :else (apply max-key #(q-pig state % pwin) (pig-actions state))))