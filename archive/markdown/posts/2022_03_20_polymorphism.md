% Types of polymorphism

To cover

* parameterized
* ad-hoc
* compile time vs. run time
* duck typing
* generics
* **DONE** rank

## Rank polymorphism

[source](https://prl.ccs.neu.edu/blog/2017/05/04/rank-polymorphism/)

Rank polymorphism gives you code reuse on arguments of different dimensions (or 'rank').

Take a function which does linear interpolation for scalars.

```clojure
(defn lerp
  "Linear interpolation between two scalars x and y
  (x <= y) with weight w (between 0 and 1)"
  [x y w]
  (+ (* x (- 1 w)) (* y w)))

(lerp 1 5 0) => 1
(lerp 1 5 1) => 5
(lerp 1 5 0.5) => 3.0
```

[picture of number line]

We can extend this to the case of linear interpolation of two dimensional vectors:

```clojure
(defn v+ [v1 v2] (mapv + v1 v2))
(defn v* [v s]   (mapv * v (repeat s)))

(defn lerp2d [x y w]
  (v+ (v* x (- 1 w)) (v* y w)))

(lerp2d [1 2] [3 4] 0.5)
```

A rank polymorphic version of `lerp` would be one in which arguments of any dimension for x, y and w can be accepted.

Rank polymorphism is at the heart of array languages like APL. A function in APL like:

```apl
1 2 3 lerp 4 5 6
```

Doesn't care what its left and right arguments (here vectors `[1 2 3]` and `[4 5 6]` respectively) are.
