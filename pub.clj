#!/usr/bin/env bb

(require '[hiccup.core :refer [html]]
         '[clojure.java.io :as io]
         '[clojure.java.shell :refer [sh]]
         '[clojure.string :as str])

(def adoc-post-path "./asciidocs/posts/")
(def adoc-book-path "./asciidocs/books/")
(def markdown-post-path "./markdown/posts/")
(def markdown-book-path "./markdown/books/")
(def html-post-path "./posts/")
(def html-book-path "./books/")
(def index "./index.html")
(def books "./books.html")

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

(defn publish-ascii [path]
  (sh "asciidoctor" path)
  (str/replace path ".adoc" ".html"))

(defn publish-markdown [path]
  (sh "pandoc"
      path "-f" "markdown" "-t" "html" "-s" "-o"
      (str/replace path ".md" ".html"))
  (str/replace path ".md" ".html"))

(defn move-file [in-path out-path]
  (sh "mv" in-path out-path))

(defn publish! [pub-fn in-path out-path]
  (->> (get-file-paths in-path)
       (map pub-fn)
       (map #(move-file % out-path))
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
   [:h2 "Other stuff"]
   [:ul [:li [:a {:href "./books.html"} "Notes on books"]]]
   [:h2 "Blog posts"]
   [:table
    [:tr [:th "Date"] [:th "Title"]]
    (for [entry (reverse (sort-by :date entries))]
      [:tr
       [:td (:date entry)]
       [:td [:a {:href (str html-post-path (:filename entry))}
             (:title entry)]]])]])

(defn create-index! []
  (println "Creating index")
  (->> (get-file-paths html-post-path)
       (map entry-from-file)
       build-index
       html
       (spit index)))

(defn -main []
  (println "Publishing Adoc Posts")
  (publish! publish-ascii adoc-post-path html-post-path)
  (println "Publishing markdown Posts")
  (publish! publish-markdown markdown-post-path html-post-path)
  (println "Publishing Adoc Books")
  (publish! publish-ascii adoc-book-path html-book-path)
  (println "Publishing markdown Books")
  (publish! publish-markdown markdown-book-path html-book-path)
  (println "Creating index")
  (create-index!))

(-main)

