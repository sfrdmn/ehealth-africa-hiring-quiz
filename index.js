let moment = require('moment')
require('moment-range')

// Question 1

/**
 * Takes a grammar and creates an automata that accepts or rejects strings
 */
function patternMatcher (pattern = '', {delimiter} = {delimiter: /\s+/}) {
  return function accept (str = '') {
    const bindings = {}
    const bindingsReverse = {}
    const words = str.split(delimiter)
    return pattern.split(' ').every((key, i) => {
      const word = words[i]
      if (!word) return
      if (bindings[key]) {
        return bindings[key] === word
      } else if (!bindingsReverse[word]) {
        return (bindings[key] = bindingsReverse[word] = word)
      }
    })
  }
}

// Question 2

/**
 * Yields list of proper divisors for n
 */
function divisors (n = 0) {
  const result = []
  for (let i = 0; i <= n / 2; i++) {
    if (n % i === 0) {
      result.push(i)
    }
  }
  return result
}

/**
 * Whether n is a perfect number
 */
function isPerfect (n = 0) {
  if (n < 6) return false
  return divisors(n).reduce((sum, x) => sum + x, 0) === n
}

// Question 3

/**
 * Combinations on list choosing 2
 */
function combinePairs (arr = []) {
  const length = arr.length
  const result = []
  for (let i = 0; i < length; i++) {
    for (let j = i + 1; j < length; j++) {
      result.push([arr[i], arr[j]])
    }
  }
  return result
}

/**
 * Builds an adjacency matrix representation of the given
 * values as a graph
 */
function graph (vals = []) {
  const g = []
  for (let i = 0; i < vals.length; i++) {
    let row = []
    for (let j = 0; j < vals.length; j++) {
      row.push(i === j ? vals[i] : false)
    }
    g.push(row)
  }
  return g
}

function addEdges (g = [], edges = []) {
  return edges.reduce((g, edges, i) => {
    const id = edges.pop()
    edges.forEach((e) => {
      // Mark adjacency in both directions because undirected graph
      g[id][e] = true
      g[e][id] = true
    })
    return g
  }, g)
}

/**
 * Whether the given graph node is eligible to be colored with the given color
 */
function canColor (g = [], colorMap = [], id, color) {
  return g[id].every((e, i) => !e ? true : colorMap[i] !== color)
}

/**
 * Yields a k-coloring of graph, where k <= bound,
 * or false if no such coloring can be found
 * *air horn sound*
 */
function kColoringBounded (g = [], bound) {
  // Clamp bound
  bound = bound < 1 ? 1 : (bound > g.length ? g.length : bound)
  const colorMap = new Array(g.length)
  return branchOK(0) ? colorMap : false

  function branchOK (id) {
    if (id === bound) return colorMap.length === bound
    for (let color = 0; color < bound; color++) {
      if (canColor(g, colorMap, id, color)) {
        colorMap[id] = color
        if (!branchOK(id + 1)) {
          colorMap[id] = null
        } else {
          return true
        }
      }
    }
  }
}

/**
 * Yields k-chromatic coloring for given graph
 */
function kChromatic (g = []) {
  for (let i = 1; i <= g.length; i++) {
    let result = kColoringBounded(g, i)
    if (result) return result
  }
}

/**
 * Streaming insertion sort
 */
function insertion (fn) {
  return function insert (arr = [], val) {
    arr.splice(bisect(arr, val, 0, arr.length), 0, val)
    return arr
  }

  function bisect (arr, val, from, to) {
    if (from === to) return from
    const i = Math.floor(from + ((to - from) / 2))
    const mid = arr[i]
    const diff = fn(val, mid)
    if (diff > 0) {
      return bisect(arr, val, i + 1, to)
    } else if (diff < 0) {
      return bisect(arr, val, from, i - 1)
    } else {
      return from
    }
  }
}

/**
 * Insertion for sorting schedule events by start time
 */
let insertEvent = insertion(({start: a}, {start: b}) => moment(a).diff(moment(b)))

/**
 * Map schedule events to graph nodes
 */
function parseSchedule (schedge = []) {
  return schedge.map((event, i) => {
    const node = {}
    node.id = i
    const start = node.start = moment.utc(event.start)
    const end = node.end = moment.utc(event.end)
    node.time = moment.range(start, end)
    return node
  })
}

/**
 * Optimize schedule for minimum number of rooms needed
 * Returns map of rooms to events
 */
function optimizeSchedule (schedge = []) {
  const edges = combinePairs(parseSchedule(schedge))
      .filter(([{time: a}, {time: b}]) => a.overlaps(b))
      .map(([{id: a}, {id: b}]) => [a, b])
  // Build graph
  const g = addEdges(graph(schedge), edges)
  // Find k-chromatic coloring
  const rooms =  kChromatic(g)
  // Format output
  return rooms.reduce((obj, r, i) => {
    if (!obj[r]) obj[r] = []
    insertEvent(obj[r], schedge[i])
    return obj
  }, {})
}

module.exports = {
  patternMatcher,
  isPerfect,
  optimizeSchedule,
}
