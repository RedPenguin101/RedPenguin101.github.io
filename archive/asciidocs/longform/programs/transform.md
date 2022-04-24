# Chapter 2: transforming information

In chapter 1 we looked at how we can represent information. In this chapter we will look at how we can transform and combine that information with functions.

## The function

The primary method of transforming information is the 'function'. A function is a tiny program: it takes information as input (called parameters or arguments), transforms it, and 'returns' the result of the transformation as output. We will call this process of applying a function to its arguments an 'operation'.

Let's look at a simple function: `+` or the 'plus' function.

The plus function takes two or more numbers as inputs, and returns another number that is the result of adding those two numbers together. We will denote this operation like this:

```
(+ 1 2) => 3
```

You can follow along in your text editor by writing `(+ 1 2)`, and then with the cursor between the brackets, hitting `Alt+Enter`. You should see the computer generate the `=> 3` for you.

This notation contains several elements. First, the operation itself is enclosed in parentheses. This says 'the thing inside these brackets is an operation'.

Next, we have the first element of the operation: the function itself, `+`. After the function, we have the arguments that the function will act upon, here the numbers `1` and `2`.

Then we have the `=>`, which can be understood as 'this operation outputs this value'.

Last we have the result of the operation itself, the number `3`.

Looking at the operation, we can say that the first element, the function, is the 'operator', and the other elements are the 'operands'. The operation consists of applying the operator to the operands to generate the result. This process is called 'evaluating the operation'. When we have been hitting `Alt+Enter` in our text editor, what we have been instructing the computer to do is 'evaluate this operation'

Let's have a look at some other functions that we can use.

Since we have `+`, it makes sense that we have other arithmetic operations. Follow along with these in your editor, writing in the operations and checking that you get the same result.

```
(- 10 3) => 7
(/ 15 5) => 3
(* 7 3 2) => 42
```

The last example there was different from the others, because instead of 2 operands, it had 3. All of these operations can accept any number of operands. Some functions can't do this, and can only accept specific numbers of arguments. For example, the function `inc`, short for 'increment', takes one, number argument. So does `dec` or 'decrement'

```
(inc 5) => 6
(dec 4) => 3
```

If you try to evaluate an operation starting with `inc` with more than one operation, you will get an error:

```
(inc 5 6) => Syntax Error (ArityException) ...
```

Functions don't just have to operate on numbers. Take the function `str`, short for 'string':

```
(str "Hello" "World") => "HelloWorld"
```

This operation combines several strings together.

Functions can also convert between types of information. For example the function `name` takes a keyword type, and returns a string type:

```
(name :hello) => "hello"
```

## Nesting operations

We have seen how you can evaluate an operation to add two numbers together. But what if you want to do something more complicated? Happily, operations can be nested, like this:

```
(* 5 (+ 2 10)) => 60
```

To evaluate a nested expression, first we evaluate all the sub-expressions, then we evaluate the outer expression. After evaluating the sub-expression `(+ 2 10) => 12`, we end up with `(* 5 12)`. This itself evaluates to `60`.

We can make expressions as nested as we like. Try to evaluate the following nested expressions step by step, and in your editor to see if you come to the same answer.

```
(* (- 10 5) (+ 2 10))
(* (- 10 5) (+ (/ 10 5) 10))
(* (- 10 5) (+ (/ 10 (- 23 18)) 10))
```

## Symbols in operations

In the previous chapter, we used the operation `(def pi 3.14159)` to define the 'name' `pi` as `3.14159`. Now, we can use that name in an expression:

```
(* 2 pi 10) => 62.8318
```

We have just calculated the perimeter of circle with radius 10!

## Defining our own functions

We just used the 'built in' function `*` to calculate the perimeter of a circle.

This is fine if we only want to calculate the perimeter of one circle. Even if there are two or three, we would be fine typing `(* 2 pi 4)`, `(* 2 pi 15)` etc.

However, if you want to calculate the perimeter of a circle more than three times, typing that lots of times will get tedious, especially since most of the operation is the same - it's only ever the radius, the last number, that changes.

Instead, we can define our own function that calculated the perimeter of any circle, given its radius. This is similar to how we 'named' `pi` in the last chapter. The difference here is that we are naming a function, not a number.

```
(defn p [r]
  (* 2 pi r))
```

Evaluate this operation in your editor so the computer will remember it.

Let's unpick this.

The first word is `defn`. Before, when you defined `pi`, you used the word `def`. When defining a function, we instead use `defn`, for 'define function'.

Next is the word `p`. This is the name you are giving to the function. It can be any word you want.

Next is a square bracket containing `r`. The square brackets following the name are where you will put the parameters that you want the function to have. Here, we want the one parameter the function takes to be the radius, which we will call `r`.

On the second line, we see a familiar operation: `(* 2 pi r)`. This is what we were typing in before, with the difference that instead a number, the last operand is the name `r`. What you are saying here is "when applying the function `p` take the provided number and substitute it for `r` in this operation".

Now we have defined the function `p` for 'perimeter', you can use it instead of typing out `(* 2 pi 10)` every time.

```
(p 10) => 62.8318
(p 4) => 25.13272
(p 15) => 94.2477
```

## Naming functions

When naming your functions and parameters, you can use whatever names you like. The computer doesn't care. We could have defined the function like this:

```
(defn elephant [blackberry]
  (* 2 pi blackberry))

(elephant 10) => 62.8318
(elephant 4) => 25.13272
(elephant 15) => 94.2477
```

The outcome is exactly the same. However, even though the computer doesn't care, other people reading your program certainly will! You want to make it very clear to the reader what a function is doing. In our small example here, the definition of the function is close to where we are using it, so we can always refer back to the definition if we want to see what it does. In a proper program, the function will likely not be so conveniently close to where we use it, so the reader won't be able to easily refer back to it.

So it's essential that you think carefully about the names you give to your functions, parameters and variable. Our first attempt used the function name `p` for 'perimeter' and `r` for radius. But even this is not as clear as it could be. What the reader of the code will see is just `(p 5)` on it's own, and it's not clear what it does.

If we define our function like this:

```
(defn circle-perimeter [radius]
  (* 2 pi radius))
```

What the reader will see when they come across this function being used is `(circle-perimeter 10)`. From the name, it is obvious what it does.

While you don't always need to be so explicit, naming your functions in a way that it is obvious what they do is a key part of writing good programs.

## Functions in functions: Function composition

We have defined a function that calculates the perimeter of a circle. Let's also do one that calculates the area of a circle.

The area of a circle is 'pi r squared'. Or `pi*r*r`. So we can define it like that.

```
(defn circle-area [radius]
  (* pi radius radius))
```

But this isn't exactly the same as the commonly used definition 'pi r squared'. 'pi r r' doesn't have the same ring to it. Instead, let's define a function `square` that takes a number `x` and multiplies it by itself. Then we can use that in our `circle-area` function.

```
(defn square [x]
  (* x x))

(defn circle-area [radius]
  (* pi (square radius)))
```

Now we have a `circle-area` function that matches how we think about the problem. This close correspondence between the code and how we think about the problem we are trying to solve is absolutely critical for writing clear programs. The closer you can get these two things, the easier your programs will be to write, maintain and change.

## Example program: A checkout

Let's write a small program to bring together everything we've learned so far: A program that can sit in a checkout at a grocery store.

Our program needs to solve the following problem:

> The local grocery store is run by a friend of yours. They are a very small operation, and do everything by hand. When a customer comes up to the cashier stand, your friend looks at every item in their basket, looks the item up in the printed price list they keep next to them, and adds up all the prices on a piece of paper, putting the total at the bottom. When the customer has paid, your friend makes a copy of the 'receipt' and puts it in the till with the money, and gives the original to the customer.
>
> Unfortunately your friend isn't very good at math, and it takes them a long time to add everything up. Sometimes this causes a line to form, and your friend doesn't want her customers waiting in line.
>
> Your friend wants to speed up the process of checking out her customers so that lines won't form in her store, and she has asked you to help.

Let's assume for now that you have bought an item scanner and a receipt printer, and have a way to interface with those things. The only thing left to do is write some software that will solve this problem.

What are some of the key words in the problem description? Stating the current process as simply as possible:

* A customer has a __basket__ of __items__ that the want to __check out__.
* The items are looked up in a __price-list__ to get their __price__
* All of the prices are added together to get a __total__
* A __receipt__ containing all the items, their prices, and the total at the bottom is given to the customer.

What we have identified here is the __domain language__ of the problem. It is important that when we write our program, the words we use should be closely aligned with the domain language.

Next let's think about the problem as a process flow: we will try to identify the inputs or information in, the transformation of information that takes place, and the output information.

Here we have two inputs: the basket of items, and the price list. The output is the receipt. The transformation process of 'checking out' is the process of looking up the items in the price list, calculating the total, and putting it all on a receipt.


