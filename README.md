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
You can find database models in: `Core/tickets4sale.core.db.models` and business models in `Core/tickets4sale.core.models`.!  
![models](https://github.com/mostafa-asg/tickets4sale/blob/master/pics/db-models.png)  
Here is the end result of inserting into database from raw data. Imaging we have a show like this (read from CSV file):  
```
"AS YOU LIKE IT " , 2018-03-16 , "DRAMA"
```
This is the end result in the database:  
![database](https://github.com/mostafa-asg/tickets4sale/blob/master/pics/row-values.png)  
All the logic is happening inside `DbInitializer`.

## Business Logic
In this application we want two functionality:  
1. Get shows report
2. Buy tickets
Since this project is only CLI so I did not add buy ticket functionality to the UI but the code can handle buying tickets and generate appropriate report based on that. The gateway to all business logic is [TicketingService](https://github.com/mostafaasg/tickets4sale/blob/master/core/src/main/scala/tickets4sale/core/services/TicketingService.scala):
```
class TicketingService[F[_]:Functor](repo: Repository[F]) {

  def getReport(queryDate: String,showDate:String):F[Either[Throwable,Report]]
}
```

## Functor
Since I needed to map the value inside the context of `F`, I requested the Scala compiler using context bound `F[_] : Functor` to bring an instance of Functor of `F`.

## From DB model to business model
One of the most important thing inside business layer is finding the day of the show. Take the for example:
```
"AS YOU LIKE IT " , 2018-03-16 , "DRAMA"
```
For the query like this:
```
query date: 2018-09-09
show date: 2018-06-18
```
![](https://github.com/mostafa-asg/tickets4sale/blob/master/pics/day-number.png)  
First I check that `2018-06-18` is between  `2018-03-16` (openingDay) and `2018-06-23` (lastDay). If it is between these two dates then I calculate the day number. By calculating the day number I can easily find the show's place (BigHall or SmallHall) and the price of the show. When database's show model converted to business's show model I can easily call the show's instance methods. One of the useful method defined in `Show` is `getReport`:
```
def getReport(queryDate: LocalDate): ShowReport
```

## The power of sum type
I used the idea of some type for generating reports. The position of the `query-date` can produce *4 possible* outcome:  
![timeline](https://github.com/mostafa-asg/tickets4sale/blob/master/pics/timeline.png)  
The `OpenForSale` section is a little bit trickier than other 3 ones. Constructing ShowReport from 3 others is relatively simple, just need to change the status text, but in the `OpenForSale` section we need to calculate the available tickets and tickets left , so a little bit code needed. If the `query-date` is in the `OpenForSale` section something like this could be happen:  
![open for sale](https://github.com/mostafa-asg/tickets4sale/blob/master/pics/open-for-sale.png)

## Monoid
All the logic of calculating tickets number in the `OpenForSale` section is in the [OpenForSale](https://github.com/mostafa-asg/tickets4sale/blob/master/core/src/main/scala/tickets4sale/core/model/timeline/ShowTimeline.scala) object and I used the concept of `Monoid` to sum the tickets. (using cat's foldMap extension method):
1. Line number 31
2. Line number 47

## Validation
I used cat's `Validated` for validating file and date. You can find it in `Core/tickets4sale.core.util.Validation`. Also I composed 2 of these functions to simulate AND behavior in `Core/tickets4sale.cli.Main; Line 49`.

## Learn from Cat
Like fabulous cat library, I also moved all the extension methods to syntax package. Why I need extension methods? Well, for instance `LocalDate` has `isBefore` or `isAfter` method but has not `isBeforeOrEqual` or `isAfterOrEqual`. You can find extension methods in `Core/tickets4sale.core.syntax`.

## CLI
Let's focus on some important codes in CLI.

### Monadic flow
The interesting part is in the CLI method where I used for comprehension to define the application:
```
for {
  _ <- printLogo()
  _ <- printGreeting()

  filename <- filenameLoop()
  queryDate <- dateLoop("Enter query date:")
  showDate <- dateLoop("Enter show date:")

  repository <- createRepository[F,R](filename)
  service = createService[F,R](repository)
  report = getReport[F](service, queryDate, showDate)

} yield getJson[F](report)
```
And because the return value is wrapped in `IO` nothing will be happened until you call one of the unsafe methods.

### Loops
When the user enters some invalid data, after prompting the error message, user will be asked to enter again. This mechanisms is achieved through recursive call. `filenameLoop` and `dateLoop` are recursive functions but they are not **stack safe**.

### Lifting
Inside for comprehension, all the monads must be the same. In a `filenameLoop` or `dateLoop`, the first monad is `IO` but after validation we ended up with `Validated` which is not monad. So I lifted the `Validated` to `IO[Boolean]`.
```
implicit class ValidatedOps[A,B](value: Validated[A,B]) {
  def liftToIO(implicit S: Show[A]): IO[Boolean] = value match {
    case Valid(data) => IO.pure(true)
    case Invalid(data) => IO {
      println( S.show(data) )
      false
    }
  }
}
```
**Note:** I used cat's `Show` type class to convert type B to string representation.

### Type classes
In the `createRepository` method, I should create a repository and pass an instance of it to `DbInitializer`. For creating an instance of a repository I created a type class Builder:
```
trait Builder[T] {
  def build(): T
}
```
But `DbInitializer` only accepts a type that is a subtype of a `Repository`. To overcome this problem I used an upper type bound in the method signature. So overall we have:
```
def createRepository[F[_]:Applicative, R <: Repository[F]](csvFilename: String)(implicit repoBuilder: Builder[R]):                                                                                           IO[F[Either[Throwable, R]]] = {
}
```

### End
Thank you for reading.
