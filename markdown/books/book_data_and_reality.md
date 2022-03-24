% Data and Reality

## Preface


The representation of information is a universal problem in computing. It it necessary for almost any task that you are asked to create a program for. The representation of information in the computer we refer to as _Data_, and the structure of and rules governing that data is a _Data Model_.

It is the observation of the author that data models he was creating often did not seem to _"fit"_ naturally, and sometimes not at all. What exactly he means be "fit" and "naturally", he does not explain here. There _is_ no easy way to explain it. It would take a book to do so. This is that book.

### What is information and how does it relate to data?

<div class="tufte-section">
<div class="main-text">
It is easy to explain what data is. A concise definition of information is more difficult. The first attempt is an analogy: _"Data is to information, what the map is to the territory"_. That is to say, a simplification, a leaving out of detail. It implies that certain decisions are being made about what to include and what to exclude, and how to depict things. The author provides a couple of other analogies: _"Data is to information, what grammars are to language"_, and _"Data is to information, what formal logic is to the way we actually think"_

To generalize these concepts, my own gloss on it<sup>1</sup> is that there are two worlds: The large world of reality, and the small world of models. The former exists outside of people, and has certain properties. The latter are created by people to try to represent the former, for the purposes of being able to reason about, to predict, and to communicate with others about the reality<sup>2</sup>.

From this we can see the fundamental problem with saying anything about reality: The only way we _can_ say anything about reality, about the large world, is to create a model for it. That is the _only_ tool we have to reason about or communicate things. Even language itself is a model. It is therefore impossible to _actually_ say anything usefully concrete about reality itself. We are, in effect, limited to talking about the models, and how they can and can't represent reality.

Kent's ultimate conclusion is that, because of this problem, information is too elusive to ever be pinned down to an objective deterministic process. However we do still need to _do_ the process of representing information. In this book he proposing such a model in this book. He names and defines the concepts of "entities", "categories", "names", "relationships" and "attributes". 

We can say that the data model is an _artificial formalism_. Information is, in the limited sense, that which is stored, maintained in quantity, with a simplistic structure/format. We focus on describing the information content of a system.

We can also say that there are always multiple ways of representing information as data. Different _structures_. Each way will have its strengths and weaknesses, will be useful for different purposes.

</div>
<div class="sidenotes">
<sup>1</sup> Or to be more accurate, a gloss stolen from Richard McElreath's [Statistical Rethinking](https://xcelab.net/rm/statistical-rethinking/)

<sup>2</sup> Kent does hint here how he thinks of information: _"We are looking at real information, **as it occurs in the interactions between people...**_ This does imply that 'perception' has some sort of fundamental role here, but he does not elaborate. Perception is something Kent mentions several time, so it seems to be important, but I don't feel he satisfactorily explains its role.
</div>
</div>

### Motivating questions

<div class="tufte-section">
<div class="main-text">

Any kind of modeling must be done with some aim in mind.<sup>3</sup> As described above, the process of modeling is in part a process of simplification: of deciding what _not_ to include in the model. There must therefore be some criteria for deciding what to include and exclude. The criteria is "is this relevant to my aim?"

Kent phrases this as a series of questions:

1. What is a useful way to perceive information for the purpose of creating the model?
2. What constructs are useful for organizing the way with think about information?
3. Might those same constructs be employed in a computer based odel of the information?
4. How successfully are they reflected in current modeling systems?
5. How badly oversimplified is the view of information in currently used data model? 
6. Are there limits to the effectiveness of any system of constructs for modeling information?

</div>
<div class="sidenotes">

<sup>3</sup> See Eric Evans' talk "What is DDD", especially around 29:45. https://www.youtube.com/watch?v=pMuiVlnGqjk
</div>
</div>

## Chapter 1: Entities

### Instance and Kind

<div class="tufte-section">
<div class="main-text">

We intuitively expect there to be a correspondence between a 'thing' in the real world and a 'representative' in our information system. Ideally the correspondence should be one-to-one, often it is not. But what is a thing in the real world?

A 'thing' is some physical object or idea that exists in the real world that we want to _name_ and _reference_. A **'Representative'** is the information construct in the information system corresponding to a thing.

When we loosely refer to a 'Thing' we could be using the word in one of two senses. The first is in reference to an **instance** of the Thing. The second is in reference to the **kind** of Thing itself. For example, when we refer to a 'widget' we could be talking about a physical widget that you hold in your hand, or about the idea of a widget.

When I say "Here is a book", what do I mean? I could mean a physical collection of atoms, the physical book: _"You tore the cover of my book!"_ I could mean the idea of a book, implying no physicality: _"I've written a book."_<sup>1</sup>

### Sameness

We intuitively understand that two things can have a "sameness", that they are in some sense conceptually close to each other. Sameness could be:

* how many kinds of thing is this thing?
* You have a thing and I have a thing. Are we talking about the same thing? Am I pointing to the brick where you are pointing to the wall?<sup>2</sup>

### Classification and categorization

The classification problem is about unambiguously communicating a concept using the ambiguous medium of language. This makes it hard to assign a word to one 'thing' because, consumed in another context, it might be taken to represent some other thing.

There is no "natural" set of categories. The world is always fuzzy. Any categorization model we come up with is artificial.

A piece of information about a thing can often be represented either as a categorization, an attribute or a relationship, basically arbitrarily. For example, any one person could:

* be a member of the category 'parents'
* have the property 'has children'
* have the relationship 'parent of' with another person

A thing can have an essence (what category it should be put in) and it can have different uses or purposes (its roles). Roles tend to be context dependent - think of all the purposes of a metal rod - so trying to use a role as a category will make things difficult.

We are not modeling reality, but the way information about reality is processed by people.

</div>
<div class = "sidenotes">

<sup>1</sup> Also, a collection of volumes (I've just finished vol.1 of the book War and Peace, now I need to go to the library to check out Vol.2) - how does this fit in?

<sup>2</sup> On reflection I don't understand this

</div>
</div>

## Chapter 2: The nature of an information system

<div class = "tufte-section">
<div class = "main-text">

Categories, while artificial, are useful because you can specify rules and constraints that apply to any individual belonging to that category.

An integrated database is sort of like the system's analogy<sup>3</sup> of the real world: an ongoing, persistent thing that an application can query in _general_, for use in its own _specific_ context. But efforts to model the whole organization in general, with the aim of meeting all potential contexts, are doomed to failure.

A thing in the real world has a representative in the system. As we saw, such a representative could actually be spread across multiple records<sup>4</sup>, with different views or perspectives. We could sometimes integrate these records into a single "pool" that can be used more generally. But recognize that a record could include facts relevant to more than one pool. You can't cleanly separate.

The properties of a representative:

* 1:1 correspondence with a thing in the real world (even if spread across more than one record)
* Can be linked together by relationships, names and types
* Constrained/Specified by types, tests, invariants
* Not omniscient - they are perceivers, they only know what they have seen.

</div>
<div class = "sidenotes">

<sup>3</sup> An analogy being a model, a simplification. But importantly, not the _same_ as the real world.

<sup>4</sup> More preamble required on what a record is, how it is distinct from representative.

</div>
</div>

## Chapter 3: Naming

_Qualification_ of a name is the addition of terms which narrow the scope of possible reference, to resolve potential ambiguities in the name itself. Thus "Harry" can be qualified as "Harry from the office". This particular form, qualification by relationship, is common.

## Chapter 4: Relationships

A kind of relationship can be thought of as a named tuple, where the name is the statement, reason, or verb. For each cell there can be a role name, specifying the role in the relationship, and the element itself will be category, or categories, comprising a domain. The 'degree' of the relationship is the number of cells in the tuple. An instance of the relationship type puts instances in place of the domains.

```
WorksFor(Employee, Department)
WorksFor(bob, accounting)
```

## Chapter 5: Attributes

There is no fundamental, objective distinction between an attribute, a relationship, and a category. Anything can be modeled as any one. How we should model it is dependent on context, ultimately on intuition and experience.

## Chapter 6: Grouping, types, categories, sets

<div class = "tufte-section">
<div class = "main-text">

Two ideas of grouping are often combined: Grouping things by "what they are", and expressing about a thing its attributes, relationships and names.

The problem with using types to divide things into groups is that it assumes certain things which, when brought into the real world, often fall down. The biggest problem is exclusivity. Or at least non-overlapping hierarchies. Grouping using sets based on predicates eliminates most of these problems - though it does bring in some new ones.

</div>
<div class = "sidenotes">
</div>
</div>

## Chapter 7: Models

A model (that is, a technique for representing information) is not "One way". A model shapes our view of reality and limits how we perceive it. A model is a lens, and it can distort as well as reveal.

## Chapter 8: Records
## Chapter 9: Philosophy
