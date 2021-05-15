(ns dijkstra2
  (:require [clojure.data.priority-map :refer [priority-map]]))

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

(let [q (priority-map :s 0)]
  (get q :b 10000))

(defn dijkstra [queue target]
  (let [[node pri] (first queue)
        conns  (connections graph node)]
    (if (= target node) pri
        (recur (dissoc (reduce (fn [q [_ n d]]
                                 (assoc q n (min (+ pri d) (get queue n 1000000))))
                               queue
                               conns)
                       node)
               target))))

(dijkstra (priority-map :s 0) :e)
