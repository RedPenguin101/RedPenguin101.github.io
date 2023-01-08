# Dijkstra's shortest path

https://www.youtube.com/watch?v=GazC3A4OQTE

When you have a graph a common requirement is to find the shortest path between two nodes.
We'll use the analogy of roads.

Each node represents a junction in the road system. 
The length of the edges represent the distance between the junctions (or how long it takes to get between the junctions).
Dijkstra does a flood-fill through the graph, but does it in an order that makes sense. That is, we check the quickest roads first.

To implement Dijkstra we need a graph, and a starting node. And we need an idea of how long the path to a node is when we've calculated it.
We start at node S. S has a distance of 0, because we're already there. Everything else has a distance of infinity, because we haven't visited them yet.
We line up all our nodes, with their distance, in a *prioritised queue*.
We start at S, and we look at all the connections of S. S in this example has connections A (7), B (2), C (3)

* S -> FINISHED
* B 2 S
* C 3 S
* A 7 S
* D Inf

Now we pick the shortest path: B.
B has connections D (4), A (3), H (1). So our new queue looks like

* B -> FINISHED
* C 3 S
* H (2+1=3) B
* A (2+3=5) B
* D (2+4=6) B

Next is C

* C -> FINISHED
* H 3 B
* A 5 B
* L (3+2=5) C
* D 6 B

Then

* H -> FINISHED
* A 5 B
* L 5 C
* G (3+2=5) H
* D 6 B
* F (3+3=6) H

Then

* A -> FINISHED
* L 5 C
* G 5 H
* D 6 B (5+4=9, so original 6 B stays)
* F 6 H

Then

* L -> FINISHED
* G 5 H
* D 6 B
* F 6 H
* I (5+4=9) C
* J (5+4=9) C

Then

* G -> FINISHED
* D 6 B
* F 6 H
* I 9 C
* J 9 C
* E (5+2=7) G

Last, you backtrack: E->G, G->H, H->B, B->S. So the shortest path is SBHGE.

Stated concisely, the algorithm is:

1. Add start-node to priority queue, with distance 0
2. take the first node N from the queue, with distance Nd
3. get nodes connected to N, [M], and distances from N [Md] 
4. for each node in M:
    1. If M is in the Finished pile, ignore it
    2. If M is not in the priority queue, add it to the priority queue with priority (Nd + Md)
    3. If M is in the priority queue, assign it the priority (min current-priority (Nd + Md))
5. If M is the target, you are don
7. If M is not the target, go to step 2

## An implementation in Clojure

First, a very simple undirected graph implementation, with an API of 

* `connections : graph, node -> #{[from to length]}`
* `nodes : graph -> #{nodes}`

```clojure
(defn edge [graph start end weight]
  (-> graph
      (conj [start end weight])
      (conj [end start weight])))

(defn nodes [graph]
  (set (map first graph)))

(defn connections [graph node]
  (set (filter #(#{node} (first %)) graph)))

(def graph (-> #{}
               (edge :s :a 7)
               (edge :s :b 2)
               (edge :s :c 3)
               (edge :a :d 4)
               (edge :a :b 3)
               (edge :b :d 4)
               (edge :b :h 1)
               (edge :c :l 2)
               (edge :d :f 5)
               (edge :e :g 2)
               (edge :e :k 5)
               (edge :f :h 3)
               (edge :g :h 2)
               (edge :i :l 4)
               (edge :i :j 6)
               (edge :i :k 4)
               (edge :j :l 4)
               (edge :j :k 4)))

(nodes graph)
;; => #{:e :s :l :k :g :c :j :h :b :d :f :i :a}

(connections graph :s)
;; => #{[:s :c 3] [:s :a 7] [:s :b 2]}
```

Next, a priority queue implementation, with an API of

* `priority : queue -> queue-entry` (returns the priority)
* `update-queue : queue, [from-node, to-node, distance] -> queue`

A queue entry here will be `[distance from-node]` (as in the example)

```clojure
(defn update-queue [queue [from to dist]]
  (let [base (first (from queue))]
    (if (or (not (to queue)) (< (+ base dist) (first (to queue))))
      (assoc queue to [(+ base dist) from])
      queue)))

(defn priority [queue]
  (ffirst (sort-by (juxt (comp first second) first) queue)))

(deftest q
  (testing "an unseen node gets added"
    (is (= {:s [0 :s], :b [2 :s]}
           (update-queue {:s [0 :s]} [:s :b 2]))))
  (testing "a seen node where the new route is shorter gets updated"
    (is (= [5 :b]
           (:a (update-queue {:s [0 :s] :b [2 :s] :a [7 :s]}
                             [:b :a 3])))))
  (testing "a seen node where the new route is longer is unchanged"
    (is (= [6 :b]
           (:d (update-queue {:a [5 :b] :l [5 :c] :g [5 :h] :d [6 :b] :f [6 :h]}
                             [:a :d 4]))))))
```

Our Dijkstra is going to take: 

* an array of 'done' nodes, in the format [node path-length from-node]
* the graph
* the target node
* the priority queue

And will return the list of done nodes when the target is reached.

```clojure
(defn dijkstra
  ([graph start end] (dijkstra [] graph end {start [0]}))
  ([done graph target queue]
   (let [pri (priority queue)]
     (if (target queue)
       (conj done (cons target (target queue)))
       (recur (conj done (cons pri (pri queue)))
              graph
              target
              (dissoc (reduce update-queue queue (connections graph pri))
                      pri))))))

(dijkstra graph :s :e)
;; => [(:s 0) (:b 2 :s) (:c 3 :s) (:h 3 :b) (:b 4 :h) (:s 4 :b) (:a 5 :b) (:g 5 :h) (:e 7 :g)]
```

Finally, we backtrack through the Dijkstra result to get to the path 

```clojure
(defn follow [path m node]
  (if (node m)
    (recur (conj path node) m (node m))
    (reverse (conj path node))))

(defn find-path [dijkstra-result]
  (follow [] (reduce (fn [A [this _ from]]
                       (assoc A this from))
                     {}
                     (reverse dijkstra-result))
          (first (last dijkstra-result))))

(deftest t
  (is (= [:s :b :h :g :e]
         (find-path (dijkstra graph :s :e)))))
```

## New Nov 2022: An Elixir implementation

Basically the same implementation.

```elixir
defmodule Dijkstra do
  import Utils
  import Enum

  # General graph stuff
  # adding _bidirectional_ edge
  def add_edge(graph, {start, finish, weight}) do
    graph
    |> MapSet.put({start, finish, weight})
    |> MapSet.put({finish, start, weight})
  end

  def nodes(graph), do: map(graph, &first/1) |> uniq
  def connections(graph, node), do: filter(graph, fn {start, _, _} -> start == node end)

  # Priority Queue stuff
  # queue is a map of:
  #       entry => {priority, origin}
  # e.g %{:s    => {0, :s}}
  # priority is the thing with the shortest distance
  def priority(queue) do
    {node, {dist, origin}} = min_by(queue, compose(&second/1, &first/1))
    {node, dist, origin}
  end

  # Updating the queue is passed an edge
  # updating looks up the to and from nodes in the queue
  # if the to-node isn't in the queue, just put it in the queue
  # if the to-node is already there, and the existing distance to
  # reach that nde is greater than the 'new' one, then put the new distance
  # in the queue, along with the new origin.
  def update_queue({from, to, dist}, queue) do
    base_from = Map.get(queue, from)
    base_to = Map.get(queue, to)
    new_dist = (base_from |> first) + dist

    if !base_to || new_dist < (base_to |> first) do
      Map.put(queue, to, {new_dist, from})
    else
      queue
    end
  end

  def dijsktra(graph, start, finish), do: dijsktra([], graph, finish, %{start => {0, start}})
  # Done is list of {node, dist, from}
  # the shortest path is the distance (second element) of the first entry
  # in done
  def dijsktra(done, graph, target, queue) do
    reached? = Map.get(queue, target)
    if reached? do
      {dist, from} = reached?
      [{target, dist, from} | done]
    else
      pri = priority(queue)
      new_done = [pri | done]
      new_queue = graph
                  |> connections(pri |> first)
                  |> reduce(queue, &update_queue/2)
                  |> Map.delete(pri |> first)
      dijsktra(new_done, graph, target, new_queue)
    end
  end

  # dijkstra returns all the nodes visited before you hit the target,
  # so some won't be on the shortest path. You need to 'walk' the result
  # to find the path
  def find_path([]), do: []
  def find_path([{to, dist, from} | rst]) do
    unconnected = fn {x,_,_} -> x != from end
    [{to, dist} | rst |> drop_while(unconnected) |> find_path]
  end

  def demo do
    MapSet.new
    |> add_edge({:s, :a, 7})
    |> add_edge({:s, :b, 2})
    |> add_edge({:s, :c, 3})
    |> add_edge({:a, :d, 4})
    |> add_edge({:a, :b, 3})
    |> add_edge({:b, :d, 4})
    |> add_edge({:b, :h, 1})
    |> add_edge({:c, :l, 2})
    |> add_edge({:d, :f, 5})
    |> add_edge({:e, :g, 2})
    |> add_edge({:e, :k, 5})
    |> add_edge({:f, :h, 3})
    |> add_edge({:g, :h, 2})
    |> add_edge({:i, :l, 4})
    |> add_edge({:i, :j, 6})
    |> add_edge({:i, :k, 4})
    |> add_edge({:j, :l, 4})
    |> add_edge({:j, :j, 4})
    |> dijsktra(:s, :e)
    |> find_path()
    # => [e: 7, g: 5, h: 3, b: 2, s: 0]
  end
end
```