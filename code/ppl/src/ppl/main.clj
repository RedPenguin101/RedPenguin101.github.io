(ns ppl.main
  (:require [kixi.stats.core :as stats]))

(defn normal-pdf [x loc scale]
  (/ (Math/exp (/ (- (Math/pow (- x loc) 2))
                  (* 2 (Math/pow scale 2))))
     (* scale (Math/sqrt (* 2 Math/PI)))))

(defn normal-log-pdf [x loc scale] (Math/log (normal-pdf x loc scale)))



(defn latent-variable [nm args]
  {:name nm
   :args args})

(defn observed-variable [nm args observed]
  {:name nm
   :args args
   :observed observed})

(def mu (latent-variable :mu [0.0, 5.0]))
(def y-bar (observed-variable :y-bar, [mu, 1.0], 3.0))



;; nlpdf with mu~N(0,5), y-bar~N(mu,1)
;; y-bar observed at 3
;; eval at LV mu=4
;; sum of nlpdf(LV(mu), mean(mu), sd(mu)))))
;;        nlpdf(Observed(y-bar), LV(mu), sd(y-bar)))))
(+ (normal-log-pdf 4 0 5)
   (normal-log-pdf 5 4 1))
;; => -4.267314978843446

(defn collect-variables [variable]
  (if (float? variable) [variable]
      (mapcat collect-variables (:args variable))))

(collect-variables y-bar)

(defn evaluate-log-density [variable, latent-values]
  (if (and (every? float? (:args variable))
           (latent-values (:name variable)))
    (apply normal-log-pdf (latent-values (:name variable))
           (:args variable))
    :not-implemented))

(evaluate-log-density mu, {:mu 4.0 :y-bar 5.0})
;; => -2.848376445638773

(evaluate-log-density y-bar, {:mu 4.0 :y-bar 5.0})