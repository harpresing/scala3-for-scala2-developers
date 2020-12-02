import type_classes.scope.Endpoint

/**
 * TYPECLASSES
 * 
 * Scala 3 introduces direct support for typeclasses using contextual features of the language.
 * Typeclasses provide a way to abstract over similar data types, without having to change the 
 * inheritance hierarchy of those data types, providing the power of "mixin" interfaces, but 
 * with additional flexibility that plays well with third-party data types.
 */
object type_classes:

  /**
   * OOP way - We have endpoints that return Json and want a way to make serde for all 
   * objects with their endpoints generic enough
   */
  object scope:
    trait Json
    trait JsonSerializable {
      def serialize(): Json
      def deserialize(json: Json): Unit
    }

    /**
     * In typical OOP we would extend a trait letting you do serde, but we'd
     * need to do it for all objects seperately
     */
    final case class Person(name: String, age: Int) extends JsonSerializable {
      def serialize(): Json = ???
      def deserialize(json: Json): Unit = ???
    }
  
    final case class Endpoint[A]()
    
    def publish[A <: JsonSerializable](endpoint: Endpoint[A]): Json = ???

  /**
   * This is essentially a Type class, allowing Person to be 
   * decoupled from how its serde is done! Usually 
   */
  object scope2:
    final case class Person(name: String, age: Int)
    trait Json
    
    // This is the type class
    trait JsonCodec[A] {
      def serialize(): Json
      def deserialize(json: Json): A
    }
  
    // In Scala 2 you'd usually make this implicit
    val personJsonCodec: JsonCodec[Person] = ???
  
    // then you wouldn't need to pass jsonCodec explicity since it would be 
    // implicit
    def publish[A](endpoint: Endpoint[A], jsonCodec: JsonCodec[A]): Json = ???

  /**
   * The way to do this in Scala 3 is using "given" and "using".
   * given - Provides a way to perform computation on a type A by creating
   * an implementation of the type class
   * using - providing a way to convert A to Json (say) and back
   */
  object scope3:
    trait Json
    trait JsonCodec[A]:
      extension (a: A) def serialize: Json
      def deserialize(json: Json): A

    final case class Person(name: String, age: Int)
    object Person:
      given JsonCodec[Person]:
        // This is an extension method so that you can do (a: Person).serialize
        extension (a: Person) def serialize: Json = ???
        
        def deserialize(json: Json): Person = ???
      end given
      
    // Then you'd employ "using" instead of (implicit json: JsonCodec[A])  
    def publish[A](endpoint: Endpoint[A])(using json: JsonCodec[A]): Json = ???

    def publishUsingSummon[A: JsonCodec](endpoint: Endpoint[A]) =
      val jsonCodec = summon[JsonCodec[A]]
      ???
    
    /**
     * Advanced example - needs to use as keyword 
     * can be also written as:
     * given [A] (using a: JsonCodec[A]) as JsonCodec[List[A]]
     */
    given [A: JsonCodec] as JsonCodec[List[A]]:
      extension (list: List[A]) def serialize: Json = ???
      def deserialize(json: Json): List[A] = ???


  /**
   * Another TC example on UUIDs
   */
  import java.util.UUID

  /**
   * 
   * @tparam A Contravariant - If you can identify an animal, then you should
   *           be able to identify a cat. So basically you can pass subtypes
   */
  trait Identified[-A]:
    // So that you can do (a: A).uuid
    extension (a: A) def uuid: UUID
  object Identified:
    // Annonymous implementation of the TC Identified
    given Identified[UUID]:
      extension (a: UUID) def uuid: UUID = a
  
  
  
  trait PrettyPrint[-A]:
    extension (a: A) def prettyPrint: String

  given PrettyPrint[String]:
    extension (a: String) def prettyPrint: String = a

  "foo".prettyPrint

  final case class Person(name: String, age: Int)

  /**
   * EXERCISE 1
   * 
   * With the help of the `given` keyword, create an instance of the `PrettyPrint` typeclass for the 
   * data type `Person` that renders the person in a pretty way.
   */
  given ppPerson as PrettyPrint[scope3.Person]:
    extension (a: scope3.Person) def prettyPrint: String =
      s"Person with name ${a.name} and age ${a.age}"
  
  object scope4:

    // This won't work: import type_classes.ppPerson
    import type_classes.{ given PrettyPrint[scope3.Person] }

    def printPerson(p: scope3.Person)(using printer: PrettyPrint[scope3.Person]): String = 
      p.prettyPrint

    def main(args: Array[String]): Unit =
      println(printPerson(scope3.Person("John Doe", 27)))

  /**
   * EXERCISE 2
   * 
   * With the help of the `given` keyword, create a **named* instance of the `PrettyPrint` typeclass 
   * for the data type `Int` that renders the integer in a pretty way.
   */
  given intPrettyPrint as PrettyPrint[Int]:
    // Currently string interpolation fails to compile
    extension (a: Int) def prettyPrint: String = a.toString

  /**
   * EXERCISE 3
   * 
   * Using the `summon` function, summon an instance of `PrettyPrint` for `String`.
   */
  val stringPrettyPrint: PrettyPrint[String] = summon[PrettyPrint[String]]

  /**
   * EXERCISE 4
   * 
   * Using the `summon` function, summon an instance of `PrettyPrint` for `Int`.
   */
  val intPrettyPrint2: PrettyPrint[Int] = summon[PrettyPrint[Int]]

  /**
   * EXERCISE 5
   * 
   * With the help of the `using` keyword, create a method called `prettyPrintIt` that, for any type 
   * `A` for which a `PrettyPrint` instance exists, can both generate a pretty-print string, and 
   * print it out to the console using `println`.
   */
  def prettyPrintIt[A: PrettyPrint](a: A) = println(a.prettyPrint)

  /**
   * EXERCISE 6
   * 
   * With the help of both `given` and `using`, create an instance of the `PrettyPrint` type class
   * for a generic `List[A]`, given an instance of `PrettyPrint` for the type `A`.
   */
  given [A](using a: PrettyPrint[A]) as PrettyPrint[List[A]]:
    extension (a: List[A]) def prettyPrint: String = 
      a.map(_.prettyPrint).mkString("\n")

  /**
   * EXERCISE 7
   * 
   * With the help of both `given` and `using`, create a **named** instance of the `PrettyPrint` 
   * type class for a generic `Vector[A]`, given an instance of `PrettyPrint` for the type `A`.
   */
  
  given vectorPrettyPrint[A](using PrettyPrint[A]) as PrettyPrint[Vector[A]]:
    extension (a: Vector[A]) def prettyPrint: String =
      a.map(_.prettyPrint).mkString(",")

  import scala.Eql._ 

  /**
   * EXERCISE 8
   * 
   * Using the `derives` clause, derive an instance of the type class `Eql` for 
   * `Color`.
   * 
   * Can derive multiple type classes like so: enum Color derives Eql, Show, JsonEncoder:
   */
  enum Color derives Eql:
    case Red 
    case Green 
    case Blue

/**
 * IMPLICIT CONVERSIONS
 * 
 * Scala 3 introduces a new type class called `Conversion` to perform "implicit 
 * conversions"--the act of automatically converting one type to another.
 */
object conversions:
  final case class Rational(n: Int, d: Int)

  /**
   * EXERCISE 1
   * 
   * Create an instance of the type class `Conversion` for the combination of types
   * `Rational` (from) and `Double` (to).
   */
  // given ...
  given Conversion[Rational, Double]:
    def apply(r: Rational): Double = r.n.toDouble / r.d.toDouble

  /**
   * EXERCISE 2
   * 
   * Multiply a rational number by 2.0 (a double) to verify your automatic
   * conversion works as intended.
   * 
   * This is being implicity converted to a Double using the given instance
   */
  Rational(1, 2) * 2.0