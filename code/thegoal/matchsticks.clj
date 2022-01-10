(ns matchsticks)

(defn setup [stations start rolls capacities]
  {:stations (into [start] (repeat stations 0))
   :capacities capacities
   :scores  (vec (repeat stations 0))
   :round (min 1000 (count rolls)) ;; capped at 1000 to prevent infinite loops
   :station-number 0
   :rolls rolls})

(defn step [{:keys [station-number rolls stations capacities] :as state}]
  (let [produced (min (* (get capacities station-number) (first rolls))
                      (get stations station-number))]
    (-> state
        (update-in [:stations station-number]       - produced)
        (update-in [:stations (inc station-number)] + produced)
        (update-in [:scores station-number]         + produced)
        (update :rolls rest)
        (update :round dec)
        (assoc :station-number (mod (inc station-number) (dec (count stations)))))))

(defn run [state]
  (if (zero? (:round state)) {:produced (last (:stations state))
                              :scores (:scores state)}
      (recur (step state))))

(defn combine-games [r1 r2]
  {:runs ((fnil inc 1) (:runs r1))
   :scores (map + (:scores r1) (:scores r2))
   :produced (+ (:produced r1) (:produced r2))})

(defn run-multiple [n setup]
  (let [totals (reduce combine-games (repeatedly n #(run (setup))))
        div (comp float #(/ % (:runs totals)))]
    (-> totals
        (update :produced div)
        (update :scores #(map div %))
        (dissoc :runs))))

(defn dice-roll [] (inc (rand-int 6)))

(defn matchstick-game []
  (setup 6 100 (repeatedly (* 6 10) dice-roll) (vec (repeat 6 1))))

(defn distance-travelled [] (/ (inc (rand 2)) 6))

(defn scouts-with-capacities [f srt]
  #(setup 30 10
          (repeatedly (* 30 6 5) distance-travelled)
          (vec (srt (repeatedly 30 f)))))

(def scouts (scouts-with-capacities (fn [] 1) identity))

(def fast-first-scouts
  (scouts-with-capacities #(+ 0.8 (rand 0.4)) (partial sort >)))

(def slow-first-scouts
  (scouts-with-capacities #(+ 0.8 (rand 0.4)) sort))

(defn scouts-with-herbie [f srt herbie]
  #(setup 30 10
          (repeatedly (* 30 6 5) distance-travelled)
          (vec (srt (conj (repeatedly 29 f) herbie)))))

(def herbie-first
  (scouts-with-herbie #(+ 0.8 (rand 0.4)) sort 0.5))

(def herbie-last
  (scouts-with-herbie #(+ 0.8 (rand 0.4)) (partial sort >) 0.5))

(def super-scouts
  (scouts-with-herbie #(+ 1.8 (rand 0.4)) (partial sort >) 0.5))

(+ 0.8 (rand 0.4))

(comment
  (matchstick-game)
  (run-multiple 1000 matchstick-game)

  (run-multiple 1000 scouts)

  (run-multiple 1000 fast-first-scouts)
  (run-multiple 1000 slow-first-scouts)

  (run-multiple 1000 herbie-first)
  (run-multiple 1000 herbie-last)
  (run-multiple 1000 super-scouts))