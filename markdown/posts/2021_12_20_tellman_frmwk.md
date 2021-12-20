# Zach Tellman: Designing a Framework for Conversational Interfaces 

[Link](https://www.microsoft.com/en-us/research/group/msai/articles/designing-a-framework-for-conversational-interfaces)

> "Early AI researchers envisioned a world where knowledge had a singular representation and a singular repository. Instead, we live in a world where data, and the ability to interpret it, is fragmented and diffuse. As a result, our constraint solver must be unusually extensible, allowing developers to compose it with their own systems and domain expertise."

## Intro

Zach and Semantic Machines (SM) are working on the conversational interface for Outlook Mobile.

The value proposition is that users can ask for what they want in a natural way, replacing nested menus or memorized commands, which have high cognitive load and cost to learn.

Modern conversational systems usually have a 'slot and intents' model **S&I**: the user _utterance_ is matched to an _intent_ template Information is then extracted from the appropriate _slots_:

> "Order me a medium pizza with pepperoni and mushrooms" 

Would be matched to a pizza ordering intent. The information `{:size "medium", :toppings ["pepperoni", "mushrooms"]}` could be easily extracted. The function `order_pizza("medium", ["pepperoni", "mushrooms"])` can then be called. Linguistic and Business logic are well separated.

Consider a command:

> "Find a block which is taller than the one you are holding and put it into the box."

This is more difficult to model, and to separate linguistic and business logic. Natural language is _compositional_, and S&I frameworks are not. The above demands a set ideas that can be combined with little restriction (“find a block,” “taller than,” “held block,”). S&I doesn't allow for this flexibility.

## Semantic Machines solution

### Plans

Utterances are translated into small programs call **plans** using a _transformer-based encoder-decoder neural network_.

> "Find a block which is taller than the one you are holding and put it into the box."

Becomes:

```
find_block((b: Block) => taller_than(b, held_block()))
put_in_box(the[Block]())
```

The purpose is _only translation_, not _interpretation_. There is a level of indirection here, since the first line might be more naturally (for a programmer) be expressed like

```
find_block((b: Block) => b.height > held_block().height)
```

We don't want to assume. Implementations of things can get pretty nuanced when a person is involved.

The utterance has a mix of context-independent and context-dependent language (definite articles). Were you to take only the second statement: _"Put **it** into the box"_, you would not know to which thing "it" refers, until you include the surrounding context: "Find a block..." resolves "it", and so you can interpret the statement by looking for things that apply to a box.

SM has a referential resolution system which tracks the history of context. So calls to `the` will query a graph of every plan in the conversation to figure out what it resolves to.

### Constraints

An **intensional description** (as opposed to extensional) is one that provides criteria for finding the entity e.g. "A block that is taller than the one you are holding". In reality, all descriptions are intensional, since there is no way to uniquely specify an entity: the closest we get is a _name_, but they are ambiguous enough that we need to search our context to resolve the entity.

How to resolve this? A simplistic way would be as a predicate function: `the[Person](p => p.name ~= "Alice")` and map over 'people'. But the 'Person' dataset is likely to be remote, large, and limited (for privacy) in what it exposes. We need to _query_ for it. Unfortunately, that pushes the query implementation logic (of, say _similarity_) to the API, which probably won't suit us. We need to pass our _constraints_ to the query, as well as passing them the tools for interpreting those constraints, usually called _controllers_.

### Revision

Language is ambiguous. "I'm heading to the bank" can have several meanings. _Pragmatics_ is the study of how context confers meaning.

SM provide a `revise` operator which, given two constraints `a` and `b`, will merge those into a single constraint with the  parts of `a` that keep `b` from being meaningful removed.

For example, if you end with two constraints: one which looks for a meeting in the future, and one that looks for a meeting in the past, if you combine them you'll end up with a self-contradicting constraint.

```
revise(
  e => e.start > now() && e.attendees.contains(me()), 
  e => e.start.date == yesterday(),
)

e => e.start.date == yesterday() && e.attendees.contains(me())
```

Contradictions don't have to be as binary as this. Consider the constraints `e.start > now()` and `e.start > beginning_of_year()`. The first does not _contradict_ the second in all cases, but if the `> now()` is kept we will be excluding results that the user wants to see. So the `now()` must be recognised and discarded.

