# Ask your developer, by Jeff Lawson

## Content

* Part 1: Why devs matter: Build v die and the software supply chain
* Part 2: Understand/Motivate devs
* Part 3: Making devs successful: The culture, the infra, the leadership, the customer focus.

## Summary of main ideas from a skim read

### Build, Buy, Die. Disruption is coming

Software has disrupted many industries. It will disrupt yours. Don't be myopic.

To stay alive you need good devs, but you need good _management_ of devs as well, from the executive / non tech side. That's really what this book is about: a handbook for execs about how tech teams work, and how to work with them.

You want to set up software as a profit center, not a cost center. Be able to get things in prod _fast_, so you can adapt fast.

Buying off the shelf is fine for back-office stuff. But _never_ for customer facing functions, or functions that you see as a differentiator for your business. 

First: One-size-fits-all really means it fits no-one. If you have to change your process to fit the software, but your process is your differentiator, that's not going to work.

Second: You can't differentiate yourself if you're using the same stuff as everyone else.

Third: off-the-shelf is anathema to quick feedback loops, quick adaptability. You need those attributes to succeed. You can't incrementally on-board a vendor product (mostly)

Forth: Integrations get _very_ hard when you're trying to stitch together multiple vendor products.

Buying off the shelf _services_, on the other hand is fine. Things like Slack, Twillo, Zoom. You obviously don't want to build that stuff yourself. The "Third Great Era of Software" is about using service components to build original systems: using Stripe for payments, AWS for Infra etc.

> “The only things companies should build themselves are the things that are core to their business”

### What motivates developers, and how to hire them

Devs like solving problems, being creative. The do _not_ like being handed a checklist of technical tasks to complete (or at least, they become apathetic when they are). Happily, this also leads to the best solutions.

> “We just say to a team ‘Here’s the idea, here’s roughly where we want to go or want to build. It’s your responsibility to take that and figure that out.’ There’s a lot of agency here, a lot of autonomy. They decide how they want to attack the problem… We could detail something out and figure out there needs to be forty-two steps and then…give someone forty-two tasks…but then you’re telling them, ‘Don’t use your brain, just do what we tell you’” - Jason Fried

It's not hard to make the case that your Co is more attractive than the FANGs, even if you can't compete on salary. Emphasize and give examples of how you treat your devs like _people_, not resources. Candidates want autonomy, mastery, purpose. So sell on that. Salary (above a 'fair' level) are secondary. Small teams are usually compelling, so sell on that.

Early hires are especially important: recruit a great leader and the rest will follow.

### How to set up your dev teams

Small teams, as autonomous as possible. Minimal collaboration required (that is _fine_, it's not a problem). Interfaces between teams should be well defined, including the APIs between the software of the teams.

The goal with small teams is to keep that startup energy even as you grow. Divide them by mitosis when they get too big.

The division of teams should be around common customers, missions and metrics.

Most importantly the teams should have **total decision making authority**. If a manager can swoop in and veto, the team doesn't have decision making authority.

> It’s natural for leaders to want to delegate upward. It’s a sanity check on the decisions, but for hard decisions, it’s usually easier to ask your boss to make the call. But doing so is escaping accountability, which isn’t good for building a culture of empowered leaders…My goal is to make the single-threaded leaders accountable, but help them answer their own questions.

Don't create buffers between devs and customers: the more direct exposure they have, the better software they can build.

### Innovate by experiment, be tolerant of small failures

You don't know what the right thing to build is ahead of time. You have to experiment, and then base your next moves off the results of those experiments.

Experimentation, though, means accepting, even celebrating failure, which is anathema to the culture of most organizations. You have to change that.

You also have to learn how to create good experiments: you need to know what _business_ assumption (not technical assumption) you are testing _before_ you start it. And what potential results from your experiment will prove or disprove that assumption. You _have_ to measure it, and write it down.

### Infrastructure: developer happiness, productivity, safety

Good infrastructure is hard to see the value of, but it _is_ extremely valuable.

First, devs hate fighting their tools. If you have good infra around deployments etc. that's a selling point to potential hires.

Second, it speeds you up. The less time devs spend on their own infra, the more they can spend on solving actual business problems.

Create an infra team as soon as you can.

## 2 hour read

### Part 1:

* Foreword
  * Software revolution Disruption will come to your industry
  * Delusion that there is no connection between software and x (where x is your business)
  * Understand what well-crafted software can make possible
  * Management is crucial to using devs well
* Intro: the billboard. What Twillo is. Marketed to _devs_. Message to business people: you can't be disconnected from tech. "Why Software is Eating the World" (2011)
  * Companies succeed in digital xform by building, not by using software
  * digi disrupt not just about devs, about collab between functions

> That's the purpose of this book: the Ask Your Developer mindset is designed to help businesspeople better understand and collaborate with technical talent in order to achieve those shared goals.

* problems the book addresses
  * tech teams working hard but overlooking customer needs
  * frustrated at how slowly devs are delivering
  * competitors are delivering faster
  * Having trouble hiring talent (or talent is leaving)
  * Tech leader struggling to help business counterparts understand the complexity of building great software

* Why devs matter
  * "Amazon is not a retailer. We're a software company". The software is the competitive advantage. It is the product.
  * Move from IT as a cost center (support business) to profit center (part of the product).
  * "Build vs. Die", not "Build vs. Buy".
  * Speed matters: Getting from idea to prod _fast_. A skill of digital natives, good devs. Adapt quickly, Darwinian
  * Software person: When faced with a problem: "how can software solve this problem?". See through the lens of software
  * Fundamental agility of software. infinite malleability. Quick feedback loops: customer to code (cf hardware)
  * One size fits all software doesn't fit anyone. Lowest common denominator. Can't differentiate. Have to change business to match software, should be the other way round. Integrations get _very_ hard when managing multiple vendor products.
  * If you want to become a software builder, you need to start by changing the mindset of the entire organization. Hiring devs, or changing how devs work isn't enough.
  * "These world class software builders are everywhere. Companies need to find them and turn them loose. Make them feel like owners".
  * New Software supply chain. Like Ford. Divide industry in areas of expertise and specialize (in reusable chunks of code). AWS, Twillo, Stripe. Component Software. Building Blocks. _Third Great Era of Software_. (Second era, SAAS, Salesforce, AWS)
* Problem: how to organize co into small teams when work is intrinsically connected? Keep code with teams, expose via APIs with small surface areas (microservices). How to measure effectiveness? Ascribe a cost.
* Microservices are pluggable. You can sell them.
* Kutcher: "The only things companies should build themselves are the things that are core to their business". Don't rebuild Slack. Does it differentiate from competitors? Back end operations: Buy
* **ANYTHING THAT IS CUSTOMER FACING, YOU SHOULD BUILD**.
* **YOU CAN'T BUY DIFFERENTIATION, YOU CAN ONLY BUILD IT**
* Software people should decide what to build and what to buy

### Part 2

* What drives devs to do best work? How do you as a leader motivate them?
* "Building software is easy, but building the _right_ thing is hard". Experimentation and close contact with customers.
* Unlocking innovation is a cultural thing. and culture starts at the top
* "The relationship between business people and developers is not well understood, but is critical to solving business problems with technology"
* "The key to getting business people and developers to work well together is for the businesspeople to _share problems, not solutions_" 
* "When he asked me to solve a problem, I became more engaged"
* Code is creative. Devs are motivated by being allowed to create. "...when they're turned loose and allowed to dream and solve real-world customer problems creatively"
* Superficial stuff: free food, dress codes, ping pong. Forget about it.
* "[Software engineers are] problem solvers. They sit down and look at a problem and then find the most efficient way to solve it"
* Assign problems, not tasks

> "We just say to a team 'Here's the idea, here's roughly where we want to go or want to build. It's your responsibility to take that and figure that out.' There's a lot of agency here, a lot of autonomy. They decide how they want to attack the problem... We could detail something out and figure out there needs to be forty-two steps and then...give someont forty-two tasks...but then you're telling them, 'Don't use your brain, just do what we tell you'" - Jason Fried

* Most software is just CRUD. The real difference in how long it takes to solve a problem and how well its solved comes from the developer understanding the problem.
* Get your devs talking to customers/users early and often (about the _problem_). Remove any barriers you can to this

### Experimentation as prerequisite to innovation

* people are afraid to fail
* but tolerance for failure is the key to innovation
* an org should encourage incremental development to get around this, because it reduces the cost of failure.
* A culture that is good at experimentation
* Experiments running _in production_. AB testing etc.
* Learn about your customers quickly
* Build or Die: you can't incrementally adopt a purchased product (mostly)
* Can't know ahead of time what ideas are good.

> First, vet the idea...[for] two things
>  1. what assumptions about customers, the problem, or the market are you making, and how will your experiment prove or disprove those assumptions?
>  2. If you are wildly successful, will it be a big outcome? Otherwise, why bother?

* only reasons for declining: opp is too small, or don't know how to measure
* experiments must have experimental outcomes, _have_ to measure. _write it down_
* The hypothesis is a _business_ hypothesis.
* when to pull the plug. Engineers are generally going to give you straight answers, ask them.
* How to fail without failing customers:
  * Just ask them. If they say 'no, I wouldn't buy that', that's a failed experiment
  * "Painted Door": write some ads and put them on google, without building the product, see if you get bites.

### Hiring devs

* It's not hard to make the case that your co is more attractive than FANGS.
* Have to be treated like people.
* Early hires especially important
* Recruit a great leader and the rest will follow
* Don't rush, don't settle
* real reasons for work (after 'fair' salary): Automony, Mastery, purpose.
* Recruiting: get CEO's / Execs to swoop in as closers.
* Make potential hires aware of your problems, get them excited about solving them. Create a hero's journey for them
* Small teams approach is always compelling. Sell it if you have it
* Don't pay bonuses :P. Bonuses are premised on cash motivating performance. It's not true, at least for engineers
* **Ask your developers why you are having trouble hiring developers**

### Open learning environment

* Open, learning environment. Continually seek truth
* NOT classroom learning or book learning. Learning from eachother. Feedback loops to improve.
* Needs guardrails and support
* Open project reviews: OPRs. Anyone is welcome to observe any project meeting. Public calendars. Agenda published 2 days before. Only specific people allowed to speak. Become a classroom
* AWS Weekly business review
* Tension between openness and safety
* Blameless postmortem

### Small teams, single threaded leaders

* Startups have energy. How do you retain it? Small teams
* 2 pizza
* Group teams around customer, mission, metrics
* Divide teams by Mitosis, cell division
* Teams should be empowered to make decisions independently. No "cascading"

> It's natural for leaders to want to delegate upward. It's a sanity check on the decisions, but for hard decisions, it's usually easier to ask your boss to make the call. But doing so is escaping accountability, which isn't good for building a culture of empowered leaders...My goal is to make the single-threaded leaders accountable, but help them answer their own questions.

* If there's always a manager who can veto, the person who has the decision actially doesn't.

### Collaboration

* Autonomous teams don't need to work together much
* Many mistake this for a problem. It's not.
* Where teams do need to collaborate, formalize the relationship with 'service contracts'
* Interfaces are usually APIs

### Wearing customers shoes

* Eat your own dog food if you can
* Don't create buffers between devs and customers (sales, product managers etc.)
* Start from press release: focuses on what your customer wants, works backwards.

### Agile (for executives)

* features, deadlines, quality, certainty: pick 3, maybe 2
* Throwing more devs at a delayed project will delay it further.
* Forget the formal methodologies, trainers, consultants.

### Infra

* Good infra is hard to see the value of. But it _is_ extremely valuable.
* Engineers hate fighting their tools. Make it easy for them, that's motivating for them
* Twillo infra principles
  * Paved path vs. offroad. Don't ban things, make the things you want people to use attractive
  * Self service: IAAS
  * Opt into complexity: Opinionated, but customizable, flow.
  * Prioritize
  * Composability
* Don't worry about duplicating work.

> Other companies, most notably Microsoft, have become obsessed with stamping out duplication only to dicover that the "de-duplication" effort eats up more resources than it saves. That's because keeping an eye out for duplication and/or spending time de-duplicating overlapping products means creating a new layer of oversight, which slows everything down.

* **Hire an infrastructure team**
* "time spend outside code"

### Stories and example

* Billboard (Intro)
* Casper and Tempur Sealy (C1)
* Software enables quick feedback loops, malleability
  * The phone keyboard, jobs mockery. Not updateable (C1)
  * Apple TV
  * Tesla (ride an inch higher)
  * Square reader
* Buy vs. Build
  * ING (contact center systems) and Bunq
* Supply chain
  * IBM, Logo
* Business / Dev interaction
  * Matt from Versity/Nine Star
* experimentation
  * Wright brothers.
  * GE, Immalt's digital big bang
* Hire the leader, the rest will follow: Dominoes
* Open environments
  * Twillo OPR
  * AWS Weekly Business Review
* Customer focus
  * Bloomberg meeting traders
