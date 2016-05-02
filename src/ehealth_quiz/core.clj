(ns ehealth-quiz.core
  (:require [ehealth-quiz.bigmath :as big]
            [clojure.core.reducers :as r]
            [clojure.math.combinatorics :as combo]
            [clj-time [core :as t] [format :as f]]))

;; Question 1

(defn- bind-or-match
  "Takes sequence of keyword/value pairs and greedily binds the values to
  their keywords, stopping when either a bad binding is found or the sequences
  is over. Yields [number of bindings, map of bindings]"
  [binding-value-pairs]
  (let [next (fn [[i bindings [k v & r]]]
               (cond
                 (nil? k) nil
                 (= (k bindings) v) [(inc i) bindings r]
                 (not (some (partial = v) (vals bindings))) [(inc i) (assoc bindings k v) r]))]
    (drop-last (last (take-while (comp not nil?) (iterate next [0 {} binding-value-pairs]))))))

(defn pattern-machine
  "Creates a parser for the given pattern which runs on a delimited string
   Not strict about prefix/postfix delimiters, nor delimiter repetition"
  [pattern-spec & [{:keys [delimiter], :or {delimiter \space}}]]
  (fn accept? [str]
    (let [substrs (take-nth 2 (partition-by #(= % delimiter) str))]
      (= (count pattern-spec)
         (first (bind-or-match (interleave pattern-spec substrs)))))))

;; Question 2

(defn bigdivisors
  "Returns reducer over sequence of proper divisors for a BigInteger"
  [n]
  (let [bign (big/big n)]
    (r/filter (fn [x] (big/= (big/mod bign x) (big/zero)))
              (big/range (big/one) (big/+ (big/quo bign (big/big 2)) (big/one))))))

(defn bigreduce+
  ([] (big/zero))
  ([x y] (big/+ x y)))

(defn perfect?
  "Returns whether the given integer is perfect. Potentially in parallel."
  [n]
  (let [bign (big/big n)] (big/= (r/fold bigreduce+ (bigdivisors bign)) bign)))

;; Question 3

(defn graph
  "Init graph of the form [{:id :data :edges}, ...] where id is
  synonymous with index in vector"
  [& vals]
  (vec (map-indexed #(assoc {} :id %1 :data %2 :edges []) vals)))

(defn add-edges
  "Add edges to graph by supply vectors of the vertex id followed by edge ids"
  [g & edges]
  (reduce (fn [g [id & edges]]
            ;; This is an undirected graph, so map all edges both ways
            (let [pairs (partition 2 (flatten (map combo/permutations (combo/cartesian-product [id] edges))))]
              (reduce (fn [g [id edge]]
                        (let [node (nth g id)]
                          (assoc g id (assoc node :edges (cons edge (:edges node))))))
                      g pairs)))
          g edges))

(defn reduce-indexed
  "Wish this was just in core..."
  ([f acc coll]
   (reduce-indexed f acc 0 coll))
  ([f acc i coll]
   (if (empty? coll) acc
       (let [v (first coll)
             vt (f acc i v)]
         (recur f vt (inc i) (rest coll))))))

(defn take-until
  "Take until predicate is met, inclusive of matching element"
  [f coll]
  (if (or (empty? coll) (nil? coll)) '()
      (let [[head & tail] coll]
        (cons head (lazy-seq (if (f head) '() (take-until f tail)))))))

(defn adjacency
  "Build an adjacency matrix for a graph"
  [g]
  (let [n (count g)
        ;; Minimum adjacency matrix for undirected graphs
        m (vec (for [i (range 0 n)] (vec (for [j (range 0 n)] (= i j)))))]
    (reduce-indexed (fn [m i node]
                      (let [adj (nth m i) edges (:edges node)]
                        (assoc m i (reduce #(assoc %1 %2 true) adj edges))))
                    m g)))

(defn can-color?
  "Whether node with the given id can have the given color applied.
  Assumes an undirected graph"
  [adj colors id color]
  (every? #(not= (nth colors %) color) (keep-indexed #(when %2 %1) (nth adj id))))

(defn k-coloring-bounded
  "Returns either a k-coloring of g where k <= bound,
  or nil if no such coloring can be found
  *air horn sound*
  Has bug where if bound > chromatic number it gives the wrong result
  But works fine with the k-chromatic function since bound won't exceed chromatic number"
  [bound g]
  (let [n (count g)
        adj (adjacency g)
        color-choices (range 0 bound)
        check-branch (fn check-branch [id color-map]
                       (if (= id n) color-map
                           (reduce (fn [color-map c]
                                     (if (can-color? adj color-map id c)
                                       (check-branch (inc id) (assoc color-map id c))
                                       color-map))
                                   color-map color-choices)))
        color-map (check-branch 0 (vec (take n (repeat nil))))]
    ;; Reduce color-map to its length and k value
    (let [[color-count k] (reduce (fn [[n k] c] [(inc n) (max c k)])
                                  [0 0]
                                  (map inc (take-while (comp not nil?) color-map)))]

      (if (not= color-count n)
        ;; If not all nodes mapped, it means no k bounded coloring could be found
        nil
        ;; If we're good, return k and the color map
        [k color-map]))))

(defn k-chromatic
  "Gives the k-chromatic coloring for an undirected graph"
  [g]
  (let [n (count g)]
    (last (take-until (comp not nil?) (for [i (range 1 (inc n))] (k-coloring-bounded i g))))))

(defn optimize-rooms
  "Returns minimal rooms needed for given schedule as well as room mapping"
  [schedge]
  (let [g (apply graph schedge)
        g (apply add-edges g
                 ;; Schedule overlaps are represented as graph edges
                 (map #(map :id %) (filter #(apply t/overlaps? (map (comp :time :data) %))
                                           (combo/combinations g 2))))]
    (last (k-chromatic g))))

(def ^:private formatter (partial f/parse
                                  (:date-hour-minute f/formatters)))

(defn interval
  "Helper for interval generation"
  [& pair]
  (apply t/interval (map formatter pair)))

(interval "2016-05-01T08:00" "2016-05-01T09:00")

(defn optimize-schedule
  "Given a list of meetings, return a mapping of the minimum number of rooms
  needed to host them to the meetings in each room.
  Pretty ugly function. Mostly just for jiggering the data format"
  [meetings]
  (let [schedule (map #(assoc % :time (interval (:start %) (:end %))
                              :start (formatter (:start %)) :end (formatter (:end %)))
                      meetings)
        rooms (optimize-rooms schedule)
        rooms->meetings (partition 2 (interleave rooms meetings))]
    (reduce (fn [table [room meeting]]
              (assoc table room (cons meeting (get table room))))
            (reduce #(assoc %1 %2 '()) {} (distinct rooms))
            ;; Sort so that meetings will be arranged in non-descending order
            (sort (fn [& pair] (apply #(= (compare %1 %2) 1) (map (comp :start second) pair)))
                  rooms->meetings))))
