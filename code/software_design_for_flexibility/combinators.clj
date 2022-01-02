(ns combinators)

(defonce debug (atom nil))
(deref debug)

;; Arity

(def inf ##Inf)
(defonce arity-table (atom {}))

(defn count-args [args]
  (if ((set args) '&) inf (count args)))

(defmacro procedure-arities [f]
  `(keep count-args (:arglists (meta (var ~f)))))

(defn procedure-arities2 [sym]
  (keep count-args (:arglists (meta (var sym)))))

(defn get-arity [f]
  (reset! debug f)
  (or (get @arity-table f)
      (procedure-arities f)))

(:arglists (meta (var +)))
(procedure-arities2 '+)
(procedure-arities list)
(procedure-arities (fn [x y] (list 'foo x y)))

(defn restrict-arity [proc nargs]
  (swap! arity-table assoc proc nargs)
  proc)

;; compositions

(defn compose [f g]
  (fn [& args]
    (f (apply g args))))

((compose #(list 'foo %)
          #(list 'bar %))
 'z)
;; => (foo (bar z))

(defn iterate' [n]
  (fn [f]
    (if (zero? n) identity
        (compose f ((iterate' (dec n)) f)))))

(defn square [x] (* x x))

(((iterate' 3) square) 5)
;; => 390625

(defn parallel-combine [h f g]
  (fn [& args]
    (h (apply f args) (apply g args))))

((parallel-combine list 
                   (fn [x y z] (list 'foo x y z))
                   (fn [u v w] (list 'bar u v w)))
 'a 'b 'c)
;; => ((foo a b c) (bar a b c))

(defn spread-combine [h f g]
  (let [n (get-arity f)]
    (fn the-combination [& args]
      (h (apply f (take n args))
         (apply g (drop n args))))))


((spread-combine list
                 (fn [x y] (list 'foo x y))
                 (fn [u v w] (list 'bar u v w)))
 'a 'b 'c 'd 'e)

(defn spread-combine [h f g]
  (let [n (get-arity f)
        m (get-arity g)
        t (+ n m)]
    (restrict-arity
      (fn the-combination [& args]
        (assert (= (count args) t))
        (h (apply f (take n args))
           (apply g (drop n args))))
      t)))

((spread-combine list
                 (fn [x y] (list 'foo x y))
                 (fn [u v w] (list 'bar u v w)))
 'a 'b 'c 'd 'e)
