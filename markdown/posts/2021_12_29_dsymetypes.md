# Don Syme on type level programming

[Link](https://github.com/fsharp/fslang-suggestions/issues/243#issuecomment-916079347)

On the 9th of September 2021, Don Syme posted a response to a GitHub issue requesting type classes by implemented in F#. This is a summary of that response.

## The response

F# has a 'functions + data' approach at the base of its philosophy. The trade-offs of type classes in this approach are not favourable. What follows are some of the negatives.

1. Simple type-classes are not sufficient, and will lead to requests for type-level abstractions (higher kinds, type families, etc.)
1. Combination of type-class-like features leads to obfuscated, subtle code.
1. Type-level programming has serious compilation performance downsides.
1. It leads to compile-time debugging, which isn't supported by IDEs.
1. It leads to fractiousness in the community, as groups fixate on the 'right' way to use type classes.
1. More layers of type hierarchy can lead to libraries 'tweaking' their implementations, creating noise for callers.
1. It leads to a community where people without higher-level math knowledge (e.g. category theory) are disempowered.

The last one I'll quote in full

> The most effective use of these features require end-to-end design of the entire API surface area with these features in mind. Everything becomes tied up into a particular view on type-level abstractions and type-level programming. It quickly becomes impossible to program without an understanding of these abstractions, or without referencing a library which encodes this

Next comes some discussion about language details, which is not of interest here.

Finally Don has a couple of paragraphs of more general thoughts about the realities of talking about type-level programming with its advocates.

* They don't seem to believe the downsides are real.
* Or at least they can be solved by the language creator, rather than being intrinsic.
* They seem to prioritise TLP over actual programming.
* He claims TLP fans are caught up in the world of pure world of types, when real world programming is about information.
* Often when you translate code that uses higher level type features to a language that doesn't have them, it will be much simpler

> [when] type-level programming facilities are available in a language they immediately, routinely get misapplied in ways that makes code harder to understand, excludes beginners and fails to convince those from the outside.

## What is type level programming and what are type classes?

### Type Classes

Type classes are an approach to polymorphism, roughly similar in intent to interfaces in OOP. They are most common in typed and strictly functional languages like Haskell.

Type classes are an example of _ad hoc_ polymorphism, as opposed to _parametric_ polymorphism[^1].

[^1]: Parametric polymorphism are things like `map` that operates on containers, without caring what is contained in them.

The problem that we are trying to solve here can be stated as: 

> I want an `equal` function to detect if two objects have the same value. But I don't want to have a different function for each type of data I operate on (`integer-equal`, `float-equal`, `string-equal`), I want a single equal function that operates on all of them. How do I do this?[^2]

[^2]: Type classes are an _early binding_, or _compile time_ approach to ah hoc polymorphism. _Late binding_ or _run time_ polymorphism is another approach, seen in Smalltalk and Clojure.

In Haskell, we can declare that a type is an _instance_ of a type class, and provide the operations which that type class implements.

```Haskell
class Eq a where
  (==) :: a -> a -> Bool
```

This gives `==` the type `(Eq a) => a -> a -> Bool`, meaning that _"For every type `a` that is an instance of the type class Eq, there must be a declaration of how the function `==` is implemented"_.

Then, you would declare the instance and define the implementation of `==` (called a _Method_):

```Haskell
instance Eq Integer where
  x == y = integerEq x y
```

Type classes can be used for type checking. For example a sorting function can be defined like `sort :: (Ord a) => [a] -> [a]`. That is we can declare that `sort` can take a list of any types, so long as those types implement ordering (`<`,`>=`). Then, the compiler can check that for us.

### Similarity to OOP Interfaces

You can see how this is similar to an interface. Like a Java interface, a type class defines a _protocol for use_ (method names and signatures) without defining an implementation. 

Like when a Java class implements an interface, when a type is an instance of a class, it _must_ define implementations (concrete methods) for that protocol.

### Type-level programming and _Kinds_.

This is a crazy complicated topic that I don't pretend to understand, so this is a very high level summary.

Consider that in a type system, every value is an instance of a type. For example the value `"Hello, World!"` is an instance of the type `String`.

We can also say that a type is an instance of a _kind_ of type (called just a _kind_).

Without going into detail, this introduces a new level of hierarchy to the type system.

You might ask: are type classes not also a new level of hierarchy to the type system? I don't really understand kinds, so I can't say for sure, but I think yes, they are. I believe this is the source of Don's criticism that _"Combination of type-class-like features leads to obfuscated, subtle code."_. I can certainly see that it could be confusing how these things will interact.

## Thoughts on Don's critique

Don's criticisms can be divided into two: the intrinsic problems of the type system itself, and the impact on the community. 

The common denominator in both of these is _people_: The negatives in the TLP is around how they are _used_, and the paths it leads people down (requesting more TLP constructs), and communities are obviously comprised of people.

I think the meta-narrative here is about the paradox of the need for hierarchy and order in peoples minds. When trying to understand a complex system, we try to order it. We try to create categories and hierarchies, mental models. It is an incredibly useful tool. Since it is our natural way of processing things, it can't be avoided.

The paradox comes in when order is taken passed a certain point: the order starts to _create its own complexity_ rather than aid us in our comprehension of the complex system. The drive to over-order is a strong one. On the first level, people see that a little order is a good thing, and assume that a lot of order must be an even better thing. On a second, more subconscious level, the drive to order takes over.[^3] People forget the reason for creating the order is to make sense of the reality, and the goal becomes the creation of more sophisticated orderings. There seems to be a strong, probably naturally selected, satisfaction in taking something and making it more ordered.

[^3]: I notice only after writing this that I have created a little hierarchy, a mental model, neatly giving example to the idea in explaining the idea.

Why is it that hierarchy, past a certain point, becomes counter-productive? I think it is quite obviously a "mistaking the map for the territory" problem: Reality is not ordered, it is not hierarchical. It is chaotic, fractal, unpredictable. It is fundamentally and irreducibly complex. A model of any kind is a simplification of the reality, that is useful for us in comprehension. Creating a hierarchy on top of that model is to move progressively further away from the reality.[^4] 

The hierarchy presumes that the model is _"correct"_, and invalidations of the model will result in invalidations of the hierarchy. Since the underlying model is a simplification, such invalidations are inevitable: the model is slightly brittle to change. Thus, hierarchies are also brittle. The problem is that because the model is now written in terms of higher order constructs, the underlying model becomes doubly brittle: To change it, you now need to change the level above it. Subsequent levels of hierarchy compound the effect. 

It reminds me of something Russell said in his _History of Western Philosophy_:

> “The difference of method, here, may be characterized as follows: In Locke or Hume, a comparatively modest conclusion is drawn from a broad survey of many facts, whereas in Leibniz a vast edifice of deduction is pyramided upon a pin-point of logical principle. In Leibniz, if the principle is completely true and the deductions are entirely valid, all is well; but the structure is unstable, and the slightest flaw anywhere brings it down to ruins. In Locke or Hume, on the contrary, the base of the pyramid is on the solid ground of observed fact, and the pyramid tapers upward, not downward; consequently the equilibrium is stable, and a flaw here or there can be rectified without total disaster.”

This is the first problem: hierarchy in a model creates rigidity, which means an inflexibility to change. Given the model is a simplification of reality, there are a multitude of things which are in reality that have been left out of the model. Inevitably one of these things will have to be introduced into the model at some point, due to changing requirements. Thus inflexibility to change is the nemesis of models.

The second problem is that hierarchies tend to become more general the higher up them you go. You move from concrete instances that can be looked at, poked and prodded, and so can be understood easily. Go a level up and you start to get more abstract, and things can't so easily be understood by example and comparison. 

Category theory is a good example of this I think. You start off with a concrete idea of counting rocks. Very easy to understand. You abstract away the idea of rocks, and get to the idea of counting. It's more difficult to understand in isolation. The best way to understand it is to dive back down to concretions: counting rocks, counting apples etc. This is the reason that the idea of 'zero' and 'negative numbers' took so long to emerge.

Going up another level, you abstract the idea of counting, moving into areas of operations on a 'container' of objects. This is where you get into ideas like `map`, `fold`, and 'functors'. Above that you have concepts like monoids, monads, and then above that you have the idea of categories themselves. Each progressively more difficult to understand because you have a pile of abstractions, when the only way to understand abstractions is by looking at concretions.

This comes back to Syme's point about making things inaccessible to newcomers, and the centralization of knowledge and influence in the hands of those who have mastered these arcane topics, rather than those who are good at writing programs that are effective.

[^4]: Whether reality has such abstractions itself, I don't know. I think it probably does.
