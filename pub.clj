#!/usr/bin/env bb
(require '[clojure.java.shell :refer [sh]]
         '[clojure.string :refer [replace]])

(let [[type path] *command-line-args*]
  (doall [(sh "asciidoctor" path)
          (sh "mv" (replace path ".adoc" ".html") (str "../" type))]))
