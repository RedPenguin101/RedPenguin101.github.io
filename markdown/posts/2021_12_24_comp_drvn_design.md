# Simple Component Driven ClojureScript

[Link](https://davidvujic.blogspot.com/2021/12/simple-component-driven-clojurescript.html?utm_source=pocket_mylist)

* Grouping relevant code into _components_, components into features, features into views, views into an app.
* Lego like.
* To do this effectively, we would like something to host and render our components, segregated from any specific application / context. [Storybook](https://storybook.js.org/) does that, but you can do it without a tool.
* Quick and dirty: set up a new build alias 'story' with browser target, but on a separate port. Then an `app.story` namespace where you can quickly chuck any component you want to render in isolation in that target. Have one browser rendering your full app, and one just rendering the component.

When you want to get more complex, use storybook. Video on how [here](https://youtu.be/beMFh99EE7w)
