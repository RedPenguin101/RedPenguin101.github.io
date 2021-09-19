(ns dijkstra (:require [clojure.test :refer [deftest is testing]]))

;; Graph Building and Querying

(defn edge [graph start end weight]
  (-> graph
      (conj [start end weight])
      (conj [end start weight])))

(defn nodes [graph]
  (set (map first graph)))

(defn connections [graph node]
  (set (filter #(#{node} (first %)) graph)))

(def graph (-> #{}
               (edge :s :a 7)
               (edge :s :b 2)
               (edge :s :c 3)
               (edge :a :d 4)
               (edge :a :b 3)
               (edge :b :d 4)
               (edge :b :h 1)
               (edge :c :l 2)
               (edge :d :f 5)
               (edge :e :g 2)
               (edge :e :k 5)
               (edge :f :h 3)
               (edge :g :h 2)
               (edge :i :l 4)
               (edge :i :j 6)
               (edge :i :k 4)
               (edge :j :l 4)
               (edge :j :k 4)))

(nodes graph)
;; => #{:e :s :l :k :g :c :j :h :b :d :f :i :a}

(connections graph :s)
;; => #{[:s :c 3] [:s :a 7] [:s :b 2]}

;; pri queues

(defn update-queue [queue [from to dist]]
  (let [base (first (from queue))]
    (if (or (not (to queue)) (< (+ base dist) (first (to queue))))
      (assoc queue to [(+ base dist) from])
      queue)))

(defn priority [queue]
  (ffirst (sort-by (juxt (comp first second) first) queue)))

(deftest q
  (testing "an unseen node gets added"
    (is (= {:s [0 :s], :b [2 :s]}
           (update-queue {:s [0 :s]} [:s :b 2]))))
  (testing "a seen node where the new route is shorter gets updated"
    (is (= [5 :b]
           (:a (update-queue {:s [0 :s] :b [2 :s] :a [7 :s]}
                             [:b :a 3])))))
  (testing "a seen node where the new route is longer is unchanged"
    (is (= [6 :b]
           (:d (update-queue {:a [5 :b] :l [5 :c] :g [5 :h] :d [6 :b] :f [6 :h]}
                             [:a :d 4]))))))

(defn dijkstra
  ([graph start end] (dijkstra [] graph end {start [0]}))
  ([done graph target queue]
   (let [pri (priority queue)]
     (if (target queue)
       (conj done (cons target (target queue)))
       (recur (conj done (cons pri (pri queue)))
              graph
              target
              (dissoc (reduce update-queue queue (connections graph pri))
                      pri))))))

(dijkstra graph :s :e)
;; => [(:s 0) (:b 2 :s) (:c 3 :s) (:h 3 :b) (:b 4 :h) (:s 4 :b) (:a 5 :b) (:g 5 :h) (:e 7 :g)]

(defn follow [path m node]
  (if (node m)
    (recur (conj path node) m (node m))
    (reverse (conj path node))))

(defn find-path [dijkstra-result]
  (follow [] (reduce (fn [A [this _ from]]
                       (assoc A this from))
                     {}
                     (reverse dijkstra-result))
          (first (last dijkstra-result))))

(deftest t
  (is (= [:s :b :h :g :e]
         (find-path (dijkstra graph :s :e)))))