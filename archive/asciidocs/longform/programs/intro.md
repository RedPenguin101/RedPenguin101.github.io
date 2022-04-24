# Computer Programs: Introduction

## What is a program?

A program is a machine which takes information in at one end, and spits out new information at the other end. While inside that machine, the information will undergo a series of processes which transform it, usually using a branching decision tree for how to transform it.

Over the past decades, with the birth of the internet, and the vast increase in the amount of human activity that uses or depends on information, computer programs have become more and more important to the conduct of business, because they offload the onerous task of processing information from humans onto machines. As a bonus, machines can often do this processing much, much faster than people.

In the early days of computing, programs tended to be small and relatively self contained. Today such standalone programs are very rare. Instead, programs are chained together into giant "systems" that can span entire functions or businesses, to the point where the line between the computer system and the business system become rather blurred. Humans and computers participate together in a grand system, each playing their part in looking at information, making decisions about that information, and outputting new information.

There is a significant problem at the heart of program creation: it is very difficult to do well, even at a small scale. When you reach the scale of large systems comprised of many programs, the task becomes nearly impossible. In most large systems, the best you can hope for is something that works pretty well most of the time, that handles situations where it doesn't work gracefully, and that doesn't completely fail too often.

The problem seems to be that the way people think about information systems is different to the way a computer thinks about them. A computer is very, very stupid and very, very fast. It needs to be told what to do in an excruciating amount of detail. If what it is asked to do deviates in any way from what it knows, if there is any contradiction presented to it, it will fail.

Humans do _not_ think like this at all. The incredible human brain is entirely comfortable dealing with models that are not explicitly defined. It can handle two contradictory scenarios with no trouble at all. This saves us a huge amount of time and effort. If we had to rigorously define every single thing we did before we could do it, we would never get anything done.

The problem arises when these two modes of thought meet. In short, computer programs needs to be written by people, and people need to read and understand computer programs. What needs to happen is a translation between these two modes of thought. 

The essential skill of the computer programmer is to be able to take a human model of a task, with all its fuzzy definitions and contradictions, and turn that into precisely defined computer program. They have to do this in such a way that the program will retain some of the flexibility to handle unexpected information. And they need to do it in such a way that another person who is reading the computer program is able to make the same translation between modes of thought. That is, the reader is able to understand how the program works by reading it.

The first purpose of this book is to look from the ground up at the process of computation in a way that is comprehensible to a person with no prior programming knowledge. It will define what information is. It will teach the reader how to create small computer programs to manipulate information. It will teach how to chain (or "compose") those small programs into larger programs.

It is one thing to be able to technically write programs that manipulate information. It is another thing to do this _well_. A poorly designed program is one that is hard for a person to understand. If a program is poorly designed, it is hard to figure out how to fix it when it goes wrong. It is hard to change the program to meet new requirements. If you have several people working on a poorly designed program, then it is hard for them to work effectively together.

The second purpose of this book is to provide guidance on how to write well designed programs.

Computer science is a very broad topic. There are several elements of computation that this book does not address at all.

Firstly this book is not interested in the mechanics of the physical computer these programs are running on. The fact that, when given a computer program to execute, the hardware is translating that into ones and zeros, storing it in RAM, executing instructions through a central processing unit: this is incidental. We will use the fact that humans and computers have a common language (a programming language) to conveniently ignore the details of how a computer executes the program.

Secondly, beyond some very elemental theory this book is not interested in concepts of types or categories. It's fine if you don't know what that means.

Thirdly, this book is largely interested in the practical ideas behind effective program creation. It will not teach you how to create a Twitter clone, or a blog. The intention is that when you do go to build your own programs that are used by users, you can design them in an effective way.

## A note on programming language choice

This book uses the programming language Clojure for all of it's examples.

If you care about this sort of thing, the choice of this language as a teaching tool is driven by the following considerations:

* It is an untyped language. Since we don't want to get bogged down in the detail of type theory, this is beneficial.
* It is a functional language, which means that the complex topic of state mutation can be largely avoided.
* It is not _too_ strict a functional language, so we can deal with side effects in a practical way, and get imperative that best suits the program.
* It is a very high level language, meaning we don't have to worry about machine-level considerations, like the differences between flavors of integer
* It is a highly interactive language, which means the user is quickly able to see the results of the changes they make to their code.
* It has an extremely rich library of powerful functions for manipulating information

It is a goal of this book that all of the code in this book will be quite easy to re-write in any other high level, dynamically typed language which has first class functions, such as Python or JavaScript. Replication in strictly typed, class based, or low level languages like C, Java or Haskell is not recommended.

A word of warning that idiomatic Clojure code typically uses recursion instead of imperative looping constructs, and leans heavily on its excellent standard library to process collections. Both of these facts will be the primary impediments to translation to another language. Most popular languages have good third party libraries that can replicate Clojure's standard library ability.

