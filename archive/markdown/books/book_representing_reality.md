% Representing Reality

We deal here with the complexities of representing the universe, using the stunted, feeble method of data representations, crammed into anemic information systems. It is frustrating and ultimately futile. Information systems can never be more than pale reflections of reality. Interpretations of the shadows on the wall created by the light rays of our imaginations. But they are the best option we have.

We are ill equipped to handle reality. Nature seems to actively resist the grasping of our minds. It seems the tighter we try to contain it, the more energetically it slips away from us, like a bar of soap, or a quantum particle. We continue to delve deeper, searching for a solid foundation on which we can stand, for something that is _unambiguously true_, but always in vain.

We can only develop coping mechanisms. We build miniature worlds in our head. Our models, maps, information systems. We are predisposed to this, and it is a great gift. But we are also tragically predisposed to forget that the small world in our minds is _not_ the large world outside of it. Hence every financial crisis, every election surprise, every incomprehensible software system and miscommunication. 

Reality cannot be understood in a exact, complete way, like a data representation of it can. But perhaps we can say something about it. Maybe we can, in a vague, fuzzy way, discern something of the nature of reality. Its shape and behavior. And from that we might be able to say something about the right and wrong ways to create systems to represent reality.

Sadly for us, when we try to peek under its skirts, reality will put up its usual defenses. Ideas will blend into each other. Concepts will dissolve into nothingness as we try to look at them, or shatter into smaller fragments each more incomprehensible than their parent. What we will inevitably end up with, as we try to explore how pockets of reality can be represented in models, is a model of how to think about reality. A model as limited and flawed as all the others.

We look first at the **entities** of reality, the _things_ that are in it, and the kinds of things they are. We will then turn to applying the lessons learned from this into the theory of creating data representations. Finally we will look at the applying these theories to practical systems. 

# The Entities of Reality

We fall at the first hurdle. Reality _has_ no entities. It has only flowing concentrations of energy that change over four (at least) dimensions. The entities - the planets, the people, the books, the coffee cups - are all in our heads. The continuity of those entities is also a perceptive construct. The "dog" that I see now is the "same" "dog" that I saw one minute ago, despite it having totally changed it's position in space, having shed some of the hair it had a minute ago, and having gained some weight by eating a sausage off my plate.

However, we have to start somewhere, if only for a convenience of language. We can't speak only in terms of flow of energy. So we will grudgingly assume that there is such a thing as an entity and see where it goes.

## The Simple and the Fractal

There is a short story by Jorge Luis Borges called 'On Exactitude in Science'. In the story, the rulers of an empire become so enamored with cartography that a map with a one-to-one scale is created for their entire territory, and is laid lovingly over every part of it - because where else could you put it? The uselessness of such a map quickly becomes evident, and over time it slowly dissolves.

We will return to the uselessness of a representation that has a one-to-one correspondence with reality. For now consider: _is_ this in fact a complete representation? When we look at the giant map, it may look very similar to the territory it represents. Perhaps it has individual blobs of color for small pebbles, flowers so detailed you can tell the species. But look even more closely at the flower on the map, zoom in as far as you can go on one of the petals. What do you see? Perhaps a flat, smooth, uni-colored surface. Now look at the flower in reality, zoom in. First, the surface will stop looking smooth, and start looking rather bumpy. Variances in color will suddenly become glaringly apparent. Keep going and you will start to see the structure of individual cells, each containing its own multitude.

This well illustrates the first fundamental problem with attempting to represent reality. When you have a large information system, and you zoom in on an element of it, the thing in your virtual view-finder will be simpler and contain less things than before you zoomed in. A data representation must be simple, finite. Reality is just the opposite. It is complex. _Fractal_. Whenever you zoom in on a part, what you see in your view-finder will be just as complex as before you zoomed in, like a Mandelbrot diagram. This deceptive quality is found everywhere in nature. A mountain might look like a triangle from a certain distance, but the closer you get, the less triangular it will look. 

The first lesson we draw from this is that we have no choice but to create our data representations as _less detailed_ than reality. They have to _stop_ at some point. Thus, modeling becomes as much about what to leave out as to put in, a process of selection not invention. What criteria to use as the basis for selection? This is an idea we will explore later.

The second lesson is to just to remember that reality has this property. When you forget, you will find yourself in front of a whiteboard, trying to represent reality in boxes and arrows, trying to dig all the way down, and in time you will find contradictions and cycles in your diagram. The contradictions are a necessary consequence of fractality, as well as a few other things we'll look at later.

## What is a Thing?

### Limitations of language

One of the consequences of the complexities of reality is that our most common model for representing it, language, must like all others be incomplete, limited and unsatisfactory.

Can you describe a unit of reality? An entity which exists? Even in the most seemingly trivial case, describing physical objects, we run into trouble. Let us dodge the issue for now and name such a unit as a 'thing', short for 'a thing that exists'.

A particular, if rather trivial problem in English is that we tend not to distinguish or make explicit a singular instance of a thing, or the abstract idea of a thing. Which one we mean is context dependent.

Take, for example, a book. I have a copy of Domain Driven Design on my desk. You come over to chat and the topic of DDD comes up. I ask 'Have you read this book?'. Here I'm obviously not referring to my specific copy of the book, which of course you haven't read even if you've read another copy. I mean the abstract idea of the book. If you say, no you haven't, I might say 'I can lend you this book'. Now I _am_ talking about this specific instance of the book - you can't lend an abstract idea! But the two sentences use the same word to represent both the instance and the abstract, and there is no distinction between them.

If you've ever designed a system which is for delivering reports or anything similar, I'd wager that at some point you have had a confused conversation, where someone is talking about instances or runs of a specific report, and someone else is talking about the abstract idea of the report.

Another problem is drawing specific bounds around a particular thing: what constitutes part of it and what does not. Consider the book Lord of the Rings. Is it actually a book? There are three parts to Lord of the Rings: Fellowship, Two Towers, and Return. Often they are published as 3 separate volumes. Are we to consider each one a separate book? Or are they volumes of a single book? When they are published as a single bound thing, are they three books, or one? Can Fellowship be a book which is part of another book, and Lord of the Rings be a book which somehow 'has' other books? Or would that be chaos?

Think of a wall, made of bricks. You start removing bricks from it. Are the removed bricks still part of the wall? If you remove a certain combination of bricks, will you at some point have _two_ walls (Note that the Great Wall of China is not one continuous structure)? How many bricks do you have to remove before you it's not a wall at all anymore?

## Attributes, Relationships
## Groupings of things
## Essence and Role
## Form
## Identity
## Time

# Representation
From discussions of reality, we now try to draw conclusions about how to represent it in information systems.

The most valuable shift in mindset to make when doing this is to move away from modeling reality. What we are modeling is is the way information about reality is _processed by people_. This has some very attractive properties which mitigate the inherent problems with containing reality. 

Firstly, and most critically, information is processed by people for a purpose. We have seen that reality does not have a natural structure which we can overlay our model on top of, rather our model imposes an artificial structure on reality. There are few if any natural limits on how we can do that. _Purpose_, what we want to achieve by the act of processing, is the best means of determining what a good structure is. 

Secondly, we saw that, for a couple of reasons, a model must be less detailed than the thing it is representing. Thinking from the perspective of a person processing reality will nudge us towards reasonable bounds for our representation. When a person is processing sales of widgets, they do not care about the hair color of the person who is buying the widget. Therefore we can feel free to exclude that from our representation of Customer.

## Names
A name is a connection between a word or words and an entity. We will call these the "Identifier" and the "Referent".

When naming something, we are creating a link between an identifier and the referent it points to. The analogy to variables and the values they resolve to in programming, `my_thing = MyObject::new("attribute")`, is very strong. In fact it isn't even an analogy. Here, `my_thing` is the identifier, and the object (block of memory) is the referent.

The purpose of naming something is always for communication, to gain the ability for person A to indicate to person B that they are talking about a particular thing that exists.

Names can include a degree of description of the things they refer to. These can range from the definite description, that exclusively and exhaustively describes the thing

> "That man over there by the water cooler, in the red tie"

To entirely agnostic, containing no information at all about the thing which it refer to:

> "Tony"

(You could argue that even this has contains some information: an implication or probability that the referent is a human male, probably of Western heritage).

We will say that names can vary in how 'informative' they are: how much information about their referent they include.

There are various trade offs to having an informative name. On the positive side, an informative name allows you to communicate something about the thing you are describing which might be useful to your interlocutor, particularly if they don't have a pre-familiarity of the thing. Taking the previous examples, if you say "Tony" to someone who doesn't know who Tony is, that name is entirely useless. If you say "That man over there by the water cooler", that is a useful name for someone, regardless of their familiarity with the piece of reality you are referring to.

On the downside, informative names can often lose connection with the thing if the thing changes from under it. In that case the name becomes not only uninformative, but can be actively misleading. Continuing with the previous example, if the man moves away from the water cooler, the name "That man over there by the water cooler" is no longer a good name. This often happens in place names. Dartmouth in the UK is so named because it is at the mouth of the river Dart. But its various namesakes in the USA, including Dartmouth College, are not located near a Dart river at all. Likewise, most towns and cities will have a "Main Street", with that name assigned at a time when it was the main street of that town. But over time, with development, it stops being the main street, even as it continues to have that name.

Where you don't have a name which is a definitive description - that is you have a name which conceivably refer to more than one thing - you will often need to resort to qualification to avoid confusion. "Tony" might have to be "Tony from accounts" to distinguish him from "Tony from sales". This is effectively the origin of surnames. "Tony Smith", "Tony Samuelson", "Tony MacDuff" are all examples of organic qualification.

A twist on this concept is with words with more than one meaning. Naming something 'Colt' could refer to a car, a gun, a beer, a person, a place. It could even not be name at all, but a reference to a male horse.

In building systems we usually refer to objects within our systems as existing within 'namespaces', as in the Zen of Python aphorism "Namespaces are one honking great idea -- let's do more of those!". The goal of namespaces in systems is to denote the context in which a name exists, so as to prevent accidental confusion between two concepts with the same name, which nonetheless are different from each other, either in type or implementation.

For example, a customer in the sales module of the system will probably be implemented differently from a customer in the customer relations module. Namespaces allow the programmer to know whether they are dealing with a `sales/customer` or a `relations/customer`.

## assignation and sharing of names. guids

What lessons can we draw from this - what makes a good name? It very much depends on the intent. Let's go over a few scenarios.

If you have a distributed system, where several parts of the system need to be able to pass around references to entities - say for example your customer care system needs to talk to your sales system about a particular customer - it needs some sort of shared name to be able to do so. What is the right type of name to use?

If, on the other hand, you are naming a concept, or a type of thing - say a class which represents a customer - 

## Models

## Types

## Relationships, Attributes

# Practicalities

## Leveraging existing real world models

When you are modeling a concept in data, you should think about trying to anchor on something that has a widely accepted formalism, to avoid some of the fuzziness of reality.

A good example is a 'legal entity' as an anchor for an organization. In enterprise systems, quite often you will have to model some kind of organization: some collection of people who perform work to achieve a common goal. Modeling an organization can be very difficult, because the reality is that an organization is a variable number of individuals doing various things and interacting with each other, often in not very predictable patterns and groupings. Even the simplest problem of defining what the organization is, and the boundaries around it, is non-trivial. Who is in the organization and who is not? 

Say there are 10 people who have come together and agreed to invest their capital. But although they have a common pool of capital to deploy, they are in effect two separate sub-organizations of 5 people with slightly different mandates. Should we represent this as one or two organizations? When does an organization start to exist and cease to exist?

The problem is that an organization when thought about like this is very fuzzy. However, in their own efforts to impose structure on reality, world governments have come up with a useful concretion around organizations: the legal entity. A legal entity is created when the government says it is created. Is stops existing when the government says so. They even, usually, assign them a unique identifier that doesn't change, even if the name changes. People are part of a legal entity when they are employed by it. The government has helpfully reified the rules around existence, and so successfully that there is global acceptance of it. The users of your system, who may have not been able to agree on what constitutes an organization, will grudgingly have to agree on what constitutes a legal entity.

We can lean on this concretion, and others like it, to provide a template for our own data representations. This isn't to say our representation problems will magically be solved by doing this: a legal entity may, for users of your system, still represent more than one groups of people that need to be distinguished. You may need to group legal entities into categories, types, which might be as fuzzy as anything else. But being able to anchor on a nearly universally accepted construct provides you with a stake in the sand to come back to and to guide the layers of meaning you have to put on top of it.

For example, consider a customer relationship management system (or CRM) for a business-to-business organization, with the goal being to gain new customers and keep existing ones. Bob, a sales person, has a contact at Ford, and puts that contact in the CRM. Jane, another sales person, has a _different_ contact at Ford, and puts them in the CRM. It will be useful for various reasons to have some indication that these two people are connected, via their organization. However, these people work at different _parts_ of Ford, in Purchasing and Quality Control. A third contact also works at Ford, but they work at Ford Europe (in Purchasing), where the other two work at Ford USA.

In some sense all three are part of the same organization, and in another sense, they aren't. In some sense, two of the three are part of the same organization and the other is part of a different one. As a developer, you spend a good amount of time arguing about how to represent this. You likely end up with some kind of hierarchy, where a contact belongs to an organization, but an organization can be part of a 'higher' organizational construct, which itself can have contacts. In practice, while it is very flexible, this will end up as too vague. Users will be unclear about how to effectively assign contacts to organization in order to effectively meet their needs, and your data set will become a giant mess, with many duplicated contacts and organizations.

So you need to provide a _little_ more structure, just enough that your users will be able to lock on to it. But what can we anchor on? Legal entity is a good choice, since it comes with out-of-the-box unambiguity, and nearly everyone will understand what it means. Every contact you have will most likely be employed by a legal entity, and they will helpfully tell you which one on their business card. From there you can expand to other conceptual groupings, which are more ambiguous, as required by your use case. 

So our CRM will have a legal entity "Ford Motor Company USA", with our two contacts as Employees of that legal entity, and "Ford Europe Ltd" as another entity with the other contact. To increase grain, each contact could be assigned a Department. Exactly how to name and separate departments will need to be driven by your specific needs, but for arguments sake here we'll use "Purchasing" and "Quality Control".

To create a higher level aggregation, you might create a "Client" or "Group", which both Ford entities can belong to, and which _can't_ have contacts associated with it (because contacts must be employed by an entity).

This isn't to say there won't be more complications. Reality is very messy, especially when you bring humans into the mix. The challenges to the model will be unpredictable and constant. What if a client is not a legal entity, but an individual, who is also a contact? What if a contact is employed by Company A, but A is employed as a purchasing agent for Company B, but the Companies are otherwise not associated, and so from your perspective the contact works for a company that doesn't employ them? What if that same person _concurrently_ becomes an agent for Company C? What if you have a two companies A and B, but A _owns_ B, so a contact for A is also a contact for B, but a contact for B is not a contact for A?

All of these will need to be thought about very carefully, since you could make a big mess. But it is likely that using Legal Entity as your conceptual anchor will be more helpful than hindering.

It must be emphasized that we are leaning on widely accepted formalisms to define _identity_, but not necessarily the model itself. Legal entities, as defined by the government, have names and identity numbers, which probably will be in any model you come up with. But everything else about your model will have to be driven by your needs. Legal entities usually have shareholders, and that's something that the governments usually record and make publicly available. Should they be in your model? In the case of a CRM, probably not. In the case of a KYC (know-your-client) program, absolutely yes. Likewise you won't be limited to including things that have the same formalism of identity that the legal entity itself does. Often when defining a legal entity, only a _tiny_ fraction of the information you need can be said to be "inherent" to the entity itself - perhaps things like the board of directors, which is enshrined in the entities foundational documents. The vast majority of the information associated with the entity are _not_ inherent to the entity. [examples!]

## Encapsulation and stratified design

## Objects as identity proxy

## Flocks of facts, not slots for datums

The basis of much of computer science is in the inner workings of the machine. The machine-model is a small world of defined scope. Existence or non-existence of a thing is determined by whether it exists as ones and zeros in the machines memory. An array in memory has explicit bounds, a start and an end. A file exists or does not exist. This gives us a clue to the world we are operating in: we are in the small world of the model, an order which we have imposed, not the large world of reality. Objects are closed. There is something of the platonic form about things in this world, something mathematical and pure.

This has guided much of the development of existing languages and practices for dealing with things. We have structures and objects which are effectively closed. We define a 'co-ordinate' structure, which has an x co-ordinate and a y co-ordinate, and nothing can be added to this and nothing removed, by the bounds of the mathematics we have created.

From all that we have seen so far, it is not difficult to intuit that this approach will quickly break down as we try to leave the small world of the mathematical and into reality. Defining closed structures, which have pre-allocated slots for information, creates brittle structures which will break as soon as they are exposed to the fuzzy nature of reality.

Generally, modeling efforts are aimed at turning reality into these closed containers for information, whether objects or structures. Such imposition of structure is inevitable, but there is a way we can avoid locked ourselves so irreversibly into a fixed way of thinking. We can go at least some way to representing information in a way that is less fixed, more conducive to change. We can do this by thinking of things not as closed containers of information, with each datum put in its predefined slot, but instead of thinking of entities as a collection of facts which are built over time, with facts entering and leaving, being replaced and updated. In short, by thinking of entities as flocks of information, not as a slot-based form to fill out.

Recall that that our representations should be more about how information is processed by people than of what reality actually is. That implies that our information system represents what we _know_ about an entity, not what it is. A person definitely does have a first name and a last name. So if we are trying to modeling reality, our Person object has a slot for first and last name. But what if don't know the first name? Our object needs to have something in that slot, but what should we put in there? A person may or may not have a middle name. Should we have a slot for it? If we do, how should we distinguish between not knowing what it is, and knowing that they don't have one?

In systems where facts about an entity accumulated over time, modeling with closed structures or types will often lead to large objects where everything is nullable, and so you will need a lot of null checking code. This approach removes many of the benefits of a type checking system. If your compiler confirms that an object has type "Payment", but if your method required specific fields to be populated when they could potentially be null, what is the benefit of type checking at all? 

Alternatively, you can take the approach of having an object or type which represents any pre-defined state of the entity. Of course this can lead to an explosion of types. An example of an algebraic type system representation included the following types to represent an order life cycle

* An `UnvalidatedCustomerInfo`
* An `UnvalidatedShippingAddress`
* An `UnvalidatedBillingAddress`
* An `UnvalidatedProductCode`
* An `UnvalidatedOrderQuantity`
* A List of `UnvalidatedOrderLine`
* A `ValidatedCustomerInfo`
* A `ValidatedShippingAddress`
* A `ValidatedBillingAddress`
* A `ValidatedProductCode`
* A `ValidatedOrderQuantity`
* A List of `ValidatedOrderLine`
* An `OrderPlaced`
* A `BillableOrderPlaced`

Many of which can be 'undefined', null. This is a small subset of the types for an order process in the example - In total I counted around 50 of them. This was in a pedagogical example, so it may be that the pattern was exaggerated for illustration. But clearly any systems of significant size when following this pattern will have a very large number of types, and within those types the data representations will be rather brittle. The behavior of a system tends to evolve were outcomes are the _product_ rather than _sum_ of factors, meaning that type explosion will occur even if you keep a tight lid on 'valid' products.

Of course, there are benefits to this approach, otherwise people wouldn't do it ....

Let's look at how representing an entity as a flock of facts that we know about it can mitigate problems of partial information.

An information system can be thought of as a data representation flowing through processes, with those processes modifying the data representation or triggering actions based on the attributes of the data. An order arrives, and the data representation has a product code, a quantity, and some information about who ordered it. First we need to get the price. We have a function which checks the current price of the product code and adds the order price to the order. Does the order contain a valid shipping address for the customer? The function doesn't care. If the address is there it ignores it, if it is there but in an unvalidated state, it doesn't blink. Maybe we don't know the address at all yet, in which case we just leave it out - there's no need for a slot for it to tell us it's not there. If a function needs an address, or needs a valid address, and it's not in the data representation, the function can complain about it, throw an error, whatever.

[something here about ordering and composibility]

Later we might want to add a 'is preferred customer' fact to the representation. Downstream somewhere there is probably a function which calculates and applies a discount or reward based on the customer's preferred status. What needs to change in the rest of our system? Nothing. There are no types to change (`Customer` to `PreferredCustomer`), no change to any function which isn't interested in preferred status - the data will flow out the other end of those functions with the preferred status information untouched. 

## Immutable representations

## Relational and EAVT models

## State Machines

## The absence of a thing: null, maybe

## Domain Driven Design

I'm focusing here on a small subset of Domain Driven Design. DDD has a lot to say about the _process_ of working with a business to arrive at a good model, and also the 

## Events

* Identity
* Types
* Grouping things
* Models
* Form (Plato and Aristotle)
* Stratified design
* 
