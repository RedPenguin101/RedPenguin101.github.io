# Propagator Programming Model 1 - a first look

## Intro

I'm reading up on the Propagator Programming Model (PPM) proposed by Radul and Sussman[^1].
I've only read the summary of how the model works so far.
My goal is to explore the space and implementation challenges myself before reading what Radul has to say about it.

[^1]:Full 2009 paper: [Revised Report on the Propagator Model, by Radul and Sussman](https://groups.csail.mit.edu/mac/users/gjs/propagators/)

## The PPM

A theme of Sussman's work, and of his students, has always been extensible programs. 
He often compares the design of programs to the design of 'natural'/organic systems, noting how the large amount of redundancy and fault-tolerance of the latter provides flexibility and resilience.
In contrast, the programs we build tend to have a _single_ way of solving a problem, and will immediately fall over if the exact conditions it needs to solve that problem are not met, or change.

> The most important problem facing a programmer is the revision of an existing program to extend it for some new situation. Unfortunately, the traditional models of programming provide little support for this activity. The programmer often finds that commitments made in the existing code impede the extension, but the costs of reversing those commitments are excessive. - Radul, in the paper

The PPM is a model with the goal of allowing extension.
It has two fundamental concepts:
The Cell (which contains data), and a Propagator - a stateless machine which 'watches' input cells for changes, recomputes results based on the new values, and outputs them to another cell.
In the below example, if the value of `CellA` changes, then `PropX` will be aware of that, and automatically update the value of `CellC`.
The idea here is that the program designer can, by simply adding propagators, extend the system to include new methods generating results without damaging the existing methods.

```
CellA ------
            \
	        PropX -----> CellC
            /
CellB ------
```

There are a couple of parallels that immediately jump to mind.
The first is the spreadsheet[^2], where formula cells point to other cells, and automatically update when the referenced cells are changed.
The second is the Erlang-style of actor / message passing model, where 'processes' are spawned, and have 'inboxes' that can receive messages.

[^2]: Apparently, the first spreadsheet, VisiCalc, was inspired by this model, though that somewhat contradicts other things I've heard about the development of the spreadsheet.

Here's the basic syntax from the paper:

```lisp
(define-cell a)
(define-cell b)
(add-content a 3)
(add-content b 2)
(define-cell answer (e:+ a b))
(run)
(content answer) => 5
```

## Implementation

Off the top of my head I can think of two plausible ways to implement this: 
First, a synchronous program which maintains a DAG[^3] of dependencies.
When a cell is changed, the program will determine all the dependent propagators and run them, changing other cells which would in turn have their dependent propagators called.
A second implementation would be using asynchronous channels, with each cell having an in-and-out channel, and each propagator subbing to the relevant out-channels and pushing output to the relevant cell in-channel.

[^3]: Cycles are technically allowed I think, but require special handling, so for simplicity I'll ignore them for now.

Intuitively I think the former will have some order-dependence issues (not that the async won't), since the order in which dependent propagators are called will depend on the order in which the graph is navigated (e.g. DFS vs. BFS).
But the inherent complexity of async leads me to start with the graph implementation.

Nodes on the graph are cells (C) and propagators (P).
A cell will contain a value, a merge function (ignored for now), and a list of dependent propagators.
A Propagator has a list of input cells, a function, and an output cell.
When a cell value is updated, the list of P will have to be registered for recalculation.
Here's a rough psuedo-code implementation with the recursive propagation.

```
cell = value deps
prop = inputs fn output

graph: 
	c1 = 2 [p1 p2] // cell c1 has value 2, and dependent propagators p1 and p2
	c2 = 3 [p1 p2]
	c3 = 5 []
	c4 = 6 []
	p1 = [c1 c2] + [c3]
	p2 = [c1 c2] * [c4]

propagate (update graph c1 4) [p1 p2]

propagate g [p ps]
  if not p, g
  else
  let out = apply p:fn p:inputs
      new-props = p:output:deps
    recur (update graph p:output:value out)
	      concat ps new-props
```

In Clojure, here's a pure implementation of this:

```clojure
(defn sum     [xs] (apply + xs))
(defn product [xs] (apply * xs))

                     ; name  val  deps
(def g {:cells       {:c1    [2    [:p1 :p2]]
                      :c2    [3    [:p1 :p2]]
                      :c3    [5    []]
                      :c4    [6    []]}
                     ; name  inputs    function  output
        :propagators {:p1   [[:c1 :c2] sum      :c3    ]
                      :p2   [[:c1 :c2] product  :c4    ]}})

;; some private get/set abstractions
(defn- cell-value      [g c]   (get-in g   [:cells c 0]))
(defn- cell-update-val [g c v] (assoc-in g [:cells c 0] v))
(defn- cell-deps       [g c]   (get-in g   [:cells c 1]))

(defn all-cell-vals [g] (update-vals (:cells g) first))

(defn- prop-output-cell [g p] (get-in g [:propagators p 2]))

;; For a propagator, gets the value of the inputs and calculates the output
(defn- prop-run         [g p] ((get-in g [:propagators p 1]) (map #(cell-value g %) (get-in g [:propagators p 0]))))

(defn- propagate [g [p & ps]]
  (if (not p) g
      (let [out-cell (prop-output-cell g p)]
        (recur (cell-update-val g out-cell (prop-run g p))
               (into ps (cell-deps g out-cell))))))

(defn update-cell [g c v]
  (propagate (cell-update-val g c v)
             (cell-deps g c)))

(all-cell-vals (update-cell g :c1 4))
;; => {:c1 4, :c2 3, :c3 7, :c4 12}
```

And here's a layer on top of this that adds the stateful registry that is in the Scheme version.
Note the example near the end mirrors Radul's version, with the notable exception that 'running' is done incrementally as propagators are added - I'm not sure how important that is to the model.

```clojure
(def graph-register (atom {}))

(defn- prop? [val]
  (and (vector? val) (= :prop (first val))))

(defn- add-dependencies [g prop-name prop-input-cells]
  (reduce (fn [g' c]
            (update-in g' [:cells c 1] conj prop-name))
          g
          prop-input-cells))

(defn- add-cell-and-prop [g cell prop-inputs prop-fn]
  (let [prop-name (keyword (gensym "prop-"))]
    (-> g
        (assoc-in [:propagators prop-name] [prop-inputs prop-fn cell])
        (assoc-in [:cells cell]      [nil []])
        (add-dependencies prop-name prop-inputs)
        (propagate [prop-name]))))

(defn define-cell! [nm val]
  (if (prop? val)
    (swap! graph-register add-cell-and-prop nm (nth val 2) (second val))
    (swap! graph-register assoc-in [:cells nm] [val []])))

(defn content! [cell] (cell-value @graph-register cell))
(defn update-cell! [cell value] (swap! graph-register update-cell cell value))

;; EXAMPLES:
;; Following the above
(reset! graph-register {})
(define-cell! :c1 2)
(define-cell! :c2 3)
(define-cell! :c3 [:prop sum [:c1 :c2]])
(define-cell! :c4 [:prop product [:c1 :c2]])
@graph-register
;; => {:cells
;;     {:c1 [2 [:prop-8123 :prop-8124]],
;;      :c2 [3 [:prop-8123 :prop-8124]],
;;      :c3 [5 []],
;;      :c4 [6 []]},
;;     :propagators
;;     {:prop-8123 [[:c1 :c2] #function[propagators/sum] :c3],
;;      :prop-8124 [[:c1 :c2] #function[propagators/product] :c4]}}

(all-cell-vals @graph-register)
;; => {:c1 2, :c2 3, :c3 5, :c4 6}

;; Following Radul's example
(reset! graph-register {})
(define-cell! :a 3)
(define-cell! :b 2)
(define-cell! :answer [:prop sum [:a :b]])
(content! :answer)
;; => 5

;; with multiple propagations
(reset! graph-register {})
(define-cell! :a 3)
(define-cell! :b 2)
(define-cell! :c 4)
(define-cell! :intermediate [:prop sum     [:a :b]])
(define-cell! :final        [:prop product [:c :intermediate]])
(content! :final)
;; => 20
;; being (3+2)*4
(update-cell! :a 5)
(content! :final)
;; => 28
;; being (5+2)*4
(update-cell! :b 10)
(content! :final)
;; => 60
;; being (5+10)*4
```

The thing (or rather one thing, I expect there are many) in which my implementation is different to Radul's is the propagation function syntax.
Compare:

```
(define-cell answer (e:+ a b))
(define-cell! :answer [:prop sum [:a :b]])
```

The first is definitely better, but I think it would require macros for the deferred evaluation, and I don't want to get into that yet.

### What's missing?
From skimming the rest of this paper, this implementation misses some of the core value propositions of the PPM, which hopefully I'll explore more as I read further into the paper.

* Bi-directionality: if `z<-x*y`, then `x<-z/y`. A full system would be able to update `x` when `z` or `y` changes
* A constraint system - an extension of the bi-directionality
* Conditional propagation.
* Composing propagator networks
* Things which I don't even understand yet.

