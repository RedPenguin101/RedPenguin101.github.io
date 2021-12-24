#!/usr/bin/env bb

(require '[hiccup.core :refer [html]]
         '[clojure.java.io :as io]
         '[clojure.java.shell :refer [sh]]
         '[clojure.string :as str])

(def index "./index.html")
(def books "./books.html")

(def post-paths {:html "./posts/"
                 :adoc "./asciidocs/posts/"
                 :markdown "./markdown/posts/"})

(def book-paths {:html "./books/"
                 :adoc "./asciidocs/books/"
                 :markdown "./markdown/books/"})

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

(defn path-from-html [file-path extension folder-paths]
  (str (extension folder-paths)
       (str/replace (last (str/split file-path #"/"))
                    ".html"
                    (if (= extension :adoc) ".adoc" ".md"))))

(defn get-title [file-path folder-paths]
  (try (post-title (path-from-html file-path :markdown folder-paths))
       (catch Exception _
        (post-title (path-from-html file-path :adoc folder-paths)))))

(defn entry-from-post [raw-paths file-path]
  (let [filename (last (str/split file-path #"/"))
        [y m d] (re-seq #"\d+" filename)]
    {:date (str/join "-" [y m d])
     :filename filename
     :title (get-title file-path raw-paths)}))

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
       [:td [:a {:href (str (:html post-paths) (:filename entry))}
             (:title entry)]]])]])

(defn create-post-index! []
  (->> (get-file-paths (:html post-paths))
       (map #(entry-from-post post-paths %))
       build-index
       html
       (spit index)))

(defn entry-from-book [raw-paths file-path]
  (let [filename (last (str/split file-path #"/"))]
    {:filename filename
     :title (get-title file-path raw-paths)}))

(defn book-index [entries]
  [:div
   [:h1 "Books"]
   [:ul
    (for [entry (sort-by :title entries)]
      [:li [:a {:href (str (:html book-paths) (:filename entry))}
            (:title entry)]])]])

(defn create-book-index! []
  (->> (get-file-paths (:html book-paths))
       (map #(entry-from-book book-paths %))
       book-index
       html
       (spit books)))

(defn -main []
  (println "Publishing Adoc Posts")
  (publish! publish-ascii (:adoc post-paths) (:html post-paths))
  (println "Publishing markdown Posts")
  (publish! publish-markdown (:markdown post-paths) (:html post-paths))
  (println "Publishing Adoc Books")
  (publish! publish-ascii (:adoc book-paths) (:html book-paths))
  (println "Publishing markdown Books")
  (publish! publish-markdown (:markdown book-paths) (:html book-paths))
  (println "Creating index")
  (create-post-index!)
  (println "Book Index")
  (create-book-index!))

(-main)

