# On Lisp

## Plan

Chapters 2 through 6 cover functions, including the functional style, first class functions, and using functions in place of data structures.
Chapters 7 through 10 are an introduction to macros. Chapters 8 through 11 are about macro techniques.
Chapters 19 through 24 are about embedded languages.
The book concludes with a discussion of OOP, and the Common Lisp Object System.

## Bottom-up Design

A program broken into a 'layers', starting from the language primitives, where each layer defines a language of nouns and verbs, and each successive layer is built from the language of the layers below it.

Lisp is especially good at this, because operations, once defined, are not distinguishable from those defined by the language itself. The separation between the language you write in and the language you _create_ is much smaller than in other programming languages.

Thus, rather than writing your program _in_ lisp, you write your language _on_ lisp, with the language primitives acting as the lowest layer, and on which you built.

The benefits of bottom up design are code-reuse, and extensibility.

BUD is the antithesis of top-down design, where the overall purpose of the program is defined and broken down into x functional things, and the sub-parts of the program are those x things.

## Design by evolution

Lisp is a malleable language.
You can turn in into the language you need.
Elisp is a moulding of lisp into a language for configuring a text editor.
This gives it the property that, if you don't know what you're building yet, lisp is a good shout, because you can always change it later.
And that's the case with a lot of programs.
It turns out, the 'plan and implement' style of design doesn't work that well with programs.
With lisp, you can afford to plan as you write.

## Functions

Functions are lisp objects - data types.

```lisp
(defun double (x) (* x 2))
(double 1)
;; => 2

#'double
;; => <Interpreted-Function C66ACE>

((lambda (x) (* x 2)) 3)
;; => 6

(setf (symbol-function 'double)
      #'(lambda (x) (* x 2)))
```

`defun` is a macro which builds the function-object, and stores it under the symbol passed as the first argument - two separate operations.

All the below have the same effect.
All but the first accept an _function object_ as its first argument.

```lisp
(+ 1 2)
(apply #'+ '(1 2))
(apply (symbol-function '+) '(1 2))
(apply #'(lambda (x y) (+ x y)) '(1 2))
(apply #'+ 1 '(2))
(funcall #'+ 1 2)
```

Many lisp functions, especially in CL, take functions as arguments:

```lisp
(sort '(1 4 2 5 6 7 3) #'<)
(remove-if #'evenp '(1 2 3 4 5 6 7))
```

With remove-if implemented something like

```lisp
(defun our-remove-if (fn lst)
  (if (null lst)
     nil
	 (if (funcall fn (car lst))
	     (our-remove-if fn (cdr lst))
		 (cons (car lst) (our-remove-if fn (cdr lst))))))
```

Functions can be attached to other objects as properties.
This allows a polymorphism:

```lisp
(defun behave (animal)
  (funcall (get animal 'behaviour)))
  
(setf (get 'dog 'behaviour)
      #'(lambda ()
	      (wag-tail)
		  (bark)))
		  
(behave 'dog)
```
