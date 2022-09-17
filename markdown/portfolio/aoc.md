# Advent of Code

I love coding puzzles, and my favorite is [Advent of Code](https://adventofcode.com/), by Eric Wastl.
AoC is an 'advent calander' of daily puzzle challenges run every year during December since 2015.
Each puzzle is loosely strung together with a Christmas themed story.
I started doing participating in 2019, and have done so every year since. I've also gone back and at least started every other year.
My approach is usually that I keep up with the daily releases until I get to one that I can't solve in under a few hours.
After that I'll take a more leisurely approach, thinking about the puzzles more deeply, reading the theory behind them if relevant, and looking at how other people solve them if I got really stuck.
I've only completed one year (2020). For a couple of other years I got 40+, but there are a few puzzles that I just couldn't get or found too frustrating or tedious to finish. 

Most of the solutions are written in Clojure, though I've done a few for 2021 in C and Zig as part of an effort to learn those languages. 
At various times I tried to write up my solutions in a sort of 'literate programming' style, but for the more complicated ones this got too onorous.

Here are the repos, together with how many stars[^1] I've got for each.

* [2015:](https://github.com/RedPenguin101/aoc2015) 46 stars
* 2016: 8 stars (no repo for this one, clearly never made a concerted effort)
* [2017:](https://github.com/RedPenguin101/aoc2017) 31 stars
* [2018:](https://github.com/RedPenguin101/aoc2018) 25 stars
* [2019:](https://github.com/RedPenguin101/aoc2019) 43 stars
* [2020:](https://github.com/RedPenguin101/aoc2020) 50 stars
* [2021:](https://github.com/RedPenguin101/aoc2021) 36 stars


[^1]: Stars are the measure of completion in AoC. There are 25 puzzles per year, each with 2 part for a total of 50 possible stars

## Some of my favorites
These are some of the puzzle that stick out to me, either because they were particularly fun, because I learned a lot, or because they were particularly challenging.

### IntCode
Number 1 with a bullet:
Not a single puzzle, but a theme that ran throughout 2019: the IntCode VM.
The narrative behind this is that, starting on [day 2](https://adventofcode.com/2019/day/2), you're tasked with MacGuyvering together a machine that mimics the "IntCode" Computer that ran your spaceship[^2].

[^2]: Santa, obviously, having been stranded in space while trying to deliver presents.

> The ship computer bursts into flames...You notify the Elves that the computer's magic smoke seems to have escaped. "That computer ran Intcode programs like the gravity assist program it was working on; surely there are enough spare parts up there to build a new Intcode computer!"

Over the course of the month, on alternating days, we would add functionality to the VM, from basic addition operations to an ASCII display.
And even after it was feature complete, other puzzles would use IntCode programs as their puzzle input
For example, an intcode program would be the method of controlling [a robot](https://adventofcode.com/2019/day/17).

In 2019 I was very much a beginner programmer.
I knew some Python, could use a Jupyter Notebook for data analysis, and could probably spin up a Flask webserver if I had to.
But the concepts of assembly, bytecode, instructions, opcodes, and especially addressing modes that I now know are standard concepts of executing directly on the CPU were completely foreign to me.
Puzzling all this out myself was great fun, though often very frustrating, especially as I was learning Clojure at the same time.
When I actually came to learn about assembly and VM bytecode, I found a lot of the ideas I already knew, and could already reason about, because of the IntCode series.

Really though, the format of program you built had a lot of the things I love about "proper" programming (which I don't consider programming puzzles to be).
The sense of progression as you added new pieces.
The face palm moment as you realize that the new requirement in todays puzzle conflicts with how you implemented the last piece, and you'll have to rewrite it.
The moment when you can use the program as a 'service' to solve another problem.

It also taught me some lessons that are highly applicable in actual programming:

1. **Have a regression test suite, and run it every time you touch your code:** I broke _so much stuff_, and spent so much time trying to trace down how, until I eventually wrote a test suite that ran every time I saved the source file. Knowing that a change I had made had broken something 5 seconds after I'd made it, rather than 30 minutes later, was a revelation.
2. **You're not going to get the decomposition / abstractions right first time unless you've done something very similar before:** I've done, I think, three complete rewrites of my intcode computer. The way I broke out the pieces at first was wrong. The second was inelegant. The third was OK. You're not going to be able to do rewrites in real life, but you need to prototype to explore the domain and be prepared to throw the prototype away.

The latest version of th IntCode interpreter is [here](https://github.com/RedPenguin101/aoc2019/blob/main/src/aoc2019/intcode.clj)

There are lots of puzzles that, in addition to adding functionality to the interpreter, are interesting challenges in themselves, [such as building a working version of breakout](https://adventofcode.com/2019/day/13), building a [turtle-like painting robot](https://adventofcode.com/2019/day/11), and [networking several intcode computers together](https://adventofcode.com/2019/day/7).

### 2020 Day 17 - Conway Cubes
[Problem](https://adventofcode.com/2020/day/17), [Solution](https://github.com/RedPenguin101/aoc2020/blob/main/day17.clj)

Conway's game of life is always fun to implement, and this had the added twist of a fourth dimension.
Clojure allows a particularly elegant solution to this, which is below in full in about 30 lines of code.

```clojure
(defn- parse-input [input]
  (remove nil? (apply concat (map-indexed (fn bleh [idx row] (map-indexed #(if (= \# %2) [idx %1 0] nil) row)) (str/split-lines input)))))

(defn- parse-input-4d [input]
  (remove nil? (apply concat (map-indexed (fn bleh [idx row] (map-indexed #(if (= \# %2) [idx %1 0 0] nil) row)) (str/split-lines input)))))

(defn- neighbors [[x y z]]
  (for [xvar [-1 0 1]
        yvar [-1 0 1]
        zvar [-1 0 1]
        :when  (not= xvar yvar zvar 0)]
    [(+ x xvar) (+ y yvar) (+ z zvar)]))

(defn- neighbors-4d [[x y z w]]
  (for [xvar [-1 0 1]
        yvar [-1 0 1]
        zvar [-1 0 1]
        wvar [-1 0 1]
        :when  (not= xvar yvar zvar wvar 0)]
    [(+ x xvar) (+ y yvar) (+ z zvar) (+ w wvar)]))

(defn- step [neighbors coords]
  (let [stuff (reduce (fn [A [c freq]] (if (#{2 3} freq) (update A freq conj c) A)) {} (frequencies (apply concat (map neighbors coords))))]
    (concat (get stuff 3)
            (filter (set coords) (get stuff 2)))))

(comment
  (time (count (last (take 7 (iterate (partial step neighbors) (parse-input (slurp "resources/day17example")))))))
  ;; => 112
  (time (count (last (take 7 (iterate (partial step neighbors) (parse-input (slurp "resources/day17input")))))))
  ;; => 353


  (count (last (take 7 (iterate (partial step neighbors-4d) (parse-input-4d (slurp "resources/day17example"))))))
  ;; => 848
  (time (count (last (take 7 (iterate (partial step neighbors-4d) (parse-input-4d (slurp "resources/day17input")))))))
  ;; => 2472
)
```

### Others

* [2019 Day 14 - Space Stoichiometry](https://adventofcode.com/2019/day/14) ([solution]()). I found this one very hard, so it sticks in my mind.
* 2020 Day 13: Shuttle Search. (Re)learning about Modular Arithmetic and the Chinese Remainder Theorem. I did a full writeup [here](https://redpenguin101.github.io/html/posts/2020_12_13_mod_mult.html) 
* [2020 Day 20 - Sea monsters](https://adventofcode.com/2020/day/20) ([solution]()) - probably the one that took me longest. Part 2 gave me _such_ a hard time.
* [2021 Day 14](https://github.com/RedPenguin101/aoc2021/blob/main/day14.md) - One example of a few where matrix math comes in handy.
* [2021 Day 15](https://github.com/RedPenguin101/aoc2021/blob/main/day15.md) - Dijsktra's algorithm