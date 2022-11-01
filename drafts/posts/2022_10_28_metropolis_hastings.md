# The Metropolis Hastings MCMC Algorithm for Bayesian Inference

Bayesian Inference is a method for combining our theories about events with evidence/observations, and refine those theories.
We call our current theory a 'prior', and the refined theory the 'posterior'.
The theories take the form of models, which themselves consist of curves which describe the likelihood of events.

One way to combine the prior and evidence is mathematically, by using Bayes Theorem.
In practical terms this means calculating integrals of the curves that make up our model.
This is OK for simple cases, but quickly becomes impractical.

Another way to do the combination is to approximate it numerically.
MCMC is one way to do this.
It allows us to draw samples from a posterior distribution in such a way that it quickly 'figures out' the shape of the distribution curve without having to calculate it.
There are several algorithms for doing this, of which Metropolis-Hastings is one.

We need

1. An (unknown) Posterior Distribution, a curve described by P(x)
2. A Function F(x), where we know the F(x) is _proportional_ to P(x)
3. A Proposal Distribution Q
4. An Acceptance function

The algorithm:

Pick a random x to be the first observation.

```
MH: x
  x' = N(x,0.1)->sample
  r  = U(0,1)->sample
  if f(x')/f(x) >= r
    return x'
    else return x
```
