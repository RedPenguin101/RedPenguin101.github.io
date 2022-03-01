% Probabilistic Models of Cognition

# Selective content
* Generative models
* Conditioning
* Hierarchical models
* Mixture Models
* Non-parametric models

# Intro
A computation theory of mind: the mind is a computer, **mental representations** are programs, thinking is a computer process.

Generative approach: mental representations are _general descriptions_ about how the world works, **simplified models** that can be used to make inferences. A Generative Model describes a process which generates observable data. Due to sparsity of observations, these models are _probabilistic_, not deterministic.

1. Stuff happens in the real world, generating facts
2. We have a generative model, describing a process which generates observations.
3. The correlation between the actual facts and the hypothetical ones from the model gives clues about the accuracy of that model.

# Generative models

## Models, simulation, degrees of belief
The mind maintains models of parts of the world, which it can use to simulate this part of the world (i.e. Imagining what follows from initial conditions).

Simulation is related to degrees of belief. The model of a real process allows you to say, ahead of an event, how much you believe a particular outcome will happen. The _shape of expected outcomes_ (the relative belief in all possible outcomes) can be formalized as a probability distribution.

## Church, building generative models
A formal model is a description of a process for how to generate states: the steps that unfold that lead to potentially observable states. These generative process can be described as computations that use random choices to capture uncertainty.

Church is an extended scheme which permits probabilistic computation. It has Exchangeable Random Primitives (XRPs): functions that, when called, results in a samples from a distribution. For example, `(flip)` simulates a coin flip, and has a 50% chance of returning "true" or "false".

`flip` can be thought of either as a sampler for generating samples, or a characterization of the distribution itself.

Other distributions are available: `gaussian`, `gamma`, `dirichlet` etc. 

They can be combined: `(* (gaussian 0 1) (gaussian 0 1))`, or, to generate a stochastic function: `(lambda () (* (gaussian 0 1) (gaussian 0 1)))`[^1], or `(lambda (x) (if (flip) x (+ x x)))`

[^1]: A function with no arguments is called a _thunk_.

## Simulation and probability
How can we predict the outcome of a simulation? For example, how likely is it that we will see the result `(#t #f)` from `(list (flip) (flip))`? This is denoted $P(A)$, the probability of event $A$. We could run the program many times and see what fraction of the time the outcome `(#t #f)` is returned.

The _Product Rule_ states that the probability of two random choices is the product of their individual probabilities: $P(A,B)=P(A)P(B)$. However, this is only if the probabilities of $A$ and $B$ are independent. If the events are in sequence, and the outcome of $B$ could depend on the outcome of $A$, we must say that $B$ is _conditioned_ on $A$, written $P(B|A)$. The real product rules is therefore $P(A,B)=P(A)P(B|A)$.[^2]

[^2]: Note that $P(A,B)=P(B,A)$, so $P(A)P(B|A)=P(B)P(A|B)$. This is the foundation of Bayes Theorem.

The _Sum Rule_ is $P(A) = \sum_B{P(A,B)}$. Using the sum rule is called _Marginalization_.

## Stochastic recursion
A normal recursion has a 'termination condition', where it will decide when to stop. A stochastic recursion is the same, but it _randomly_ decides when to stop:

```scheme
;; A geometric distribution defined as a SR function
(define (geometric p)
  (if (flip p) 0
    (+ 1 (geometric p))))
```

## Persistent Randomness with Memoization
Often we will want the initial sample to be random, but any future samples to be consistent with that initial sample. For example if 'eye-color' is a uniform distribution over the values blue, green, brown, then the initial call `(eye-color 'bob)` might be blue. But since bob's eye color doesn't change, subsequent calls to `(eye-color 'bob)` must also be blue. We can accomplish this _persistent randomness_ with memoization, the `mem` function.

```scheme
(define eye-color
  (mem (lambda (person) (uniform-draw '(blue green brown)))))
```

This is sometimes called _random world_ style modeling.
