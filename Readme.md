# eHealth Africa Hiring Quiz

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

I took the question as an opportunity to try out a more proper "scientific computing" style implementation.
I.e. doing parallel map/reduce on large data. It's a bit silly: I would need way more cores to see any benefit,
not to mention, I would have gotten a bigger boost by just optimizing the algorithm, but it was a good exercise.

Because integers are represented as byte arrays, the implementation is approximately O(n^2) [[1]][triangle] where n is equal
to the integer given as input. Though, given infinite memory or small input, you could do it in O(n)

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