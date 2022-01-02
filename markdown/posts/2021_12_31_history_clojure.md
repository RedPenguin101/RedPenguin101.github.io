% A History of Clojure: A paper for HOPL

[Link to paper](https://download.clojure.org/papers/clojure-hopl-iv-final.pdf)

# Intro, background, motivation

The objective of Clojure is a language as acceptable as C# or Java, but based on a simpler programming model

Most applications of software are *information systems*: systems that acquire, extract, transform, maintain, analyze, transmit, render information. (information is facts about the world). This requires the imposition of regular models over messy reality.

The suitability of statically typed class models for these systems is low. The benefit of type checking minimal. OOP languages has imperative, stateful programming - the complexity caused by this is the number one problem faced by programmers.

# Lisp

## Why a Lisp?

Homoiconicity: Code is data. Not much to say here. Flexibility, Macros, normal stuff that is useless 99% of the time, and a Godsend in that 1%.

Separation of read/print from compilation/evaluation. Not sure I grasp how these are connected, but the major benefit here (which I agree is huge) is that representations of data structures _outside_ the language are identical to representation _inside_ the language. This means serializing is trivial, which opens up huge benefits in communicating outside the language: data storage, configuration, wire protocols. It eliminates a whole class of problem: think of getting JSON and parsing it to your language structures. That's not necessary here. You're sort of programming in JSON.

Small core language. Very limited syntax to learn.

Tangible at runtime. Benefits for introspection and instrumentation.

Flexibility. Omission of higher level programming model implementing static type checking (OO, cat theory, logic systems). This might have been a consequence of the times Lisp was written, but in Clojure it's a design choice. They are _available_ through libraries. The tradeoffs are reliance on documentation and testing, and fewer optimization opportunities.

## Differences from other Lisps

No CL-style reader macros.

Lisps are built on concretions: the language and standard libraries were build on mutable `cons` cell and lists based on them. These days we have decent runtime polymorphism, which allows us to build language and library on _abstractions_. From Perlis' "100 functions on one data structure" to "100 functions on one data abstraction". The implementation of this is the _sequence_ abstraction, which covers lists, vectors, sets, hash-maps. Sequences have hundreds of methods, like first, rest, that will be applicable to any of these concretions (or other concretions created by library). This includes sequences of data that may not be fully in memory. i.e. lazy sequences.

Functional programming style is technically possible in most, maybe all programming languages. But what matters is whether it is _idiomatic_ in the language. In Lisps you often reach a point where the language encourages you to dive out of immutability-land. In Clojure, it's intended that the bulk of a program was a set of functions taking and returning immutable data. That's what we tried to make idiomatic,via the use of (obviously) immutable data structures, but also immutable let bindings and standard library functions. Some will say the benefit of 'functional programming' is the pursuit of purity, and the ability to reason about programs mathematically. Clojure's aim is more pragmatic: the functional paradigm of avoiding mutable state makes the program simpler.

# Data structures and data philosophy

## Technical implementation on HAMTs

Lisps have Linked Lists at the forefront. Today arrays and hashmaps are much more prevalent. Any modern language needs a O(1) array and map structure. Since immutability is also a goal, this was a major challenge, and the aim of much of the research that was necessary. Osaki gave the purity, but was too slow and complex. Driscoll's fat-node approach - 'faking it' - had concurrency concerns. Bagwell's hash array mapped tries (HAMTs) with a high branching factor was the right fit. It's not _pure_ but it does guarantee immutability from the perspective of the user. Clojure's collections are built on these, and while technically O(logN), the high branching factor makes performance practically O(1). HAMTs have since been incorporated by several other languages.

## "Just use a map": Static vs. Ad-hoc aggregations

The problem space for Clojure is in "information programs". Information in the real world is sparse, incrementally accumulated, extensible, conditionally available, selectively into arbitrary sets on a whim. 

Aggregations of information in these systems are nearly _always_ context dependent. Whether or not an attribute is present in an aggregations is a property of the _context it is being used in_. It's not like a 'universal truth' that aggregate x must always have a y. Sometimes it won't have a y, sometimes it will. Sometimes you need x to have a y, sometimes you don't.

Thus the priorities of programming in these spaces are:

* Avoiding lock-in which prevents free combination of data.
* Having specific information being 'required' only ever in _context_, never universally.
* At the same time, not having everything be 'nullable' or 'maybe' typed. Whether an attribute is present or not is an quality of the aggregate, not of the attribute!
* The consequence of this is that establishing presence of information is important. Sparsity means it may or may not be available in whatever context.
* Selection (from a set of information thrown at you, take what you need, leave the rest)
* Arbitrary merging (have two things? Chuck 'em together. Now you have one thing, and are happier)
* Processing pipeline: You have information in, it gets piped through a series of functions that add and manipulate the information, spitting it out the other side.
* Name conflict management: required for several of the above points.

Static type/record based data structures ('aggregate types') are unsuitable for the above, because the are _parochial_. Their potential context of use is extremely limited in an information systems. Want to add in another attribute? Tough, the structure is closed. Have a context in which a specific attribute isn't required? Make a new aggregate or make it (eventually everything) nullable.

In short, aggregate types are too brittle for effective use in information systems.

In practice when faced with these challenges in other languages, many people will just use a hash-map. But the language generally doesn't make it idiomatic to do this: it _wants_ you to use aggregates, and doesn't provide good support for using hashmaps extensively (and maybe exclusively).

The goal of Clojure is to take these ideas and run with them: the facilities for creating aggregates are limited, and the support for using maps is _very_ extensive.

There are two use-cases for maps: 'record-like' maps that map properties to values. For example you have a set of different attributes about users like username, email, password. And 'singular' maps, where you map values to other values. For example, username -> site-visit count.

``` clojure
; record like map
{:username "Bob"
 :email    "bob@me.com"
 :password "abc"}

; singular map
{"Bob" 100
 "Fred" 20
 "Jane" 30}
```

There are different optimization priorities for implementation of these, since the first are often very small and the second very large. Clojure doesn't have different types for these two use cases, but behind the scenes switches implementations when the map passes a certain size.

# Why piggyback on the JVM?

Designing a language that people will actually _use_ requires that you overcome the inertia, very prevalent in 2005 and still today, of the 'language lock-in'. There were .net shops, Java shop, C/C++ shops. If you couldn't slip into that, you weren't going to get anywhere. A couple of times the author wrote in Common Lisp, and have to convert those programs to C++ to get them accepted.
