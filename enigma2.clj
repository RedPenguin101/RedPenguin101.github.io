(ns enigma2
  (:require [clojure.set :refer [map-invert]]
            [clojure.test :refer [deftest is are]]))

(def alpha (zipmap "ABCDEFGHIJKLMNOPQRSTUVWXYZ" (range)))
(defn str->num [string] (mapv alpha string))
(defn num->str [nums] (apply str (map (map-invert alpha) nums)))

(defn alpha-mapping [string]
  (zipmap (str->num "ABCDEFGHIJKLMNOPQRSTUVWXYZ") (str->num string)))

(def reflector (alpha-mapping "CDAB"))

(defn rotate
  ([rotor] (rotate rotor 1))
  ([rotor steps]
   (letfn [(f [v] (mod (- v steps) (count (:mapping rotor))))]
     (-> rotor
         (assoc :mapping (into {} (map (fn [[k v]] [(f k) (f v)]) (:mapping rotor))))
         (update :notches #(mapv f %))))))

(defn rotor [mapping notches init-pos]
  (-> {:mapping (alpha-mapping mapping)
       :notches (mapv alpha notches)}
      (rotate (alpha (first init-pos)))))

(defn notch-passed? [rotor] (some zero? (:notches rotor)))

(defn step [notch? rotors]
  (if (empty? rotors) nil
      (cons (cond-> (first rotors) notch? rotate)
            (step (notch-passed? (first rotors)) (rest rotors)))))

(defn run-through-rotors [letter rotor-mappings]
  (if (empty? rotor-mappings) letter
      (recur ((first rotor-mappings) letter)
             (rest rotor-mappings))))

(defn encode-letter [letter rotor-mappings]
  (-> letter
      (run-through-rotors rotor-mappings)
      reflector
      (run-through-rotors (reverse (map map-invert rotor-mappings)))))

(defn encode
  ([plaintext rotors] (encode [] (str->num plaintext) rotors))
  ([encoded plaintext rotors]
   (if (empty? plaintext) (num->str encoded)
       (recur (conj encoded (encode-letter (first plaintext) (map :mapping rotors)))
              (rest plaintext)
              (step true rotors)))))

(deftest degen-identity
  (is (= "" (encode "" [])))
  (is (= "" (encode "" [(rotor "DABC" "A" "A") (rotor "ABDC" "A" "A")])))
  ;; If rotors are 'identity' rotors, i.e. everything maps to itself,
  ;; then it should be the same as running through with no rotors at all
  (is (= (encode "ABCD" [])
         (encode "ABCD" [(rotor "ABCD" "A" "A")])
         (encode "ABCD" [(rotor "ABCD" "A" "A") (rotor "ABCD" "A" "A")])
         (encode "ABCD" [(rotor "ABCD" "A" "A") (rotor "ABCD" "A" "A") (rotor "ABCD" "A" "A")]))))

(deftest these-will-break
  (is (= "CDAB" (encode "ABCD" [(rotor "BADC" "A" "A")])))
  (is (= "ABCD" (encode "CDAB" [(rotor "BADC" "A" "A")])))
  (is (= "BCBA" (encode "ABCD" [(rotor "BADC" "A" "A") (rotor "BDAC" "A" "A")])))
  (is (= "ABAD" (encode "BCDA" [(rotor "BADC" "A" "A") (rotor "BDAC" "A" "A")])))
  (is (= "CDAB" (encode "ABCD" [(rotor "ABCD" "A" "A") (rotor "ABCD" "A" "A")]))))

(deftest reflection
  (let [rotors [(rotor "BADC" "A" "A") (rotor "ACDB" "A" "A") (rotor "BDAC" "A" "A")]]
    (are [input] (= input (-> input (encode rotors) (encode rotors)))
      "ABCD"
      "BCAD"
      "CADB")))

(deftest these-shouldnt-break
  (is (= "BCDAAD" (encode "ABCDBA" [(rotor "BDCA" "A" "A")]))))

(deftest non-similarity
  (let [rotors [(rotor "BCAD" "A" "A")]]
    (are [input] (not (apply = (encode input rotors)))
      "AAAA"
      "BBBB"
      "CCCC"
      "DDDD")))

(encode "ABCDCADB" [(rotor "ACDB" "C" "A") (rotor "BDCA" "B" "A")])
;; => "DDBCABBD"

(comment
  (def reflector (alpha-mapping "YRUHQSLDPXNGOKMIEBFZCWVJAT"))
  (encode "HELLOWORLDTHISISATESTOFMYENIGMAMACHINE"
          [(rotor "EKMFLGDQVZNTOWYHXUSPAIBRCJ" "A" "A")
           (rotor "AJDKSIRUXBLHWTMCQGZNPYFVOE" "A" "A")
           (rotor "BDFHJLCPRTXVZNYEIWGAKMUSQO" "A" "A")])
  ;; => "EFSORZEURZJGPTBNZIXAMTXIZYAQGRSSK"

  (encode "HELLOWORLDTHISISATESTOFMYENIGMAMACHINE"
          [(rotor "EKMFLGDQVZNTOWYHXUSPAIBRCJ" "A" "A")
           (rotor "AJDKSIRUXBLHWTMCQGZNPYFVOE" "A" "A")
           (rotor "BDFHJLCPRTXVZNYEIWGAKMUSQO" "A" "A")])
  1)

(clojure.string/upper-case "yruhqsldpxngokmiebfzcwvjat")
;; => "YRUHQSLDPXNGOKMIEBFZCWVJAT"

;; => "CIAGSNDRBYTPZFULVHEKOQXWJM"
