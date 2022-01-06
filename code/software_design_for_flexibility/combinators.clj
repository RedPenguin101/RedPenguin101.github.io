(ns combinators
  (:require [clojure.reflect :as r]))

(defonce debug (atom nil))

;; Arity

(def inf ##Inf)

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


(def arity-table (atom {}))
(comment (reset! arity-table {}))

(defn get-arity [f]
  (or (get @arity-table f)
      (first (procedure-arities f))))

(defn register-arity [proc nargs]
  (swap! arity-table assoc proc nargs)
  proc)

;; Multireturn system

(def return-arity-table (atom {}))

(defn get-return-arity [f]
  (or (get @return-arity-table f) 1))

(defn register-return-arity [f nargs]
  (swap! return-arity-table assoc f nargs)
  f)

(defn multi-return? [f] (> (get-return-arity f) 1))

(defn multi-return [f] 
  (if (multi-return? f) f
      (fn [& args] [(apply f args)])))

;; compositions

(defn compose [f g]
  (let [m (get-arity f)]
    (assert (= 1 (get-arity g)))
    (register-arity
      (fn the-combination [& args]
        (assert (= m (count args)))
        (f (apply g args)))
      m)))

(comment
  (def comp-check (compose #(list 'foo %)
                           #(list 'bar %)))


  (comp-check 'z)
   ; (foo (bar z))
  (get-arity comp-check)
  ; 1
  ) 

(comment (reset! arity-table {}))

(defn parallel-combine [h f g]
  (let [f' (get-arity f) g' (get-arity g)]
    (assert (= f' g'))
    (register-arity
      (fn the-combination [& args]
        (h (apply f args) (apply g args)))
      f')))

(comment
  (def pc2-check
    (parallel-combine (fn [a b] (list a b))
                      (fn [x y z] (list 'foo x y z))
                      (fn [u v w] (list 'bar u v w))))


  (get-arity pc2-check)
   ; 3
  (pc2-check 'a 'b 'c)
  ; ((foo a b c) (bar a b c))
  )

(defn spread-combine [h f g]
  (let [n (get-arity f) m (get-arity g)
        arity (+ n m)]
    (register-arity
     (fn the-combination [& args]
       (assert (= (count args) arity))
       (h (apply f (take n args))
          (apply g (drop n args))))
     arity)))

(comment
  (def sc2-check (spread-combine list
                                 (fn [x y] (list 'foo x y))
                                 (fn [u v w] (list 'bar u v w))))


  (get-arity sc2-check)
   ; 5

  (sc2-check 'a 'b 'c 'd 'e)
   ; ((foo a b) (bar c d e))

  (sc2-check 'a 'b 'c 'd)
  ;;  Assert failed: (= (count args) arity)
  ;; => Execution error (AssertionError) at combinators$spread_combine2$the_combination__7381/doInvoke (REPL:88).
  )

(defn spread-apply [f g]
  (let [f' (get-arity f) g' (get-arity g)
        arity (+ f' g')]
    (-> (fn the-combination [& args]
          (assert (= arity (count args)))
          (concat (apply (multi-return f) (take f' args))
                  (apply (multi-return g) (drop f' args))))
        (register-arity arity)
        (register-return-arity (+ (get-return-arity f) (get-return-arity g))))))

(defn spread-apply2 [f g return-merge]
  (let [f' (get-arity f) g' (get-arity g)
        arity (+ f' g')]
    (-> (fn spread-apply-combination [& args]
          (assert (= arity (count args)))
          (return-merge (apply f (take f' args))
                        (apply g (drop f' args))))
        (register-arity arity))))

(defn compose2 [f g]
  (let [g' (get-arity g)]
    (assert (= (get-arity f) (get-return-arity g)))
    (-> (fn the-combination [& args]
          (assert (= g' (count args)))
          (apply f (apply (multi-return g) args)))
        (register-arity g')
        (register-return-arity (get-return-arity f)))))

(defn spread-combine2 [h f g]
  (compose2 h (spread-apply f g)))

(defn compose3 [f g]
  (let [g' (get-arity g)]
    (-> (fn compose-combination [& args]
          (assert (= g' (count args)))
          (f (apply g args)))
        (register-arity g'))))

(defn spread-combine3 [h f g return-merge]
  (compose3 h (spread-apply2 f g return-merge)))

(comment
  @arity-table
  (do (reset! arity-table {})
      (reset! return-arity-table {}))

  (def sa-test (spread-apply (fn [x y] (list 'foo x y))
                             (fn [u v w] (list 'bar u v w))))

  (get-arity sa-test) ;; => 5
  (get-return-arity sa-test) ;; => 2

  (sa-test 'a 'b 'c 'd 'e)
  ;; => ((foo a b) (bar c d e))

  (def sc-test (spread-combine2 (fn [r s] {:from-first r :from-second s})
                                (fn [x y] (list 'foo x y))
                                (fn [u v w] (list 'bar u v w))))

  (get-arity sc-test) ;; => 5
  (get-return-arity sa-test) ;; => 2

  (sc-test 'a 'b 'c 'd 'e)
  ;; => {:from-first (foo a b), :from-second (bar c d e)}


  (def sc3-test (spread-combine3 (fn [r s] {:from-first r :from-second s})
                                 (fn [x y] (list 'foo x y))
                                 (fn [u v w] (list 'bar u v w))
                                 list))

  (get-arity sc3-test) ;; => 5

  (sc3-test 'a 'b 'c 'd 'e)
  ;; => {:from-first (foo a b), :from-second (bar c d e)}

  (def sa2-test (spread-apply2
                 (fn [x y] {:f (list 'foo x y)})
                 (fn [u v w] {:g (list 'bar u v w)})
                 merge))
  
  (get-arity sa2-test)
  (sa2-test 'a 'b 'c 'd 'e)

  (def sc3-test2 (spread-combine3 (fn [r] {:from-first (:f r) :from-second (:g r)})
                                  (fn [x y] {:f (list 'foo x y)})
                                  (fn [u v w] {:g (list 'bar u v w)})
                                  merge))
  
  (get-arity sc3-test2)
  (sc3-test2 'a 'b 'c 'd 'e)

  )