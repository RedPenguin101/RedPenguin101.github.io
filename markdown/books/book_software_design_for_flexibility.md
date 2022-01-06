% Software Design for Flexibility, by Sussman and Hanson

# Content

* Flexibility in Nature and Design
* DSLs
    * Combinators
    * Regular expressions
    * Wrappers
    * Domain abstraction
* Pattern Matching
* Evaluation
* Layering
* Propagation

# Introduction: Flexibility in nature and design

Making a design decision that backs you into a corner is common. This book contains code organization methods to mitigate that.

The goal is to build systems that have acceptable behavior over a larger class of situations than was initially anticipated. Or, _evolvable_ systems than can be changed to meet new requirements with only minor modifications to the code. An _additive_ approach is ideal: when a new requirement comes up, you don't need to change existing code, only add new code. But often, poor decisions about 

A typical approach to design is specification. However this does not scale well as the complexity of the system increases. Specifying a program to play a _legal_ game of chess is trivial. Specifying one that plays a _good_ game of chess is very hard.

We can take inspiration in design from biology. Using a very small program (About 1GB of ROM, for a full genetic package), biology builds incredibly robust systems. Admittedly with a long boot-up period of several months. They have high _redundancy_ - they have more capacity than is required to do the job - and they high _degeneracy_ - there are usually several ways to satisfy a requirement.

# Domain Specific Languages

A DSL is an abstraction in which the nouns and verbs of the language are directly related to the problem domain. The language should be of the domain _generally_, not just what is required to do the job at hand. Thus it provides the framework for a variety of programs that share the domain of discourse.

## Combinators

Biological systems achieve much of their flexibility through the use of general parts (cells) that are dynamically configured to meet their environments. Computational designs have generally relied on _hierarchical_ systems of dependent parts.

If our goal is to have a system of small parts (functions) that can be mixed-and-matched, we need the means of combination of these parts to be generally available. Here we build a _system of combinators_ to achieve this. A _combiner_ is a function that takes two or more functions, and returns another function that applies each of the input in a particular way.

Our first attempt is the simplest **compose**: we have a procedure `f` that takes 1 argument, and a procedure `g` that takes `n` arguments. We want to compose these together into a procedure that takes `n` arguments, applies `g`, then applies `f`.[^1]

[^1]: More specifically, the _codomain_ of `g`, the set of possible return values, must be a subset of the _domain_, allowable input values, of `f`.

```clojure
(defn compose [f g]
  (fn [& args]
    (f (apply g args))))

((compose #(list 'foo %)
          #(list 'bar %))
 'z)
;; => (foo (bar z))
```

A second combiner is **parallel-combine**. Given _n_ arguments, we apply `f` to those arguments, `g` to those same arguments, and pass the return of both functions to `h`.

```
Given:
  f :: n -> 1
  g :: n -> 1
  h :: 2 -> 1

  h.(f,g) :: n -> 1
```

```clojure
(defn parallel-combine [h f g]
  (fn [& args]
    (h (apply f args) (apply g args))))

((parallel-combine list 
                   (fn [x y z] (list 'foo x y z))
                   (fn [u v w] (list 'bar u v w)))
 'a 'b 'c)
;; => ((foo a b c) (bar a b c))
```

For example, if you are writing image recognition software that identifies vegetables. You write a procedure `f` that recognizes color, a procedure `g` that recognizes shape, and a procedure `h` that takes the color and shape of an image and decides what vegetable it is.

A third combiners is **spread-combine**. Given _n+m_ arguments, we pass _n_ to `f`, _m_ to `g`, and the results of both to `h`.

```
Given:
  f :: n -> 1
  g :: m -> 1
  h :: 2 -> 1

  h.(f,g) :: n+m -> 1
```

We come to a problem we haven't had till now: we need to be able to detect how many arguments `f` accepts to determine how many arguments to assign to it. An implementation of the relevant functions are at the end of this file.

```clojure
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
```

There is a secondary problem: `spread-combine` relies on being able to get the arity of its input functions. But all of the combiners we've written so far return procedures that have an argument list `& args`. They 'advertise' infinite arity. This means that the results of our combiners can't themselves be passed to `spread-combine`! Clearly this is death to our goal.

We therefore want the return value of our combiners to 'advertise' their arities. For good measure, we will also check that we have the right number of arguments going in.

```clojure
(defn spread-combine [h f g]
  (let [n (get-arity f) m (get-arity g)
        arity (+ n m)]
    (restrict-arity
      (fn the-combination [& args]
        (assert (= (count args) arity))
        (h (apply f (take n args))
           (apply g (drop n args))))
      arity)))

(def sc2-check (spread-combine list
                 (fn [x y] (list 'foo x y))
                 (fn [u v w] (list 'bar u v w))))

(get-arity sc2-check) ; 5

(sc2-check 'a 'b 'c 'd 'e) ; ((foo a b) (bar c d e))

(sc2-check 'a 'b 'c 'd)
;; => Execution error (AssertionError) at combinators$spread_combine2$the_combination__7381/doInvoke (REPL:88).
;;    Assert failed: (= (count args) arity)
```

One restriction of our implementation of our combiners is that the 'second level' (`h` in spread and parallel, `g` in compose) are limited to arity 1 functions, because the preceding functions return single value. If we have a _multiple-value return mechanism_ we can get around this. Scheme has one, Clojure doesn't. Or rather, we can just return a list and get the same functionality I think.

We'll use this to separate `spread-combine` into two components: `spread-apply` and then just the compose we've already seen.

`spread-apply`

```
Given:
  f :: n -> p
  g :: m -> q

  spread-apply = (f@g) :: n+m -> p+q

  h :: p+q -> 1
  spread-combine = h.(f@g) :: p+q -> 1
```

## Regular Expressions

## Wrappers

# Exercises

## Chapter 2

### 2.1 Arity Repair

Rewrite `compose` and `parallel-combine` so they have the same arity-checking properties as `spread-combine`.

```clojure
(defn compose [f g]
  (let [m (get-arity f)]
    (assert (= 1 (get-arity g)))
    (restrict-arity
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
    (restrict-arity
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
```

### 2.2: Arity extension TODO

Our approach to arities effectively assumes that procedures have one and only one arity. Design and implement a system for dealing with multiple arity functions

# Appendix

## Arity functions

Our public functions here are `get-arity` and `restrict-arity`. Arity is detected (imperfectly) by reflection. However we can 'tag' specific functions with arities by putting them in a hash-table. This will be necessary for our compositions, which by nature will not be able to advertise arity by reflection.

```clojure
(def inf ##Inf) ;; because double-hash can mess with linters
(defonce arity-table (atom {})) ;; where our manually tagged arities live

(defn count-args [arg-list]
  (if (some #(= "clojure.lang.ISeq" (name %)) arg-list)
    inf (count arg-list)))
;; The reason we need this is do deal with variadics. If a function
;; has an argument list that has `& args`, then the arity is infinite.

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
  (or (get @arity-table f) ;; check the table first
      (first (procedure-arities f))))

(defn restrict-arity [proc nargs]
  (swap! arity-table assoc proc nargs)
  proc)
```
