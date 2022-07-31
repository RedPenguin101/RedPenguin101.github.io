# More reflections on a modelling framework

* The ability to execute custom code is much less important than I thought. 99% of formula you'll need to execute will be a few core functions, mostly simple arithmetics and booleans.
* Speed is a problem. I always thought it would be, but my current method has a calc time of ~ 1s for a very small model. That really needs to be around 100ms.
* I do like the separation of model and results, I think it's a good fit. As well as the explicitness of the model.
* Goal seeking is a problem I need to solve. The basic idea is that you have a bunch of constants and measures, and based on those you calculate a new constant (say, the premium you would need to pay as a coinvestor to get a target IRR). That constant is then used in future calculations.

```
   measures
      |
goal seek -> constant
                |
           more measures
```

* There's a subclass of circularity problem - the debt sizing problem - which will be hard to solve I think. This involves sizing the debt based on the cashflow available for debt service, which itself is based on the size of the debt. This means you need to calculate the whole model, hard code the CAFDS, re-paste the calculated CAFDS, and repeat until the delta between the hardcoded and calculated CAFDS are below a tolerance. So you could be looking at one 'run' of the model being actually 100 runs of the model.
* Error handling is both easier and harder than I thought it would be. The REPL thing doesn't help, since it's no longer the focus of your workflow. Maybe better would be to pipe repl output to the output window.
