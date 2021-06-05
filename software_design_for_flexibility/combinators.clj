(ns combinators)

(defn compose [f g]
  (fn [& args]
    (f (apply g args))))

((compose #(list 'foo %)
          #(list 'bar %))
 'z)
;; => (foo (bar z))

(defn iterate [n]
  (fn [f]
    (if (zero? n) identity
        (compose f ((iterate (dec n)) f)))))

(defn square [x] (* x x))

(((iterate 3) square) 5)
;; => 390625

(defn parallel-combine [h f g]
  (fn [& args]
    (h (apply f args) (apply g args))))

((parallel-combine list 
                   (fn [x y z] (list 'foo x y z))
                   (fn [u v w] (list 'bar u v w)))
 'a 'b 'c)
;; => ((foo a b c) (bar a b c))

(defmacro get-arities [f]
  `(map count (:arglists (meta (var ~f)))))

(get-arities map)

(macroexpand '(get-arities map))

(macroexpand (get-arities +))

(defn spread-combine [h f g]
  (let [n (get-arities f)]
    (fn [& args]
      (h (apply f (take n args))
         (apply g (drop n args))))))

(defn get-arities [f]
  (let [m (first (.getDeclaredMethods (class f)))
        p (.getParameterTypes m)]
    (alength p)))

(count (.getDeclaredMethods (class map)))
(.getParameterTypes (nth (.getDeclaredMethods (class map)) 0))
(.getParameterTypes (nth (.getDeclaredMethods (class map)) 1))
(.getParameterTypes (nth (.getDeclaredMethods (class map)) 2))
(.getParameterTypes (nth (.getDeclaredMethods (class map)) 3))
(.getParameterTypes (nth (.getDeclaredMethods (class map)) 4))
(.getParameterTypes (nth (.getDeclaredMethods (class map)) 5))
(.getParameterTypes (nth (.getDeclaredMethods (class map)) 6))
(.getParameterTypes (nth (.getDeclaredMethods (class map)) 7))
(.getParameterTypes (nth (.getDeclaredMethods (class map)) 8))
(.getParameterTypes (nth (.getDeclaredMethods (class map)) 9))
(.getParameterTypes (nth (.getDeclaredMethods (class map)) 10))

(defn get-arity [f]
  (dedupe (sort (map count (map #(.getParameterTypes %) (.getDeclaredMethods (class f)))))))

(get-arity )