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
