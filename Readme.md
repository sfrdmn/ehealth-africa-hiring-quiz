# eHealth Africa Hiring Quiz

[![Build Status](https://travis-ci.org/sfrdmn/ehealth-africa-hiring-quiz.svg?branch=master)](https://travis-ci.org/sfrdmn/ehealth-africa-hiring-quiz)

## 1

```
Given a pattern and a string, find if the string follows the same pattern. Eg: Pattern : [a, b, b, a], String : "cat dog dog cat"
```

## 2

```
Given a number N return whether N is a perfect number or not.
Explain the time and memory complexity of your implementation.
(A perfect number is a positive integer that is equal to the sum of its proper positive divisors excluding the number itself)
```

Because integers are represented as floating point numbers with MAX of approx. _1.79e+308_, the algorithm runs in O(n) linear
time. The limitation is that it can only validate perfect numbers with <= 309 digits, i.e. the first 12 perfect numbers. To validate
any arbitrary perfect number, assuming bounded memory, the algorithm would have a O(n^2) complexity, where n is equal to x
in f(x) [[1]][triangle]

## 3

```
You have a number of meetings (with their start and end times).
You need to schedule them using the minimum number of rooms.
Return the list of meetings in every room as well as their starting and ending timestamps.
```

This is a type of interval scheduling problem [[2]][isp]. One solution is to represent each interval as a node in a graph where the edges represent scheduling conflicts with other nodes. The schedule then uses the minimal amount of rooms when its representative graph has a _k-coloring_ of minimum _k_ [[3]][vertex-coloring]

I do an exhaustive search with backtracking to find the k-chromatic graph. Exponential time complexity.

<!-- Links -->

[triangle]: https://en.wikipedia.org/wiki/Triangular_number
[isp]: https://en.wikipedia.org/wiki/Interval_scheduling
[vertex-coloring]: https://en.wikipedia.org/wiki/Graph_coloring#Vertex_coloring
