(ns symbolic-calculus)

(defn constant? [exp var]
  (and (or (number? exp) (symbol? exp)) (not= exp var)))

(defn is-variable? [exp var]
  (and (symbol? exp) (= exp var)))

(defn is-sum? [exp]
  (and (coll? exp) (= '+ (first exp))))

(defn is-product? [exp]
  (and (coll? exp) (= '* (first exp))))

(defn zero-number? [x] (and (number? x) (zero? x)))
(defn one-number? [x] (and (number? x) (= 1 x)))

(defn make-sum [a1 a2]
  (cond (zero-number? a1) a2
        (zero-number? a2) a1
        :else (list '+ a1 a2)))

(defn make-product [a1 a2]
  (cond (zero-number? a1) 0
        (zero-number? a2) 0
        (one-number? a1) a2
        (one-number? a2) a1
        :else (list '* a1 a2)))

(defn a1 [exp] (second exp))
(defn a2 [exp] (nth exp 2))
(defn m1 [exp] (second exp))
(defn m2 [exp] (nth exp 2))

(defn derive-function [exp var]
  (cond (constant? exp var) 0
        (is-variable? exp var) 1
        (is-sum? exp) (make-sum (derive-function (a1 exp) var)
                                (derive-function (a2 exp) var))
        (is-product? exp) (make-sum
                           (make-product (m1 exp) (derive-function (m2 exp) var))
                           (make-product (m2 exp) (derive-function (m1 exp) var)))))

(def foo '(+ (* a (* x x))
             (+ (* b x) c)))

(derive-function foo 'x)
(derive-function '(+ (* 3 (* x x))
                     (+ (* 2 x) 9)) 'x)

(with-bindings [a 1 b 2 x 6]
  (derive-function foo 'x))

(eval (let [a 1 b 2 x 5]
        (derive-function foo 'x)))

(derive-function '(+ (* 3 (* x x))
                     (+ (* 2 x) 9)) 'x)
;; => (+ (* 3 (+ x x)) 2)


(defmacro functionalize [exp]
  `(fn [x] ~exp))

;; (+ (* 3 (+ x x)) 2)
;; (fn [x] (+ (* 3 (+ x x)) 2))

(macroexpand-1 '(functionalize '(+ (* 3 (* x x))
                                   (+ (* 2 x) 9))))

((functionalize '(+ (* 3 (* x x))
                    (+ (* 2 x) 9)))
 10)