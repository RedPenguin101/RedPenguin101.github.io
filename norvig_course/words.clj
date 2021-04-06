(ns words
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.test :refer [deftest is testing]]
            [taoensso.tufte :as tufte :refer  [p  profile]]))

(def letters "ABCDEFGHIJKLMNOPQRSTUVWXYZ")

(def points (zipmap (str letters "_")
                    [1 3 3 2 1 4 2 4 1 8 5 1 3 1 1 3 10 1 1 1 1 4 4 8 4 10 0]))

(defn read-word-list [filename]
  (reduce (fn [[words pref] word]
            [(conj words word) (set/union pref (set (butlast (reductions str "" word))))])
          [#{}]
          (str/split-lines (str/upper-case (slurp filename)))))

(def words (first (read-word-list "words4k.txt")))
(def prefixes (second (read-word-list "words4k.txt")))

(defn find-words
  ([hand] (find-words "" hand #{}))
  ([word hand found-words]
   (let [found-words (if (words word) (conj found-words word) found-words)]
     (if (or (not (prefixes word)) (empty? hand)) found-words
         (set (apply concat found-words (for [l hand] (find-words (str word l) (str/replace-first hand l "") found-words))))))))

(defn find-prefixes
  ([hand] (find-prefixes hand "" #{}))
  ([hand pre results]
   (let [results (if (prefixes pre) (conj results pre) results)]
     (if (or (not (prefixes pre)) (empty? hand)) results
         (set (apply concat results (for [l hand] (find-prefixes (str/replace-first hand l "") (str pre l) results))))))))

(defn add-suffixes
  ([hand pre] (add-suffixes hand pre #{}))
  ([hand pre results]
   (let [results (if (words pre) (conj results pre) results)]
     (if (or (not (prefixes pre)) (empty? hand)) results
         (set (apply concat results (for [l hand] (add-suffixes (str/replace-first hand l "") (str pre l) results))))))))

(defn word-plays [hand board-letters]
  (apply set/union
         (for [pre (find-prefixes hand)
               l board-letters]
           (add-suffixes (str/replace-first hand pre "") (str pre l)))))

(comment
  (time (word-plays "ADEQUAT" ["I" "R" "E"])))

(defn word-score [word]
  (apply + (map points word)))

(defn top-n [hand board-letters n]
  (reverse (take-last n (sort-by word-score (word-plays hand board-letters)))))

(defn longest-words [hand board-letters]
  (reverse (sort-by count (word-plays hand board-letters))))

(deftest word-play-test
  (is (= #{"URD" "IT" "QUIET" "DUE" "RUT" "DIT" "TRADE" "ARE" "TUI" "DEE" "QUITE" "RUE" "EQUID" "DATE" "AID" "ERA" "DIE" "DEAR"
           "ART" "ATE" "RET" "ETA" "QI" "AR" "RAT" "TED" "TI" "ID" "QAID" "IDEA" "AREA" "RATE" "QUIT" "TIE" "TRUE" "DE" "EAR"
           "ET" "READ" "QUID" "EAU" "EAT" "TAR" "TEE" "ED" "RAD" "DUI" "ADEQUATE" "RE" "RED" "QUADRATE" "AIT" "AQUAE" "ER" "AE" "TEA" "TAE" "TEAR" "AI"}
         (word-plays "ADEQUAT" ["I" "R" "E"]))))

(deftest t
  (is (= #{"ERE" "SEL" "TREE" "ELSE" "SER" "REE" "SEE" "SET" "LETTER" "ELS" "TET" "REST" "STREET" "EL" "RET" "LET" "TEST"
           "LETTERS" "RES" "EEL" "TEL" "ET" "ERS" "TEE" "LEE" "RE" "ES" "TREES" "ER"}
         (find-words "LETTERS")))
  (is (= #{"REB" "ERE" "BAR" "REC" "ARE" "REE" "DAB" "DEE" "ARC" "ERA" "CEE" "DEAR" "CARE" "BAD" "BED" "CAR" "AR" "ARB" "BA"
           "DEB" "AD" "DE" "BRA" "EAR" "ACE" "READ" "CAD" "BE" "RACE" "ED" "RAD" "AB" "RE" "BEE" "RED" "ER" "CAB" "AE" "BEAR"}
         (find-words "ABECEDR")))
  (is (= #{"EN" "ERN" "SI" "RISE" "RAS" "IT" "ARE" "INS" "SER" "SEI" "SET" "NE" "TRAIN" "SAT" "RAIN" "REST" "IS" "STAIR" "TA"
           "RAISE" "ERA" "AT" "TIS" "RAN" "ART" "REI" "ATE" "IRE" "RET" "AN" "ANT" "NAE" "EAST" "ETA" "ANESTRI" "AIS" "ANE"
           "RETAINS" "SIR" "AIR" "AR" "RAT" "TI" "NASTIER" "TEN" "STEARIN" "STAIN" "ANTSIER" "TIN" "ANI" "RIA" "RETSINA" "RES" "RATE"
           "RATINES" "SAE" "STIR" "SEA" "TIE" "EAR" "AS" "RIN" "ET" "ARTS" "AIN" "ERS" "EAT" "ENS"
           "SEN" "TAR" "SIT" "SIN" "TAN" "EARN" "NET" "STAINER" "SRI" "ITS" "RE" "IN" "ES" "SENT" "NEAR" "SEAT" "ARS" "AIT" "ER"
           "AE" "TAS" "RETINAS" "NIT" "TEA" "TAE" "TEAR" "AI" "NA"}
         (find-words "AEINRST")))
  (is (= #{"ARM" "MAR" "TAM" "IT" "RID" "RAM" "AM" "MID" "DIT" "MAC" "MAT" "ARC" "TA" "AID" "AT" "ART" "TIC" "MAD" "MI" "AMI"
           "AIR" "CAR" "AR" "RAT" "TI" "ID" "CAM" "ACT" "MA" "RIA" "AD" "MIR" "DAM" "CAD" "DIM" "TAD" "TAR" "RAD" "RIM" "AIT"
           "CAT" "AIM" "AI"}
         (find-words "DRAMITC")))
  (is (= #{"RIDE" "EN" "ERN" "SI" "RISE" "RAS" "IT" "RID" "DIT" "TRADE" "ARE" "INS" "SER" "DEN" "SEI" "DETRAINS" "SET" "NE"
           "TRAIN" "DIS" "SAT" "RAIN" "DETRAIN" "SEND" "REST" "IS" "DATE" "STAIR" "TA" "RAISE" "AID" "ERA" "SIDE" "RANDIEST"
           "DIE" "AT" "TIS" "DEAR" "RAN" "ART" "REI" "ATE" "IDEAS" "IRE" "RET" "AN" "ANT" "NAE" "END" "EAST"
           "ETA" "ANESTRI" "AIS" "DIN" "ANE" "RETAINS" "SIR" "AIR" "AND" "AR" "ENDS" "RAT" "TED" "TI" "ID" "NASTIER" "TEN"
           "STEARIN" "STAIN" "ANTSIER" "STRAINED" "TIN" "IDEA" "ANI" "RIA" "AD" "RETSINA" "RES" "RATE" "RATINES" "SAE" "STIR"
           "SEA" "TIE" "SAD" "ADS" "RAISED" "ANTIRED" "DE" "ASIDE" "EAR" "AS" "RIN" "ET" "ARTS" "READ" "AIN" "ERS" "DINE" "TAD"
           "STAND" "EAT" "ENS" "SEN" "TAR" "SIT" "SIN" "IDS" "TAN" "EARN" "ED" "NET" "RAD" "STAINER" "TRIED" "SRI" "ITS" "RE"
           "IN" "ES" "SENT" "NEAR" "SEAT" "ARS" "RED" "AIT" "ER" "AE" "TAS" "INSTEAD" "RETINAS" "NIT" "TEA" "TAE" "TEAR" "AI"
           "SAID" "TRAINED" "NA"}
         (find-words "ADEINRST")))
  (is (= #{"EN" "IT" "OAT" "NE" "NOTE" "ON" "TOE" "ION" "TA" "AT" "ATE" "AN" "ANT" "NAE" "ETA" "NOT" "ANE" "TI" "TEN" "TO" "TIN"
           "ANI" "OE" "TIE" "TON" "TAO" "NO" "ET" "AIN" "EAT" "TAN" "NET" "IN" "INTO" "EON" "ONE" "AIT" "AE" "NIT" "TEA" "TAE"
           "AI" "NA"}
         (find-words "ETAOIN")))
  (is (= #{"URD" "UH" "SH" "US"}
         (find-words "SHRDLU")))
  (is (= #{"SIX" "EXIST" "EN" "SI" "IT" "STONE" "INS" "SEI" "SET" "NE" "NOTE" "ON" "NOS" "TOE" "NEXT" "EX" "ION" "SON" "IS"
           "TIS" "OES" "NOT" "XI" "ONS" "TI" "TEN" "OSE" "TO" "SEX" "TIN" "OE" "SO" "TIE" "TON" "SOT" "ONES" "NO" "ET" "NIX"
           "ENS" "XIS" "SEN" "SIT" "SIN" "OX" "NET" "NOSE" "ITS" "IN" "ES" "INTO" "OS" "SENT" "SOX" "EON" "ONE" "NIT"}
         (find-words "TOXENSI"))))
(comment
  (time (mapv find-words ["ABECEDR" "AEINRST" "DRAMITC" "ADEINRST" "ETAOIN" "SHRDLU" "TOXENSI"]))
  ;; 238.351ms
  ;; 23.6ms after prefix
  )

;; |A.....BE.C...D.| <- a row 

(def any-anchor (set (map str letters)))

(defn anchor [string]
  (set (map str string)))

(def anchor? set?)

(defn legal-prefix
  ""
  [i row]
  (let [prefix (apply str (reverse (take-while (comp not anchor?) (reverse (take (dec i) (drop 1 row))))))]
    [(when (not (every? #{\.} prefix)) prefix)
     (count prefix)]))

(comment
  (defn row-plays
    "Returns a set of legal plays in row. A row play is an (i, 'WORD') pair"
    [hand row]
    (for [[idx sq] (butlast (rest (map-indexed vector row)))]
      (if (anchor? sq)
        (let [[pre max-size] (legal-prefix idx row)]
          (if pre (add-suffixes hand pre (- idx (count pre)) #{})))))))

(comment
  (let [any (anchor letters)
        mnx (anchor "MNX")
        moab (anchor "MOAB")
        a_row ["|" "A" mnx moab "." "." any "B" "E" any "C" any "." any "D" any]
        a_hand "ABCEHKN"]
    (for [i (range 16)
          :when (anchor? (nth a_row i))]
      [i (apply str (reverse (take-while (comp not anchor?) (reverse (take (dec i) (drop 1 a_row))))))
       (legal-prefix i a_row)]
      #_(take-while (comp not anchor?) (reverse (take i (drop 1 a_row)))))))
