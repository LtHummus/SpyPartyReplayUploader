# SpyParty Replay Uploader #

This is the beginnings of a more general version of [SCLManager](https://github.com/LtHummus/SclManager). While SCL is the biggest SpyParty tournament, I wanted to make a more general version for the smaller tournaments. The eventual goal for this project is for tournament organizers to be able to fill out a form and have everything they need to automatically run a tournament. 

## The Real Motivation ##

Ok, the real motivation for this project is for me to write a better version of my replay parser as well as have more experience with [Play Framework](https://www.playframework.com/) and [Slick](http://slick.lightbend.com/).

## Creating a Tournament ##

### Rules Declaration ###

Each tournament admin needs to decide the following things:

1. Any data that needs to be collected from the players for each bout (see the "Bout Form" section below).
1. Whether matches should be scored or not. If matches are not scored, submissions are recorded only for informational purposes and winners and losers must be tracked manually. If scoring is disabled, the rest of the items in this list are ignored.
1. Whether ties are allowed.
1. The number of games a player needs to win in order to win a bout.

### Bout Form ###

As a part of tournament creation, tournament admins can specify match specific information for players to submit on bout submission. Form fields can be either String (free text), Number (self explanatory), or Selection (select from a list). Basic validation is offered (minimums and maximums for numbers, max length for strings, etc). This data will be collected and stored alongside the bout data and it is up to tournament admins to decide how to use the data.

## TODO List ##

1. Integrate with [Challonge](https://challonge.com/)
1. Make a "Tournament Creation Form" for admins to set up tournaments and restrictions (for example, restrict matches to a certain map pool, etc). This would be awesome if we could run little snippets of JavaScript or something in order to _really_ customize things.
1. A frontend!

## Tech and Libraries Used ##

|  |  |
| ---  | ---  |
| [Scala](https://www.scala-lang.org/) | Programming Language |
| [Play](https://www.playframework.com/) | Web framework for Scala and Java |
| [Slick](http://slick.lightbend.com/) | Database query and access library |
| [SLF4J](https://www.slf4j.org/) | Logging Frontend |
| [ScalaCache](https://github.com/cb372/scalacache) | High performance cache facade for Scala |
| [Scalaz](https://scalaz.github.io/7/) | Functional programming library |
| [AWS](https://aws.amazon.com/) | Uses S3 for replay storage |
| [Apache Commons](https://commons.apache.org/) | Provides a lot of nice utility classes for Java
