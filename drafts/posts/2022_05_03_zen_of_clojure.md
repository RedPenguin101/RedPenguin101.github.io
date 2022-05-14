# The Zen of Clojure

Inspired by [This thread](https://clojurians.slack.com/archives/C0353589RFC/p1651393707055989), and of course PEP20, the Zen of Python:

```
Beautiful is better than ugly.
Explicit is better than implicit.
Simple is better than complex.
Complex is better than complicated.
Flat is better than nested.
Sparse is better than dense.
Readability counts.
Special cases aren't special enough to break the rules.
Although practicality beats purity.
Errors should never pass silently.
Unless explicitly silenced.
In the face of ambiguity, refuse the temptation to guess.
There should be one-- and preferably only one --obvious way to do it.
Although that way may not be obvious at first unless you're Dutch.
Now is better than never.
Although never is often better than *right* now.
If the implementation is hard to explain, it's a bad idea.
If the implementation is easy to explain, it may be a good idea.
Namespaces are one honking great idea -- let's do more of those!
```

* Think hard about it, sleep on it, only then do it.
* Closed structures are the enemy of changeability (or, "Just use a map")
* You need fewer named abstractions than you think (or, "just use generic collections")
* The best time to avoid bugs is while designing.
* The second best time to avoid bugs is at dev time.
* The shorter the time between writing code and running it, the better (or, "use a REPL").
* Finding a good name is hard. Namespaces mean you need fewer unique names.
* Keep functions pure.
* If you can't keep a function pure, be explicit that it has side effects.
* Keep mutable state at the edges of your program. You need it much less than you think.
* A programming language is like an instrument: you need to know how to play it to get good sounds out.
* Prefer simple tools that you can understand and compose, to complex ones that get you up and running quickly.
* Never break your callers
