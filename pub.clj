#!/usr/bin/env bb

(require '[hiccup.core :refer [html]]
         '[clojure.java.io :as io]
         '[clojure.java.shell :refer [sh]]
         '[clojure.string :as str])

(def adoc-post-path "./asciidocs/posts/")
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

(defn publish-post [path]
  (sh "asciidoctor" path)
  (str/replace path ".adoc" ".html"))

(defn move-file [in-path out-path]
  (sh "mv" in-path out-path))

(defn publish! []
  (->> (get-file-paths adoc-post-path)
       (map publish-post)
       (map #(move-file % html-post-path))
       doall))

;;;;;;;;;;;;;;;;;;;;
;; Index building
;;;;;;;;;;;;;;;;;;;;

(defn post-title [adoc-file-path]
  (subs (file-first-line adoc-file-path) 2))

(defn adoc-path-from-html [file-path]
  (str adoc-post-path (str/replace (last (str/split file-path #"/")) ".html" ".adoc")))

(defn entry-from-file [file-path]
  (let [filename (last (str/split file-path #"/"))
        [y m d] (re-seq #"\d+" filename)]
    {:date (str/join "-" [y m d])
     :filename filename
     :title (post-title (adoc-path-from-html file-path))}))

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
  (publish!)
  (create-index!))

(-main)