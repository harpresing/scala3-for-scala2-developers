/**
 * ENUMS
 * 
 * Scala 3 adds support for "enums", which are to sealed traits like case classes 
 * were to classes. That is, enums cut down on the boilerplate required to use 
 * the "sealed trait" pattern for modeling so-called sum types, in a fashion very 
 * similar to how case classes cut down on the boilerplate required to use 
 * classes to model so-called product types.
 * 
 * Strictly speaking, Scala 3 enums are not the same as Java enums: while the 
 * constructors of enums are finite, and defined statically at compile-time in the 
 * same file, these constructors may have parameters, and therefore, the total 
 * number of values of any enum type could be large or infinite.
 * 
 * Enums and case classes provide first-class support for "algebraic data types" 
 * in Scala 3.
 */
package enums:


  /**
   * Example of Sum type in Scala 3
   * 
   * Haskell equivalent: 
   * data FavouriteIDE = IntelliJIdea Int Int | VSCode Int | Emacs | Vim
  */
  enum FavouriteIDE:
    case IntelliJIdea(minorVersion: Int, majorVersion: Int)
    case VSCode(version: Int)
    case Emacs
    case Vim

  val favouriteIDE = FavouriteIDE.IntelliJIdea(3, 2020)

  def showFavouriteIDE =
    favouriteIDE match
      case FavouriteIDE.IntelliJIdea(v,_) => println(s"You like IntelliJ Idea $v")
      case FavouriteIDE.VSCode(v) => println(s"You like VSCode $v")
      case FavouriteIDE.Emacs => println(s"You like Emacs")
      case FavouriteIDE.Vim => println(s"You like Vim")
  
  /**
   * EXERCISE 1
   * 
   * Convert this "sealed trait" to an enum.
   */
  enum DayOfWeek:
    case Sunday
    case Monday
    case Tuesday
    case Wednesday
    case Thursday
    case Friday
    case Saturday

  /**
   * EXERCISE 2
   * 
   * Explore interop with Java enums by finding all values of `DayOfWeek`, and by 
   * finding the value corresponding to the string "Sunday".
   */
  def daysOfWeek: Array[DayOfWeek] = DayOfWeek.values
  def sunday: DayOfWeek = DayOfWeek.valueOf("Sunday")

  /**
   * EXERCISE 3
   * 
   * Convert this "sealed trait" to an enum.
   * 
   * Take special note of the inferred type of any of the case constructors!
   */
  enum Color:
    case Red 
    case Green
    case Blue
    case Custom(red: Int, green: Int, blue: Int)

  /** 
   * Now by default it's type is Color, in the case of a sealed trait
   * it would have been Color.Custom unless the Color type was explicitily
   * added to the variable as val custom: Color 
  */ 
  val custom = Color.Custom(1, 2, 3)

  /**
   * EXERCISE 4
   * 
   * Convert this "sealed trait" to an enum.
   * 
   * Take special note of the inferred type parameters in the case constructors!
   * 
   * Haskell: data Result e v = Succeed v | Fail e
   * 
   * +Error = Covariant in Error
   * -Error = Contravariant in Error
   * Error = Invariant in Error
   *
   */
  enum Result[+Error, +Value]:
    case Succeed(value: Value)
    case Fail(error: Error)

  /**
   * EXERCISE 5
   * 
   * Convert this "sealed trait" to an enum.
   * 
   * Take special note of the inferred type parameters in the case constructors!
   */
  enum Workflow[-Input, +Output]:
    case End(value: Output)

  /**
   * EXERCISE 6
   * 
   * Convert this "sealed trait" to an enum.
   */
  enum Conversion[-From, +To]:
    case AnyToString extends Conversion[Any, String]
    case StringToInt extends Conversion[String, Option[Int]]

  // If we ommited the extends above then the type of 'a' would be [Any, Nothing]
  val a = Conversion.AnyToString

/**
 * CASE CLASSES
 * 
 * Scala 3 makes a number of improvements to case classes.
 */
package case_classes:
  /**
   * EXERCISE 1
   * 
   * By making the public constructor private, make a smart constructor for `Email` so that only 
   * valid emails may be created.
   */
  final case class Email private (value: String)
  object Email:
    def fromString(v: String): Option[Email] =
      if isValidEmail(v) then Some(Email(v))
      else None

    def isValidEmail(v: String): Boolean = v.matches("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")

  /**
   * EXERCISE 2
   * 
   * Try to make a copy of an existing `Email` using `Email#copy` and note what happens.
   * Even if we used a private constructor, the .copy() method on the object was still 
   * accessible in Scala 2.x so below we could do email.copy()
   */
  def changeEmail(email: Email): Email = ???

  /**
   * EXERCISE 3
   * 
   * Try to create an Email directly by using the generated constructor in the companion object.
   * Similarly to copy(), the apply() method was also publically accessible!
   */
  def caseClassApply(value: String): Email = ???

/**
 * PATTERN MATCHING
 * 
 * Scala 3 provides upgrades to the power and flexibility of pattern matching.
 */  
object pattern_matching:
  /**
   */
  def foo: Int = 2