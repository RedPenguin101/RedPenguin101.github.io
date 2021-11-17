# Part 1: Elements of computation process, Chapter 1: Representing information

We have defined a computer program as a machine that is given information, transforms that information, and then outputs other information. The key words here are "Information" and "Transforms". We'll start off our journey into program design by looking at what information is. Then in chapter 2, we will look at how to transform that information.

[What is information]

## Primitive data

### Numbers

Numbers are a type of information. While there are plenty of mathematical distinctions of different types of numbers, we will largely ignore them.

One important distinction we will make is between whole numbers (called integers) and decimal numbers. Integers are used for counting things, and (with some exceptions) you don't count parts of a thing.

Examples of numbers: `0`, `-1`, `1233453452`, `0.234252`, `4/3`

The last number is a fraction. It's really just an alternative way of representing a decimal.

If you have a mathematical mindset you might have noticed that we don't have a way of representing some numbers: irrational numbers. That is, infinite decimals that can't be expressed as a fraction. Since this distinction rarely comes up in real-world programs, our approach is going to be to ignore it.

### Text

Text, usually called a "String" in computing, is an ordered sequence of characters. In this book we denote text as being enclosed in double quotation marks, like this:

`"This is some text. 123, #$%^"`

Strings are a surprisingly complicated topic in low level computing, with lots of nuance and head-scratching elements. We will largely ignore that in favor of an every day definition of text.

One aspect we do need to cover is escape characters and special characters. You might have noticed that a consequence of representing strings as being enclosed in double quotations is that if you want to include a double quotation _inside the text_, you have a problem: How can you tell if the quotation mark denotes the end of the string, or is part of the text?

```
"once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, "and what is the use of a book," thought Alice "without pictures or conversations?""
```

_You_ might be able to tell from context, but a computer needs to be explicitly told. This is accomplished with an 'escape character', that tells the computer "The next letter is just a letter, not the end of the text". We will follow the general practice in computing and use the character `\` as our escape character.

```
"once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, \"and what is the use of a book,\" thought Alice \"without pictures or conversations?\""
```

Special characters are how you represent non-literal characters. The most common one is a 'newline', which is represented as `\n`

`This is the first line\nThis is the second line`

### True and False

Sometimes you just want to say 'yes' or 'no': A binary choice. In computing these are called "Booleans".

Very frequently we will want our program have branching, "conditional" logic, where if a condition is met (the condition is "true") the program goes down one branch, and if the condition is not met ("false") the program goes down another branch.

We will use the special words `true` and `false` to represent these. Not that, since these are _not_ enclosed in double quotes, they are not text. The text `"true"` is not the same as the special word `true`

### Keywords

The last primitive we will look at is the Keyword. It has been left to last because it the most nuanced in its use.

A keyword is something that starts with a `:`, which is followed by a sequence of letters or numbers or symbols, but contains no spaces. For example, `:hello-world` is a keyword, but `:hello world` is not, because it has a space in it.

You might be asking "Why not just use text? Why a whole separate primitive?". It is true that you could equally represent `:hello-world` as `"hello-world"`, and not lose any information.

The only thing to say at this point is that keywords are useful for _naming things_. Why keywords are better than text for this purpose, and in what situations it is better to use keywords than text and vice versa, should become apparent as we start to use them.

## Compound data, or collections

So far we have looked at the _primitive_ information types: numbers, text, booleans and keywords.

Often you will want to represent information that can't be easily captured as _individual_ primitives, but only as a _group_ of several primitives. A 2 dimensional coordinate, for example, is a collection of two numbers. If you are writing a program for a library, you will probably want to represent a book as some collection of texts, like the title, the name of the author, etc.

There are three fundamental types of collections, or "compound data": Sequential Collections, Associative Collections, and Sets.

### Sequential collections

A 'sequence' - also called in computing a 'vector', 'array' or 'tuple' - is an _ordered_ collection of data. We will represent sequential information as being enclosed in square brackets.

So a 'coordinate' can be represented as a sequence of two numbers, like `[4, 5]`. Note that in a coordinate system, the order of the elements in the sequence matters: `[4, 5]` is not the same as the coordinate `[5, 4]`.

Or, let's say you're watching a football game and you want to keep track of the number of yards gained on each play. You can represent that as a sequence of numbers like `[1, 14, 12, 3, -2, 0]`. When one of the teams runs a new play, you can add the number of yards gained onto the end of the sequence. In the case, the order represents the order of the plays run.

Some programming languages distinguish between types of sequence that can only hold one type of thing (e.g. It can only hold integers, but not integers and strings) and sequences that can hold many types of things. Those same programming languages often distinguish between sequences that are fixed-length (like our 2d coordinate) and sequences that can be of variable length (like our sequence of plays).

### Associative collections

An associative collection, called either a 'Dictionary', 'Hash Map' or just 'Map' in computing, is a collection where names are associated to values.

Because 'Associative Collection' is long-winded, this book will generally refer to one of these as a 'map'

The "Dictionary" metaphor is a good one: In a real dictionary, you look up the 'word', and paired with that word you will find its definition.

An associative collection is that concept, generalized. You store a series of 'values', with each value being 'associated' with a 'key', which can be used to look it up.

One difference between a literal dictionary and an associative collection is that, while a dictionary might contain the same word several times, for example if it can be used as a noun or a verb, the keys in an associative collection must be unique. 

We will denote associative collections as being enclosed with curly braces, like this:

```
{key1, value1
 key2, value2}
```
For example, you could decide to represent the information for a book like this:

```
{:title "Alice in Wonderland"
 :author "Lewis Carroll"
 :publication-year 1865}
```

The 'keys' here are `:title`, `:author`, and `:publication-date`, and the values associated with those keys are `"Alice in Wonderland"` (text), `"Lewis Carroll"` (text) and `1865` (number) respectively.

We have used keywords as the keys, and as the name suggests, these are a primary use of these primitives.

Unlike the sequence, an associative collection is _not_ ordered. There is not a sense that the title of the book comes 'before' the author.

### Sets

A Set of information (which we will denote as being enclosed in `#{}`) is a type of collection that is interested in _membership_. A set has _members_, and it can be used to ask whether a piece of information is a member of the set.

For example, if you are writing a program to keep track of a user's collection of trading cards, they will probably want to know the answer to the question "Do I already have this card?".

It would be natural to represent the users collection as a _set_ of cards, like this:

```
#{"Pikachu", "Charizard", "Squirtle", "Pidgey"}
```

Then if they come across a new card, they can compare it to the set and see if the new card is a member of that set.

A set is _not_ ordered. In the above example it is not meaningful that "Pikachu" comes before "Charizard"

A set can only have one instance of each element. `#{"Pikachu", "Charizard", "Pikachu"}` is not a valid set, because it contains the same element ("Pikachu") twice.

### Alternative representations

There is no 'correct' way to represent any information, and that goes double for collections. Often there will several possible ways to represent a collection of.

For example, we initially represented a coordinate as a sequence: `[4, 5]`. But we could equally represent it as a map, like `{:x 4, :y 5}`.

Likewise we could represent a book as a sequence: `["Alice in Wonderland", "Lewis Carroll", 1865]`

Each of the collection types have their own characteristics. The challenge is to select the one that is most appropriate for what you want to do. This will be a major theme in this book.

### Collections of collections

So far we have looked at examples of collections that only contain primitives. This was for simplicity, but there is no rule saying that collections can't contain _other collections_. In fact, it is very common.

For example, you might have a sequence of coordinates: `[[4, 5], [0,2], [-4,3]]`

This is referred to as "nesting", because you have collections nested in other collections.

In theory there is no limit to how many levels you nesting you can have. You could have a sequence of sets of maps of sequences. But 'deeply nested' collections are incredibly difficult to deal with, and generally you should avoid nesting more than two or three levels.

## Naming information with Symbols

Now that we have defined the theory of representing information, we can put that into practice.

In your editor, type the following:

```
(def pi 3.14159)
```

Now with the cursor somewhere between the brackets, hit `Alt+Enter`.

Now on a line below that, write

```
pi
```

With the cursor over `pi`, hit `Alt+Enter` again. The result should look like this:

```
pi => 3.14159
```

What we have just done is defined the word `pi` as being equivalent to the value `3.14159`. Now when we use the word `pi`, the program will remember that what you actually mean is the number `3.14159`. Let's unpack that.

The first word we wrote is `def`. This is short for "Define".

Next is the word `pi`. This is the 'symbol' or 'variable' that we are defining.

Last is the number `3.14159`. This is the thing we want to define `pi` to be.

Looking at `pi`, we can see this is different to the information we have seen before. It isn't enclosed with quotation marks, so it's not text. It doesn't start with `:` so it's not a keyword.

This is a special primitive called a 'symbol' or 'variable'. Essentially, it is a word that 'points' to some other piece of information. Because you have defined it like this, when you use `pi` in a program, the computer will remember that the symbol `pi` is what you have defined as `3.14159`, and substitute `3.14159`. That's what happened when we hit `Alt+Enter` over `pi`: the computer showed you that it remembers when you say `pi`, what you actually mean is `3.14159`.

Defining a symbol is really creating a 'name' that you can refer to later. The utility of this might not be obvious for this - why not just write `3.14159`? - but it can become apparent with larger data structures. It means you don't have to type out the information several times, you can just type the much shorter name. 

In this case, it means _we_ don't need to remember that pi is equal to 3.14159.

It also means that the person reading your code doesn't need to guess that you are using pi when you type 3.14159: it's right there in front of them.

Naming things, and naming them well, is a crucial element of creating good programs, and we will have much more to say about it.

Now we have defined how to represent information, we can move on to the other element of a program: Transforming that information.

