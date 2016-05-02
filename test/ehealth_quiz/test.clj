(ns ehealth-quiz.test
  (:require [ehealth-quiz.core :refer :all]
            [clojure.test :refer :all]))

(deftest test-q1

  (let [p1 (pattern-machine [:a :b :a :b])
        p2 (pattern-machine [:a :b :c :a :b :c :d])
        p3 (pattern-machine [:a :b :b :a] {:delimiter \,})]

    (testing "correct string acceptance"
      (is (not (p1 "")))
      (is (p1 "beep boop beep boop"))
      (is (not (p1 "beep beep beep beep")))
      (is (not (p1 "beep boop")))
      (is (p2 "cat ate a cat ate a dog"))
      (is (p2 "hello kitty is hello kitty is cool"))
      (is (p3 "why,not,not,why")))))

(deftest test-q2

  (let [perfects [6 28 496 8128]
        almost-perfects (map inc perfects)
        randos (filter #(not (contains? perfects %))
                       (take 10 (repeatedly #(rand-int 10000))))]

    (testing "perfect number calculation"
      (is (every? perfect? perfects))
      (is (every? (comp not perfect?) almost-perfects))
      (is (every? (comp not perfect?) randos)))))

(deftest test-q3

  (let [talks [{:title "what we're all doing wrong"
                :start "2016-05-01T08:00"
                :end "2016-05-01T09:00"}
               {:title "10 steps to becoming"
                :start "2016-05-01T10:15"
                :end "2016-05-01T10:30"}
               {:title "unintentional intentions"
                :start "2016-05-01T08:30"
                :end "2016-05-01T08:45"}
               {:title "neurosis of neuroscience"
                :start "2016-05-01T09:20"
                :end "2016-05-01T10:10"}
               {:title "how to eat beans"
                :start "2016-05-01T08:30"
                :end "2016-05-01T09:15"}]
        nth-talks (fn [indices] (map (partial nth talks) indices))]

    (is (= (optimize-schedule talks)
           {0 (nth-talks [0])
            1 (nth-talks [2])
            2 (nth-talks [4 3 1])}))))
