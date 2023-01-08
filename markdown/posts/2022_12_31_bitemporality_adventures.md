# Adventures in Bitemporality

Let's say we're a company that provides regular services to other companies, and we periodically bill them for them. 
We have a system which calculates the billable amounts.

We a simple reference system to track our customers.
For now, the only attribute we care about is their name. 
We also assign each company an internal ID.

We set up our system on 01-15 (MM-DD), and want to put a company in our database.
One of our clients is called 'Old Name'. It was incorporated on 01-01 (note: we're not actually _tracking_ incorporation dates).

They want to change their name to 'New name'. 
They do it on 02-01, but we only find out about it on on 02-15.

So we have the following events:

* 01-01: the company 'Old Name' is incorporated
* 01-15: we add the company into our reference system, with an entity ID of 1
* 02-01: Old Name changes their name to New Name
* 02-15: we find out about it, and update our records accordingly

Our first table, when we set it up on 01-15, might look like this:

```
id   name
1    Old Name
```

Then when we find out they've changed their name on 02-15, we update the table (note the ID remains the same, because it's the same company, just with a different name):

```
id   name
1    New Name
```

Even on the surface level there are problems with this:
Your system now doesn't 'remember' that the company with ID 1 used to be called 'Old Name'.
If you get correspondence which erroneously refers to 'Old Name'.
Your system won't be able to help you out, because it doesn't remember.

Another scenario: say it's currently 2-20.
We want to issue an invoice to the company, but the date of the invoice is 1-30 (the day before the name change happened).
For tax reasons, the invoice has to reference the _old name_ of the company.
But a) your system doesn't even 'remember' the old name, and b) it couldn't know that on 1-30 the name was 'Old Name'.

## History tables and Timelines

What we need to maintain, instead of the current state of the entity, is a the **history** of the entity:
An unbroken series, from 'birth' to 'now' of the attributes of an entity.

A common way to implement this is to replace our entity table with a 'history' table.

```
COMPANY_TABLE
// state on 1-15
uid   eid  from  to    name
1     1    1-1   Null  Old Name

// state on 2-15
uid   eid  from  to    name
1     1    1-1   2-1   Old Name
2     1    2-1   Null  New Name
```

Querying the table for the value as it was on 1-30 (`get company 1 1-30`) would look something like this:

```sql
select * from COMPANY_TABLE
where eid = 1
  and from_date > '01-30'
  and (to_date is null or to_date <= '01-30')
```

Changing the name to 'New Name 2' on 3-1 would look like this

```sql
UPDATE COMPANY_TABLE
SET to = '03-01'
WHERE to is Null;

INSERT INTO COMPANY_TABLE (eid, from_date, to_date)
VALUES (1, '03-01', Null, 'New Name 2');
```

```
COMPANY_TABLE
uid   eid  from_date  to_date    name
1     1    1-1        2-1        Old Name
2     1    2-1        3-1        New Name
3     1    3-1        Null       New Name 2
```

We have had to change record 2 so the 'to' date is no longer `Null`, and inserted the line with `uid=3`.

To generalise this, we can say that what we are creating for each 'entity' we care about is a **timeline**. A timeline-entry has the structure `{FROM TO <DATA>}`. A timeline is a collection of timeline-entries. `[{FROM TO <DATA>}]`.

There are certain invariants for a timeline:

1. the records in a timeline must be 'contiguous': there must be no instant between the earliest `to` to the last `from` which is not covered by a record.
2. the records must be non-overlapping: no instant can appear in multiple records.
3. there must be at most one record with a NULL value for `from`
4. there must be at most one record with a NULL value to `to`

## Mutable History

The update model proposed above is compatible with immutable history.
The timeline can only move forward.
The only acceptable update is to add a new record onto the 'end' of the timeline, with a from date that is _after_ the from data of the previous end.
For example, you could not go back and insert a new name for the period `1-15` to `2-15`.

But what if you do want to do that?
There are certainly real-world circumstances when you might need to do that.
And there's nothing _stopping_ you from doing it.
But it is not trivial.

Say you want to insert into the timeline 'Tween Name', from 1-15 to 2-15:

```
COMPANY_TABLE
uid   eid  from  to    name
1     1    1-1   1-15  Old Name
3     1    1-15  2-15  Tween Name
2     1    2-15  Null  New Name
```

Notice that _both_ existing records had to be modified: the from and to date respectively. Also, the rid is now 'out of order' - i.e. doesn't follow the actual timeline.

If we now insert another record, 'OW name', valid from 1-14 to 2-16, we get this

```
COMPANY_TABLE
uid   eid  from  to    name
1     1    1-1   1-14  Old Name
4     1    1-14  2-16  OW Name
2     1    2-16  Null  New Name
```

Here, we not only modified records 1 and 2 _again_, but we actually had to _delete_ record 3.

All this deleting and updating-in-place should be starting to make your skin crawl.
Writing SQL to do this safely and reliably would not be a fun thing.

There is another problem here as well:
The whole point of introducing the history table was to _remember_ the changes that were made, and when.
But now we want to _change the changes_, and we will run into the same problem, a level down:
Our system won't remember that, at some point, we _thought_ the name of the company was 'Tween Name' (even though it wasn't).

Say you're giving a presentation, and one of the slides (prepared a while ago) shows the company 'Tween Name'.
Someone asks "who is that?"
You look in the system, and you see no reference to a company called 'Tween Name' - only Old Name, OW Name, and New Name.
You have forgotten, again.[^1]

[^1]: Of course this example is quite unrealistic - the company name itself is unlikely to change that frequently. But it could easily be the case for other attributes.

What we need is to record the history of the history.

## Perception and Bitemporality

Recall the timeline of events:

* 01-01: the company 'Old Name' is incorporated
* 01-15: we add the company into our reference system, with an entity ID of 1
* 02-01: Old Name changes their name to New Name
* 02-15: we find out about it, and update our records accordingly

Up to now we've focused on when things 'actually' happened: on 1-1 the company was incorporated.
On 2-1 the name was changed.
What we have ignored is that, in both cases, there is a gap between when the event _actually happened_ and when we _recorded it_ in our system.
In the first case, we recorded the incorporation in our system on 1-15, because that's when they became a customer.
In the second case, we recorded the name change on 2-15 because that's when we (and our system) found out about it.

What we have, in effect, are _two separate timelines_: One representing events that actually happened, and one which represents when we recorded the effect of those events.
Call these two timelines the 'actual timeline', and the 'knowledge timeline'.

On a knowledge date basis, you could represent the situation like this.

```
COMPANY_TABLE_KNOWLEDGE
uid   eid  from  to    name
1     1    1-15  NULL  Old Name

COMPANY_TABLE_KNOWLEDGE
uid   eid  from  to    name
1     1    1-15  2-15  Old Name
2     1    2-15  Null  New Name
```

### Aside: What is 'actual' date something happened?

I called the first timeline the 'actual' timeline.
But there's a bit of subtly there.

Consider these two timelines as being observations of the same events from the perspective of two different participants.
The first (where the incorporation happened on 1-1 and the name change on 2-1) is the perception of the Company itself, and importantly also of the 'Company Registry', who is legally responsible for maintaining company records.

We can refer to these as being the 'real world' or 'actual' events, but really this is just convention:
The group who will agree on this perception includes a recognised 'authority' (the Registry, having the force of law), which gives the perception of the Registry a special significance.
Most neutral participants would concede that the authority's perception is the correct one.
So we call this the 'actual' timeline, for convenience.

The second perception is your system's.
Your system observed the events on 1-15 and 2-15 respectively.
This viewpoint can often be relevant to you, because ultimately your business is constantly making decisions based on what your system knows at the time the decision is made.

Ultimately there is nothing fundamentally special or privileged about each of these viewpoints: 
they are just different perceptions of the same events.
We are lucky here that there _is_ an authority whom we can rely on to provide an authoritative ordering of events.
Sometimes we won't be so lucky, and then things can get _hard_.
We are also simplifying things significantly, in that the two perspectives differ in _when_ things are changing, but do not have different perceptions of _what_ is happening, which would add another layer of complexity.

### Bitemporality: a product of timelines

We saw that you can represent the two timelines (the actual and knowledge) separately pretty easily.
Things get more complicated when you try to represent both at the same time - which is what we need to do to solve the 'history of the history' problem posed above.
This is the primary problem of bitemporality that the rest of this post will explore.

## A matrix representation of two-dimensional time

We have two timelines for our entities:
1. The 'actual' timeline (the perception of the authority)
2. The 'system' timeline (the perception of the system, really a proxy for the knowledge state of our business)

Things get more complicated when you try to record _both timelines at once_, because they become a product.
You can represent them as a partially-filled matrix:

```
AD=Actual Date
KD=Knowledge Date

X=not in system
O=old name
N=new name

AD→  12 1 1 2 2 2
	  | | | | | |
KD    1 1 1 1 1 1
↓         5   4 5	
12-1  X
1-1   X X
1-15  X O O
2-1   X O O O
2-14  X O O O O
2-15  X O O N N N
```

Thinking in multi-dimensional time is hard, so the semantics of this can be tough to unpack.

You can read the 'rows' as views of history.
So at `KD=2-14` we thought the company started existing on 1-1, and from that point up to 'now' (2-14) the name was Old Name.
But b `KD=2-15` we learned that this was incorrect: the company did start existing on 1-1, and was initially named 'Old Name', but from 2-1 it was called 'New Name'.

You can read the columns as history of the our knowledge of the state of the system at a particular date: What did we think the name was at 2-1?
Reading down the relevant column, we first admit our ignorance: when `KD=12-1 and 1-1 and 1-15`, the knowledge dates are _before_ the actual dates.
Our system won't guess the future, so it just says "I don't know".
At `KD=2-1` we thought the name was 'Old Name'.
Note that this is `KD=AD`. 
This has special significance, because it represents 'what we knew at the time'.
At `KD=2-14` we still thought the name at `AD=2-1` was 'Old Name'.
But at `KD=2-15` we finally realize that the name at `AD=2-1` was actually 'New Name'.

So to coin a few terms, we broadly have three 'queries' we can do over two time dimensions:

1. **Co-temporal**: AD=KD. "What we knew at the time". Based on what we knew at the time, the name of the company at 2-1 was 'Old Name'
2. **Retrospective**: AD<KD. Hindsight. Based on what we knew at 2-15, the name of the company at 2-1 was 'New Name'
3. **Predictive**: where AD>KD. We are asking the system what it's knowledge state was _in the future_. Erroring out here is a reasonable option, since you might not want your system to try and 'guess' things. If you do, then this would be like: Based on what we know at 2-15, the name of the company at 3-1 will be 'New Name'.

Here are some more examples:

```
    AD   KD
get 1-1 1-03 => nothing
get 1-1 1-16 => Old Name 

get 2-1 2-01 => Old Name          (A=K: cotemporal)
get 2-1 2-16 => New Name          (A<K: retrospective)
get 2-1 1-16 => Old Name OR Error (A>K: predictive)

get 3-1 2-15 => New Name OR Error
```

## Histories of histories of histories? No thanks

The problem we set out to solve with bitemporality was that we wanted our timeline to be mutable, but also to be able to remember _historical_ changes we made to the actual timeline.
We did that by adding a second time dimension.
Thinking in general terms, there is no reason the second timeline can't _also_ be immutable.
That is, you could change when your system found out about things.
However this would bring the same problem of 'forgetting' changes, and the solution would be the same:
Add a _third_ time dimension.

In practice, however, it's hard to build a real use-case for 'tritemporal' time.
If reasoning in bitemporal time is hard, reasoning in three time dimensions doesn't sound attractive.
The reality is that, if your two time dimensions can reasonably be called the 'actual' and 'knowledge' timelines, then you're probably OK, because your knowledge _can_ be said to be immutable.
You don't suddenly discover that you knew something a week ago.
If you knew it a week ago, you knew it a week ago.

So while it's not absurd to think about higher dimensions of time, we will constrain ourselves to two dimensions.

## Alternative to matrix representation: Timeline of Timelines

Matrix representation has the benefits that it doesn't privilege one timeline over another: you can easily read it either way.
It's also completely immutable: you don't lose information, and can fully recreate a previous state from the current state.

The downsides are that it can be tough to understand, and that it's really hard to represent as a data structure. A matrix that is growing in both dimensions all the time isn't easy to model either in structures (i.e. nested name-values) or tables.

We've already seen the timelines structure: `TIMELINE = [{FROM TO <DATA>}]`.
There's no reason this can't be extended to a timeline of timelines:
`TOT = [{FROM TO <TIMELINE>}]`.
The only real requirement here is that you have to _order_ the dimensions:
Do you want a KD-Timeline of AD-Timelines, or an AD-Timeline of KD-Timelines?
This impacts access patterns and such.
In most cases, the answer is pretty simple: you want a KD-timeline of AD-Timelines.
You want to have _immutable_ timeline on the 'outside', and the mutable on the inside.
A couple of representations:

```
k-from  k-to
1-15    2-15
  Name       a-from a-to
  Old Name   1-1    Null

K-from  k-to
2-15    Null
  Name       a-from a-to
  Old Name   1-1    2-1
  New Name   2-1    Null
```

```
EID=1
[{1-15 2-15 [{1-1 Null Old-Name}]},
 {2-15 Null [{1-1 2-1  Old-Name}, 
             {2-1 Null New-Name}]}]
```

### Timeline of timelines in code

The datastructure for timelines in clojure are basically as above:

```clojure
;; timeline
[{:from-date "01-01", :to-date "02-01", :data "Old Name"} 
 {:from-date "02-01", :to-date nil,     :data "New Name"}]
 
;; timelime of timelines
[{:from-date "01-15", :to-date "02-15",
  :data [{:from-date "01-01", :to-date nil, :data "Old Name"}]} 
 {:from-date "02-15", :to-date nil,
  :data [{:from-date "01-01", :to-date "02-01", :data "Old Name"}
         {:from-date "02-01", :to-date nil, :data "New Name"}]}]
```

This implementation of this would be:

```clojure
;; tot = timeline of timelines
(defn update-timeline-bitemporal [tot new-data from to know-date]
  (update-timeline tot
   (update-timeline (:data (last tot)) new-data from to)
   know-date nil))
   
(defn update-timeline [timeline new-data from-date to-date] 
  (->> timeline
       (mapcat #(eliminate-overlap from-date to-date %))
       (cons {:from-date from-date, :to-date to-date :data new-data})
       (sort-by :from-date)
       vec))

(defn eliminate-overlap [from to entry]
  (case (overlap from to (:from-date entry) (:to-date entry))
    :full    []
    :contain [(assoc entry :to-date from) (assoc entry :from-date to)]
    :left    [(assoc entry :to-date from)]
    :right   [(assoc entry :from-date to)]
    :none    [entry]))
	
(defn overlap [new-from new-to old-from old-to]
  (cond
    (d< old-from new-from new-to old-to)  :contain
    (d<= new-from old-from old-to new-to) :full
    (d<= old-from new-from old-to)        :left
    (d<= old-from new-to old-to)          :right
    :else                                 :none))
```

The bitemporal update is just updating the _outer_ timeline _with_ the update of the inner timeline.
The update itself places the new timeline entry in the timeline, and detects and resolves any overlaps, adjusting dates and deleting entries as required.
Note the `d<` functions are just ordering predicates for whatever representation of dates you're using.

```clojure
(update-timeline-bitemporal
  ;; first timeline: when we only knew the old name from 1-15 -> 2-15
 [{:from-date "01-15", :to-date "02-15",
   :data [{:from-date "01-01", :to-date nil, :data "Old Name"}]} 
  ;; second timeline: the current one, after we found out the 
  ;; new name on 2-15
  {:from-date "02-15", :to-date nil,
   :data [{:from-date "01-01", :to-date "02-01", :data "Old Name"}
          {:from-date "02-01", :to-date nil, :data "New Name"}]}]
   ;; adding a third name, valid from 3-1, known on 3-15
   "Third Name" "03-01" nil "03-15")
;;=>
[;; unchanged
 {:from-date "01-15", :to-date "02-15", 
  :data [{:from-date "01-01", :to-date nil, :data "Old Name"}]}
 ;; updated to date. This is now the state of system knowledge until
 ;; 3-15
 {:from-date "02-15",:to-date "03-15",
 :data [{:from-date "01-01", :to-date "02-01", :data "Old Name"} 
        {:from-date "02-01", :to-date nil, :data "New Name"}]}
 ;; new timeline-of-timeline, valid from 3-15
 {:from-date "03-15",:to-date nil,
 :data [{:from-date "01-01", :to-date "02-01", :data "Old Name"}
        ;; inner update has put a 'to date' to the actual change of name
        {:from-date "02-01", :to-date "03-01", :data "New Name"}
		;; and added the 3rd entry for the third name
        {:from-date "03-01", :to-date nil, :data "Third Name"}]}]
		
(update-timeline-bitemporal
 ;; initial tot is the same
 [{:from-date "01-15", :to-date "02-15",
   :data [{:from-date "01-01", :to-date nil, :data "Old Name"}]}
  {:from-date "02-15", :to-date nil,
   :data [{:from-date "01-01", :to-date "02-01", :data "Old Name"}
          {:from-date "02-01", :to-date nil, :data "New Name"}]}]
 ;; but now we are inserting a name into the history
 "Tween Name" "01-15" "02-15" "03-15")
;;=>
[{:from-date "01-15", :to-date "02-15",
  :data [{:from-date "01-01", :to-date nil, :data "Old Name"}]}
 {:from-date "02-15" :to-date "03-15",
  :data [{:from-date "01-01", :to-date "02-01", :data "Old Name"}
         {:from-date "02-01", :to-date nil, :data "New Name"}]}
 {:from-date "03-15", :to-date nil,
  :data [{:from-date "01-01", :to-date "01-15", :data "Old Name"}
         {:from-date "01-15", :to-date "02-15", :data "Tween Name"}
         {:from-date "02-15", :to-date nil, :data "New Name"}]}]
```

## Next

Persisting bitemporal timelines in a SQ
