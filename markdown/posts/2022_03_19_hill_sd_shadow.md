% The Shadows of Software Design


## Trip-Lets
<div class="tufte-section">
<div class="main-text">
The post is about a metaphor.<sup>1</sup>

In the metaphor, Software is compared to the trip-let: a single three dimensional sculpture that, when light is shone on it from multiple directions, casts different recognizable shadows. In this case the letters G, E and B.

In the metaphor, the sculpture itself is the software, the machine, the system. GPH refers to it as the 'artifact'. The shadows are the outcomes, or the characteristics of the machine. How it can be interacted with.

GPH identifies three shadows without claiming they are exhaustive:

1. *Behavioral* characteristics: The experience of those who interact with the running system
2. *Interpretive* characteristics: How easily the workings of the system can be understood
3. *Reactive* characteristics: How easily the system can be changed to accommodate change

The metaphor works on the basis of a couple of similarities. First, you can't change the shadows of the trip-let directly, you can only affect them indirectly by changing the artifact. Second, the shadows are not independent. By changing the sculpture with the aim of affecting one shadow, it is likely (without proper care) to affect the others.

GPH provides examples of 'images' of each of the three shadows. User stories for behavioral, class diagrams for interpretive, but not really anything for reactive. I don't _think_ he is saying these images are the shadows themselves, at least it doesn't make sense to me that they are. Rather the shadows are the intangible behavior. The images are maybe the shadow of the shadows.

</div>

<div class="sidenotes">
<sup>1</sup> [_The shadows of Software Design_](https://www.geepawhill.org/2022/02/15/the-shadows-of-software-design), GeePaw Hill, 2022-02-15


![](../../images/2022_03_19_hill_sd_shadow/geb.png)

The trip-let sculpture is from the cover of Hofstadter's _Godel, Escher, Bach: an Eternal Golden Braid_.
</div>
</div>

## Criticism
I like the metaphor. It possibly starts to break down if you try to unpick it too much, but that's not a criticism, so does almost any analogy. It does enough to be useful.

The only issue I have is that one of the shadows is not like the others. The "Reactive" shadow is a bit of an odd-one-out.

The other two shadows are about how things _outside_ the system interact with the system itself. In one case, the things that depend on the outcomes of the system (the "users"), and in the other case the things that need to understand the _mechanism_ of the artifact. GPH says this in the context of the engineers who are building it, but I think it can be generalized to anything that needs to know how the system works. The two can be equated with the "what" and the "how", which GPH wisely separates.

The reactive shadow is a little more tough to fit into that mold. The motivating question is _"can we change the program?"_ The inclusion of this in one of the three shadows is understandable, given it's centrality in software design. How easy it is to change is really _the_ thing that separates good software from bad. But it sits uncomfortably alongside the other shadows.

It bleeds into the behavioral: _why_ does the software need to change? Because (probably) the users want its behavior to change.

It bleeds into the interpretive: the interpretive shadow is about _how_ the machine does what it does, from the perspective of the people who are going to change it. This can't be separated from the organization of the machine.

The slightly awkward fit of the reactive shadow is shown in how GPH writes about it

> The ways we envision the reactive shadow are the least well-known and coherent. Though class diagrams capture some of it, so do backlogs and to-do lists. The tools we use here are nearly all just "in our minds". It’s a particularly fruitful area of study just now.

### An Alternative Third Shadow

Some of what GPH calls the reactive shadow is actually part of the behavioral shadow, and some is part is the interpretive. But some of it is part of neither of these too. What is the remainder?

Another option for verbalizing the third shadow is to stay with the theme of the _interactors_ with the system.

Some actors interact with the machine to achieve outcomes. Mostly what GPH call the users. These are people who are interested in the "what". This is equivalent to GPH's _behavioural_ shadow.

Some actors interact with the machine to build and maintain it. They are interested in the "how". This is GPH's _interpretive_ shadow.

There are some actors who interact with the machine incidentally. They do not want anything from the system, they may not even know about it. But they do impact it. You might call this the _environment_ in which the system is situated.

The _"Environment"_, then, is the third interactor.

The problem is that it breaks the metaphor! In the trip-let, the shadows are 'one way'. The artifact casts them, but shadows can never impact the artifact itself. The shadows are perceptions of a thing from a particular perspective.

What is the perception of the artifact from the perspective of the environment? How does the environment see the artifact? It sees it from the perspective of its own system. It might not see it at all. So, if you think about it, do the other interactors. Systems, interacting with systems, which are all part of systems.

---

## Raw Notes
* Hofstadter's "GEB" trip-let. Three shadows, all different letters.
* A program is a trip-let. The shadows are: behavioral, interpretive, reactive.
* Software design is shaping the trip-let to create effects in those shadows.
* But affecting one affects all
* Behavioral shadow: Does the program do what we want?
    * It's about the satisfaction of the users.
    * How the artifact causes the behavior we want
    * The image: sequence/object diagram. Wireframes. User stories
    * Given two designs, take the one that works (better)
* Interpretive shadow: Do we understand the program?
    * Naming, local complexity, function length, interfaces
    * The image: Class diagrams, architectural documents
    * Given two designs, take the one that makes the most sense to us (?)
* Reactive shadow: Can we change the program?
    * Responding to changes.
    * Dependency, coupling, isolation, sequencing. Abstractions
    * Images: not many! We don't have much to deal with this.
    * Given two designs, take the one that will flex under change
* Back to larger features
* Shadows are indirect consequences of the artifact. We can only affect the shadow by affecting the artifact
* Design is a multi-human thing. Collaboration has to be a central consideration.
* Are there more shadows? Maybe
* Shadows are not dimensions. They are not orthogonal.
* Shadows are not layers. They are somewhat separable, but only somewhat.
* The three shadows should form a felicitous balance
* The shadows are not independent: for example performance optimizations can improve behavioral shadow, but at the expense of the interpretive and reactive shadows (i.e. they make the code harder to understand and more brittle to change)
