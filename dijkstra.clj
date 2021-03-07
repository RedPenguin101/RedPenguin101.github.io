(ns dijkstra)

;; Graph Building and Querying

(defn edge [graph start end weight]
  (-> graph
      (conj [start end weight])
      (conj [end start weight])))

(defn nodes [graph]
  (set (map first graph)))

(defn connections [graph node]
  (set (filter #(#{node} (first %)) graph)))

(defn neighbours [graph node]
  (map second (connections graph node)))

(comment
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

  (connections graph :s))

(defn dijkstra [graph start end])

;; Priority Queues

(defn build-queue [graph start]
  (into {} 
   (for [node (nodes graph)]
     [node (if (= node start) [0] [##Inf])])))

(build-queue graph :s)
;; => {:e [##Inf],
;;     :s [0],
;;     :l [##Inf],
;;     :k [##Inf],,,

(defn priority [queue]
  (ffirst (sort-by (juxt (comp first second) first) queue)))

(priority (build-queue graph :s))

(defn update-entry [[old-dist :as old] from new-dist]
  #_(println [:update-entry {:old old :from from :new-dist new-dist}])
  (cond (< new-dist old-dist) [new-dist from]
        :else old))

(defn update-queue [queue new-conns]
  (reduce (fn [q [from entry new-dist]]
            (if (entry q)
              (update q entry update-entry from (+ (first (from q)) new-dist))
              q))
          queue new-conns))


(defn dijkstra 
  ([graph start end] (dijkstra [] graph end (build-queue graph start)))
  ([done graph target queue] 
   (let [pri (priority queue)]
     (if (< (first (target queue)) ##Inf)
       (conj done (cons target (target queue)))
       (recur (conj done (cons pri (pri queue)))
              graph
              target
              (dissoc
               (update-queue queue (connections graph pri))
               pri))))))

(dijkstra graph :s :e)
;; => [(:s 0) (:b 2 :s) (:c 3 :s) (:h 3 :b) (:a 5 :b) (:g 5 :h) (:e 7 :g)]


(defn find-path [dijkstra-result]
  (reduce (fn [A [this dist from]]
            (assoc A this from))
          {}
          (reverse dijkstra-result)))

(find-path '[(:s 0) (:b 2 :s) (:c 3 :s) (:h 3 :b) (:a 5 :b) (:g 5 :h) (:e 7 :g)])