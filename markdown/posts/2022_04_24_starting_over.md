# Starting Over: A clean slate and new publication program
The blog was getting much too messy. There was lot's of half finished stuff in there. In addition, I rewrote the publication program so that it now pre-processes the markdown, _post_-processes the markdown to Tufte format, and got rid of some old ASCIIDoc functionality that I won't be using.

## The new publishing policy
From now on, I'll only be publishing things when I think they are _finished_. This will prevent, I hope, the prevalence of half-written rubbish that was a large portion of the old list. Markdown files will be kept in a drafts folder until they are ready to be published

## The new publication functionality
Previously I was using a [Babashka](https://github.com/babashka/babashka) script to generate the markdown for files. It quite simply got a list of files from the markdown posts/books location and ran pandoc over them. It also _created the index_ of both posts and blogs.

The new functionality is very similar. First, It is now a proper program, not a bb script. This is just because the uberjar building was easier, I'm sure it could be done with bb.

The new program, which is version controlled [here](https://github.com/RedPenguin101/blogpublish) uses much of the same code. The additions are the preprocessor, and the postprocessor. The preprocessor takes a markdown file, rewrites some things, and outputs a new markdown file. Currently the only thing it does is re-write footnotes in native html:

```clojure
(def reference #"\[\^(\d+)\](?!:)")
(def reference-html "<sup>$1</sup>")

(def footnote #"(?m)^\[\^(\d+)\]:(.*)$")
(def footnote-html "<p class=\"footnote\">$1: $2</p>")

(defn prewrite-markdown [text]
  (-> text
      (str/replace reference reference-html)
      (str/replace footnote footnote-html)))
```

The post-processor takes html, parses it to a hiccup AST, and messes around with that AST. The focus is on formatting the doc in the "Tufte Style". That is, it pulls out footnotes and images, and puts them into a 'sidenote' div.

```clojure
(def newline? #{"\n" "\n  "})

;; HTML element type matching

(defn is-html-type [tags] (fn [hic] (and (coll? hic) (tags (first hic)))))
(defn is-class? [class] (fn [hic] (= class (:class (second hic)))))
(def heading? (is-html-type #{:h1 :h2 :h3}))
(def footnote? (every-pred (is-html-type #{:p}) (is-class? "footnote")))

(def tufte-section? (complement heading?))

(def t-section [:div.tufte-section])
(def main-text [:div.main-text])
(def sidenotes [:div.sidenotes])

(def side-content? (comp boolean (some-fn (is-html-type #{:figure :image}) footnote?)))

(defn divide-content [hic]
  (let [content (group-by side-content? hic)]
    [(content false) (content true)]))

(defn section-collect-and-split [hic]
  (let [[main-content side-content] (divide-content hic)]
    (conj t-section (into main-text main-content) (into sidenotes side-content))))

(defn parse
  ([hic] (parse [] hic 0))
  ([completed remaining itbreak]
   (cond
     (> itbreak 100) :break
     (empty? remaining) completed
     (= 1 (count remaining)) (into completed remaining)
     :else (let [[pre-heading [heading & post-heading]] (split-with tufte-section? remaining)
                 [section & from-next-head] (split-with tufte-section? post-heading)]
             (recur (vec (remove empty? (conj completed pre-heading heading (section-collect-and-split section))))
                    (first from-next-head)
                    (inc itbreak))))))

(defn html-prep [html]
  (let [hiccup (remove newline? (discard-doc (hk/as-hiccup (hk/parse html))))
        body (vec (remove newline? (nth hiccup 3)))]
    {:html-start (vec (take 2 hiccup))
     :head (vec (remove newline? (nth hiccup 2)))
     :body-start (vec (take 2 body))
     :body-content (vec (drop 2 body))}))

(defn rewrite [html]
  (let [{:keys [html-start head body-start body-content]} (html-prep html)]
    (conj html-start head (into body-start (parse body-content)))))

(defn tufte-style [html-in]
  (hc/html (rewrite html-in)))
```
