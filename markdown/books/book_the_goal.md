% The Goal, by Eliyahu M. Goldratt

# Some stream of conscious notes while reading

This is just some scattered notes based on an ongoing listen to the audiobook.

The Goal is to make money.

In the corporate interpretation, this means to increase net profit while increasing return on investment, and maintaining a positive cash flow.

In the operational interpretation (say, running a factory) this means increasing throughput while simultaneously decreasing operational expense and reducing inventory.

These three terms roughly mean sales (throughput), amounts spent on materials (inventory) and amounts spent on transforming inventory to throughput.

If you increase productivity in one specific area of the factory, but that doesn't increase throughput, then by definition you are increasing inventory. And if you don't lay people off, you are also increasing operational expense. Therefore you are not increasing productivity. **Local optima are to be avoided**.

Trying to manage a factory so that the production is exactly balanced with sales (a "balanced plant") is sub-optimal, because many processes are stochastic. For example, the sale themselves, the time machines take to produce, the availability of inventory. This means that at some point you will not have the capacity to meet sales, and therefore be missing out on throughput. **An optimally managed factory will have some slack in it some of the time.**

# A model for a factory

* A **component**: any material that flows through the factory, including raw materials and finished product.
* A **sale order**: demand for a certain quantity of finished products
* **Machine**: a process which transforms some components into other components, in a stochastic time, with a probability of problems occurring. One physical machines can 'be' several machines, in that they can do more than one process. But only one at once, and the changing from one to another takes labor.
* A **machine run** is an instance of such a process. It requires a certain amount of labor to set up for a run
* A **worker** is a supplier of labor. They run the machines: setting up runs and moving inventory. More specialized labor is required to maintain machines and switch them from one type of job setup to another. If a worker has no job they are idle. Workers are paid a fixed amount daily.
* **Component pile**: a pile of physical parts. They take up space, and cost money to store (in a warehouse). A machine has a fixed amount of space around it to store components. Components also cost time and labor to move from one pile to another pile.

```clojure
{:machines {:cx10 {}}
 :workers #{workers}
 :warehouses {}}
```

## A narrative / tutorial

The narrative would serve to take you through 'traditional' plant management. Essentially to the point where the Goal starts.

You start as the lead worker with a very simple factory. You have a machine that assembles eyeglasses from frames and lenses. 

1. You get orders in
2. you have to order the parts 
3. You have to assign labor to get the parts and bring them to the machine
4. You have to set up the machine for a run
5. You wait for the machine to finish the glasses. 
6. You assign labor to move the finished product to shipping

After a couple months of this, corporate would send you an accountant, who would point out that your machines and labor are idle much of the time, and therefore you are wasting money, and you are not shipping orders on time. He would tell you to maintain an inventory of parts and finished products ready to ship to get your efficiencies up. He would show you how to predict the amount of orders you will get every month, and maintain enough inventory to meet that demand.

The escalations would be something like

* Producing more than one type of eyeglasses (intro to machine setup). Here you would experiment with maintaining inventories of multiple types.
* Bringing in two more machines: frame assembly and lens finishing. This would introduce multi machine management
* Warehousing of components, in response to a purchaser getting a great deal on frame parts
