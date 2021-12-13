#!/usr/bin/env bb

(require '[hiccup.core :refer [html]]
         '[clojure.java.io :as io]
         '[clojure.java.shell :refer [sh]]
         '[clojure.string :as str])

(def adoc-post-path "./asciidocs/posts/")
(def markdown-post-path "./markdown/posts/")
(def html-post-path "./posts/")
(def index "./index.html")

;;;;;;;;;;;;;;;;;;;;
;; file operations
;;;;;;;;;;;;;;;;;;;;

(defn get-file-paths [path]
  (mapv #(str path %) (str/split-lines (:out (sh "ls" path)))))

(defn file-first-line [path]
  (with-open [rdr (io/reader path)]
    (first (line-seq rdr))))

;;;;;;;;;;;;;;;;;;;;
;; Post publishing
;;;;;;;;;;;;;;;;;;;;

(defn publish-ascii-post [path]
  (sh "asciidoctor" path)
  (str/replace path ".adoc" ".html"))

(defn publish-markdown-post [path]
  #_(sh "asciidoctor" path)
  (str/replace path ".md" ".html"))

(defn move-file [in-path out-path]
  (sh "mv" in-path out-path))

(defn publish-adoc! []
  (->> (get-file-paths adoc-post-path)
       (map publish-ascii-post)
       (map #(move-file % html-post-path))
       doall))

(defn publish-markdown! []
  (->> (get-file-paths markdown-post-path)
       (map publish-markdown-post)
       (map #(move-file % html-post-path))
       doall))

;;;;;;;;;;;;;;;;;;;;
;; Index building
;;;;;;;;;;;;;;;;;;;;

(defn post-title [file-path]
  (subs (file-first-line file-path) 2))

(defn path-from-html [html-post-path extention]
  (str (if (= extention :adoc) adoc-post-path markdown-post-path)
       (str/replace (last (str/split html-post-path #"/"))
                    ".html"
                    (if (= extention :adoc) ".adoc" ".md"))))

(defn get-post-title [html-post-path]
  (try (post-title (path-from-html html-post-path :md))
       (catch Exception _
        (post-title (path-from-html html-post-path :adoc)))))

(defn entry-from-file [file-path]
  (let [filename (last (str/split file-path #"/"))
        [y m d] (re-seq #"\d+" filename)]
    {:date (str/join "-" [y m d])
     :filename filename
     :title (get-post-title file-path)}))

(defn build-index [entries]
  [:div
   [:h1 "Joe's Blog"]
   [:table
    [:tr [:th "Date"] [:th "Title"]]
    (for [entry (reverse (sort-by :date entries))]
      [:tr
       [:td (:date entry)]
       [:td [:a {:href (str html-post-path (:filename entry))}
             (:title entry)]]])]])

(defn create-index! []
  (->> (get-file-paths html-post-path)
       (map entry-from-file)
       build-index
       html
       (spit index)))

(defn -main []
  (publish-adoc!)
  #_(publish-markdown!)
  (create-index!))

(-main)
