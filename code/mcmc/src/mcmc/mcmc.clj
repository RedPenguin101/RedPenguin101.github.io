(ns mcmc.mcmc
  (:require [oz.core :as oz]
            [fastmath.random :as rand])
  (:import (org.apache.commons.math3.distribution
            NormalDistribution
            ExponentialDistribution
            BinomialDistribution)))

(defn normal [mu sd] (rand/distribution :normal {:mu mu :sd sd}))

(defn mcmc [start-point proposal-fn better?]
  (let [new-point (proposal-fn start-point)]
    (if (better? new-point start-point)
      new-point 
      (recur start-point proposal-fn better? ))))

(def secret-distribution (NormalDistribution. 8 3))
(def sample (repeatedly 1e4 #(.sample secret-distribution)))

(defn llikelihood [{:keys [a b]}]
  (rand/log-likelihood (NormalDistribution. a b)
                       sample))

(def start-point {:a 5 :b 5})

(defn proposal-fn1 [x] (.sample (NormalDistribution. x 0.1)))
(defn proposal-fn2 [x] (max 0.00001 (proposal-fn1 x)))

(defn f [p] (mcmc p
                  #(-> %
                       (update :a proposal-fn1)
                       (update :b proposal-fn2))
                  (fn [b a] (> (llikelihood b) (llikelihood a)))))

(last (take 50 (iterate f start-point)))

