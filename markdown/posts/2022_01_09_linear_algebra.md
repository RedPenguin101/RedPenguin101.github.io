% Linear Algebra: Vector spaces

## Vector spaces

A vector space is a set of elements (vectors) that have _addition_ and _scalar multiplication_ operations. Applying these operations is called a _linear combination_: $a\vec{v}+b\vec{u}=\vec{w}$

The space must be _closed_ under these operations. Under any linear combination, the resulting vector must also be in the vector space. You can't 'escape' the space.

To qualify as a vector space, the elements in the set must have the following properties:

* addition operation is commutative, associative and have an identity and inverse
* scalar multiplication operation is compatible[^1], and have an identity
* vector addition and scalar addition are both distributive with respect to scalar multiplication.[^2]

[^1]:$a(b\vec{v})=(ab)\vec{v}$
[^2]:$a(\vec{v}+\vec{w})=a\vec{v}+a\vec{w}$ and $(a+b)\vec{v}=a\vec{v}+b\vec{v}$

## Span

Given a set of vectors, the _span_ of those vectors is defined as the set of all linear combinations of those vectors. So if your 'starting set' of $\{\vec{u}, \vec{v}, \vec{w}\}$, the span of that set is every vector $a\vec{u}+b\vec{v}+c\vec{w}$. We can denote it as $span(\{\vec{u}, \vec{v}, \vec{w}\})$

## Vector Subspaces

A vector space that is 'within' another vector space of the same dimension is called a _vector subspace_. For example the 2D plane is a subspace of the 3D vector space (if 2d vectors are thought of as being in the form $(x, y, 0)$). Notice that using this representation you can't 'escape' the space by linear combination: you can never get to a $z$ that isn't zero.

Similarly, the set of 2d vectors with $(x,0)$ is a vector subspace of $R^2$. This can be thought of as a _line_, or specifically the real number line.

Consider a set containing a single non-zero vector, $\{\vec{v}\}, \vec{v} \in R^2, \vec{v} \ne \vec{0}$. This clearly doesn't form a subspace on it's own because you can multiply it by a scalar and get a vector that is not $\vec{v}$, and therefore not in the set.

What about the span of that set? That is, all linear combinations $a\vec{v}$. This _does_ form a vector subspace of $R^2$, since you can't 'escape' from it. You only have one specific vector to work with, so any addition of that vector with itself will be a scalar multiplication, which by definition is in the subspace. Again, we are back at a line: one dimension.

Notice that, since $0 \in R, 0 \cdot \vec{v} = \vec{0}$, or the _origin_, is always in the span.

What about if we introduce another vector to our starting set: $\{\vec{v}, \vec{w}\}, \text{ where } \vec{v},\vec{w} \in R^2$?

Now the span of the set is every linear combination $a\vec{v}+v\vec{w}$.

Provided that $\vec{v}$ and $\vec{w}$ are not _parallel_, the span is the entire plan of $R^2$. Consider the set $\{(1,0),(0,1)\}$. The linear combinations of these are every vector $a(1,0)+b(0,1)=(a,b)$, which is $R^2$ by definition.

## Linear independence

So far we have seen that:

* The span of two 2d vectors describes $R^2$
* The span of a single 2d vector describes a line that passes through the origin.

What happens when we extend this to $R^3$?

A single 3d vector still describes a line in 3d space, passing through the origin. Two vectors describe a _plane_ in 3d passing through the origin, provided that the first is not a scalar combination of the other: $\vec{v} \ne a\vec{w}$.

What about 3 vectors? Does this describe $R^3$? For $R^2$ two vectors did if they were not parallel: a scalar combination of each other. For higher dimensions we need a  more general concept of parallelism.

We've defined parallelism of two vectors to mean that $\vec{v} \ne a\vec{w}$.

Generalizing the concept to more to three vectors, if $\vec{u} \ne a\vec{v} + b\vec{w}$, then the three vectors are said to be _linearly independent_. And to $n$ vectors, $\vec{v_n} \ne a_0\vec{v_0} + a_1\vec{v_1} + ... + a_{v_{n-1}}\vec{v_{n-1}}$. Or, you can't arrive at any one vector by a linear combination of the other vectors.

So in 3 dimensions, 3 vectors span $R^3$ if they are linearly independent.

Take the set $\vec{u}, \vec{v}, \vec{w} = (1,0,0),(0,1,0),(4,3,0)$. These vectors are not linearly independent, because $4\vec{u}+3\vec{v}=\vec{w}$. Therefore $span(\{\vec{u},\vec{v},\vec{u}\})$ is not a description of $R^3$.

## Basis and dimension

A linearly independent set of vectors that spans an entire vector space is called a _basis_.

A special case of this is the _standard basis_. This occurs when the vectors of the basis each have exactly one 'one'. For example $\{(1,0),(0,1)\}$ is the standard basis of $R^2$. $\{(1,0,0),(0,1,0),(0,0,1)\}$ is the standard basis of $R^3$. This is usually denoted by $\{\vec{e_1},\vec{e_2},\vec{e_3}\}$

Notice that each standard basis has as many elements as the dimension of the vector space. This isn't a coincidence: The basis of a vector space must have _exactly_ the number of vectors as the dimension of the space it spans. Since the vectors are linearly independent, if you remove one it will no longer be spanning. If you add one, since it already spans the vector space, that added vector must be linearly dependent on the others.

Said another way: this is what dimension actual _means_: The number of linear independent vectors that span the vector space.

## Interesting tangents

* The set of $f: R \rightarrow R$ is a vector space, and so can be added and scaled (composed, in programming terms). Polynominal functions are a subspace of this space, and each order of polynomials are a subspace of higher order polynomial functions.
* Strings are not a vector space, because not commutative under 'addition' (concatenation)
* Matrices of given dimensions are vector spaces, since an $n\times m$ matrix can be treated as a vector in $R^{nm}$, and it'll meet all the requirements.
* A pixel image of size $x \times y$ is a $R^{3xy}$ vector space (3 because of RGB color definition).

