# Spaced Repetition Algorithm

[Article link](https://e-student.org/spaced-repetition/)

## SM-18

[Link](https://supermemo.guru/wiki/Algorithm_SM-18)

1. Compute **startup interval** using **first forgetting curve**
2. After interval, make a **repetition**
3. After repetition, compute new **difficulty**
4. Estimate **retrievability** 
5. Estimate **stability**
6. Compute new **optimum interval** using **stabilization curve**
7. Update **memory matrices** (stabilization matrix and recall martix)
8. Go to 2

This is _way_ too complicated for a first pass.

##  Leitner System

[Link](https://e-student.org/leitner-system/)

This is by far the simplest system I came across. It's a 'box based system' rather than a 'card based system'. As in the reviews are on _boxes of cards_ rather than on the cards themselves.

There are 3 boxes. As you start reviewing the cards, the order of the cards in the box is changed.

Box 1 (the "every day box") contains new cards, and cards from box 2 that were answered incorrectly. Every card in box 1 should be answered every day. Cards from box 1 that are answered incorrectly stay in box 1, cards that are answered correctly go to box 2.

Box 2 contains cards from box 1 that were answered correctly, and cards from box 3 that were answered incorrectly. Box 2 is reviewed every other day.

Box 3 contains cards from box 2 that were answered correctly, and cards from box 3 that were answered correctly. Cards in box 3 are reviewed once per week.

This has the benefit of simplicity, and extensibility. Adding a box 4 with a repetition of one month is trivial.

The biggest downside I can thing of is that it seems to make 'lumps' in review. i.e. you'll have a big lump of cards to review when the month comes up.

I think a mitigation of this would be card base system which tags cards with boxes, like this:

```
Repetition = card-id, next-review-date, box
Review :: Repetition, correct? -> Review
NewDate :: current-date, box, correct? -> new-date
```

The `NewDate` function would calculate the new date based on the current box. The `Review` function would calculate the new review date, and the new box (incremented or decremented) based on the response. Something like:

```
review(old-rep, correct?):
  new-rep = new Repetition
  new-rep.card-id = old-rep.card-id
  new-rep.box = old-rep.box + (if correct? 1 else -1) 
  new-rep.date = new-date(old-rep.date, old-rep.box, correct?)
  return new-rep
```

One thing that Anki had which I think is worth emulating is that _any_ card that you get wrong goes back into box 1, regardless of its current box. So the function would be like `new-rep.box = if correct? old-rep + 1 else 1`

