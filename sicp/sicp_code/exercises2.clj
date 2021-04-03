(ns sicp-code.exercises2)

;; utils defined earlier in the book

(defn average [x y]
  (float (/ (+ x y) 2)))

(defn difference [x y]
  (Math/abs (- x y)))

(defn gcd [a b]
  (if (or (not (integer? a)) (not (integer? b)))
    (throw (IllegalArgumentException. "gcd requires two integers"))
    (loop [a (Math/abs a) b (Math/abs b)]
      (if (zero? b) a
          (recur b (mod a b))))))


(defn make-rat [n d] (list n d))
(defn numer [x] (first x))
(defn denom [x] (second x))
(defn print-rat [x] (println (str (numer x) "/" (denom x))))

(defn add-rat [x y]
  (make-rat (+ (* (numer x) (denom y))
               (* (numer y) (denom x)))
            (* (denom x) (denom y))))

(defn sub-rat [x y]
  (make-rat (- (* (numer x) (denom y))
               (* (numer y) (denom x)))
            (* (denom x) (denom y))))

(defn mul-rat [x y]
  (make-rat (* (numer x) (numer y))
            (* (denom x) (denom y))))

(defn div-rat [x y]
  (make-rat (* (numer x) (denom y))
            (* (denom x) (numer y))))

(defn equal-rat? [x y]
  (= (* (numer x) (denom y))
     (* (denom x) (numer y))))

"Improved make-rat with simplification"

(defn make-rat [x y]
  (let [g (gcd x y)]
    (list (/ x g) (/ y g))))

"2.1 : Define a better version of make-rat that handles both positive and negative arguments. Make-rat should normalize the sign so that if the rational number is positive, both the numerator and denominator are positive, and if the rational number is negative, only the numerator is negative."

(defn make-rat [x y]
  (let [g (gcd x y) x (/ x g) y (/ y g)]
    (if (pos? y) (list x y)
        (list (- x) (- y)))))

"2.2 Exercise 2.2.  Consider the problem of representing line segments in a plane. Each segment is represented as a pair of points: a starting point and an ending point. Define a constructor make-segment and selectors start-segment and end-segment that define the representation of segments in terms of points. Furthermore, a point can be represented as a pair of numbers: the x coordinate and the y coordinate. Accordingly, specify a constructor make-point and selectors x-point and y-point that define this representation. Finally, using your selectors and constructors, define a procedure midpoint-segment that takes a line segment as argument and returns its midpoint (the point whose coordinates are the average of the coordinates of the endpoints). To try your procedures, you'll need a way to print points:"

(defn make-point [x y] (list x y))
(defn x-point [p] (first p))
(defn y-point [p] (second p))
(defn print-point [point] (println (str "[" (x-point point) ", " (y-point point) "]")))

(defn make-segment [p1 p2] (list p1 p2))
(defn start-segment [p] (first p))
(defn end-segment [p] (second p))

(make-segment (make-point 2 2) (make-point 3 3))

(defn midpoint-segment [seg]
  (make-point (average (x-point (start-segment seg))
                       (x-point (end-segment seg)))
              (average (y-point (start-segment seg))
                       (y-point (end-segment seg)))))

(print-point (midpoint-segment (make-segment (make-point 2 2)
                                             (make-point 3 3))))

"2.3.  Implement a representation for rectangles in a plane. (Hint: You may want to make use of exercise 2.2.) In terms of your constructors and selectors, create procedures that compute the perimeter and the area of a given rectangle."

"Bearing in mind we only have pairs to represent data at this point"

(defn make-rectangle [upper-left-point lower-right-point]
  (list upper-left-point lower-right-point))
(defn upper-left [rectangle] (first rectangle))
(defn lower-right [rectangle] (second rectangle))

(defn height [rect]
  (difference (y-point (upper-left rect))
              (y-point (lower-right rect))))

(defn width [rect]
  (difference (x-point (upper-left rect))
              (x-point (lower-right rect))))

"Now implement a different representation for rectangles. Can you design your system with suitable abstraction barriers, so that the same perimeter and area procedures will work using either representation?"

"We'll represent a rectangle as a single segment from upper left to lower right"

(defn make-rectangle [ulp lrp]
  (make-segment ulp lrp))

(defn height [rect]
  (difference (y-point (start-segment rect))
              (y-point (end-segment rect))))

(defn width [rect]
  (difference (x-point (start-segment rect))
              (x-point (end-segment rect))))

"A wish thinking implementation of perimeter would be"

(defn perimeter [rect]
  (* 2 (+ (width rect)
          (height rect))))

(defn area [rect]
  (* (width rect)
     (height rect)))

(area (make-rectangle (make-point 0 0) (make-point 10 10)))
;; => 100

(perimeter (make-rectangle (make-point 0 0) (make-point 10 10)))
;; => 40

"Exercise 2.4.  Here is an alternative procedural representation of pairs. For this representation, verify that (car (cons x y)) yields x for any objects x and y.

(define (cons x y)
  (lambda (m) (m x y)))

(define (car z)
  (z (lambda (p q) p)))

What is the corresponding definition of cdr?"

(defn cons' [x y]
  (fn [m] (m x y)))

(defn car [z]
  (z (fn [p q] p)))

(car (cons' 2 3))

(comment "Substitution of car"
         (car (cons' 2 3))
         "Sub out cons"
         (car (fn [m] (m 2 3)))
         "sub out car"
         ((fn [m] (m 2 3)) (fn [p q] p))
         "sub in m"
         ((fn [p q] p) 2 3)
         ((fn [p q] p) 2 3)
         2)

(defn cdr [z]
  (z (fn [p q] q)))

(cdr (cons' 2 3))

(comment "Substitution of car"
         (cdr (cons' 2 3))
         "Sub out cons"
         (cdr (fn [m] (m 2 3)))
         "sub out car"
         ((fn [m] (m 2 3)) (fn [p q] q))
         "sub in m"
         ((fn [p q] q) 2 3)
         ((fn [p q] 3) 2 3)
         3)

((fn [z] (z +)) (cons' 2 3))

(defn apply-operation [f] (fn [z] (z f)))

((apply-operation *) (cons' 2 3))
(def car (apply-operation (fn [p q] p)))
(def cdr (apply-operation (fn [p q] q)))

(car (cons' 2 3))
(cdr (cons' 2 3))
((apply-operation +) (cons' 2 3))
((apply-operation *) (cons' 2 3))
((apply-operation difference) (cons' 2 3))

"Exercise 2.5.  Show that we can represent pairs of nonnegative integers using only numbers and arithmetic operations if we represent the pair a and b as the integer that is the product 2a 3b. Give the corresponding definitions of the procedures cons, car, and cdr. "

(defn cons' [a b]
  (* (bigint (Math/pow 2 a)) (bigint (Math/pow 3 b))))

(bigint (Math/pow 2 991))

(Math/pow 2 5)
(Math/pow 3 6)
(map #(Math/pow 3 %) (range 10))

(defn log [base num]
  (/ (Math/log num)
     (Math/log base)))

(log 3 1023)

(defn cdr [x]
  (if (zero? (mod x 2))
    (cdr (/ x 2))
    (int (log 3 x))))

(defn car [x]
  (if (zero? (mod x 3))
    (car (/ x 3))
    (int (log 2 x))))

(cdr (cons' 5 6))
(car (cons' 5 6))

(Math/pow 2 100)

"Exercise 2.6.  In case representing pairs as procedures wasn't mind-boggling enough, consider that, in a language that can manipulate procedures, we can get by without numbers (at least insofar as nonnegative integers are concerned) by implementing 0 and the operation of adding 1 as

(define zero (lambda (f) (lambda (x) x)))

(define (add-1 n)
  (lambda (f) (lambda (x) (f ((n f) x)))))

This representation is known as Church numerals, after its inventor, Alonzo Church, the logician who invented the calculus.

Define one and two directly (not in terms of zero and add-1). (Hint: Use substitution to evaluate (add-1 zero)). Give a direct definition of the addition procedure + (not in terms of repeated application of add-1)."

(def zero (fn [f] (fn [x] x)))
(defn add-1 [n]
  (fn [f] (fn [x] (f ((n f) x)))))

(comment
  "First recognize we can rewrite zero as"
  (defn zero [f] identity)
  "i.e. just returns the identity function and (zero f) => identity for any f"
  (add-1 zero)
  (fn [f] (fn [x] (f ((zero f) x))))
  (fn [f] (fn [x] (f (identity x))))
  (fn [f] (fn [x] (f x)))

  (add-1 one)
  (fn [f] (fn [x] (f ((one f) x))))
  (fn [f] (fn [x] (f ((fn [x] (f x)) x))))
  (fn [f] (fn [x] (f (f x))))

  (add-1 two)
  (fn [f] (fn [x] (f ((two f) x))))
  (fn [f] (fn [x] (f ((fn [x] (f (f x))) x))))
  (fn [f] (fn [x] (f (f (f x))))))

(defn zero [f] (fn [x] x))
(defn one [f] (fn [x] (f x)))
(defn two [f] (fn [x] (f (f x))))
(defn three [f] (fn [x] (f (f (f x)))))

(defn church [n] (fn [f] (fn [x] (last (take (inc n) (iterate f x))))))

((one dec) 5)
(((church 0) inc) (church 5))


(defn church-to-int [n]
  ((n inc) 0))

(church-to-int three)