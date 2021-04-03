(ns church)

(defn zero [f]  (fn [x] x))
(defn one [f]   (fn [x] (-> x f)))
(defn two [f]   (fn [x] (-> x f f)))
(defn three [f] (fn [x] (-> x f f f)))
(defn four [f]  (fn [x] (-> x f f f f)))

(defn church->int [n] ((n inc) 0))

(church->int zero)
(church->int three)

(church->int (add three two))
(church->int (add three four))


(defn add-1 [n] (fn [f] (fn [x] (-> x ((n f)) f))))
(defn add-2 [n] (fn [f] (fn [x] (-> x ((n f)) f f))))
(defn add-3 [n] (fn [f] (fn [x] (-> x ((n f)) f f f))))


(church->int (add-1 three))
(church->int (add-2 three))
(church->int (add-3 three))

(defn add-3 [n] (fn [f] (fn [x] (-> x ((n f)) ((three f))))))
(church->int (add-3 three))
(church->int (add-3 two))
(church->int (add-3 four))


(defn add [n1 n2] (fn [f] (fn [x] (-> x ((n1 f)) ((n2 f))))))

(church->int (add one two))
(church->int (add three four))

(defn square [x] (* x x))
((three square) 0)