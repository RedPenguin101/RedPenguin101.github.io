# Summary: ORMS are the Vietnam of computer science

This is based on [Ted Neward's Post](http://blogs.tedneward.com/post/the-vietnam-of-computer-science/) from June 2006. To editorialize a bit, the date seems important. 2006 was _deep_ in the OO pit, and in particular the Java EE period. Concepts and practices that were considered standard at the time are now considered highly questionable, from the concept of OO itself to things like inheritance being accepted as a 'way things are done'.

The US began the Vietnam war with unclear and conflicting goals, and disaster resulted. This article is about the parallels between that and ORM. ORM starts well, gets complicated, then gets monstrous. Early success yields a commitment to use it in places where success becomes more elusive. Know when to cut and run - ignore sunk costs. The author draws parallels to ORMs.

## Impedance mismatch

"Impedance mismatch" is about the differences between the Object vs. Relationship modeling paradigms. They are subtle but profound. With OO modeling, you encapsulate a (usually implicit) identity, state, and behavior. With relational modeling, you have storage of facts and predicate based retrieval logic.

The relational model is characterized by _relation, attribute, tuple, relation value,_ and _relation variable_. For example, a _relation_ `PERSON` might be defined as the _attributes_ `{SSN, Name, City}`. The _tuple_ is a truth statement in the context of the relation: `{PERSON SSN='123-45-6789' Name='Catherine Kennedy' City='Seattle'}`. The relation value is the combination of the relation and set of tuples that 'match' that relation (i.e. has the same attributes).

Unpacking these definitions to what I think are more everyday RDBM language gives the following:

* relation -> table definition
* attribute -> column definition
* tuple -> table row
* relation value -> table (or set of table rows)

From the above, it should be clear we're effectively with working with _sets_. Two tuples/rows are identical if they contain the same attributes and attribute values. A table is a set of tuples/rows, meaning they can't contain two of the 'same' row. You then use set operators: join, union, intersection, that allow for _'derived relation values'_, such as the number of people living in individual cities from the people relation.

## Object-Relational Mapping
If you're using an OO language and have relational storage, you always need to translate between the two. You have the following practical options:[^1]

[^1]: Technically there's another option to eliminate the mismatch: Go all in OO, and use an object database, or go all in RM without using an OO language. Object databases are currently out of fashion, and while there are more alternatives to OO these days than in 2006, it's still the industry default.

* Automated tools, like hibernate.
* Hand rolled
* Just accept the relational schema as your OO model - slave the OO objects. Active Record is a variant of this.

Slaving is the usual answer. In effect, it solves the problem by ignoring half of it. 

## The Object to table problem

So what actually is the problem? Can we not just say that OO classes are tables, class fields are columns, objects are rows? It sounds so easy. The problems are quite subtle. The problems start with _inheritance_[^2]. The relational model doesn't support an 'is-a' relationship or polymorphism. Let's look at the solutions available.

[^2]: As mentioned earlier, this article was written at a time before the advisability of widespread inheritance relations was questioned. Likely articles like this were part of the literature which lead to that re-examination.

### A table-per-class. Simple but expensive

Each class gets it's own table. Derived types are stitched together through joins. For example, a `Person` Class with a `Student` sub-class which itself has a `GraduateStudent` sub-class. You'd have a table for each, and you'd join across all 3 tables to get all the fields of the full object.

When you join, you need to join on the identity, which isn't in the object itself. Where would you get it?[^3]

[^3]: I don't really understand what he means here.

If you don't want to manually specify the inheritance tree with each call, you'll also have to have something in the database which does that - i.e. table pointers.

It's also inefficient - three joins to bring one object into memory! It gets worse the more subclasses you add. A 'get all people' call gets crazy expensive.

### A table per concrete class

More complex, a fair amount of duplication, but more efficient. It's effectively de-normalizing.

### A (sparse) table per class family (e.g. Student)

Also de-normalizing. You'll need to have a column for 'subclass'. It introduces a lot of nulls, which is harmful to the integrity constraints that are one of the main benefits of an RMDBS.

## One-to-many, Many-to-many relationships 

1:N and M:N cardinalities are handled differently by OO and RDB. In fact they are handled in the _opposite_ way. OO has unidirectional object traversal. An car object 'has' 4 tyre objects, and you would get to the tyre objects from the car object. It's a 'parent to child' traversal.

RDB also has unidirectional traversal through FKs. But it's in the opposite direction! A tyre would have an FK for the car it belongs to, but the Car table will have no reference to it's tyres. For M:N relationships you need to have a 3rd 'relationship' table.

In both cases, you'll need to do a broad (and thus inefficient) join to 'discover' relationships.

## Schema-Ownership conflict - can you change both?

ORM is predicated on the developer owning design of both schemas. Quite often this is not the case. Even if it is at first, over time devs will lose the ability to change the DB schema, as the database gets used downstream and implicitly coupled with those downstream processes[^4].

[^4]: Avoidance of this problem is one of the main motivations for the "Microservices" movement, which strictly limits the ability of downstream services to get at the database directly. Another interesting development is the _data warehouse_, which I believe was already coming into vogue at the time, and which also has the effect of decoupling downstream users from database implementations.

This all results in pressure to not have changes in the OO schema impact the DB schema. Which breaks the premise of ORM. The solution is often to create a separate, private database, resulting in complexity and silos.

## Identity

Objects have an identity (_this_) which is independent of its attributes. Usually it is implicit. Objects represent an _entity_, something which has an identity which is consistent even as its values change.

Relational models are best at storing _values_, not entities. i.e. if you have two rows in a table with identical field values, that's usually considered a data corruption.[^5]

[^5]: it _can_ handle entities with say auto-incrementing PKs, but it's a bit inelegant

You run into transactional issues if entities are being accessed both via ORM and via SQL. This only increases with caching, which many ORM systems have built in.

## Data retrieval concerns

OO systems will use constructors to reconstruct objects. But these generally operate one at a time, so if you want to do a bunch of objects you need to do several round trips to the DB. The solution to this is one of 3 approaches:

### Query by example

```python
p = Person()
p.LastName = "Smith"
people = QueryExecutor.execute(p)
```

This isn't nearly expressive enough for anything complicated. There's no ability to say 'find all people named Smith or Cromwell' without complicating the API a lot. Domain objects _must_ support nullable fields, which could violate model invariants.

### Query by API

```python
q = Query()
q.from("PERSON").Where(EqualsCriteria("PERSON.LAST_NAME", "SMITH")people = QueryExecutor.execute(q)
```

This is more verbose and complicated that the actual SQL equivalent of the same query! Behind the scenes, the join logic is hard. Because you're hardcoding the query params in strings, it's easy to make mistakes. You can avoid this with unit tests, but it's very easy to miss things. It's very tightly coupled with the db schema. The dev has to know a lot about it, which nullifies one of the supposed benefits of ORM.

### Query by Language

There are several languages which are meant to encode SQL logic in an Object call style: OQL, HQL. If you're doing this, you no longer have an ORM. You're just writing SQL with a different dialect - a dialect you have to learn!
