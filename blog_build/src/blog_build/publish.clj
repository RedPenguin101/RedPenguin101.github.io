(ns blog-build.publish
  (:require [hiccup.core :refer [html]]
            [clojure.java.shell :refer [sh]]
            [clojure.string :as str]))

(def adoc-post-path "../asciidocs/posts/")
(def html-post-path "../posts/")

;;;;;;;;;;;;;;;;;;;;
;; file operations
;;;;;;;;;;;;;;;;;;;;

(defn get-file-names [path]
  (str/split-lines (:out (sh "ls" path))))

(defn get-file-paths [path]
  (mapv #(str path %) (str/split-lines (:out (sh "ls" path)))))

(get-file-paths adoc-post-path)

;;;;;;;;;;;;;;;;;;;;
;; Post publishing
;;;;;;;;;;;;;;;;;;;;

(defn publish-post [path]
  (sh "asciidoctor" path)
  (str/replace path ".adoc" ".html"))

(defn move-file [in-path out-path]
  (sh "mv" in-path out-path))

(defn publish! []
  (->> (get-file-paths adoc-post-path)
       (map publish-post)
       (map #(move-file % html-post-path))))

(publish!)

(comment
  (publish-post (first (get-file-paths "../asciidocs/posts/")))
  (sh "mv" "../asciidocs/posts/2020_02_16_coord_systems_clojure.html" "./")
  (move-html-files "../asciidocs/posts/" "../posts"))

;;;;;;;;;;;;;;;;;;;;
;; Index building
;;;;;;;;;;;;;;;;;;;;

(defn build-index [entries]
  [:div
   [:h1 "Joe's Blog"]
   [:table
    [:tr [:th "Date"] [:th "Title"]]
    (for [entry (reverse (sort-by :date entries))]
      [:tr
       [:td (:date entry)]
       [:td [:a {:href (str "./posts/" (:filename entry))}
             (:title entry)]]])]])

(comment
  (html (build-index (read-entries! ds))))

(defn create-index! [filepath]
  (spit filepath (html (build-index (read-entries! ds)))))

(defn -main []
  (create-index! "../index.html"))

