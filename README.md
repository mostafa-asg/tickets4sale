## Introduction
This document is going to describe the most important aspects of the code. I will start from top to bottom. It 
only contains the CLI part.

## The magic start here
In the main method of the [CLI](https://github.com/mostafa-asg/tickets4sale/blob/master/cli/src/main/scala/tickets4sale/cli/Main.scala) 
you can start with:
```
cli[Id , InMemoryRepository].unsafeRunSync()
```
or
```
cli[Future , H2Repository].unsafeRunSync()
```
As you can see `cli` returns cats-effect's `IO` monad so nothing is happening unless you call `unsafeRunSync()` method.

## Repositories
`InMemoryRepository` and `H2Repository` are two different implementation of `Repository` trait:
```
trait Repository[F[_]] {
  def getAllShowsOpenForSale(queryDate: java.sql.Date): F[MultiResult[Show]]
  def findShowsAtSpecificDate(date: java.sql.Date): F[MultiResult[Show]]
  def getSoldTickets(showId: Int, dayNumber: Int): F[MultiResult[SoldTicket]]
  def insertShow(show: Show): F[ActionResult]
  def insertSoldTicket(ticket: SoldTicket): F[ActionResult]
}
```
As you can see I used higher-kinded type for parameterizing the return type of methods so each implementation of 
[Repository](https://github.com/mostafa-asg/tickets4sale/blob/master/core/src/main/scala/tickets4sale/core/db/repositories/Repository.scala) 
can have their own wrapper(effect). For instance I used cat's `Id` type for 
[InMemoryRepository](https://github.com/mostafa-asg/tickets4sale/blob/master/core/src/main/scala/tickets4sale/core/db/repositories/InMemoryRepository.scala) 
and `Future` for 
[H2Repository](https://github.com/mostafa-asg/tickets4sale/blob/master/core/src/main/scala/tickets4sale/core/db/repositories/H2Repository.scala). 
In this design we can easily add or change the return type instead of sticking to the fixed one like Future. 
Also we can use the sync version `Id` in the tests and use the async version like `Future` or Monix's `Task` in the production.

## General Database Initializer
I wanted to read the content of a CSV file, and insert the content to the database( In-memory or real). All the interaction 
with database should be done using a concrete instance of [Repository](https://github.com/mostafa-asg/tickets4sale/blob/master/core/src/main/scala/tickets4sale/core/db/repositories/Repository.scala). 
In this demo there are two instance but we can add more. How should I write a code that mutate database without caring 
the implementation of database or number of repositories? Meet [DbInitializer](https://github.com/mostafa-asg/tickets4sale/blob/master/core/src/main/scala/tickets4sale/core/db/DbInitializer.scala).
```
object DbInitializer {
  def initialize[F[_](shows: List[(String,String,String)], repo: Repository[F]): F[Either[Throwable,Unit]] = ???
}
```
Look at the return type of the initialize method. I wanted if all the content of the file, successfully written to the 
database then return `Unit` otherwise return an *exception*.

## Applicative
Inside the `initialize` method, for converting `List[F[Either[Throwable,Int]]]` to F[List[Either[Throwable,Unit]]] I used 
`sequence` method of cat's extension methods that requires a proper instance of `Applicative` type class:
```
def initialize[F[_] : Applicative](...) = {
   …
   val f_andList: F[List[ErrorOr[Int]]] = listOfF.sequence
   val f_andEither: F[ErrorOr[List[Int]]] = f_andList.map( _.sequence )
   ...
}
```

## There is database models and business models
There is a distinction between database models and business models. All repositories work with database model. 
You can find database models in: `Core/tickets4sale.core.db.models` and business models in `Core/tickets4sale.core.models`.
