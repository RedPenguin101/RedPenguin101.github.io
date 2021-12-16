# Don't start with Microservices

Notes on a [blog post](https://arnoldgalovics.com/microservices-in-production/) by Arnold Galovics

Microservices have several purported benefits. 

* Fault Isolation
* Avoid locking in to technology
* Easier to understand
* Faster to deploy
* Scalable

But _none_ of them come for free. You have to design for them, and in practice they are hard to accomplish.

The first diffultly is **infra**. Monoliths just need less stuff than microservices. More accurately they tend to handle more stuff in the frameworks they run on. Which means more cost. Also, more moving pieces means more failures, and more complex failures.

**JL:** Agreed. Monoliths handle a lot of that stuff for you. They are obviously very tightly coupled, but usually very reliable as a result of that.

The **faster deployments** benefit should come with an asterisk. The goal of the microservice is that it's _independently deployable_. i.e. you can deploy a change without it impacting anything else. This is either _very hard_ to do, or for a subset of work, maybe just impossible. If you have a chain of microservices that talk to eachother, and you want to change one in the middle, it's unlikely you will _want_ to deploy that one in isolation, because the changes from that will likely _by design_ flow down the service chain to impact an end result (otherwise why do the change?). This isn't the case with pure refactorings or bug fixes, obviously, but for actual feature addition it's practically definitional that you won't want to deploy independently.

**JL:** counterpoint to this: As long as you're not making breaking changes to the API, you should be able to deploy separately. Like you make a non-breaking change to Service A, which is used by B. Maybe you add a new route `thing2` in addition to `thing`. A can be deployed just fine, since B will just use `thing`, which is unchanged. Then you can move on to deploying a new version of B, which now calls `thing2`. You are once again independently deployable.

Another difficulty is the impact an MS architecture has on the organization. The reality is that a PO (who doesn't care about the architecture) will ask for a feature that will require touching many services, but the changes to each service will not be meaningful in isolation, so you can't develop iteratively. The change will take _longer_ than in a corresponding monolith.

**JL:** reminds me of that [Krazam](https://www.youtube.com/watch?v=y8OnoxKotPQ) video. All of these points are based on Microservices not being actually isolated in practice. The title of the article might be **_Don't start with microservices if you can't isolate them properly_**. Maybe a corollary would be **_you can't isolate microservices properly in a new build, because you don't understand the correct way to divide the parts yet_**. The author doesn't explicitly say it I think, but it seems to me to be implied.

Addressing the **fault isolation** point, the authors point is that it take a lot of difficult design work to get that right. It's never free, you pay for it, and like anything you have to weigh the costs and benefits.

## My thoughts

The title says _don't **start** with microservices_, but I don't think he ever comes out and says what to me is the main thrust of the article. It even comes in a pleasingly syllogistic form:

1. The benefits of microservices all come from decoupled services
2. In a new system, you don't know ahead of time where the appropriate "dividing lines" between services are. So any services you build for a new system will not be optimally isolated
4. So you shouldn't use microservices on new build.

The article mainly talks about the problems that arise from inadequately decoupled services. His other main point is that adequate decoupling doesn't come for free: you have to work on it a lot.

I'm not clear on whether the author recommends _never_ using microservices, or is cautioning about the costs, or is saying that you should start more monolithically and _evolve_ towards a microservice architecture.

One thing that kept popping up in my head is that the way this article, and most of them, talk about microservices is like the 'swarm of bees' model. Like you have 100 services all buzzing around and interacting. And you have to think about all the bees in the swarm.

But that's alien to how we do software design anywhere else. A big part of design is in creating _layers_ and _hiding detail_. Combining lower level things into a higher level thing, enabling you to think in that higher level thing without worrying about the detail. But that seems to be pretty absent from the microservice discourse. You have to think about all the bees.

It seems to me that creating that higher level view would be beneficial, since you have to worry less about whether the individual services within that view are totally isolated, as long the higher level things _themselves_ are isolated from eachother.

