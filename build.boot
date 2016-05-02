(def version "1.0.0")

(set-env!
 :resource-paths #{"src"}
 :source-paths #{"test"}
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [org.clojure/math.combinatorics "0.1.1"]
                 [clj-time "0.11.0"]
                 [adzerk/boot-test "1.1.1"]])

(require '[adzerk.boot-test :refer :all])

(task-options!
 pom {:project 'ehealth-quiz/core
      :version version})
