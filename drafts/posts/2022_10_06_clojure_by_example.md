# Clojure by Practical Example

A collection of short code snippets illustrating how to do practical things in Clojure.
It doesn't go into detail on the how and why.
Its aim is to give you enough code to get started and let you take it from there.
It doesn't provide idiom guidance or tell you how to write Clojure.

## Language basics
### Primitives
### Collections and their functions
### Common HOF
Map, Reduce, Mapcat.
### Throwing, catching and handling exceptions
## Project, Libraries and Build
### How to find and use libraries
### Building an uberjar with build-tools
## Tooling and IDEs
### VSCode and Calva
### Emacs and Cider
## Java interop
### Calling Java code
### Importing Java libraries
## Practical things
### Dealing with time
### Webservers
#### A simple webserver with routing
You need:
* Ring (and sub-libraries)
* Compojure, a routing library

```clojure
{:deps {org.clojure/clojure   {:mvn/version "1.11.1"}
        ring/ring             {:mvn/version "1.9.5"}
        ring/ring-defaults    {:mvn/version "0.3.3"}
        compojure/compojure   {:mvn/version "1.7.0"}}
 :paths ["src" "resources"]}
```

```clojure
(ns clojure-by-example.webserver
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [api-defaults wrap-defaults site-defaults]]
            [ring.util.request :as rreq]
            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]))

;; We keep a reference to our server here, so we can stop it on demand
(defonce server (atom nil))

;; Each route has a 'handler', which takes a ring request and returns
;; a map which it turned into a ring response
(defn readback-handler [request]
  {:status  200
   :headers {"Content-Type" "text"}
   :body    (pr-str request)})

;; This is our routing table. For any request the server receives, based on
;; its properties, it decides how to deal with it, including which handlers
;; to call
(defroutes routes
  (GET "/" [] "<h1>My Webserver</h1>")
  (GET "/readback" [] readback-handler)
  (POST "/readback" [] readback-handler)
  (route/not-found "<h1>Page not found</h1>"))

;; starting a jetty server. We put the resulting reference
;; into the server variable.
(defn start [port]
  (when-not @server
    (reset! server
            (jetty/run-jetty
             (wrap-defaults #'routes api-defaults)
             {:port port :join? false}))))

;; Stopping the server is a matter of calling its stop method
(defn stop []
  (when @server
    (.stop @server)
    (reset! server nil)))

(comment
  ;; running this will start the server. Go to localhost:3000/
  ;; and you should see a page with "My Webserver" on it.
  ;; Going to localhost:3000/readback will show you the full request
  (start 3000) 
  (stop) ;; stop the server by running this
  )
```

### Databases
### IO
## Spec
