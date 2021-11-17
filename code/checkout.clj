(ns checkout
  (:require [clojure.string :as str]))

(def price-list {:beans 0.5
                 :tomatoes 0.3
                 :rice 0.7
                 :beef 1.4
                 :onion 0.2})

(defn receipt-line [item]
  (str (name (first item)) ": " (second item)))

(defn total [items]
  (apply + (vals items)))

(defn checkout [basket price-list]
  (let [items (select-keys price-list basket)]
    (str/join "\n" (conj (mapv receipt-line items)
                         (str "TOTAL: " (total items))))))

(println (checkout [:beans :tomatoes :rice :beef :onion]
                   price-list))