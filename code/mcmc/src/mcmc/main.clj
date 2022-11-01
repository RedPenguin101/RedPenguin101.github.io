(ns mcmc.main
  (:require [oz.core :as oz]
            [fastmath.random :as rand])
  (:import (org.apache.commons.math3.distribution
            NormalDistribution
            ExponentialDistribution
            BinomialDistribution)))

(oz/start-server!)

(def sample (repeatedly 1e4 #(.sample (NormalDistribution. 10 3))))

(defn MH [[mu sd] data]
  (let [sd' (max 0.00001 (.sample (NormalDistribution. sd 0.1)))
        mu' (max 0.00001 (.sample (NormalDistribution. mu 0.1)))]
    (if (< (rand)
           (Math/exp (- (rand/log-likelihood (NormalDistribution. mu' sd') data)
                        (rand/log-likelihood (NormalDistribution. mu sd) data))))
      [mu' sd']
      [mu sd])))

(defn MH2 [params d data]
  (let [params' (map #(max 0.00001 (.sample (NormalDistribution. % 0.1))) params)]
    (if (< (rand)
           (Math/exp (- (rand/log-likelihood (apply d params') data)
                        (rand/log-likelihood (apply d params) data))))
      params'
      params)))

(defn normal [mu sd] (rand/distribution :normal {:mu mu :sd sd}))
(def priors [5 5])
(last (take 200 (iterate #(MH2 % normal sample) priors)))

(def mcmc (take 10000 (iterate #(MH % sample) priors)))
(def results (apply concat
                    (map-indexed
                     #(vector {:t %1 :var :mu    :val (first %2)}
                              {:t %1 :var :sigma :val (second %2)})
                     mcmc)))

(oz/view!
 [:div
  [:vega-lite {:data {:values results}
               :mark :line
               :encoding {:x {:field :t :type :quantitative}
                          :y {:field :val :type :quantitative}
                          :color {:field :var :type :nominal}}}]
  [:vega-lite {:data {:values (drop 200 (filter #(= :mu (:var %)) results))}
               :mark :bar
               :encoding {:x {:field :val :bin {:step 0.01} :title "mu"}
                          :y {:aggregate :count}}}]
  [:vega-lite {:data {:values (drop 200 (filter #(= :sigma (:var %)) results))}
               :mark :bar
               :encoding {:x {:field :val :bin {:step 0.01} :title "sigma"}
                          :y {:aggregate :count}}}]])


(def posterior-mu    (map first  (drop 200 mcmc)))
(def posterior-sigma (map second (drop 200 mcmc)))


(def generated (take 1e4 (repeatedly #(.sample (NormalDistribution. (rand-nth posterior-mu)
                                                                    (rand-nth posterior-sigma))))))

(oz/view!
 [:div
  [:vega-lite
   {:data {:values (map #(hash-map :value %) sample)}
    :mark :bar
    :encoding {:x {:field :value :bin {:step 1}
                   :title "Posterior predictive"}
               :y {:aggregate :count}}}]
  [:vega-lite
   {:data {:values (map #(hash-map :value %) sample)}
    :mark :bar
    :encoding {:x {:field :value :bin {:step 1}
                   :title "Sample"}
               :y {:aggregate :count}}}]])

(concat (map #(assoc (zipmap [:x :count] %)
                     :cat :samples)
             (frequencies (map #(int (Math/floor %)) sample)))
        (map #(assoc (zipmap [:x :count] %)
                     :cat :generated)
             (frequencies (map #(int (Math/floor %)) generated))))

(oz/view!
 {:data {:values (concat (map #(assoc (zipmap [:x :count] %)
                                      :cat :samples)
                              (frequencies (map #(int (Math/floor %)) sample)))
                         (map #(assoc (zipmap [:x :count] %)
                                      :cat :generated)
                              (frequencies (map #(int (Math/floor %)) generated))))}
  :mark :bar
  :encoding {:x {:field :x :type :quantitative}
             :y {:field :count :type :quantitative
                 :stack nil}
             :color {:field :cat :type :nominal}
             :opacity {:field :cat :type :nominal}}})

;; generalise to many distrs

(def p 0.3)
(def s2 (repeatedly 1e4 #(.sample (BinomialDistribution. 10 p))))
(defn binom [trials p] (rand/distribution :binomial {:trials trials :p p}))

(def debug3 [])
(defn uniform [lower upper]
  (def debug3 [lower upper])
  (rand/distribution
   :uniform-real
   {:lower (min lower upper)
    :upper (max lower upper)}))

(last (take 200 (iterate #(MH2 % (partial binom 10) s2) [0.5])))

;; generalise to larger model

;; x~N(mu,sigma)
;; mu~N(8, 1)
;; sigma~U(2, 4)



(defn propose [x] (if (number? x) (max 0.0001 (.sample (NormalDistribution. x 0.1)))
                      x))

(defn draw-from [priors param]
  (if (number? param) param
      (let [[d & ps] (param priors)]
        (rand/sample (apply d (map #(draw-from priors %) ps))))))

(draw-from priors :outcome)

(defn propose-all [[d & ps]]
  (into [d] (map propose ps)))

(defn MH3 [priors data]

  (let [priors' (update-vals priors propose-all)
        [d & ops'] (:outcome priors')
        [_ & ops]  (:outcome priors)]
    (def debug {:priors priors :priors' priors'})
    (if (< (rand)
           (Math/exp (- (rand/log-likelihood (apply d (map #(draw-from priors' %) ops')) data)
                        (rand/log-likelihood (apply d (map #(draw-from priors %) ops)) data))))
      priors' priors)))

(def priors {:outcome [normal  :mu :sigma]
             :mu      [normal    5      5]
             :sigma   [uniform   2      4]})

(last (take 1000 (iterate #(MH3 % sample) priors)))

debug3

(MH3 debug sample)
