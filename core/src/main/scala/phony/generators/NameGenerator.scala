package phony.generators

import cats.Monad
import cats.implicits._
import phony.RandomUtility

import scala.language.higherKinds

class NameGenerator[F[_]: Monad](implicit val utility: RandomUtility[F]) {
  def firstName: F[String] =
    utility.name.map(_.firstNames) >>= utility.randomItem

  def lastName: F[String] =
    utility.name.map(_.lastNames) >>= utility.randomItem

  def prefix: F[String] =
    utility.name.map(_.prefixes) >>= utility.randomItem

  def suffix: F[String] =
    utility.name.map(_.suffixes) >>= utility.randomItem

  def fullName: F[String] =
    fullName(false, false)

  def fullName(withPrefix: Boolean = false, withSuffix: Boolean = false): F[String] =
    (withPrefix, withSuffix) match {
      case (true, true) => (prefix, firstName, lastName, suffix).mapN(combine4(" "))
      case (true, false) => (prefix, firstName, lastName).mapN(combine3(" "))
      case (false, true) => (firstName, lastName, suffix).mapN(combine3(" "))
      case (false, false) => (firstName, lastName).mapN(combine2(" "))
    }

  def username: F[String] =
    for {
      first <- utility.name.map(_.firstNames) >>= utility.randomItem
      last <- utility.name.map(_.lastNames) >>= utility.randomItem
      rand <- utility.int(1000)
    } yield s"${first}_${last}_${rand}".toLowerCase

  private def combine4(glue: String)(p1: String, p2: String, p3: String, p4: String): String =
    List(p1, p2, p3, p4).mkString(glue).trim

  private def combine3(glue: String)(p1: String, p2: String, p3: String): String =
    List(p1, p2, p3).mkString(glue).trim

  private def combine2(glue: String)(p1: String, p2: String): String =
    List(p1, p2).mkString(glue).trim
}