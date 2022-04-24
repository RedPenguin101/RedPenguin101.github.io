# Jottings
## Pattern language? Just thoughts about software

The sense of place, the environment. Fundamental to building anything. What are the edges of the software? How does it situate in the area around it? If you can't say this with clarity, your software will be a mess. Software is like a goo, it's so malleable, if you don't lay out boundaries to give it structure it'll leak all over the place and make a mess.

Pattern language for software? Like a hotel: You have guests, users, people who _want_ something from it, but don't live there, they pass through. You have the people who run the hotel, engineers, who _live_ there. They want things from it too, but they are there because they run it for the guests. Even this isn't quite right because the people who run the hotel can't, say, add a _new wing_ to it. They can only clean up, make interior decorating decisions. And the users and engineers don't even inhabit the same space. The view the engineers have would be alien and incomprehensible to the users. It's like the Eloi and Morlocks. One of the things that Alexander says has caught on I think: the removal of separation between architect and builder.

Alexander's point that I've been able to grasp so far is this:

* There is an quality with no name. A building either has it or doesn't. It seems to be a rather "know it when you see it" deal
* The space and the patterns of 'usual' events in that space are inseparable from each other. One guides the other.

The second point is what I tried to describe in my first paragraph here.

The first point, I don't know how practically useful it is to talk about it. It's so hard to even describe what it is, surely that means we can't really _use_ it in a conscious way.

In "A Pattern Language", Alexander expands on these, creating descriptions of patterns. 

1. A picture with an archetypal example of the pattern
2. Description para of how it completes larger patterns
3. Headline, bold, problem essence, 2 sentences max
4. Body: empirical background, evidence for truth, range of manifestations
5. Solution: In bold again, "the field of physical and social relationships which are required to solve the state problem." Stated in form of instruction
6. Diagram with labels which shows the solution
7. Para that ties this to the smaller patterns

What are some potential patterns that can be talked about in this way?

### Port and adapter
### Data integration and model sharing/transformation
### Orchestration of steps: Threading, Splitting, Flow
### Service provision
### Separation of mechanism and meaning
### Replicability - getting a separate environment which is identical
### Testing the plumbing
### Automated deployment across environments, any knowledge contained in configuration
### Locality - having what you need close
### Wholeness: each piece entirely it's own piece, a consistent level of abstraction
### Situation: how is the program situated in it's environment?

## Some motivating questions / ideas

How recursive are patterns of software as compared to physical architecture?

## The Station

The purpose of the station is to efficiently get travelers onto and off of trains. To get on their train, a traveler needs to be able to effectively find it. There are usually many different platforms and, due to the size and space requirements of trains, they are often far apart. Most people will arrive at a station knowing what they want their destination to be. So entering the station, they want to know what is the next train that will get them to that location, and how to get to that train.

Getting off the train is easier: the traveler needs to know the quickest path to the exit, and they want that path to be clear and unobstructed. Note however, that this can be in conflict with those boarding the train, since the path will likely be similar.

A key point in how the station is situated in its environment is the travel patterns of the travelers. For example, a station situated in a largely residential area will likely see much more outbound traffic in the morning as people commute, and much more inbound in the evening as they return home. There may be significant seasonal variations as well. The layout of the station therefore may need to change in the morning and evening to accommodate these different patterns.


## Something about prescriptive approaches

> Beware overly prescriptive approaches to anything in this industry. Don't trust the word of some blog over what is best for your current team and circumstances.

https://old.reddit.com/r/devops/comments/tarp8j/am_i_misunderstanding_something_or_does_the/i02ubpz/

The model is not the territory. The theory is the model, treat it like one

In theory there is no etc. etc. Yogi Berra

All laws, all x-driven-development

TDD especially. That beardy guy talk, but 100 others which 'dial back' the rigidity. Martin's attempts to live in the model.

Scrum, most Agile stuff.

What to do? How to act?

## Writing
Communication, meeting of minds. Information/idea exchange.

Types
* quick communications, emails, slack. "I want you to do this."
* Essays, explanatory. Ideas, rationales for actions. "This is what's in my head."

Quick comms
* Miller's law: nobody reads anything.
* Law is proportional to the apparent length of the message. The longer it is, the less likely it will be read. This is probably the opposite of what you intend if you write a long message.
* People won't read these, at best they will skim them
* Never ask for more than one thing in one message. You don't have to have everything in one go.
* Or at least, if you ask for 3 things, expect that you will have to follow up for the other two.
* Be much more explicit than you think you need to be. No subtly, no allusion, no _hints_.
* Have a point that can be expressed in a short sentence. That thing should stand alone and be the thing the eye is drawn to when looking at the message.
* If you have to explain, separate the point, the conclusion, the question, from the explanation. But probably don't explain.
* You can come across as blunt. Don't be afraid of surrounding your message with the niceties: the person's name, a proper sign off. People won't _read_ them, but they will still soften the delivery.

## Explaining things
Problem with explaining certain things.

You need _examples_ to show the problem, and how you solve the problem. Often the best way to do this is to take the overall situation you want to explain, and carve out a small piece of it - a simplified model - and explain _that_.

This creates an issue when the problem involves complexity, since shrinking the example to a point where it is comprehensible makes the problem vanish.

Most of the actually difficult problems in programming and system design come only when you reach a certain size. This is when poor organization starts to bite. It's where code that's not laid out to be readable becomes a real velocity killer. It's where muddled or inconsistent abstractions start to obscure the intent of the program. It's where mutable state starts to make it impossible to understand what is going to happen when you make changes.

All of the above are problems that programmers need to learn to recognize. They need to learn to prophylactically avoid the problems, and they need to know how to fix them when they come up.

How do you teach that?

Often you'll teach something by taking the overall overall thing and coming up with a small 'toy' example which illustrates the point. For example in teaching physics, you will probably see some simple examples of motion - a cannonball trajectory, or a car stopping. These examples, while simplified, keep enough of their integrity when the simplification is removed to continue being useful.

Complex problems are not like that. You can't remove much without castrating the problem. You throw out the baby with the bathwater. Consequently it's very hard to satisfactorily explain the value of some concepts to people who don't already have enough experience that, consciously or not, they already know it: immutability, dynamic typing, good code structure, tests, consistent domain model, separation of concerns. Really, all the interesting concepts in programming.

This doesn't stop people from trying to use small examples to explain problems of complexity. Here are a few that I've seen:

* Replacing conditionals with polymorphism, resulting in much more, and much more complex code.
* Almost all design patterns are explained in the context of very small things, when they are generally _anti-patterns_ in the small scale. (there's nothing more dangerous that an eager junior developer holding a copy of GOF)
* Explanations of technical debt tend to focus on code smells, where in the examples generally provided, the smells are not actually that smelly

When reading posts on forums of programmers you will often see some form of the phrase "Your thought is invalid until you show me some code". This exacerbates the above problem

