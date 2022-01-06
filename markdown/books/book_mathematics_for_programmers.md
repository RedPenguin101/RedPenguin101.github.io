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

* _Linear algebra_ deals with computations on multidimensional data.
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
(defn v- [v1 v2] (v+ v1 (v* -1 v2)))
```

### Trigonometry and Polar coordinates

If you know the _length_ of a vector, and its _direction_ (angle relative to axis) you can infer the coordinates. Put another way, the length (`r`) and angle (`a`) of a vector is an alternative and equivalent description of a vector: `[x,y] -> [r,a]`. The first method is called the _Cartesian Coordinate_ system, the second is the _Polar Coordinate_ system.

Given an angle `a`, the coordinates of vectors along the line drawn to that angle will have a constant ratio. This ratio is called the _tangent_ of the angle. `tan(a) = x/y`

The _sine_ and _cosine_, given an angle `a`, give the vertical and horizontal distance covered relative to the length of the vector. `sin(a) = y/r`, `cos(a) = x/r`

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

2d vectors can be described as `[x, y]`. 3d vectors can be described with `[x, y, z]`

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
b = sqrt(z^2 + (sqrt(x^2 + y^2))^2)
b = sqrt(z^2 + x^2 + y^2)
```

Our function for `vector-length` will need to be generalized to handle higher dimensions.

```clojure
(defn square [x] (* x x))

(defn vector-length [v]
  (Math/sqrt (apply + (map square v))))
```

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
(defn normalized-dot [v1 v2]
  (/ (dot-product v1 v2)
     (* (vector-length v1) (vector-length v2))))

(defn angle [v1 v2]
  (Math/acos (normalized-dot v1 v2))

(angle [1 2 2] [2 2 1]) ; 0.47588224966041665
(radian->degree (angle [1 2 2] [2 2 1])) ; 27.266044450732828
```

### Cross Product

The formula for a 3d cross product is simple enough

```
u x v = (uy vz - uz vy, uz vx - ux vz, ux vy - uy vx)
```

```clojure
(defn cross-product [[ux uy uz] [vx vy vz]]
  [(- (* uy vz) (* uz vy))
   (- (* uz vx) (* ux vz))
   (- (* ux vy) (* uy vx))])
```

The geometric description, what the cross product _means_, is trickier, and will take some preliminary work.

First, direction. The cross product of two 3d vectors returns a 3d vector that is _perpendicular_ two the two input vectors. (Perpendicular according to a _right hand rule_ orientation.)

The length of the cross product tells us something like how perpendicular the two _input_ vectors are to eachother. Specifically, it describes the area of the parallelogram with sides u and v.

### Projecting 3d shapes onto a 2d plane

First, we must choose what direction we are observing from, by defining vectors that are 'up' `v-up` and 'right' `v-right` from our perspective.

We can then extract a _component_ from any vector, relative to a direction vector with `dot-product(v, dir)/len(dir)`. Our projection function is therefore simply `[component(v, v-up), component(v, v-right)]`

This will suffice to render the shape, but without distinguishing the faces of the shape with color, it will be hard for us to tell what it actually look like.

If we define a light source vector `v-light`, then the amount of light on the face of a shape (represented by a vector that is _normal_ to the face) will be the alignment (i.e. dot-product) of that vector with `v-light`.

The normal vector of a face described by 3 vectors _v1, v2, v2_ is

```
n-vec = (v2-v1) x (v3-v1)
```

```clojure
(defn normal [[v1 v2 v3]]
  (cross-product (v- v2 v1) (v- v3 v1)))
```

A final helper we will use is `unit`, which normalizes the vector to an overall length of 1.

```clojure
(defn unit [v] (v* (/ 1 (vector-length v)) v))
```

Bringing all this together, we want a function that translates a 3d-shape to a description of a 2 dimensional projection, including appropriate shading.

```
project-shape :: 3d-polygon, v-up, v-right, v-light -> 2d-shapes
```

A 3d polygon is a sequence of 3 3-d vectors describing the triangular face of a 3d shape. `[v1 v2 v3]`

A 2d shape is a sequence of three 2d vectors (describing a triangle) with a number describing it's alignment (in 3d) to the light source.

```clojure
(defn project-2d [v, v-up, v-right]
  [(component v v-up) (component v v-right)])

(defn project-face [face v-up v-right v-light]
  (when (> (last (unit (normal face))) 0) 
    {:vertices    (map #(project-2d % v-up v-right) face)
     :light-align (normalized-dot (normal face) v-light)}))
```

This returns a 'shape', comprised of the 2d vertices of the shape and its alignment to the light source. The `when` statement here filters out all faces whose z-component of the normal is less than zero - i.e. is facing downwards and is therefore not visible from the 'overhead' perspective we will start by using. This will be insufficient later, but for now is fine.

All that's necessary now is to render the triangles using a drawing library, here [Clojure2D](https://github.com/Clojure2D/clojure2d).

```clojure
(defn color [light-align]
  (col/color :blue (+ 20 (* (- 1 light-align) 235))))

(defn s [p] (+ 500 (* 500 p)))
(defn flip-y [v] (update v 1 * -1))

(defn draw-triangle [canvas {:keys [vertices light-align]}]
  (c2d/with-canvas [c canvas]
    (c2d/set-color c (color light-align))
    (let [[[x1 y1] [x2 y2] [x3 y3]] (map flip-y vertices)]
      (c2d/triangle c (s x1) (s y1) (s x2)
                    (s y2) (s x3) (s y3)))))

(defn render-triangles [triangles]
  (let [canvas (c2d/canvas 1000 1000)]
    (doseq [t triangles] (draw-triangle canvas t))
    (c2d/show-window canvas "Drawing")))
```

TODO: 
* Add pictures
* Write stuff about interpretting up, right, light vectors
* filter faces that are not facing the _camera_
* Better shading - alpha isn't correct. Include better use of negative light-aligns
* Better implementation of camera? Don't really need 2 vectors surely. Something to do with view stretching?
* Tidy up code and better explain component, unit, etc.
* Axis label

## Linear transformations

A Linear Transfomation is a vector transformation _T_ that preserves vector addition and scalar multiplication. That is, for any input vectors _u_ and _v_, `T(u)+T(v)=T(u+v)`, and `T(sv)=sT(v)`. The transformation `[x^2, y^2]`, for example, is not a linear transformation. Translations are _not_ (surprisingly). Scaling, reflection, projection, shearing, rotation all are.

A _linear combination_ of a collection of vectors is a sum of scalar multiples of them. For example: _3u-2v_. Since these are linear transformations, addition and scalar multiplication are preserved.

```
T(s1v1+s2v2+...+snvn)=s1T(v1)+s2T(v2)+...+snT(vn)
```

The combination _0.5u+0.5v_ is the midpoint of a line connecting two points _u_ and _v_. In fact any _au+(1-a)v_ (where _a<=1_) is on the line connecting two points.

Any vector can be decomposed into a linear combination of dimensional units. `[4,3,5]` can be describes as the linear combination `4[1 0 0]+3[0 1 0]+5[0 0 1]`. These dimensional units are called the _standard basis_ for n-dimensional spaces, and denoted _e1, e2, e3_. So the previous example can be written `4e1+3e2+5e3`. This is just a notation change, but it is a useful one for linear transformations.

## Computing transformations with matrices

Using the ideas from the last chapter: Let _a_ be a linear transformation. All we know about _a_ is that `a(e1)=[1 1 1], a(e2)=[1 0 -1], a(e3)=[0 1 1]` If `v=[-1,2,2]` what is `a(v)`?

We can write _v_ as `-e1+2e2+2e3`. 

```
a(v)=a(-e1+2e2+2e3)=-a(e1)+2a(e2)+2a(e3)
= -[1 1 1]+2[1 0 -1]+2[0 1 1]=[1 1 -1]
```

What we have done here is express a transformation as how it impacts the standard basis: `[1 0 -1],[0 1 1],[-1 2 2]`. _Any_ transformation (in 3d) can be represented in this way, with these 9 numbers. We express this in a matrix for convenience (with the vectors 'flipped' vertical and set next to each other):

```
[ 1 0 -1
  0 1  2
 -1 1  2]
```

### Matrix . Vector: Applying linear transformation

If a linear transformation _T_ can be represented as a matrix, then the application of the linear transformation _Tv_ is obtained by multiplying the matrix by the vector _v_.

```
[0 2  1  [ 3
 0 1  0   -2
 1 0 -1]   5]
```

The calculation is done by applying the dot-product of the rows of the matrix to the vector:

```
[0 2 1].[3 -2 5] = 1
[0 1 0].[3 -2 5] = -2
[1 0 -1].[3 -2 5] = -2
```

(Note that _Bv != vB_. Matrix multiplication is non-commutative)

### Matrix . Matrix: Composition of linear transformations

Let's say you have 1000 vectors, and you want to apply the same 1000 transformations to every vector. This takes 1000000 calculations. However, if you can compose the 1000 linear transformations into _one_ linear transformation (which by the properties of linear transformations we can), then we can apply the composition to the vector, and complete the task in 1001 calculations.

In other words, instead of _A(Bv)_, we want to do _(AB)v_. This, we can do. The above method of matrix multiplication extends for the second argument being a 3x3 matrix, just with 9 dot products, 3 for each 'column' of matrix _B_.

In general, a matrix that is _mxn_ can be multiplied by a _pxq_ matrix only when _n=p_. The resulting matrix is _nxq_.

### Projection as a linear map from 3d to 2d

A _linear map_ (sometimes linear function) is also an operation on vectors. Like a linear transformation it preserves vector sums and scalar multiplications. But, unlike a linear transformation, it _doesn't_ need to preserve dimensions.

If _v_ is a 3d vector (i.e. _3x1_ matrix), and _A_ is a _2x3_ matrix, the result of _Av_ is a _2x1_ matrix - or a 2d vector. We can use this to describe projection as a linear map.

Consider the rules we established before for projection.

```
Given v-up u=[u1 u2 u3] and v-right r=[r1 r2 r3]

project2d of v=[v1 v2 v3]
= [(v.u)/len(u), (v.r)/len(r)]
= [v.(u/len(u)), v.(r/len(r))]
```

Assuming u and r are already in normalized form (length=1) this can be expressed as matrix multiplication

```
[u1 u2 u3  [v1    [[u1 u2 u3].[v1 v2 v3]
 r1 r2 r3]  v2  =  [r1 r2 r3].[v1 v2 v3]]
            v3]
```

What's more, transformation and projection can also be composed: `TP` is a _3x3_ matrix multiplied by a _3x2_ matrix, yielding a _3x2_ matrix.

### Translating vectors with matrices

We noted earlier that translations (shifting points around a plane) are not linear transformations. Another way of saying this is that there is no 2d matrix that, when applied to a linear combination of vectors, will preserve addition and multiplication.

This is inconvenient because it means we can't use our matrix method to do this. 

There is a trick for doing translations in 2d: You can think of the 2d vectors as _3d_ vectors, and then, magically, you _can
