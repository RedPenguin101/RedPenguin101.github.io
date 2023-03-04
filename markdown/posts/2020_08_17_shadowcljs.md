# How to set up shadow-cljs

## Installation

Note, this was written for Ubuntu linux. Other distros, Mac and especially Windows will have differences in how you install these, but each of them have good instructions.

You need 3 things to get shadow-cljs up and running.

* nodejs
* npm (the node package manager, that comes with nodejs)
* the shadow-cljs command line tool

### Install Nodejs

https://github.com/nodesource/distributions/blob/master/README.md#installation-instructions

from the command line, add the node repository, and install node. Here, we're installing the LTS variant.

```bash
# Using Ubuntu
curl -sL https://deb.nodesource.com/setup_lts.x | sudo -E bash -
sudo apt-get install -y nodejs
```

### Install Shadow-cljs

This will install shadow-cljs globally. It's the most convenient way to use it.

`sudo npm install -g shadow-cljs`

## Project Setup

### Quick reference
* `npm init`
* `npm install --save-dev shadow-cljs`
* `shadow-cljs init`
* update package.json to include react
* update shadow-cljs.edn per below
* create public/index.html per below
* create src/[projectname]/main/main.cljs per below
* run `shadow-cljs watch app` and watch the build log
* go to http://localhost:9090/ to check your app is running - you should see "hello world" on the page
* change the text of the h1 element in main.cljs, save the file and make sure it updates in your browser.

### Detailed

From the terminal:

Create the directory you want to make your project in.

Run `npm init` to initiate the project. This will create a package.json file, where your npm dependencies will live

Run `npm install --save-dev shadows-cljs`. This installs shadow-cljs locally (in the previous section you installed it globally). the `--save-dev` flag puts the dependency in your package.json file.

Run `shadow-cljs init` to initiate the shadow-cljs project. This will create a shadow-cljs.edn, which is where all your _clojurescript_ dependencies go, and also where your build instructions live. It tells shadow-cljs where to run the dev-server, where to look for the main function, and where to output the compiled javascript.

Update shadow-cljs.edn using the below config. Here's an explanation of the parts

* At the top you have the source paths. These are the paths that shadow-cljs will be able to access when it's looking for source code
* dependencies - the clojar resources that you want in your project. reagent and re-frame are two of the most common for most applications.
* dev-http - this is the port that shadow will launch the dev-server on. So when you run the app, you can go to http://localhost:9090 to see your running application
* builds - this is an edn map where each key is a build. When you run your app with shadow-cljs from the command line, you'll tell it which build to use. Here it's 'app'
* output-dir - this is where shadow-cljs will put the compiled js. It's important that this path is never touched by anything except shadow-cljs. the path is relative to the project root
* asset-path - where shadow will look for js asset when launching. This is relative to the _index.html_ file that is being used for the app
* target - tells shadow that this is a web app, as opposed to a node back-end app
* modules - here you'll tell shadow-cljs what function to run to launch the application. Here it will look for the `main` function in the file (relative to the root) _src/main/my_project/main.cljs_
* devtools - Misc stuff that shadow will do when in dev mode (i.e. when you use `shadow-cljs watch app`. `:after-load` will tell it to run the `reload` function in _src/main/my_project/mains.cljs_ every time you reload the application (which is every time you save a source file you're editing)

Create a file _public/index.html_ and put the below in it.

* the 'app' div is where we will be mounting our application
* the `script` is pointing our compiled js from the shadow-cljs.edn file we configured above

Create the file _src/main/my-project/main.cljs_ and put the below code in it.

* the `app` is your app - it's what is turned into html and rendered in the page
* the `mount` uses reagent to get the `app` and render it in the html index page we set up - specifically under the div with if `app` we set up in that file
* the `main` is what's called on initialization of the app. Here it just calls `mount`. This is one of the things we pointed shadow-cljs.edn to
* the `reload` does the same thing, but is only called when the app is reloaded. Again, this is what pointed shadow-cljs to.

Run `shadow-cljs watch app` from the project root directory. This will launch the application

Watch the build script - on the first launch it will update any dependencies necessary (including adding them to your package.json), launch all the infrastructure, and compile your clojurescript files to js, outputting them to where you instructed it. It will take a while to actually launch, because it has to compile everything. On reload (when you edit and save a .cljs file) it will only have to compile the changed files, so will be much quicker.

Once the build is complete, navigate your browser to http://localhost:9090/. You should see "hello world" in an h1 block (from your `app` function in main.cljs).

Next, make sure the 'reload' is working properly - while the app is running, go to main.cljs and change the text in the h1 div from "hello world" to something else. When you save, your terminal should show that the project is recompiling, and your browser should quickly reflect the changed text.

## Further resources
For actually learning how build a clojurescript application with reagent and re-frame, I recommend Eric Normands various courses. They're not free, but the quality is excellent, and he explains how all the pieces work very well.

* https://purelyfunctional.tv/courses/markdown-editor/[Your first re-frame app - a markdown editor]
* https://purelyfunctional.tv/courses/understanding-re-frame/[Understanding re-frame] (and reagent, and react, and the DOM)
* https://purelyfunctional.tv/courses/building-re-frame-components/[Building re-frame components]

## File templates
### package.json

```json
{
  "name": "qniform",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "author": "",
  "license": "ISC",
  "devDependencies": {
    "shadow-cljs": "^2.20.1"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0"
  }
}
```

### shadow-cljs.edn

```clojure
;; shadow-cljs configuration
{:source-paths
 ["src/dev"
  "src/main"
  "src/test"]

 :dependencies
 [[reagent "1.0.0-alpha2"]
  [re-frame "1.0.0"]]

 :dev-http {9090 "public/"}

 :builds
 {:app {:output-dir "public/compiledjs/"
        :asset-path "compiledjs"
        :target     :browser
        :modules    {:main {:init-fn my-project.main/main}}
        :devtools   {:after-load my-project.main/reload}}}}
```

### Index.html

```html
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
    <head>
    </head>
    <body>
        <div id="app"></div> 
        <script src="/compiledjs/main.js"></script>
    </body>
</html>
```

### main.cljs

```clojure
(ns my-project.main
  (:require [reagent.core :as r]
            [reagent.dom :as rd]))

(defn app []
  [:div
   [:h1 "hello world"]])

(defn mount []
  (rd/render [app]
             (.getElementById js/document "app")))

(defn main []
  (mount))

(defn reload []
  (mount))
```
