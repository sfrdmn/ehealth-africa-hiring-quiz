const test = require('tape')
const {patternMatcher, isPerfect, optimizeSchedule} = require('./')

test('test question 1', function (t) {
  const p1 = 'a b a b'
  const p2 = 'a b c a b c d'
  const p3 = 'a b b a'
  const m1 = patternMatcher(p1)
  const m2 = patternMatcher(p2)
  const m3 = patternMatcher(p3, {delimiter: ', '})
  const t1 = 'beep boop beep boop'
  const t2 = 'beep beep beep beep'
  const t3 = 'beep boop'
  const t4 = 'cat ate a cat ate a dog'
  const t5 = 'hello kitty is hello kitty is cool'
  const t6 = 'why, not, not, why'

  t.notOk(m1(''), `[${p1}] rejects empty string`)
  t.ok(m1(t1), `[${p1}] accepts '${t1}'`)
  t.notOk(m1(t2), `[${p1}] rejects '${t2}'`)
  t.notOk(m1(t3), `[${p1}] rejects '${t3}'`)
  t.ok(m2(t4), `[${p2}] accepts '${t4}'`)
  t.ok(m2(t4), `[${p2}] accepts '${t5}'`)
  t.ok(m3(t6), `[${p3}] accepts '${t6}'`)
  t.end()
})

test('test question 2', function (t) {
  const perfects = [6, 28, 496, 8128]
  const almostPerfects = perfects.map((n) => n + 1)
  const randos = (new Array(5).fill(true))
      .map((_) => Math.floor(Math.random() * 10000))
      .filter((n) => perfects.indexOf(n) === -1)

  t.ok(perfects.every(isPerfect), `[${perfects.join(', ')}] are perfect`)
  t.notOk(almostPerfects.some(isPerfect), `[${almostPerfects.join(', ')}] are not perfect`)
  t.notOk(randos.some(isPerfect), `[${randos.join(', ')}] are not perfect`)
  t.end()
})

test('test question 3', function (t) {
	let talks = [{
		title: "what we're all doing wrong",
		start: "2016-05-01 08:00",
		end: "2016-05-01 09:00",
	}, {
		title: "10 steps to becoming",
		start: "2016-05-01 10:15",
		end: "2016-05-01 10:30",
	}, {
		title: "unintentional intentions",
		start: "2016-05-01 08:30",
		end: "2016-05-01 08:45",
	}, {
		title: "neurosis of neuroscience",
		start: "2016-05-01 09:20",
		end: "2016-05-01 10:10",
	}, {
		title: "how to eat beans",
		start: "2016-05-01 08:30",
		end: "2016-05-01 09:15",
	}]
  let rooms = optimizeSchedule(talks)

  t.deepEquals(rooms, {
    0: [talks[3], talks[0], talks[1]],
    1: [talks[2]],
    2: [talks[4]],
  }, `${JSON.stringify(rooms)} is optimized`)
  t.end()
})
