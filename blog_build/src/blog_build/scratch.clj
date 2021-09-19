(ns blog-build.scratch
  (:require [clojure.java.shell :refer [sh]]
            [clojure.string :as str]))


(let [[type path] *command-line-args*]
  (doall [(sh "asciidoctor" path)
          (sh "mv" (str/replace path ".adoc" ".html")
              (str "../" type))]))

(defn convert [path filename]
  (sh "asciidoctor" (str path filename)))

(str/split-lines (:out (sh "ls" "../asciidocs/posts")))

(convert "../asciidocs/posts/"
         "2020_12_10_aoc2020_day10.adoc")

(time (mapv #(convert "../asciidocs/posts/" %)
            (str/split-lines (:out (sh "ls" "../asciidocs/posts")))))