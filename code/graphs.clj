(ns graphs
  (:require [clojure.set :as set])
  (:refer-clojure :exclude [parents ancestors descendants]))

(defn add-directed-edge [graph edge] (conj graph edge))

(defn nodes [graph] (set (mapcat #(take 2 %) graph)))
(defn parents  [graph node] (keep #(when (= node (second %)) (first %)) graph))
(defn children [graph node] (keep #(when (= node (first %)) (second %)) graph))

(defn ancestors [graph node]
  (let [prnts (parents graph node)]
    (set (concat prnts (mapcat #(ancestors graph %) prnts)))))

(defn descendants [graph node]
  (let [chldrn (children graph node)]
    (set (concat chldrn (mapcat #(children graph %) chldrn)))))


(defn roots [graph] (set/difference (nodes graph) (set (map second graph))))
(defn leaves [graph] (set/difference (nodes graph) (set (map first graph))))

(defn find-edge [graph from to] (first (filter #(= (take 2 %) [from to]) graph)))

(defn add-edge [graph edge]
  (let [[from to & values] edge]
    (-> graph (add-directed-edge edge) (add-directed-edge (apply vector to from values)))))

(comment
  (def test-graph
    (-> []
        (add-directed-edge [:a :b 2])
        (add-directed-edge [:b :c 1])
        (add-directed-edge [:a :d 3])
        (add-directed-edge [:d :e 4])))
  ;; => [[:a :b 2] [:b :c 1] [:a :d 3] [:d :e 4]]

  (nodes test-graph) ;; => #{:e :c :b :d :a}
  (parents test-graph :c) ;; => (:b)
  (children test-graph :a) ;; => (:b :d)
  (parents test-graph :a) ;; => ()
  (children test-graph :e)
   ;; => ()
  (ancestors test-graph :e)
   ;; => #{:d :a}
  (ancestors test-graph :d)
   ;; => #{:a}
  (ancestors test-graph :a)
   ;; => #{}
  (ancestors test-graph :c)
   ;; => #{:b :a}
  (descendants test-graph :a)
   ;; => #{:e :c :b :d}
  (descendants test-graph :b)
   ;; => #{:c}
  (descendants test-graph :c)
   ;; => #{}
  (descendants test-graph :d)
   ;; => #{:e}
  (roots test-graph)
   ;; => #{:a}
  (leaves test-graph)
   ;; => #{:e :c}
  (find-edge test-graph :a :b)
   ;; => [:a :b 2]

  (def test-graph2 (-> []
                       (add-edge [:a :b 2])
                       (add-edge [:b :c 1])
                       (add-edge [:a :d 3])
                       (add-edge [:d :e 4])))

;; => [[:a :b 2] [:b :a 2] [:b :c 1] [:c :b 1] [:a :d 3] [:d :a 3] [:d :e 4] [:e :d 4]]

  (nodes test-graph2)
   ;; => #{:e :c :b :d :a}
  (parents test-graph2 :c)
   ;; => (:b)
  (children test-graph2 :a)
   ;; => (:b :d)
  (parents test-graph2 :b)
   ;; => (:a :c)
  (ancestors test-graph2 :e)
  ;; => Execution error (StackOverflowError) at graphs/ancestors (REPL:28).
  ;;    null

  (roots test-graph2)
   ;; => #{}
  (leaves test-graph2)
   ;; => #{}
  (find-edge test-graph2 :a :b)
   ;; => [:a :b 2]
  (find-edge test-graph2 :b :a)
   ;; => [:b :a 2]
  )

;; dfs

(defn depth-first-search
  ([graph start] (depth-first-search graph [] [start] #{start}))
  ([graph processed unprocessed seen]
   (if (= (set processed) (nodes graph)) processed
       (let [[fup & rup] unprocessed
             new-children (remove seen (children graph fup))]
         (recur graph
                (conj processed fup)
                (into rup new-children)
                (apply conj seen new-children))))))

(let [dfs-graph [[0 1] [0 2] [1 2] [2 0] [2 3]]]
  (depth-first-search dfs-graph 2))
