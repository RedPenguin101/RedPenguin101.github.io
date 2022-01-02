% Mathematics for Programmers

# Parts

* Vectors and linear algebra: 
    * 2d 
    * 3d 
    * linear transformations
    * Matrices
    * Systems of linear equations
* Calculus
    * Rates of change
    * Motion simulation
    * Symbolic expressions
    * Force simulation
    * Physical system optimization
    * Fourier series
* Machine Learning
    * Fitting
    * Logistic regression
    * Neural networks

# Part 1: Vectors and Linear Algebra

* _Linear Algebra_ deals with computations on multidimensional data.
* A _vector_ is a point in multidimensional space.
* A _linear transformation_ is an `f :: v -> v` which preserves geometry. They can be represented as a _matrix_.
* A _system of linear equations_ tells us where dimensional bodies intersect in a vector space

## Chapter 2: 2d vectors

* A 2d space is a _plane_
* The reference point of a plane is the _origin_

A 2d vector can be thought of as a point on the plane, or as an 'arrow' starting at the origin.

### Arithmetic

Vector addition is pairwise. Vector addition is a _translation_ on a 2d plane. You can 'shift' drawings around a canvas using vector addition.

The length of a line described by a vector (or distance of point from origin) is obtained by the Pythagorean theorem.

```clojure
(defn vector-length [[x y]]
  (Math/sqrt (+ (square x) (square y))))
```

Scalar multiplication is `* :: s, v -> v`, where `s` is a real number. The _opposite_ vector to v is `-1 * v`.

Scalar multiplication _rescales_ vectors on a 2d plane. That is, you can make images bigger or smaller on a canvas.

The _displacement_ of two vectors `v` and `w` is a description of the line connecting them. It is obtained by `v-w`.

```clojure
(defn v+ [& vs] (apply map + vs))
(defn v* [s v] (map #(* s %) v))
```

### Trigonometry and Polar coordinates

If you know the _length_ of a vector, and its _direction_ (angle relative to axis) you can infer the coordinates. Put another way, the length and angle of a vector is an alternative and equivalent description of a vector: `[x,y] -> [l,d]`. The first method is called the _Cartesian Coordinate_ system, the second is the _Polar Coordinate_ system.

Given an angle `a`, the coordinates of vectors along the line drawn to that angle will have a constant ratio `r`. This ratio is called the _tangent_ of the angle. `tan(a) = r`

The _sine_ and _cosine_, given an angle `a`, give the vertical and horizontal distance covered relative to the length of the vector. `sin(a) = v/d`, `cos(a) = h/d`

```clojure
(defn polar->cartesian [[r a]]
  [(* r (Math/cos a)) (* r (Math/sin a))])
```

The _arcsine_, _arccosine_ and _arctangent_ are the _inverse_ of the trigonomic functions. That is, they translate from ratios of distances to angles. However, there are some gotchas here, and most programming languages provide an `atan2` (2-argument arctangent) function, which given an _x_ and _y_ coordinate will calculate the angle.

```clojure
(defn cartesian->polar [[x y]]
  [(vector-length [x y]) (Math/atan2 y x)])
```

Translations are easier with Cartesian, but rotation is easier with Polar. The rotation operation is `rotate :: angle, vector -> vector`. When stated in polar coordinates, rotation is simply the addition of the angle parameter to the angle element of the polar-vector.

```clojure
(defn vector-rotate [a v]
  (polar->cartesian (v+ [0 a] (cartesian->polar v))))
```

### Transformation of vector collections

The three operations (translate with addition, rescale with multiplication, and rotate with rotation) can easily be extended to vector sets:

```clojure
(defn xform-vectors [xform]
  (fn [vs x] (map #(xform x %) vs)))

(def translate (xform-vectors v+))
(def rescale   (xform-vectors v*))
(def rotate    (xform-vectors vector-rotate))

(-> dino-vectors
    (rotate (* 5 (/ Math/PI 3)))
    (translate [8 8]))
```

## 3d vectors

2d vectors can be described as `[x, y]`. 3d vectors can simply be described with `[x, y, z]`

### Arithmetic

Arithmetic in three (or more) directions is very similar to two dimensions. We therefore need only to generalize our functions from the previous section.

Our addition and scalar multiplication functions don't need to be changed to account for the 3rd dimension:

```clojure
(defn v+ [& vs] (apply map + vs))
(defn v* [s v]  (map #(* s %) v))

(v+ [1 2 3] [4 5 6]) ; (5 7 9)
(v* 10 [1 2 3]) ; (10 20 30)
```

To calculate line length of `x,y,z`, we can think of the problem as being decomposed into two right angle triangles, the first in the `x,y` plane, and the second adding in the `z` plane.

The first will have length `a`, and will be calculated as before: `a = sqrt(x^2 + y^2)`. The second will be `b = sqrt(z^2 + a^2)`. This expands to 

```
b = sqrt(z^2 + (sqrt(a^2 + b^2))^2)
b = sqrt(z^2 + a^2 + b^2)
```

Our function for `vector-length` will need to be generalized to handle higher dimensions.

```clojure
(defn square [x] (* x x))

(defn vector-length [v]
  (Math/sqrt (apply + (map square v))))
```

Things are more complicated for polar representations of higher dimensional vectors. While a single angle was sufficient for 2d, 2 angles are necessary for 3d. In general (n-1) angles will be necessary to describe a n-dimensional vector in polar form.

We will wait until we have a better tool, _vector products_ before tackling this.

### Dot products

There are two ways to multiply vectors together. The first is the `dot-product :: v1, v2 -> s` (aka _inner product_), where s is a scalar. The second is the `cross-product :: v1, v2 -> v3`. Each can help us reason about vectors in 3d.

You can think of the dot-product as representing _alignment_ of two vectors. A positive dot product means positive alignment: they are pointing in around the same direction. Negative means the opposite. A dot-product of 0 means the vectors are _perpendicular_. This is very useful because otherwise you would need trigonometry to calculate the relative angle of vectors.

```clojure
(defn dot-product [v1 v2]
   (apply + (map * v1 v2)))
```

Some properties of dot-products:

Vectors lying on different axes are perpendicular (e.g. `[0 3 0], [0 0 -5]`)

```
av dot w = v dot aw

a[x y] dot [n m]
= [ax ay] dot [n m]
= axn + aym
= [x y] dot [an am]
= [x y] dot a[n m]
```

The maximum value of the dot product is the product of the length of the vectors. The minimum value is the negative of the product of lengths. Thus if `v . w = len(v) * len(w)`, v and w are perfectly aligned, and if `v . w = -(len(v) * len(w))` then v and w are opposite.

In other words, the dot-product divided by the product of lengths gives the angle between two vectors: 1 means 0, -1 means 180 degrees, 0 means 90 degrees. This, you might notice, is very similar to the cosine:

```
cos(90) = 0
cos(0) = 1
cos(180) = -1
```

In other words: `cos(a) = u.v / len(u)*len(v)`. By applying `acos` we use the dot product to calculate angles between vectors, `a = acos((u.v)/(len(u)*len(v)))`

```clojure
(defn angle [v1 v2]
  (Math/acos 
    (/ (dot-product v1 v2)
       (* (vector-length v1) (vector-length v2)))))

(angle [1 2 2] [2 2 1]) ; 0.47588224966041665
(radian->degree (angle [1 2 2] [2 2 1])) ; 27.266044450732828
```

### Cross Product


