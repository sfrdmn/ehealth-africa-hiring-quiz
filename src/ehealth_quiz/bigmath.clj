(ns ehealth-quiz.bigmath
  "Helpers for manipulating BigIntegers"
  (:refer-clojure :exclude [mod - + * < <= = range])
  (:require [clojure.core.reducers :as r]))

(defmacro binop-tree
  "Transform binop and list into eval tree of binop on list with left -> right precedence"
  [op exprs]
  (first (last (take-while (comp not nil?)
                           (iterate (fn [[x y & r]] (if (nil? y) nil (cons (list op x y) r)))
                                    exprs)))))

(defmacro big [x] `(biginteger ~x))
(defmacro small [x] `(.longValue ~x))
(defmacro bits [x] `(.bitLength ~x))
(defmacro quo [x y] `(.divide ~x ~y))
(defmacro mod [x y] `(.remainder ~x ~y))
(defmacro quomod [x y] `(.divideAndRemainder ~x ~y))
(defmacro - [& args] `(binop-tree .subtract ~args))
(defmacro + [& args] `(binop-tree .add ~args))
(defmacro * [& args] `(binop-tree .multiply ~args))
(defmacro < [x y] `(clojure.core/= (.compareTo ~x ~y) -1))
(defmacro <= [x y] `(clojure.core/< (.compareTo ~x ~y) 1))
(defmacro = [x y] `(clojure.core/= (.compareTo ~x ~y) 0))
(defmacro one [] java.math.BigInteger/ONE)
(defmacro zero [] java.math.BigInteger/ZERO)

(defn range
  "Returns reducer on BigInteger range. Really bad performance.
  Would be cool if there were a BigInt persistent data structure"
  [from to]
  (r/take-while #(< % to) (iterate #(+ % (one)) from)))
