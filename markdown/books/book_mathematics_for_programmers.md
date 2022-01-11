% Mathematics for Programmers

# Parts

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

Arithmetic in three (or more) dimensions is very similar to two dimensions. We therefore need only to generalize our functions from the previous section.

Our addition and scalar multiplication functions don't need to be changed to account for the 3rd dimension:

```clojure
(defn v+ [& vs] (apply map + vs))
(defn v* [s v]  (map #(* s %) v))

(v+ [1 2 3] [4 5 6]) ; (5 7 9)
(v* 10 [1 2 3]) ; (10 20 30)
```

To calculate line length of `x,y,z`, we can think of the problem as being decomposed into two right angle triangles, the first in the `x,y` plane, and the second adding in the `z` plane.

The first will have length `a`, and will be calculated as before: $a = \sqrt{x^2 + y^2}$. The second will be $b = \sqrt{z^2 + a^2}$. This expands to 

$$
b = \sqrt{z^2 + \sqrt{x^2 + y^2}^2}
\newline
b = \sqrt{z^2 + x^2 + y^2}
$$

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

$av \cdot w = v \cdot aw$.

$$
av \cdot w \newline
= a(x,y) \cdot (n,m) \newline
= (ax,ay) \cdot (n,m) \newline
= axn + aym \newline
= (x,y) \cdot (an,am) \newline
= (x,y) \cdot a(n,m)
= v \cdot aw
$$

The maximum value of the dot product is the product of the length of the vectors. The minimum value is the negative of the product of lengths. Thus if $v \cdot w = |v||w|$, $v$ and $w$ are perfectly aligned, and if $v \cdot w = -|v||w|$ then $v$ and $w$ are opposite.

In other words, the dot-product divided by the product of lengths gives the angle between two vectors: 1 means 0, -1 means 180 degrees, 0 means 90 degrees. This, you might notice, is very similar to the cosine:

```
cos(90) = 0
cos(0) = 1
cos(180) = -1
```

Therefore $cos(a) = \frac{u \cdot v}{|u||v|}$. By applying `acos` we use the dot product to calculate angles between vectors, $a = acos\left(\frac{u \cdot v}{|u||v|}\right)$

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

**TODO:** More stuff here about how this translates to 3d spaces, how to think about it etc.

The formula for a 3d cross product is simple enough

$$
u \times v = ((u_y v_z - u_z v_y), (u_z v_x - u_x v_z), (u_x v_y - u_y v_x))
$$

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

We can then extract a _component_ from any vector, relative to a direction vector with $\frac{v \cdot d}{|d|}$. Our projection function is therefore simply `[component(v, v-up), component(v, v-right)]`

This will suffice to render the shape, but without distinguishing the faces of the shape with color, it will be hard for us to tell what it actually look like.

If we define a light source vector `v-light`, then the amount of light on the face of a shape (represented by a vector that is _normal_ to the face) will be the alignment (i.e. dot-product) of that vector with `v-light`.

The normal vector of a face described by 3 vectors _v1, v2, v2_ is

$$
normal(v_1,v_2,v_3) = (v_2-v_1) \times (v_3-v_1)
$$

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

**TODO:**

* Add pictures
* Write stuff about interpreting up, right, light vectors. How does this actually project? Need a mental model for it
* More about _why_ the z-filtering isn't good enough.
* Better shading - Use darken
* Tidy up code and better explain component, unit, etc.
* Axis label?

## Linear transformations

A _linear transformation_ is a vector transformation $T$ that preserves vector addition and scalar multiplication. That is, for any input vectors $u$ and $v$, $T(u)+T(v)=T(u+v)$, and $T(sv)=sT(v)$. The transformation $(x^2, y^2)$, for example, is not a linear transformation. Translations are _not_ (surprisingly). Scaling, reflection, projection, shearing, rotation all are.

A _linear combination_ of a collection of vectors is a sum of scalar multiples of them. For example: $3u-2v$.

$$
T(s_1v_1+s_2v_2+...+s_nv_n)=s_1T(v_1)+s_2T(v_2)+...+s_nT(v_n)
$$

The combination $0.5u+0.5v$ is the midpoint of a line connecting two points $u$ and $v$. In fact any $au+(1-a)v$ is on the line connecting two points.

Any vector can be decomposed into a linear combination of dimensional units. $(4,3,5)$ can be describes as the linear combination $4(1,0,0)+3(0,1,0)+5(0,0,1)$. These dimensional units are called the _standard basis_ for n-dimensional spaces, and denoted $e_1, e_2, e_3$. So the previous example can be written $4e_1+3e_2+5e_3$. This is just a notation change, but it is a useful one for linear transformations.

## Computing transformations with matrices

Using the ideas from the last chapter: Let $a$ be a linear transformation. All we know about $a$ is that $a(e_1)=(1,1,1), a(e_2)=(1,0,-1), a(e_3)=(0,1,1)$.

If $v=(-1,2,2)$ what is $a(v)$?

We can write $v=-e_1+2e_2+2e_3$.

$$
a(v)=a(-e_1+2e_2+2e_3)=-a(e_1)+2a(e_2)+2a(e_3) \newline
= -(1,1,1)+2(1,0,-1)+2(0,1,1)=(1,1,-1)
$$

What we have done here is express a transformation as how it impacts the standard basis: `[1 0 -1],[0 1 1],[-1 2 2]`. _Any_ transformation (in 3d) can be represented in this way, with these 9 numbers. We express this in a matrix for convenience (with the vectors 'flipped' vertical and set next to each other):

$$
\begin{bmatrix}
1 & 0 & -1 \\
0 & 1 & 2 \\
-1 & 1 & 2
\end{bmatrix}
$$

**TODO:** check the numbers here, looks off

### Matrix . Vector: Applying linear transformation

If a linear transformation $T$ can be represented as a matrix, then the application of the linear transformation $Tv$ is obtained by multiplying the matrix by the vector $v$.

The calculation is done by applying the dot-product of the rows of the matrix to the vector:

$$
\begin{bmatrix}
 0 & 2 &  1 \\
 0 & 1 &  0 \\
 1 & 0 & -1
\end{bmatrix}

\cdot

\begin{bmatrix}
 3 \\
-2 \\
 5
\end{bmatrix}

= 

\begin{bmatrix}
 (0,2,1) \cdot  (3,-2,5)\\
 (0,1,0) \cdot  (3,-2,5)\\
 (1,0,-1) \cdot  (3,-2,5)
\end{bmatrix}

=

\begin{bmatrix}
 1 \\
-2 \\
-2
\end{bmatrix}
$$

(Note that $Bv \ne vB$. Matrix multiplication is non-commutative)

### Matrix . Matrix: Composition of linear transformations

Let's say you have 1000 vectors, and you want to apply the same 1000 transformations to every vector. This takes 1000000 calculations. However, if you can compose the 1000 linear transformations into _one_ linear transformation (which by the properties of linear transformations we can), then we can apply the composition to the vector, and complete the task in 1001 calculations.

In other words, instead of $A(Bv)$, we want to do $(AB)v$. This, we can do. The above method of matrix multiplication extends for the second argument being a 3x3 matrix, just with 9 dot products, 3 for each 'column' of matrix $B$.

In general, a matrix that is $m\times n$ can be multiplied by a $p \times q$ matrix only when $n=p$. The resulting matrix is $n\times q$.

### Projection as a linear map from 3d to 2d

A _linear map_ (sometimes linear function) is also an operation on vectors. Like a linear transformation it preserves vector sums and scalar multiplications. But, unlike a linear transformation, it _doesn't_ need to preserve dimensions.

If _v_ is a 3d vector (i.e. $3 \times 1$ matrix), and $A$ is a $2 \times 3$ matrix, the result of $Av$ is a $2 \times 1$ matrix - or a 2d vector. We can use this to describe projection as a linear map.

Consider the rules we established before for projection.

```
Given v-up u=[u1 u2 u3] and v-right r=[r1 r2 r3]

project2d of v=[v1 v2 v3]
= [(v.u)/len(u), (v.r)/len(r)]
= [v.(u/len(u)), v.(r/len(r))]
```

Assuming u and r are already in normalized form (length=1) this can be expressed as matrix multiplication

$$
\begin{bmatrix}
 u_1 & u_2 & u_3 \\
 r_1 & r_2 & r_3 \\
\end{bmatrix}

\cdot

\begin{bmatrix}
 v_1 \\
 v_2 \\
 v_3
\end{bmatrix}

= 

\begin{bmatrix}
 (u_1,u_2,u_3) \cdot  (v_1,v_2,v_3) \\
 (r_1,r_2,r_3) \cdot  (v_1,v_2,v_3) \\
\end{bmatrix}
$$

What's more, transformation and projection can also be composed: $TP$ is a $3 \times 3$ matrix multiplied by a $3 \times 2$ matrix, yielding a $3 \times 2$ matrix.

### Translating vectors with matrices

We noted earlier that translations (shifting points around a plane) are not linear transformations. Another way of saying this is that there is no 2d matrix that, when applied to a linear combination of vectors, will preserve addition and multiplication. This is inconvenient because it means we can't use our matrix method to do this. 

There is a trick for doing translations in 2d: You can think of the 2d vectors as _3d_ vectors. If you have a 2d image, add a z-dimension with value 1 to all the vertices. Multiply that by the following 'magic matrix'. Then drop the z coordinate. You have translated the 2d image by `[3 1]`

$$
\begin{bmatrix}
 1 & 0 & 3 \\
 0 & 1 & 1 \\
 0 & 0 & 1 \\
\end{bmatrix}
$$

More generally

$$
\begin{bmatrix}
 1 & 0 & a \\
 0 & 1 & b \\
 0 & 0 & 1 \\
\end{bmatrix}
\cdot

\begin{bmatrix}
 x \\
 y \\
 1 \\
\end{bmatrix}

=

\begin{bmatrix}
 x+a \\
 y+b \\
 1 \\
\end{bmatrix}
$$

In 3 dimensions

$$
\begin{bmatrix}
 1 & 0 & 0 & a \\
 0 & 1 & 0 & b \\
 0 & 0 & 1 & c \\
 0 & 0 & 0 & 1 \\
\end{bmatrix}
\cdot

\begin{bmatrix}
 x \\
 y \\
 z \\
 1 \\
\end{bmatrix}

=

\begin{bmatrix}
 x+a \\
 y+b \\
 z+c \\
 1 \\
\end{bmatrix}
$$

## Higher dimensions

### Vector spaces

* _Linear Algebra_ is the concepts we have covered so far, generalized to any number of dimensions.
* A _vector space_ are a set of objects we can treat as vectors. e.g you can treat images like vectors.
* Vector spaces are denoted as $\R^n$, where $n$ is the number of dimensions
* The main operations we are interested in with vectors are vector addition and scalar multiplication
* To form a vector space, objects should have the following properties
    1. addition is commutative $u+v = v+u$
    2. addition is associative $(u+v)+w=u+(v+w)$
    3. scalar multiplication should be associative: $a(bv) = (ab)v$
    4. sm should have an identity $1 \cdot v = v$
    5. $av + bv = (a+b)v$
    6. $a(v+w)=av + aw$
* consequences:
    1. the _zero vector_ $0 \cdot v = 0$
    2. the _opposite vector_ $-1 \cdot v = -v$
* an $n \times m$ matrix can be treated as a $n \cdot m$ vector, and so an $\R^{nm}$ vector space.
* We saw before that a matrix can be considered a linear function. In vector space terms we would say $a: \R^n \rightarrow \R^m$. A $n \times m$ matrix $a$ is a mapping between vector spaces $\R^n$ and $\R^m$

**TODO**: add the $\{v\}$ syntax in here somewhere

### Vector subspaces and Span

* A _vector subspace_ is a vector space that exists in another vector space. The 2d vector space is a subspace of the 3d vector space, where $z=0$
* A set of vectors is a subspace only if it is _closed_ under operations. That is, you can't get to a vector not in the set by multiplying and adding vectors that _are_ in the set.
* A vector subspace containing a vector $v$ necessarily contains at least all scalar multiples of $v$
* A vector subspace containing vectors $v$ and $w$ contains all vectors in the _span_ of those vectors. _Span_ of a set of vectors is the set of linear combinations of those vectors.
* If $v$ and $w$ are not parallel, the span is the entire plane. Consider the standard basis vectors: Any point $(x,y)$ on the plane can be reached by $x \cdot (1,0) + y \cdot (0,1)$. And the standard basis can be reached from any non-parallel vectors $v,w$. So the standard basis vectors are in the vector space 

### Linear independence and basis

* In 3 dimensions, a subspace containing two non-parallel vectors spans a plane passing though the origin. A sub
* The concept of 'parallel' can be generalized to _linearly independent_. A collection of vectors is linearly dependent if any of its members can be obtained by a linear combination of any of the others.
* An n-dimensional space can have only n vectors that both span the entire dimension and still be linearly independent. That is what the concept of 'dimension' actually means.
* A linearly independent set of vectors that spans a vector space is call a _basis_ - hence the 'standard basis' we have used before.

* a Linear Function of the form $f(x) = ax+b$ can be described as a vector $(a,b)$. That is, linear functions are a vector space. We can prove that it's a 2d space by stating the basis $f(x)=x, g(x)=1$. Any linear function can be created by $af+bg$, where $a$ and $b$ are scalars.
* A Quadratic Function $f(x)=ax^2+bx+c$ is a 3d vector space with basis $f(x)=x^2, g(x)=x, h(x)=1$. Linear functions are a subspace of the quadratic functions vector space
* Generalizing, polynomial functions (linear combinations of powers of $x$, $f(x) = a_0 + a_1x + a_2x^2 +...+a_nx^n$) are vector spaces.

**TODO:** Color thing

## Solving systems of linear equations

* _SOLEs_ are problems of finding points where lines, planes, or higher dimensional vectors, intersect
* any equation representing a line is called a _linear equation_
* The _parametric formula_ definition of a line is $r(t)=a + t \cdot b$.
* The _standard form_ for a linear equation is $ax+by=c$
* Translation between the two is $(a_y-b_y)x-(a_x-b_x)y=a_yb_x-a_xb_y$
* A SOLE can be described in the form $M \cdot v=w$, where $M$ is a matrix, and $v,w$ are vectors.
* A Matrix that describes a SOLE with no unique solutions is called a _singular_ matrix. This is tied in with the rows and columns of the matrix being linearly dependent.


### Example: hit-detection

* Does the laser from the ship hit the asteroid?
* Does the line representing the laser intersect with any of the line segments defining the asteroid?
* The ships and Asteroids will be modeled as collections of vectors, defined from a 'center'. A `PolygonModel`
* The laser is a line segment starting at the 'tip' of the ship, and extending in the direction of the ship.
* The problem is now finding whether the laser intersects with an 'edge' of an asteroid. It involves finding the intersection of two lines (unless they are parallel), and determining whether the intersection occurs within both line segments.
* This is a _SOLE in two variables_.

### Formula for line

* 1d subspaces (that is, scalar multiples of a vector, $a \cdot v$) are sufficient to describe lines that pass through the origin ($0 \cdot v$), but not ones that don't.
* We need a second vector to translate to get a more general representation of a line: $u+t \cdot v$ Notice the subspace is bounded because there's no scaling of $u$ permitted. This makes it a definition of a line, not a plane.
* This is an alternative representation to $y=ax+b$. Or equivalently $ax+by=c$, the standard form. Each method has benefits and drawbacks. We'll be using the standard form mostly.
* If you have two vectors $u$ and $v$, the line connecting these two is $u-v$. This defines the direction of the line. To define the offset we can use either one of the points. So $r(t)=a+t(a-b)$. One of the benefits of this form is that it's trivial, given two points on the line, to write its definition.
* To rewrite this to standard form:

$$
r(t) = a + t (a-b) \\
(x,y)=(a_x, a_y)+t \cdot (a_x-b_x, a_y-b_y) \newline
x= a_x + t(a_x-b_x) \\
y= a_y + t(a_y-b_y) \\
t=\frac{x-a_x}{a_x-b_x}=\frac{y-a_y}{a_y-b_y} \\
(a_y-b_y)(x-a_x)=(y-a_y)(a_x-b_x) \\
a_yx-b_yx-a_xa_y+a_xb_y = a_xy-b_xy-a_xa_y+a_yb_x \\
(a_y-b_y)x+(a_xb_y-a_xa_y)=(a_x-b_x)y+(a_yb_x-a_xa_y) \\
(a_y-b_y)x-(a_x-b_x)y=a_yb_x-a_xa_y-a_xb_y+a_xa_y \\
(a_y-b_y)x-(a_x-b_x)y=a_yb_x-a_xb_y
$$

When you have two or more standard form linear equations, you can write them as a single matrix equation. For example

$$
x-y=0
x+2y=8
$$

Would be written in vector form

$$
x
\begin{pmatrix}
 1 \\
 1 \\
\end{pmatrix} + y
\begin{pmatrix}
 -1 \\
 2 \\
\end{pmatrix} =
\begin{pmatrix}
 0 \\
 8 \\
\end{pmatrix}

\newline
$$

And in matrix form

$$
\begin{pmatrix}
 1 & -1 \\
 1 & 2 \\
\end{pmatrix}

\begin{pmatrix}
 x \\
 y \\
\end{pmatrix} =

\begin{pmatrix}
 0 \\
 8 \\
\end{pmatrix}
$$

The goal is to find $x$ and $y$. 

Not every SOLE can be solved - that is, there is no point where the lines, planes, whatever, intersect. This is most obviously the case with parallel lines. However, if the lines are the _exact_ same line, then there are infinitely many points of intersection. A matrix that has no unique solution is called a _singular matrix_

### Generalizing to higher dimensions

These are when you have more than an $x$ and a $y$. Like with our 2d example, SOLEs in $n$ dimensions can be represented in matrix form: $M \cdot \vec{v} = \vec{a}$, were the dimension of $\vec{v}$ and $\vec{a}$ are $n$ dimensional, and $M$ is $n \times n$ dimensional.

For example consider three 3d planes described by

$$
x+y+z = -1 \newline
2y-z=4 \newline
x+z = 2
$$

What is the intersection of these planes?

We can write this in matrix form:

$$
\begin{pmatrix}
  1 & 1 & -1 \\
  0 & 2 & -1 \\
  1 & 0 & 1 \\
\end{pmatrix}
\begin{pmatrix}
  x \\
  y \\
  z \\
\end{pmatrix} =
\begin{pmatrix}
  -1 \\
  3  \\
  2  \\
\end{pmatrix}
$$

Running this through a SOLE solver, we get $(x,y,z)=(-1,3,3)$ as the point where all three planes intersect.

### Counting solutions

How do we know if a SOLE has a unique solution?

It should be clear that in a 3d space, at least 3 planes are required to arrive at a unique solution. With 2 planes, your solution is a 1d space of solutions (that is, a line). In general, you need $n$ equations to get a unique solution in $n$ dimensions. Or, an $n$ dimensional space with $m \le n$ equations leads to solutions in an $n-m$ vector space.

So in an $n$ dimensional space you need at _least_ $n$ equations to guarantee a solution. What if we have more that that? How can we tell if the SOLE has a unique solution?

We've said that vectors can be linearly independent. It's also the case that equations can be linearly independent (or dependent). A linearly independent equation can be thought of as an equation which, when added to the other equations in a SOLE, does _not_ reduce the dimensionality of the solution space. So if we have a SOLE in 3 dimensions that has 3 equations, there is a unique solution if and only if the 3 equations are linearly independent. If one is dependent on the others, the dimensionality will be reduced by at most 2.

The number of basis vectors in a space is sometimes called the _degrees of freedom_ of a vector space, because it 'frees' us to move in a new direction in the vector space. Each new linearly independent equation we add _removes_ a degree of freedom, and constrains the solution space. A space with 0 DOF is a point.

### Changing basis by solving SOLEs

Linear independence exists for both vectors and equations. What is the cnonection between them?

The connection is that solving SOLEs is equivalent to re-writing vectors in a different basis.

We've said that any independent vectors are a basis of a vector space. For example $\vec{v_1}=(1,1) and \vec{v_2}=(-1,1)$ are a basis of $R^2$. This implies that the point $(4,2)$ can be reached by some linear combination $a(1,1)+b(-1,2)=(4,2)$

This can be rewritten as a SOLE:

$$
\begin{pmatrix}
1 & -1 \\
1 & 1 \\
\end{pmatrix}
\begin{pmatrix}
c \\
d \\
\end{pmatrix} = 
\begin{pmatrix}
4 \\
2 \\
\end{pmatrix}
$$

The unique solution to this is $c=3, d=-1$. No other combination of c and d will get you to this point. You have 0 degrees of freedom in the solution space.

## Part 2: Calculus

* Calculus is the study of _continuous change_
* If we have an equation that calculates a cumulative value at a given point in time (say, distance), the _derivative_ of that function provides another equation that describes a _rate of change_ (speed) of that cumulative function at a point in time.
* The reverse of this operation (`f: rate-fn -> cumulative-fn`) is the _integral_
* Chapter 8 sets up intuition for these things
* Chapter 9 extends the ideas to multiple dimensions.
* Chapter 10 covers the mechanics, and introduces a tool for automating the process using symbolic programming
* Chapter 11 returns to multiple dimensions with the new tool
* Chapter 12 covers function optimization (find the input which returns the highest output).
* Chapter 13 covers Fourier Series: using integration to compare function similarity, with an application to sound waves.
