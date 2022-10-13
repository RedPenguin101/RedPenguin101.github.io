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
