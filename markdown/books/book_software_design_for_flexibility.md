% Software Design for Flexibility, by Sussman and Hanson

# Content

* Flexibility in Nature and Design
* DSLs
* Combinators
* Pattern Matching
* Evaluation
* Layering
* Propagation

# Introduction

Making a design decision that backs you into a corner is common. This book contains code organization methods to mitigate that.

The goal is to build systems that have acceptable behavior over a larger class of situations than was initially anticipated. Or, _evolvable_ systems than can be changed to meet new requirements with only minor modifications to the code. An _additive_ approach is ideal: when a new requirement comes up, you don't need to change existing code, only add new code. But often, poor decisions about 
