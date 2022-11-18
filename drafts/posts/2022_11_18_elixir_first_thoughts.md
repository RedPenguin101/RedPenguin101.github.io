# Elixir: First Impressions

I'm planning to do [AoC2022](https://adventofcode.com/2022) with Elixir as a way of learning the language.
Maybe not the best choice of language to learn - a functional, dynamically typed, GC'd language is not so far out of my comfort zone.
But what the heck, it looks fun.

I've started off by going through [Elixir In Action](https://www.manning.com/books/elixir-in-action-second-edition) and doing the AoC 2016 puzzles (up to [day 7](https://github.com/RedPenguin101/aoc2016_elixir) at time of writing).
The [HexDocs](https://hexdocs.pm/elixir) have also been invaluable.
Here are a list of my first impressions.
The things I like, the things I don't like - or, more likely, the things I don't understand the reasons behind yet, and at some point will 'get'.

## I like the standard library

I'm definitely spoiled by Clojure's excellent core libraries, but Elixir's seems excellent too.
There's clearly a lot of cross-pollination between them, which is great because a lot of the idioms transpose well, sometimes with a name-change (`partition` becomes `chunk`).
There are even a couple of things I've found already that I don't think _are_ in the Clojure standard library, but which I think should be. `Enum.find_value` being one, `min_max` being another.

## Pattern Matching is great and fun

Pattern matching as a central idiom, when you get used to it, is lovely.
Using it in case-statements, and in function argument-lists, feels incredibly powerful, especially when combined with multiclause function branching and guards.
I guess this is ML-inspired.

## Tuple Trouble

I'm a bit confused by the strictness of _tuple_ pattern matching though.
For example:

```elixir
{a,b,c} = {1,2,3}    # fine
{a,b,c} = {1,2,3,4}  # not OK
```

The matching is _closed_.
With lists, you have the option to say "match the first and second, and oh yeah, some other stuff of indeterminate length is on the end"

```elixir
[a,b,c | other_stuff] = [1,2,3,4,5,6,7]
```

This seem counter to the idioms of using the first element tuple as a sort of 'soft type', like `{:typeA, other_stuff}`, or as a Maybe/Result-like Monad: `{:OK result}, {:error error_detail}`, then using these types for matching.
My concern is that I set up a match on the first clause `{:typeA, stuff}`, but then later I want to put `more_stuff` into the structure, maybe like some additional context.
My options are, I change the structure of `stuff`, but that will likely break existing callers.
Or, I change the structure itself to `{:typeA, stuff, more_stuff}`.
That's really what I would like to do, but that will break every instance of my pattern matching.
Having `{:typeA | some_things}` would be a way around it. 

Obviously there are tradeoffs here.
For example you can rely on matching to distinguish between `{:typeA, a}` and `{:typeA, a, b}`, and if you did `{:typeA | stuff}` you couldn't.
That would be a big vector for bugs too - and ones that can't be caught at compile-time.
I guess this is a conscious decision, and I'm just not seeing the same values yet.

Interestingly, you _can_ do this generous matching with maps:

```elixir
%{age: age} = %{name: "Bob", age: 25}
# works fine
```

If my tuple had been a map, like `%{type: :typeA, stuff: stuff, more_stuff: more_stuff}`, then I wouldn't break anything.
Maybe it's like "Duh, then do that", which is fair enough.
But then what of the 'match on first tuple element' thing?

While on the topic of tuples, having different access and modification patterns for tuples than for other things is irritating.
I get tuples are not Enumberable (though I'm not completely clear why not) but even so, why do I have to learn different patterns for the same things twice?

The linked lists are very nice to work with, but given the number of "be careful of linked list performance concerns!" warnings in the docs when you try and do non-listy things (e.g. add something to the end), why not have an array/vector-like structure that can alleviate some of those?

## Lambdas and Functions

Coming from a lisp, I'm used to functions being functions. 
There's no fundamental difference between an 'anonymous' function and a 'proper' one: the latter just happens to have a name and be stored in a environment lookup somewhere. The difference between `(defn x [y] body)` and `(def x (fn [y] body))` is just sugar.
This is great in a language where passing functions around as arguments is done constantly.
Even Python, where HOF aren't so idiomatic, the language is pretty ambivalent whether you give it a lamda or a 'proper' function.

But Exilir is!
You have to 'cast' proper functions to lambdas when you want to use them in a first class way.
Since I'm doing this _constantly_ when mapping over an enumerable, this is quite irritating.
It's not a huge deal, but I don't get _why_ it's different.
The calling method for lambdas is also different (`my_lambda.()` vs. `my_fun()`), which means they can't be used in pipelines in the same way as proper functions.
Again, why?!?
I feel like there must be a good reason for this, I just don't see it.

## Tooling and The development workflow

My workflow with Elixir is:

* I open a REPL from commandline with `iex -S mix`
* I write a new function in the source file in VSCode
* I define some test input data, either as a module attribute, or in the REPL.
* I `recompile` in the REPL and 
* Still in the REPL, I execute the function with the data by typing it in.
* I see the output (or Exception) and change the function, then repeat

This is pretty good.
Better than most compiled languages anyway.
But (again coming from lisp) I'm baffled why there isn't better tooling around this, since it seems the way BEAM is set up would be perfect for it.

My ideal workflow would be something like:

* Connect to the REPL from VSCode
* Define some data, storing it in some environment
* Write or tweak a function in the source file and tell VSCode to evaluate it.
* Still from VS Code, run the function on the data, see the output, and tweak.

Obviously, this is just a standard lisp REPL-connected-IDE flow.
But I don't why you can't do it with Elixir.
The CLI is just a process, which you send messages to and receive messages back from.
Elixir has 'scripting' capabilities which allow for having function calls outside of modules.
Again, I'm probably missing something big here.

