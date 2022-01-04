(ns combinators
  (:require [clojure.reflect :as r]))

(defonce debug (atom nil))

;; Arity

(def inf ##Inf)
(defonce arity-table (atom {}))
(comment (reset! arity-table {}))

(defn count-args [arg-list]
  (if (some #(= "clojure.lang.ISeq" (name %)) arg-list)
    inf (count arg-list)))

(defn procedure-arities [f]
  (->> (:members (r/reflect f))
       (filter #(#{"invoke" "invokeStatic"} (name (:name %))))
       (map #(count-args (:parameter-types %)))
       (sort)
       (dedupe)))

(comment
  (procedure-arities list) ; (##Inf)
  (procedure-arities +) ; (0 1 2 ##Inf)
  (procedure-arities constantly) ; (1)
  (procedure-arities #(- %)) ; (1)
  (procedure-arities #(- %1 %2)) ; (2)
  (procedure-arities (fn ([x] x) ([x y] x))) ; (1 2)
  )

(defn get-arity [f]
  (or (get @arity-table f)
      (first (procedure-arities f))))

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
; ((foo a b) (bar c d e))

(defn spread-combine2 [h f g]
  (let [n (get-arity f) m (get-arity g)
        t (+ n m)]
    (restrict-arity
      (fn the-combination [& args]
        (assert (= (count args) t))
        (h (apply f (take n args))
           (apply g (drop n args))))
      t)))

(def sc2-check (spread-combine2 list
                 (fn [x y] (list 'foo x y))
                 (fn [u v w] (list 'bar u v w))))

(sc2-check 'a 'b 'c 'd 'e) ; ((foo a b) (bar c d e))

(get-arity sc2-check) ; 5

;; exercise 2.1 Arity repair: check, assert, advertise arity

(defn compose2 [f g]
  (let [m (get-arity f)]
    (assert (= 1 (get-arity g)))
    (restrict-arity
      (fn the-combination [& args]
        (assert (= m (count args)))
        (f (apply g args)))
      m)))

(def comp-check (compose2 #(list 'foo %)
                          #(list 'bar %)))

(comp-check 'z) ; (foo (bar z))
(get-arity comp-check) ; 1

(comment (reset! arity-table {}))

(defn parallel-combine2 [h f g]
  (let [f' (get-arity f) g' (get-arity g)]
    (assert (= f' g'))
    (assert (= 2 (get-arity h)))
    (restrict-arity
      (fn the-combination [& args]
        (h (apply f args) (apply g args)))
      f')))

(def pc2-check
  (parallel-combine2 (fn [a b] (list a b))
                     (fn [x y z] (list 'foo x y z))
                     (fn [u v w] (list 'bar u v w))))

(get-arity pc2-check) ; 3
(pc2-check 'a 'b 'c) ; ((foo a b c) (bar a b c))
