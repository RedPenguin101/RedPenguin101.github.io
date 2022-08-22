# A short design exercise: Tic-Tac-Toe in Clojure

Inspired by [this thread](https://old.reddit.com/r/Clojure/comments/wulfen/i_updated_my_first_clojure_program_based_on/), where a programmer implemented tic-tac-toe in about 100 LOC.

A 2 player, no-ai implementation of Tic Tac Toe.
Playable through the command line.
The user inputs one of `TL, TC, TR, L, C, R, BL, BC, BR` to indicate where they want to put their piece.

a sample game:

```
> _|_|_
> _|_|_
>  | |
> X's turn. Where do you want to go?
> C
> _|_|_
> _|X|_
>  | |
> O's turn. Where to you want to go?
> TC
> _|0|_
> _|X|_
>  | |
,,,
> _|O|_
> X|X|X
> O| |
> X wins!
```
## Game logic

Start with the game logic.
I'll need a `place :: board position -> board`
(place will have to detect if a move is legal)
and a `won? board -> \X \O or nil`
(won will have to check if there are 3 in a row).
I'll need to print the board

A 9-length array is probably the simplest structure for the board.
The board will be initialized as `nil`s, and have "X" or "O" put in positions.

```clojure
(def places (zipmap ["TL" "TC" "TR" "L" "C" "R" "BL" "BC" "BR"] (range)))
(defn place [board position player] (assoc board (places position) player))
(def init-board (vec (repeat 9 nil)))

(place init-board "C" "X")
;; [nil nil nil 
;;  nil "X" nil 
;;  nil nil nil]
(place init-board "TL" "O")
;; ["O" nil nil 
;;  nil nil nil
;;  nil nil nil]
```

To detect if a board is won (has three of the same symbol in a row), you need to pull out the rows, columns and diagonals.

```clojure
(defn rows [board] (partition 3 board))
(defn cols [board] (apply map vector (rows board)))
(defn diags [[a _ b _ c _ d _ e]] [[a c e] [b c d]])
(defn all-trips [board] (mapcat #(% board) [rows cols diags]))
(defn won? [board] (some #(or (apply = "X" %) (apply = "O" %)) (all-trips board)))


(won? [nil nil nil
       nil nil nil
       nil nil nil])
;; => nil
(won? ["X" "X" "X"
       nil nil nil
       nil nil nil])
;; => true
(won? ["X" "O" "X"
       nil nil nil
       nil nil nil])
;; => nil
(won? ["X" "O" "X"
       "X" nil nil
       "X" nil nil])
;; => true
(won? ["X" "O" "X"
       "O" nil nil
       "X" nil nil])
;; => nil
(won? ["X" "O" "X"
       "O" "X" nil
       "X" nil nil])
;; => true
```
And a draw is just when every slot is filled and the board isn't won:
```clojure
(defn draw? [board] (and (not (won? board)) (every? #{"X" "O"} board)))

(draw? ["X" "O" "X"
        "O" "O" "X"
        "X" "X" "O"])
;; => true
```

A move is legal if it's one of the recognized positions, and the slot isn't already filled:

```clojure

(defn legal? [board position]
  (and ((set (keys places)) position)
       (nil? (get board (places position)))))

(legal? init-board "C") ;; => true
(legal? init-board "BBL") ;; => nil
(legal? (place init-board "C" "X") "C") ;; => false
```

## Gameloop and IO
Now a quick way to print the board[^1] 

[^1]: Join is imported from Clojure.String - I don't love the unqualified import because the common idiom here is `str/join`. But it's OK for this small program.

```clojure
(defn print-board [board]
  (println (join "\n" (map #(join " " %) (partition 3 board)))))
```
You could definately get fancier, but this does the job.

I found it better to have `_` as the empty character, as that maintains spacing more easily.
That caused a problem with `legal`, since I'm relying on the fact that empties are `nil`.
This is probably a bad idea anyway: I've decided what constitutes a 'placed' character (X or O), so I really shouldn't care what an empty space is.
It's just "not X or O".
so Take 2.

```clojure
(defn legal? [board position]
  (and ((set (keys places)) position)
       (not (#{"X" "O"} (get board (places position))))))
```
Luckily that's the only place I put an explicit `nil`.

I think that's all I need to implement the game itself.
It's going to be a recursive function that:

* Prints the board state
* Checks if the game is won or drawn, and if so terminates with a message
* Otherwise, gets a new move, checks it, and recurs with that move.
I am going to iterate a bit.

```clojure
(defn play [board player off-turn moves])
```
The initial signature.
Player and offturn will be X and O, since having these to hand is useful.
Moves is a temp counter to prevent infinite loops, always a good idea when implementing a recursive function

```clojure
(defn play2 [board player off-player moves]
  (print-board board)
  (cond (> moves 9) :break
        (won? board) (println (str off-player " wins!"))
        (draw? board) (println "Game is drawn")
        :else :unmpl))
```

Implementing the break and termination conditions.
Not the _off-player_ wins in the case of a won board, because they're the last person to place a piece.


```
(play2 ["X" "X" "X"
        "_" "O" "_"
        "O" "_" "O"] "O" "X" 0)
;; =>
;; X X X
;; _ O _
;; O _ O
;; X wins!
```

```clojure
(defn play [board player off-player moves]
  (print-board board)
  (cond (> moves 9) :break
        (won? board) (println (str off-player " wins!"))
        (draw? board) (println "Game is drawn")
        :else
        (do (println (str player "'s turn. Enter (tb)/(lcr)"))
            (recur (place board (read-line) player)
                   off-player player
                   (inc moves)))))
```

Implementing the recursion, getting input from the player.
The problem here it there's no way to validate the input.
So put a separate function here:

```clojure
(defn get-legal-move [board]
  (let [move (upper-case (read-line))]
    (if (legal? board move) move
        (do (println "That's an illegal move!")
            (recur board)))))

(defn play [board player off-player moves]
  (print-board board)
  (cond (> moves 9) :break
        (won? board) (println (str off-player " wins!"))
        (draw? board) (println "Game is drawn")
        :else
        (do (println (str player "'s turn. Enter (tb)/(lcr)"))
            (recur (place board (get-legal-move board) player)
                   off-player player
                   (inc moves)))))
```

Note I also put an upper-case function in here, so the program itself will be scoped for upper case letters.

This is now a complete implementation.
Some cleaning up:

```clojure
(defn play
  ([] (play init-board "X" "O"))
  ([board player off-player]
   (print-board board)
   (cond (won? board) (println (str off-player " wins!"))
         (draw? board) (println "Game is drawn")
         :else
         (do (println (str player "'s turn. Enter (tb)/(lcr)"))
             (recur (place board (get-legal-move board) player)
                    off-player player)))))

(defn -main [] (play))
```

* A 0-arity function for convenience
* Remove the recursion-break-check
* A main wrapper for play.

Here's the full program.
It's about 35 LOC in total, with about 14 (down to `legal?`) for the game logic itself, and the rest being orchestration.
There are probably some bugs in there, and I never actually compiled it to a jar file.
But I think it's good enough for an exercise.

```clojure
(ns tictactoe
  (:require [clojure.string :refer [upper-case join]]))

(def places (zipmap ["TL" "TC" "TR" "L" "C" "R" "BL" "BC" "BR"] (range)))
(defn place [board position player] (assoc board (places position) player))
(def init-board (vec (repeat 9 "_")))

(defn rows [board] (partition 3 board))
(defn cols [board] (apply map vector (rows board)))
(defn diags [[a _ b _ c _ d _ e]] [[a c e] [b c d]])
(defn all-trips [board] (mapcat #(% board) [rows cols diags]))
(defn won? [board] (some #(or (apply = "X" %) (apply = "O" %)) (all-trips board)))
(defn draw? [board] (and (not (won? board)) (every? #{"X" "O"} board)))

(defn legal? [board position]
  (and ((set (keys places)) position)
       (not (#{"X" "O"} (get board (places position))))))

(defn print-board [board]
  (println (join "\n" (map #(join " " %) (partition 3 board)))))

(defn get-legal-move [board]
  (let [move (upper-case (read-line))]
    (if (legal? board move) move
        (do (println "That's an illegal move!")
            (recur board)))))

(defn play
  ([] (play init-board "X" "O"))
  ([board player off-turn]
   (print-board board)
   (cond (won? board) (println (str off-turn " wins!"))
         (draw? board) (println "Game is drawn")
         :else
         (do (println (str player "'s turn. Enter (tb)/(lcr)"))
             (recur (place board (get-legal-move board) player)
                    off-turn player)))))

(defn -main [] (play))
```

Comparing this against the link that started this post, I have the following observations:

* Being much shorter is better.
* But the redditors code is very clear
* I chose not to use `nil` to indicate an empty space. The comparison code does, and it works well. But using `nil` like this makes me nervous about overloading it. If it's an empty square, and that has particular semantics, I'd prefer to make it explicit.
* The comparison has much more in the way of user feedback than mine. Better printing, allows user to pick their mark etc. But I think the simplicity of mine is better.
* I like the comparisons way of just declaring the index of winning positions in `winning-configs`. "Config" is a funny word to use for it, but this could definately replace my `rows` `cols` and `diags`, and there would just be a map get over them. Something like

```clojure
(def trips [0 1 2 0 4 8 ,,,])
(defn get-trips [board] (partition 3 (map #(get board %) trips)))
```
