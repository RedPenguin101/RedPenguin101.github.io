# Jottings
## Something about prescriptive approaches

> Beware overly prescriptive approaches to anything in this industry. Don't trust the word of some blog over what is best for your current team and circumstances.

https://old.reddit.com/r/devops/comments/tarp8j/am_i_misunderstanding_something_or_does_the/i02ubpz/

The model is not the territory. The theory is the model, treat it like one

In theory there is no etc. etc. Yoga Berri

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
* Be much more explicit than you think you need to be.
* Have a point that can be expressed in a short sentence. That thing should stand alone and be the thing the eye is drawn to when looking at the message.
* If you have to explain, separate the point, the conclusion, the question, from the explanation. But probably don't explain.

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
