# Sussmans Regex

This is the implantation of, and challenges for, the regular expression creator in Hanson and Sussman's (henceforce H&S) [Software Design for Flexibility](https://mitpress.mit.edu/9780262045490/software-design-for-flexibility/), in Clojure.

## Motivation and preface

I assume you know what regex are.
H&S's assertion is that, while the theoretical basis of regex is sound, the usual implementations are often bad.
In particular, the double-meanings of parens and carets.

H&S present regex (ignoring these poor implementation choices) as an example of a _Combinator Language_, the main topic of chapter 2 of their book.

> A system of combinators is a set of primitive parts and a set of means of combining parts such that the interface specifications of the combinations are the same as those of the primitives

Regex is combinator language because fragments of valid regex can be embedded in others, and strung together to form new expressions that are also valid regex.

## The basic combinator language

The primitive patterns, each a function, each producing a regex expression, or `expr` are:

```
r-dot                      (equivalent to regex ".")
r-bol                      (equivalent to regex "^")
r-eol                      (equivalent to regex "$")
r-quote :: string          (equivalent to a character sequence)
r-char-from :: string      (equivalent to regex "[string]")
r-char-not-from :: string  (equivalent to regex "[^string]")
r-seq :: expr, expr, ...   (equivalent to regex sequence)
r-alt :: expr, expr, ...   (equivalent to regex |)
r-repeat :: min, max, expr (equivalent-ish to regex {})
```

Each function, when evaluated, will produce a correct POSIX BRE pattern.
Though it won't look like anything a person would write - for example:

```clojure
(r-repeat 3 5 (r-alt (r-quote "cat") (r-quote "dog")))
;;=>
"(((cat)|(dog))((cat)|(dog))((cat)|(dog))(((cat)|(dog))|)(((cat)|(dog))|))"
```

This is fine, because our goal is _correctness_, not concision or human readability (this is what the combinator language is for).

```clojure
(def char-needs-escaping #{\. \[ \\ \^ \# \*})

(defn dot [] ".")
(defn bol [] "^")
(defn eol [] "$")

(defn r-seq [& exprs]
  (str "(" (str/join exprs) ")"))

(defn r-quote [string]
  (->> string
       (mapcat #(if (char-needs-escaping %) (list \\ %) (list %)))
       str/join
       r-seq))

(defn r-alt [& exprs]
  (if (> (count exprs) 1)
    (apply r-seq
           (cons (first exprs)
                 (mapcat (fn [expr] (list "|" expr))
                         (rest exprs))))
    (r-seq)))

(defn r-repeat [min max expr]
  (apply r-seq
         (concat (repeat min expr)
                 (cond (not max)    (list expr "*")
                       (= max min) '()
                       :else (repeat (- max min)
                                     (r-alt expr ""))))))

(defn bracket
  "after applying f to string, wraps it in square brackets"
  [string f]
  (str/join
   (concat '(\[)
           (f string)
           '(\]))))

(def char-needs-escaping-if-in-brackets #{\] \^ \-})

(defn quote-bracketed-contents
  [members]
  (def debug members)
  (let [optional #(if (members %) (list \\ %) '())]
    (concat (optional \])
            (remove char-needs-escaping-if-in-brackets members)
            (optional \^)
            (optional \-))))

(defn r-char-from
  "Produces a regex which matches one character from a char sequence.
  In regex, this corresponds to putting the characters into square brackets.
  The characters '^' and '-' have special meaning (not and range respectively)
  This presents the problem that if you are _searching_ for those characters,
  as does the case where you need to search for the close bracket ']'."
  [string]
  (case (count string)
    0 (r-seq)
    1 (r-quote string)
    (bracket string
             (fn [members]
               (let [members (set members)]
                 (quote-bracketed-contents members))))))

(defn r-char-not-from [string]
  (bracket string
           (fn [members]
             (cons \^ (quote-bracketed-contents members)))))

(for [str (str/split-lines (slurp "sussman_regex.txt"))
      [re-nm re]  (update-vals {:a-c a-c :foo foo-bar-baz :dog dog-cat} re-pattern )]
  [re-nm str :-> (first (re-find re str))])
```

This is pretty much straight from the book, with minor substitutions of Clojure functions for Scheme ones.
One significant difference is that we don't need to 'double escape' parens and pipes, because we're targeting Clojure's own regex functions (e.g. `re-find`), rather than the POSIX _grep_ as in the book.
Another change is on the `quote-bracketed-contents` behavior, which I didn't see how the H&S version worked, but I might be misunderstanding the scheme.

Here are some examples of the language in use:

```clojure
(def a-c (r-seq (r-quote "a") (dot) (r-quote "c")))
(def foo-bar-baz
  (r-alt (r-quote "foo") (r-quote "bar") (r-quote "baz")))
(def dog-cat (r-repeat 3 5 (r-alt (r-quote "cat") (r-quote "dog"))))

(for [str (str/split-lines (slurp "sussman_regex.txt"))
      [re-nm re]  (update-vals {:a-c a-c :foo foo-bar-baz :dog dog-cat} re-pattern)]
  [re-nm str :-> (first (re-find re str))])
```

## Exercise 2.6: Adding * and +

The `*` and `+` expressions mean "zero/one or more of the preceding expression".
This is simple to implement in terms of `r-repeat`:

```clojure
(defn r-* [expr] (r-repeat 0 nil expr))
(defn r-+ [expr] (r-repeat 1 nil expr))
```

## Exercise 2.7: Null sequences

Our `r-repeat` function has a problem: the `(r-alt expr "")`.
This evaluates to `(expr|)`, meaning "this or nothing" - the intended effect being "one or none".

```clojure
(r-repeat 2 5 "beans")
;;=>
"(beansbeans(beans|)(beans|)(beans|))"
```
This pattern of a `|` preceding a `)` is undefined behavior, according to the spec.
So we need an alternative way of decribing "one or none".

The simplest way I can 
