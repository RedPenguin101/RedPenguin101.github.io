(ns sicp)

(defn square [x] (* x x))
(defn cube [x] (* x x x))
(defn average [a b] (/ (+ a b) 2))

;; Sqrt Newton

(defn good-enough? [guess x]
  (< (Math/abs (double (- (* guess guess) x))) 0.001))

(defn improve [guess x]
  (/ (+ guess (/ x guess)) 2))

(defn sqrt-iter [guess x]
  (if (good-enough? guess x)
    guess
    (sqrt-iter (improve guess x) x)))

(double (sqrt-iter 1 2))

(defn sqrt [x]
  (letfn [(good-enough? [guess] (< (Math/abs (double (- (* guess guess) x))) 0.001))
          (improve [guess] (/ (+ guess (/ x guess)) 2))
          (sqrt-iter [guess x] (if (good-enough? guess) guess (sqrt-iter (improve guess) x)))]
    (sqrt-iter 1 x)))

(sqrt 2)

;; 1.2
;; linear recursion and iteration

(defn factorial [n] (if (= n 1) 1 (* n (factorial (dec n)))))
(factorial 5)

(defn factorial
  ([n] (factorial 1 1 n))
  ([acc i n] (if (= n i) (* acc i) (factorial (* acc i) (inc i) n))))
(factorial 5)

(defn fib [n] (if (< n 2) n (+ (fib (- n 1)) (fib (- n 2)))))

(defn fib-iter [a b count] (if (zero? count) b (fib-iter (+ a b) a (dec count))))

(defn expt [b n] (if (zero? n) 1 (* b (expt b (dec n)))))

(defn expt-iter [b counter product]
  (if (zero? counter) product (expt-iter b (dec counter) (* b product))))

(defn fast-exp [b n]
  (cond (zero? n) 1
        (even? n) (square (fast-exp b (/ n 2)))
        :else (* b (fast-exp b (- n 1)))))

(defn sum-integers [a b] (if (> a b) 0 (+ a (sum-integers (+ a 1) b))))
(defn sum-cubes    [a b] (if (> a b) 0 (+ (cube a) (sum-cubes (+ a 1) b))))
(defn pi-sum       [a b] (if (> a b) 0 (+ (/ 1 (* a (+ a 2))) (pi-sum (+ a 4) b))))
(defn sum [term a next b] (if (> a b) 0 (+ (term a) (pi-sum (next a) b))))
(sum identity 5 inc 10)

;; half interval

(defn close-enough? [a b]
  (< (Math/abs (- a b)) 0.001))

(defn search [f a b]
  (let [mid (average a b)]
    (if (close-enough? a b)
      mid
      (let [test (f mid)]
        (cond (pos? test) (search f a mid)
              (neg? test) (search f mid b)
              :else mid)))))

(defn half-interval [f a b]
  (let [fa (f a) fb (f b)]
    (cond (< fa 0 fb) (search f a b)
          (< fb 0 fa) (search f b a)
          :else "Error: values are not of opposite sign")))

(half-interval #(Math/sin %) 2.0 4.0)
;; => 3.14111328125
(half-interval #(- (cube %) (* 2 %) 3) 1.0 2.0)
;; => 1.89306640625

(defn fixed-point [f first-guess]
  (letfn [(close-enough? [v1 v2] (< (Math/abs (- v1 v2)) 0.00001))
          (try* [guess] (let [next (f guess)] (if (close-enough? guess next) next (try* next))))]
    (try* first-guess)))

(fixed-point #(Math/cos %) 1.0)
;; => 1.0
(fixed-point #(+ (Math/sin %) (Math/cos %)) 1.0)
;; => 1.2587315962971173

(def dx 0.00001)
(defn deriv [g] (fn [x] (/ (- (g (+ x dx)) (g x)) dx)))
((deriv cube) 5)
;; => 75.00014999664018

(defn newton-transform [g]
  (fn [x] (- x (/ (g x) ((deriv g) x)))))
(defn newtons-method [g guess]
  (fixed-point (newton-transform g) guess))
(defn sqrt [x] (newtons-method #(- (square %) x) 1.0))
(sqrt 2)
;; => 1.4142135623822438


;; Symbolic differentiation

(def variable? symbol?)
(defn same-variable? [a b] (and (every? symbol? [a b]) (= a b)))
(defn sum? [expr] (and (seq expr) (= (first expr) '+)))
(defn product? [expr] (and (seq expr) (= (first expr) '*)))
(defn make-sum [e1 e2] (list '+ e1 e2))
(defn make-product [e1 e2] (list '* e1 e2))
(defn addend [expr] (nth expr 1))
(defn augend [expr] (nth expr 2))
(defn multiplier [expr] (nth expr 1))
(defn multiplicand [expr] (nth expr 2))

(defn deriv [exp var]
  (cond (number? exp) 0
        (variable? exp) (if (same-variable? exp var) 1 0)
        (sum? exp) (make-sum (deriv (addend exp) var)
                             (deriv (augend exp) var))
        (product? exp) (make-sum (make-product (multiplier exp)
                                               (deriv (multiplicand exp) var))
                                 (make-product (deriv (multiplier exp) var)
                                               (multiplicand exp)))
        :else (throw (ex-info "unknown expression type -- DERIV" exp))))

(deriv 1 'x)
;; => 0
(deriv '(+ x 3) 'x)
;; => (+ 1 0)
(deriv '(* x y) 'x)
;; => (+ (* x 0) (* 1 y))
(deriv '(* (* x y) (+ x 3)) 'x)
;; => (+ (* (* x y) (+ 1 0)) (* (+ (* x 0) (* 1 y)) (+ x 3)))

(defn make-sum [e1 e2]
  (cond (every? number? [e1 e2]) (+ e1 e2)
        (and (number? e1) (zero? e1)) e2
        (and (number? e2) (zero? e2)) e1
        :else (list '+ e1 e2)))

(defn make-product [e1 e2]
  (cond (every? number? [e1 e2]) (* e1 e2)
        (and (number? e1) (zero? e1)) 0
        (and (number? e1) (= 1 e1)) e2
        (and (number? e2) (zero? e2)) 0
        (and (number? e2) (= 1 e2)) e1
        :else (list '* e1 e2)))

(deriv 1 'x)
;; => 0
(deriv '(+ x 3) 'x)
;; => 1
(deriv '(* x y) 'x)
;; => y
(deriv '(* (* x y) (+ x 3)) 'x)
;; => (+ (* x y) (* y (+ x 3)))

(defn pair [x y]
  [x y])

(def x (pair 1 2))
(def y (pair 3 4))
(def z (pair x y))

(first (first z))
(first (second z))

(pair 1
      (pair 2
            (pair 3
                  (pair 4 nil))))
;; => [1 [2 [3 [4 nil]]]]

(defn scheme-list [fst & rst]
  (cond (nil? fst) nil
        (empty? rst) (pair fst nil)
        :else (pair fst (apply scheme-list rst))))

(scheme-list 1 2 4)

(defn print-scheme-list [sl]
  (if (nil? (second sl)) (print (first sl))
      (do (print (first sl))
          (print ", ")
          (print-scheme-list (second sl)))))

(comment (print-scheme-list (scheme-list 1 2 4)))

(pair 1 nil)

(first '())

(defn list-ref [items n]
  (if (= n 0) (first items)
      (list-ref (rest items) (- n 1))))

(list-ref (list 1 4 9 16 25) 3)

(defn length [items]
  (if (empty? items) 0
      (+ 1 (length (rest items)))))

(length (list 1 3 5 7))

(defn append [list1 list2]
  (if (empty? list1) list2
      (cons (first list1) (append (rest list1) list2))))

(append (list 1 4 9 16 25) (list 1 3 5 7)) ;; => (1 4 9 16 25 1 3 5 7)
(append (list 1 3 5 7) (list 1 4 9 16 25)) ;; => (1 3 5 7 1 4 9 16 25)

(defn scale-list [items factor]
  (if (empty? items) nil
      (cons (* (first items) factor)
            (scale-list (rest items) factor))))

(scale-list (list 1 2 3 4 5) 10)

(defn map' [f items]
  (if (empty? items) nil
      (cons (f (first items))
            (map' f (rest items)))))

(map' (fn [item] (* item 10)) (list 1 2 3 4 5))

(defn count-leaves [tree]
  (cond (nil? tree) 0
        (not (coll? tree)) 1
        (empty? tree) 0
        :else (+ (count-leaves (first tree))
                 (count-leaves (rest tree)))))

(length (list (list 1 2) 3 4)) ;; => 3
(count-leaves (list (list 1 2) 3 4)) ;; => 4

(length (pair (scheme-list 1 2) (scheme-list 3 4)))
(count-leaves (pair (scheme-list 1 2) (scheme-list 3 4)))

(seq? [1 2 3])

(defn scale-tree [tree factor]
  (map (fn [subtree]
         (if (coll? subtree) (scale-tree subtree factor)
             (* subtree factor)))
       tree))

(scale-tree '((1) 2 (3 4)) 3)
;; => ((3) 6 (9 12))

;; 2.2.3

(defn sum-odd-squares [tree]
  (cond (not (list? tree)) (if (odd? tree) (square tree) 0)
        (empty? tree) 0
        :else (+ (sum-odd-squares (first tree))
                 (sum-odd-squares (rest tree)))))

(sum-odd-squares '(1 2 3 4 5))
;; => 35

(defn even-fibs [n]
  (letfn [(next [k]
            (if (> k n) nil
                (let [f (fib k)]
                  (if (even? f) (cons f (next (inc k))) (next (+ k 1))))))]
    (next 0)))

(even-fibs 10)
;; => (0 2 8 34)

(map fib (list 1 2 3 4 5))

(defn filter' [pred s]
  (cond (empty? s) nil
        (pred (first s)) (cons (first s) (filter' pred (rest s)))
        :else (filter' pred (rest s))))

(filter odd? (list 1 2 3 4 5))
;; => (1 3 5)

(defn accumulate [op init s]
  (if (empty? s) init
      (op (first s)
          (accumulate op init (rest s)))))

(accumulate + 0 (list 1 2 3 4 5))
;; => 15

(defn enumerate-interval [low high]
  (if (> low high) nil
      (cons low (enumerate-interval (inc low) high))))

(enumerate-interval 4 10)
;; => (4 5 6 7 8 9 10)

(defn enumerate-tree [tree]
  (cond (not (coll? tree)) (list tree)
        (empty? tree) nil
        :else (concat (enumerate-tree (first tree))
                      (enumerate-tree (rest tree)))))

(enumerate-tree '(1 (2 3) 4 5 (6 7)))
;; => (1 2 3 4 5 6 7)


(defn even-fibs [n]
  (->> (enumerate-interval 0 n)
       (map fib)
       (filter even?)
       (accumulate cons '())))

(even-fibs 10)
;; => (0 2 8 34)

(defn sum-odd-squares [tree]
  (->> (enumerate-tree tree)
       (filter odd?)
       (map square)
       (accumulate + 0)))

(sum-odd-squares '(1 (2 3) 4 5 (6 7)))
;; => 84

;; quick prime - presumably earlier in the book, but haven't got there
;; uses 6k+-1 optimisation

(defn prime? [n]
  (cond (<= n 3) (> n 1)
        (or (zero? (mod n 2)) (zero? (mod n 3))) false
        :else (not (some (fn [i] (or (zero? (mod n i)) (zero? (mod n (+ i 2))))) (range 5 (int (Math/sqrt n)) 6)))))

(map (fn [j] (list 5 j)) (enumerate-interval 1 (- 5 1)))
;; => ((5 1) (5 2) (5 3) (5 4))



(map (fn [i] (map (fn [j] (list i j)) (enumerate-interval 1 (- i 1))))
     (enumerate-interval 1 10))
;; => (()
;;     ((2 1))
;;     ((3 1) (3 2))
;;     ((4 1) (4 2) (4 3))
;;     ((5 1) (5 2) (5 3) (5 4))
;;     ((6 1) (6 2) (6 3) (6 4) (6 5))
;;     ((7 1) (7 2) (7 3) (7 4) (7 5) (7 6))
;;     ((8 1) (8 2) (8 3) (8 4) (8 5) (8 6) (8 7))
;;     ((9 1) (9 2) (9 3) (9 4) (9 5) (9 6) (9 7) (9 8))
;;     ((10 1) (10 2) (10 3) (10 4) (10 5) (10 6) (10 7) (10 8) (10 9)))


(accumulate append
            nil
            (map (fn [i] (map (fn [j] (list i j)) (enumerate-interval 1 (- i 1))))
                 (enumerate-interval 1 10)))
;; => ((2 1)
;;     (3 1)
;;     (3 2)
;;     (4 1)
;;     (4 2)
;;     (4 3)
;;     (5 1)
;;     (5 2)
;;     etc.


(defn flatmap [f xs]
  (accumulate append nil (map f xs)))

(flatmap (fn [i] (map (fn [j] (list i j)) (enumerate-interval 1 (- i 1))))
         (enumerate-interval 1 10))

(defn prime-sum? [pair]
  (prime? (+ (first pair) (second pair))))

(defn make-pair-sum [pair]
  (list (first pair) (second pair) (+ (first pair) (second pair))))

(defn prime-sum-pairs [n]
  (->> (enumerate-interval 1 n)
       (flatmap (fn [i] (map (fn [j] (list i j)) (enumerate-interval 1 (- i 1)))))
       (filter prime-sum?)
       (map make-pair-sum)))

(prime-sum-pairs 6)
;; => ((2 1 3) (3 2 5) (4 1 5) (4 3 7) (5 2 7) (6 1 7) (6 5 11))

(defn nest-map [f enum s]
  (flatmap (fn [i] (map (fn [j] (f i j)) (enum i))) s))

(nest-map list #(enumerate-interval 1 (- % 1)) (enumerate-interval 1 10))

;; 2.3.3 sets

(defn element-of-set? [x set]
  (cond (empty? set) false
        (= x (first set)) true
        :else (element-of-set? x (rest set))))

(defn adjoin-set [x set]
  (if (element-of-set? x set) set
      (cons x set)))

(defn intersection-set [set1 set2]
  (cond (or (empty? set1) (empty? set2)) '()
        (element-of-set? (first set1) set2) (cons (first set1)
                                                  (intersection-set (rest set1) (set2)))
        :else (intersection-set (rest set1) set2)))

(defn element-of-set? [x set]
  (cond (empty? set) false
        (< x (first set)) false
        (= x (first set)) true
        :else (element-of-set? x (rest set))))

(defn intersection-set [set1 set2]
  (cond (or (empty? set1) (empty? set2)) '()
        (= (first set1) (first set2)) (cons (first set1) (intersection-set (rest set1) (rest set2)))
        (< (first set1) (first set2)) (intersection-set (rest set1) set2)
        (> (first set1) (first set2)) (intersection-set set1 (rest set2))))

(intersection-set (list 2 4 6 8) (list 3 4 5 6 7))

(defn entry [tree] (first tree))
(defn left-branch [tree] (second tree))
(defn right-branch [tree] (nth tree 2 nil))
(defn make-tree [entry left-branch right-branch]
  (list entry left-branch right-branch))

(defn element-of-set? [x set]
  (cond (nil? set) false
        (= x (entry set)) true
        (< x (entry set)) (element-of-set? x (left-branch set))
        (> x (entry set)) (element-of-set? x (right-branch set))))

(element-of-set? 6 '(7 (3 (1) (5)) (9 nil (11))))
;; => false
(element-of-set? 5 '(7 (3 (1) (5)) (9 nil (11))))
;; => true

(defn adjoin-set [x set]
  (cond (empty? set) (make-tree x nil nil)
        (= x (entry set)) set
        (< x (entry set)) (make-tree (entry set)
                                     (adjoin-set x (left-branch set))
                                     (right-branch set))
        (> x (entry set)) (make-tree (entry set)
                                     (left-branch set)
                                     (adjoin-set x (right-branch set)))))

(adjoin-set 1 '())
;; => (1 nil nil)
(adjoin-set 1 '(2))
;; => (2 (1 nil nil) nil)
(adjoin-set 3 '(2))
;; => (2 nil (3 nil nil))
(adjoin-set 1 '(7 (3 (1) (5)) (9 nil (11))))
;; => (7 (3 (1) (5)) (9 nil (11)))
(adjoin-set 4 '(7 (3 (1) (5)) (9 nil (11))))
;; => (7 (3 (1) (5 (4 nil nil) nil)) (9 nil (11)))

;; 2.3.4 huffman

(def huff '((A 8) (B 3) (C 1) (D 1) (E 1) (F 1) (G 1) (H 1)))

(defn make-leaf [symbol weight] (list 'leaf symbol weight))
(defn leaf? [x] (= 'leaf (first x)))
(defn symbol-leaf [x] (second x))
(defn weight-leaf [x] (nth x 2))

(leaf? (make-leaf 'A 8)) ;; => true
(symbol-leaf (make-leaf 'A 8)) ;; => A
(weight-leaf (make-leaf 'A 8)) ;; => 8

(defn symbols [node] (if (leaf? node) (list (symbol-leaf node)) (nth node 2)))
(defn weight [node] (if (leaf? node) (weight-leaf node) (nth node 3)))
(defn make-code-tree [left right]
  (list left right (append (symbols left) (symbols right)) (+ (weight left) (weight right))))

(defn left-branch [tree] (first tree))
(defn right-branch [tree] (second tree))

(defn make-huff-tree [codes]
  (let [[fst scd & rst] (sort-by weight codes)]
    (if (= 1 (count codes)) codes
        (make-huff-tree (cons (make-code-tree fst scd) rst)))))

(first (make-huff-tree (map #(apply make-leaf %) huff)))
;; =>
'(((leaf A 8)
   ((((leaf G 1)
      (leaf H 1)
      (G H) 2)
     ((leaf E 1)
      (leaf F 1)
      (E F) 2)
     (G H E F) 4)
    (((leaf C 1)
      (leaf D 1)
      (C D) 2)
     (leaf B 3)
     (C D B) 5)
    (G H E F C D B) 9)
   (A G H E F C D B) 17))

(defn decode-bits [bits tree]
  (cond (leaf? tree) (symbol-leaf tree)
        (zero? (first bits)) (decode-bits (rest bits) (left-branch tree))
        :else (decode-bits (rest bits) (right-branch tree))))

;; Redefining unordered list implementation of element of set
(defn element-of-set? [x set]
  (cond (empty? set) false
        (= x (first set)) true
        :else (element-of-set? x (rest set))))

(defn encode-bits [sym tree]
  (cond (leaf? tree) '()
        (element-of-set? sym (symbols (left-branch tree))) (cons 0 (encode-bits sym (left-branch tree)))
        :else (cons 1 (encode-bits sym (right-branch tree)))))

(encode-bits 'A (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => (0)
(encode-bits 'B (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => (1 1 1)
(encode-bits 'C (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => (1 1 0 0)
(encode-bits 'D (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => (1 1 0 1)
(encode-bits 'E (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => (1 0 1 0)
(encode-bits 'F (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => (1 0 1 1)
(encode-bits 'G (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => (1 0 0 0)
(encode-bits 'H (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => (1 0 0 1)


(decode-bits '(0) (first (make-huff-tree (map #(apply make-leaf %) huff))))       ;; => A
(decode-bits '(1 1 1) (first (make-huff-tree (map #(apply make-leaf %) huff))))   ;; => B
(decode-bits '(1 1 0 0) (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => C
(decode-bits '(1 1 0 1) (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => D
(decode-bits '(1 0 1 0) (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => E
(decode-bits '(1 0 1 1) (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => F
(decode-bits '(1 0 0 0) (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => G
(decode-bits '(1 0 0 1) (first (make-huff-tree (map #(apply make-leaf %) huff)))) ;; => H

;; 2.4 Complex Numbers

(defn attach-tag [type-tag contents] (list type-tag contents))
(def type-tag first)
(def contents second)

(defn rectangular? [z] (= 'rectangular (type-tag z)))
(defn polar? [z] (= 'polar (type-tag z)))

(def magnitude-polar first)
(def angle-polar second)

(defn real-part-rectangular [z] (first z))
(defn real-part-polar [z]
  (* (magnitude-polar z) (Math/cos (angle-polar z))))

(defn real-part [z]
  (cond (rectangular? z) (real-part-rectangular (contents z))
        (polar? z) (real-part-polar (contents z))
        :else (throw (ex-info "Unknown type -- REAL PART" z))))