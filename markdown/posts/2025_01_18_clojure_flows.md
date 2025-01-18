# Clojure core.async.flow: A first look

> "There comes a time in all good programs when components or
> subsystems must stop communicating directly with one another."

## The basics

Core.async provides _channels_ which allow processes to communicate in
a decoupled way, by putting messages on queues. core.async.flow
(C.A.F) is built on top of these channels, and provides 2 higher level
abstractions: The **Process** - a thread of activity - and the
**Flow** - a directed graph of Processes which communicate via
channels. The objective of C.A.F: is separation of the application
logic from:

- topology
- execution
- communication
- lifecycle
- monitoring
- error handling

Flows provide and centralize these things. Use it by:

1. defining pure functions that do the computational part of
   processing messages.
2. wrap these in Processes which read from inchans and write to
   outchans.
3. defining a Flow that collects these processes and defines the
   connections between them.

The flow is a directed graph, where the nodes are the processes, and
the edges are connections between the inputs and outputs of the
processes. C.A.F handles all the internals of channel creation and
process instantiation. The C.A.F API provides functions for
starting/stopping, pausing/resuming, state-querying, and injecting
data into the flow.

## Running through the gist

The usage API:

```clojure
;; Create a flow by providing its config - i.e. the flow graph definition
(def g (flow/create-flow gdef))
;; start the flow, wrapping it in a monitoring function
(monitoring (flow/start g))
;; the flow actually starts paused, so you need to resume it to make
;; it do stuff
(flow/resume g)
;; wait a bit for some output to print to repl, then pause it
(flow/pause g)
(flow/inject g [:craps-finder :in] [[1 2] [2 1] [2 1] [6 6] [6 6] [4 3] [1 1]])
(flow/ping g)
(flow/stop g)
```

A flow-graph definition has two primary top-level keys: processes and
connections, called `:procs` and `:conns`. These are your nodes and
edges. 

The flow-graph in the gist linked below is very simple, without any
branching. It looks like this:

```
 dice-source
     ↓
   dedupe
     ↓
craps-finder
     ↓
  printer
```

The `dice-source` rolls two 6-sided dice. After going through a
deduplication function to remove consecutive identical rolls, the
`craps` finder removes cases where the dice don't sum to 2, 3 or 12
(presumably to do with the rules of craps, which I don't know). The
output is then printed.

The full definition of graph looks like this.

```clojure
(def gdef
  {:procs
   {:dice-source
    {:proc (flow/process
            {:describe (fn [] {:outs {:out "roll the dice!"}})
             :introduce (fn [_]
                          (Thread/sleep 200)
                          [nil {:out [[(inc (rand-int 6)) (inc (rand-int 6))]]}])})}

    :craps-finder
    {:proc (-> #(when (#{2 3 12} (apply + %)) %) flow/lift1->step flow/step-process)}

    :dedupe
    {:proc (flow/step-process #'ddupe)}

    :prn-sink
    {:proc (flow/process
            {:describe (fn [] {:ins {:in "gimme stuff to print!"}})
             :transform (fn [_ _ v] (prn v))})}}
   :conns
   [[[:dice-source :out] [:dedupe :in]]
    [[:dedupe :out] [:craps-finder :in]]
    [[:craps-finder :out] [:prn-sink :in]]]})
```

The connections part is pretty simple: a sequence of outchan to inchan
pairs creating a link between the two. The `flow/process` takes a map
of functions:

Describe (required) is a 0-arity function which returns a map of keys
`:ins`, `:outs`, which enumerate the inchans and outchans. The values
are just docstrings. For each key in `:ins`, a channel will be
created, and same for `:outs`. No key can be in both ins and outs.
These keys will be used to coordinate the flow in the connections
section. If you provide an ins key `:foo` for node `:bar`, your
connection tuple will be `[:bar :foo]`.

A process can be a _transforming_ process, taking inputs, or an
_introducing_ process, with no input channel. For any process you
create, you have to provide either the transform or introduce key, but
not both.

Transform is a 3-arity function which is called whenever a message
arrives. It does stuff with the input messages and (usually) will drop
a message on an output channel. The 3 params are `state in-name msg`,
and it returns a `[state' output-map]`. The output-map is a map of
output-channel names to values. output-map can be nil. You can see
this most clearly in the `dice-source` process:

```clojure
[nil ;; no state, so state is nil
 ;; puts two random numbers on the :out chan
 {:out [[(inc (rand-int 6)) (inc (rand-int 6))]]}]
```

Introduce is 1-arity function which takes a state and returns a `[state'
output-map]`. It will be called repeatedly to introduce data into the
flow.

Init is a 1-arity function returning the initial state. It's not shown
explicitly in the gist, but I mention it because it comes up later.

```clojure
;; prn-sink
(flow/process
  ;; a process with a single inchan 'in' and no outchan.
  ;; it prints any messages it receives. Note the transform returns nil
  {:describe (fn [] {:ins {:in "gimme stuff to print!"}})
   :transform (fn [_ _ v] (prn v))})

;; dice-source
(flow/process
  ;; a stateless introducing process which generates 2 random dice rolls
  ;; every 200ms and puts it on the only outchan 'out'
  {:describe (fn [] {:outs {:out "roll the dice!"}})
   :introduce (fn [_]
                (Thread/sleep 200)
                [nil {:out [[(inc (rand-int 6)) (inc (rand-int 6))]]}])})}
```

The second way of describing a process is a `step-process`. It's just
sugar on top of the process function. A step process takes a single
function with 0, 1 and 3 arity, with the arities corresponding to the
describe, init, and transform keys defined above. Looking at the ddupe:

```clojure
(defn ddupe
  ([] {:ins {:in "stuff"}
       :outs {:out "stuff w/o consecutive dupes"}})
  ([_] {:last nil})
  ([{:keys [last]} _ v]
   ;; updates the state to the msg
   [{:last v}
    ;; returns the input message, provided it's not the same as the
    ;; last one
    (when (not= last v) {:out [v]})]))
```

This could equally be described as

```clojure
(def ddupe2
  {:describe (fn [] {:ins {:in "stuff"}
                     :outs {:out "stuff w/o consecutive dupes"}})
   :init (fn [_] {:last nil})
   :transform (fn [{:keys [last]} _ v]
                [{:last v} (when (not= last v) {:out [v]})])})
```

The final process is craps-finder:

```clojure
(-> #(when (#{2 3 12} (apply + %)) %)
    flow/lift1->step
    flow/step-process)

;; separating out the function more explicitly:
;; The function returns the roll when it sums 2, 3 or 12. Otherwise nil
(defn craps-finder [roll]
  (when (#{2 3 12} (apply + roll)) roll))
```

`lift1` is a specific case of a more general function `lift*`. `lift*`
takes a stateless function of `msg->[msg']` and turns it into a
step-process with a single inchan and outchan.

```clojure
(fn [_ _ msg] [nil {:out (f msg)}])
```

Note that the out value must be a _vector_. Which is no good if your
function is, say an `int->int` function. When `lift*` is applied it'll put
an `int` on the outchan, which isn't allowed. lift1 wraps the vector for
you: `(fn [x] (when-some [m (f x)] (vector m)))`

The last part of the gist has the following, which doesn't actually
use flows, but illustrates the monitoring functionality of flows:

```clojure
(defn monitoring [{:keys [report-chan error-chan]}]
  (prn "========= monitoring start")
  (async/thread
    (loop []
      (let [[val port] (async/alts!! [report-chan error-chan])]
        (if (nil? val)
          (prn "========= monitoring shutdown")
          (do
            (prn (str "======== message from " (if (= port error-chan) :error-chan :report-chan)))
            (pp/pprint val)
            (recur))))))
  nil)

(monitoring (flow/start g))
```

from this you can see that `flow/start` returns a map with keys
`report-chan` and `error-chan`. The monitoring function just takes
stuff off these two channels and prints them. As we'll see this is
used with the `ping` function, which writes to these channels.
Presumably this would be used for logging and stuff - stdout/stderr
like.

## Running the gist

Loading up the gist (you need to have `{io.github.clojure/core.async
{:git/sha "c5a524c6607a792bc51a81f727e7d24ef4cae23b"}}` in your deps),
we can play around with it.

```clojure
(def g (flow/create-flow gdef))
g ;; it's a flow object
;; #object[clojure.core.async.flow.impl$create_flow$reify__17448 0x5c36347f "clojure.core.async.flow.impl$create_flow$reify__17448@5c36347f"]
```

If we start the flow, we can see that it does the 'monitoring start'

```clojure
(monitoring (flow/start g))
;; printed to repl
"========= monitoring start"
```

We can ping the flow, which prints the messages from the report-chan
(and presumably the error-chan) to the repl. Note that the procs are
all paused initially.

```clojure
(flow/ping g)
"======== message from :report-chan"
#:clojure.core.async.flow{:report :ping,
                          :pid :prn-sink,
                          :status :paused,
                          :state nil,
                          :count 0,
                          :ins
                          {:in
                           {:buffer-type FixedBuffer,
                            :buffer-count 0,
                            :put-count 0,
                            :take-count 0,
                            :closed? false}},
                          :outs {}}
"======== message from :report-chan"
#:clojure.core.async.flow{:report :ping,
                          :pid :craps-finder,
                          :status :paused,
                          :state nil,
                          :count 0,
                          :ins
                          {:in
                           {:buffer-type FixedBuffer,
                            :buffer-count 0,
                            :put-count 0,
                            :take-count 0,
                            :closed? false}},
                          :outs
                          {:out
                           {:buffer-type FixedBuffer,
                            :buffer-count 0,
                            :put-count 0,
                            :take-count 0,
                            :closed? false}}}
"======== message from :report-chan"
#:clojure.core.async.flow{:report :ping,
                          :pid :dedupe,
                          :status :paused,
                          :state {:last nil},
                          :count 0,
                          :ins
                          {:in
                           {:buffer-type FixedBuffer,
                            :buffer-count 0,
                            :put-count 0,
                            :take-count 0,
                            :closed? false}},
                          :outs
                          {:out
                           {:buffer-type FixedBuffer,
                            :buffer-count 0,
                            :put-count 0,
                            :take-count 0,
                            :closed? false}}}
"======== message from :report-chan"
#:clojure.core.async.flow{:report :ping,
                          :pid :dice-source,
                          :status :paused,
                          :state nil,
                          :count 0,
                          :ins {},
                          :outs
                          {:out
                           {:buffer-type FixedBuffer,
                            :buffer-count 0,
                            :put-count 0,
                            :take-count 0,
                            :closed? false}}}
```

When I resume, I start to get output printed to the repl

```clojure
(flow/resume g)
;; to repl
[1 2]
[2 1]
[2 1]
[1 1]
[2 1]
[6 6]
[6 6]
```

And a ping shows the following (edited).

```clojure
{:report :ping,
 :pid :craps-finder,
 :status :running,
 :state nil,
 :count 76,
 :ins {:in :snipped},
 :outs {:out :snipped}}

{:report :ping,
 :pid :dedupe,
 :status :running,
 :state {:last [3 4]},
 :count 79,
 :ins {:in :snipped},
 :outs {:out :snipped}}
```

If I do the pause, then the inject, I get the following report on the
craps finder - note the 7 buffer-count on the craps-finder in and
dedupe out (i.e. they are the same channel, and the inject just put
the messages on that channel)

```clojure
(flow/pause g)
(flow/inject g [:craps-finder :in] [[1 2] [2 1] [2 1] [6 6] [6 6] [4 3] [1 1]])
(flow/ping g)

{:report :ping,
 :pid :craps-finder,
 :status :paused,
 :state nil,
 :count 85,
 :ins
 {:in
  {:buffer-type FixedBuffer,
   :buffer-count 7,
   :put-count 0,
   :take-count 0,
   :closed? false}},
 :outs {:out :snip}}

{:report :ping,
 :pid :dedupe,
 :status :paused,
 :state {:last [1 1]},
 :count 88,
 :ins {:in :snip},
 :outs
 {:out
  {:buffer-type FixedBuffer,
   :buffer-count 7,
   :put-count 0,
   :take-count 0,
   :closed? false}}}
```

If I resume the flow again, I get the values I injected (except the
[4,3] which was filtered out by craps-finder) on the repl

```clojure
(flow/resume g)

[1 2]
[2 1]
[2 1]
[6 6]
[6 6]
[1 1]
```

Doing the stop just shuts it all down.

## Things not covered in the Gist

There are several things in the docs which are not covered by the
Gist:

All the processes in the `:procs` map of the graph only has the
`:proc` key (i.e. the actual process) in it. It can also take an
`:args` key (a map which is passed on to the process constructor), and
a `:chan-opts` map of configurations for the channels that will be
created. By default the channels created have a buffer size of 10
before they start rejecting input and exerting back pressure.

Multiple inputs/outputs for a proc are not shown here, but possible. A
consequence of not having a multiple inchan process is that there's no
illustration of using the second argument of the transform, which is
the inchan name, but presumably the pattern will be a pretty standard
"if inchan is a, do this, but if it's b, do this" sort of thing.

Multiple _connections_, e.g. one proc connects one output to the
inputs of two separate procs, isn't shown here. I think it's just more
tuples in the conns map, but it's not explicit.

There's a final set of keys to flow creation which allows the
ExecutorService to be specified. I don't know what that is, but the
keys are `:mixed-exec/:io-exec/:compute-exec`

For most of the flow-level controls there are proc-level equivalents -
e.g. `(pause-proc flow pid)`

The `:params` and `:workload` keys of the `:describe` key in process
creation are not illustrated, but I think they're just documentation,
as opposed to ins and outs, which are used in channel creation.

Proc can have a `:transition` function, which will be called when the
proc (or flow) is paused, resumed or stopped. This allows stuff like
cleaning up state.

It's not shown how to use `:introduce` for more 'realistic' workflows,
which would presumably be taking input from an API or something. The
docs mention a `::flow/control` channel.

Processes accept an option key which can include a `:workload` key,
which can be `:mixed`, `:io` or `:compute`. I think this affects the
internals of what kinds of threads are used, or how they are used.

There is one other function in the API ns: `futurize`, though I don't
know enough about async programming to understand what it does.

## Links

- [Overview](https://github.com/clojure/core.async/blob/master/doc/flow.md)
- [src](https://github.com/clojure/core.async/blob/master/src/main/clojure/clojure/core/async/flow.clj)
- [gist](https://gist.github.com/richhickey/fbc1439372b1f5319bb37aa1934d19b8)
- [reddit](https://www.reddit.com/r/Clojure/comments/1i27n1k/rich_introduces_new_namespace_in_coreasync_flow/)
