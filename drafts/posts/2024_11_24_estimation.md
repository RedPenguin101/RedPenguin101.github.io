# Estimation in Software

The more experience I get, the more convinced I am that it's not
possible to reliably estimate how long software will take to be
finished. At the _very_ least, trying to estimate software timelines
is not a cost-effective exercise. In this post, I will lay out my
arguments and thoughts for why this is.

I will also make the point that, while a software estimate is a
basically useless tool if you want a reliable guess of how long
software will take to build, there are other positive outcomes both of
the _process_ of estimating software and the actual resulting estimate
itself - though unfortunately they are often ethically questionable.

## Experience tells me it's not possible

The first argument is very unscientific, but is the best, in my
opinion. I've tried to estimate software projects for a long time, in
lots of different ways, and I've never been able to do it. I've known
a lot of good engineers, and _they've_ never been able to do it. And
looking at what the most experienced people in the industry say, they
don't know how to do it either. Every time I've heard someone claim to
be able to do it, and I've been able to test those claims by seeing
their methods in action, it turns out they can't do it, and their
methods are ineffective (not that this usually stops them from
continuing to use that method).

If after all this time, I can't do it, and nor can anyone else, surely
the only logical conclusion to draw is that it can't be done? If it
could, someone I know or have heard of would've figured it out.

## Data tells me it's practically pointless

There's plenty of data out there, far too much for me to really see
most of it, let alone understand the nuances. But a moderate amount of
reading around yields a pretty clear conclusion: Most IT projects
overrun, and when they do overrun they tend to do so by a _lot_.
Here's a quote from Bent Flyvbjerg's 'How Big Things Get Done':

> 18 percent of IT projects have cost overruns above 50 percent in
> real terms. And for those projects the average overrun is 447
> percent! That's the _average_ in the tail, meaning that many IT
> projects have even higher overruns than this. Information technology
> is _truely_ fat tailed!

Also notable from that dataset is that the mean cost overrun is 73% -
but really it's the fat tails that kill you. The conclusion here is
that when you are estimating a software project, there is a
significant chance that the actual time to complete the project will
be multiples of what you estimate. An estimate which admits to a real
possibility of being wrong by 500% is just useless, at least as a tool
to get a realistic sense of how long the thing will take.

The obvious counterpoint to this is "well sure, if you do it _badly_,
obviously you're going to wrong by a lot. But you can mitigate that
risk by doing it well. The consultants I hired have this great
scientific methodology which...", etc. etc.

But this is wrong. In my experience, a group who does a good estimate
for one project is just as susceptable to creating an estimate that is
_extremely_ wrong on the next project, even if they use the same
rigour and method. To look at why, we need to dissect the process of
estimation itself.

## The process of estimation

### Scope, purpose and definition of done

### Knowledge asymmetry

### Incentivisation, optimism and politics

## The legofication of individuals

One thing you find with estimates that you see being produced is that,
at anything over the size of a single work item (on a Jira ticket,
say) very rarely is any consideration given to the people actually
building the thing. Developers are treated as interchangeable
resources, so a project is estimated in terms of 'man-days'. In
reality there is huge variance in the productivity of developers. The
'10x' developer is maybe a bit of a simplification, but anyone who has
worked in development for any length of time will have come across
situations where one developer could do the same piece of work in a
fraction of a time of another, at least when factoring in the full
lifecycle.

There is also a compounding effect both across team size and project
size: if you have two teams of two developers, and each of the members
of the first team can do a single piece of work twice as fast as their
equivalents on the second, if you were to assign both teams a project
consisting of many pieces of work, the first team would be done much
more than twice as fast as the second. In practice this tends to be
mitigated by building mixed-ability teams, rather then grouping all
your best developers together. Though it's possible this is actually
not a great idea.

So if you're estimating a significant pieces of work, but you know
that, depending on who is given the work, there is a reasonable
possibility that the estimate could be 5 times larger, what actually
is the point?

## Estimating novelty

## The actual benefits of trying to estimate

### The process of estimation

Estimating software necessarily involves focused discussion between
the people who want the software and the people who will build it
about what software should do and how it should do it. This, if done
well, will result in a reduction in misconception and miscommunication
about the software - by far the largest wastage cost in development.

In other words, an estimation process can be used as a _design_ tool.
This can be very useful because many developers don't have a process
for designing software, don't even _know_ that you can have a process
for designing software, and are resistant to any attempt to get them
to design. But they have often drunk the Koolaid on the value and need
to estimate their software. While this state of affairs is lamentable,
and this is no _real_ substitute for a proper design process, it can
be leveraged to sneak some design into an otherwise design-resistant
team.

### The politics of estimation and getting things started

## What to do instead

Well, obviously it's being agile. There's plenty of critisisms of
agile methods, but most of them are critisisms in the small, or
frameworks claiming to be 'Agile', [which have merit, but tend to be
strawman
arguments](https://redpenguin101.github.io/html/posts/2022_10_14_agile_in_1000.html).

Agility in software development can best be summed up in a single
sentence, which happens to be one of the [four
values](http://agilemanifesto.org): Responding to change over
following a plan. The though process behind this should be pretty
obvious from everything that's been said so far: your plan (and so,
your estimate) is useless from the minute you take the first step,
since the assumptions on which you based that plan will very quickly
be invalidated. 

What tends _not_ to change over the course of the project are the
overall aims, the value that you expect to realize from being able to
do the thing that the software project will allow you to do[^1]. Even
if the path you intend to take to get there might change wildly over
the course of the project. A sense of this value, as assessed against
how much you can afford to spend for this to have an attractive return
on investment (ROI), should be the thing you structure your planning
process around.

[^1]: This is more true in more established businesses.

There are several ways you can improve this ROI. First, you can
structure your project so you focus on incremental delivery of value.
The faster you start to 'earn' back some of that investment, the
better your ROI is going to be. This has the ancilliary benefit
(though really it's very significant) of being able to incorporate
feedback from real, working software into your next plan, rather than
relying on vapourware.

Second, you want to avoid sunk-cost fallacy at all costs, by having a
circuit breaker in the process whereby, if the project is clearly in
trouble to the point where you don't think there's unlikely every to
be a positive ROI, you should just cancel it.

Startups has a very explicit - almost Darwinian - method for both of
these. They call it 'the runway'. Basically, it's how long you can
keep operating, based on the money you have, until the company folds.
There are two ways of extending the runway: either you get more
revenue from your product (incentiving incremental delivery), or you
get further capital investment (avoiding sunk cost - external
investors won't put money into something they don't see as likely to
have a decent ROI). If you reach the end of the runway, game over,
everyone goes home.

Even outside of a startup, software projects should be framed like
this: Someone has an idea for an improvement which will add value to
the company. They 'pitch' for 'investment' - really for permission to
work on delivering this idea within a fixed, quite short, maybe 6
months max, length of time. If they get the nod, after that 6 months
they run out of runway, and they have to go back to the 'investors'
again, demonstrate what value they've delivered so far, and what they
can deliver if they are given another fixed time increment.

## Design is not estimation
