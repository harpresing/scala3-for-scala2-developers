/**
 * EXTENSION METHODS
 * 
 * Scala 3 brings first-class support for "extension methods", which allow adding methods to 
 * classes after their definition. Previously, this feature was emulated using implicits.
 */
object ext_methods:
  
  //Example on Future
  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global
  
  // Inconvenient because ideally we'd like left.zip(right)
  def zip[A, B](left: Future[A], right: Future[B]): Future[(A, B)] =
    for 
      l <- left
      r <- right
    yield (l, r)

  /** To add this method to Future in Scala 2, we'd use an implicit class
   * implicit class FutureSyntax[A](self: Future[A]) {
      def zip[B](right: Future[B]): Future[(A, B)] =
        self.flatMap(l => right.map(r => (l, r)))
   
   We use extension in Scala 3
   */
  extension [A, B] (self: Future[A]) def zip(r: Future[B]): Future[(A, B)] =
    self.flatMap(l => r.map(r => (l, r)))
    
  def l: Future[Int] = ???
  def r: Future[Int] = ???
  
  l.zip(r) // extension in action
  
  final case class Email(value: String)

  /**
   * EXERCISE 1
   * 
   * Add an extension method to `Email` to retrieve the username of the email address (the part 
   * of the string before the `@` symbol).
   */
  extension (e: Email) def username: String =
    e.value.takeWhile(_ != '@')

  val sherlock = Email("sherlock@holmes.com").username

  /**
   * EXERCISE 2
   * 
   * Add an extension method to `Email` to retrieve the server of the email address (the part of 
   * the string after the `@` symbol).
   */
  
  extension (e: Email) def server: String =
    e.value.dropWhile(_ != '@').tail

  /**
   * EXERCISE 3
   * 
   * Add an extension method to `Option[A]` that can zip one option with another `Option[B]`, to 
   * return an `Option[(A, B)]`.
   */
  
  extension [A, B] (self: Option[A]) def zip(other: Option[B]): Option[(A, B)] =
    self.flatMap(s => other.map(o => (s, o)))
   

  /**
   * A rational number is one in the form n/m, where n and m are integers.
   */
  final case class Rational(numerator: BigInt, denominator: BigInt)

  /**
   * EXERCISE 4
   * 
   * Add a collection of extension methods to `Rational`, including `+`, to add two rational 
   * numbers, `*`, to multiply two rational numbers, and `-`, to subtract one rational number 
   * from another rational number.
   */
  extension (a: Rational):
    def +(b: Rational): Rational = ???
    def *(b: Rational): Rational = ???
    def -(b: Rational): Rational = ???

  /**
   * EXERCISE 5
   * 
   * Convert this implicit syntax class to use extension methods.
   */
  extension (self: String) def equalsIgnoreCase(that: String) = 
    self.toLowerCase == that.toLowerCase

  object scope:
    extension (s: String) def isSherlock: Boolean = s.startsWith("Sherlock")

  /**
   * EXERCISE 6
   * 
   * Import the extension method `isSherlock` into the following object so the code will compile.
   * 
   * Tip: Put your extension methods in a package or an object so that they can be easily
   * imported!
   */
  object test:
    import scope._
    "John Watson".isSherlock