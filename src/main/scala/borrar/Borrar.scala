package borrar

import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.util.Future
import org.scalactic.{Bad, Good, Or}


class Borrar {

   abstract sealed class BaseError(s: String)

   case class DesA(s: String) extends BaseError(s)

   case class DesB(s: String) extends BaseError(s)

   def m1(s: String): Future[Int Or DesA] = Future {
      if (s == "first") Good(1) else Bad(DesA("not first"))
   }

   def m2(n: Int): Future[Int Or DesB] = Future {
      if (n == 1) Good(2) else Bad(DesB("..."))
   }

   object Example {
      abstract sealed class Error {}
      case class HeadFellOff() extends Error {}
      case class PantsFellOff() extends Error {}
      case class WTF() extends Error {}

      def returnString: FutureEither[Error, String] = new FutureEither(Future(Right("a string!")))
      def takeStringAndReturnInt(string: String): FutureEither[Error, Int] = new FutureEither(Future(Right(10)))
      def takeIntAndReturnString(int: Int): FutureEither[Error, String] = new FutureEither(Future(Right("ftw")))

      def main(args: Array[String]) {
         val result = returnString.flatMap(
            takeStringAndReturnInt(_).flatMap(
               takeIntAndReturnString(_).map(_.toUpperCase)
            )
         ).recover {
            case HeadFellOff() => "head fell off!"
            case PantsFellOff() => "pants fell off!"
            case unanticipated => s"$unanticipated"
         }

         println(result.get())
      }
   }



   class FutureEither[L, R](private val future: Future[Either[L, R]]) {

      def flatMap[R2](block: R => FutureEither[L, R2]): FutureEither[L, R2] = {
         val result = future.flatMap {
            case Right(r) => block.apply(r).future
            case Left(l) => Future(Left(l))
         }
         new FutureEither(result)
      }

      def map[R2](block: R => R2): FutureEither[L, R2] = {
         val result = future.map {
            case Right(r) => Right(block.apply(r))
            case Left(l) => Left(l)
         }
         new FutureEither(result)
      }

      def recover(block: PartialFunction[L, R]): Future[R] = {
         future.map {
            case Right(r) => r
            case Left(left) => block.apply(left)
         }
      }
   }




/*   case class FutOr[G, E](fut: Future[Or[G, E]]) {
      def flatMap[A2](f: G => FutOr[A2, E]): FutOr[A2, E] = {
         val _r = fut.flatMap {
            case Good(r) => f(r).fut
            case Bad(b) => Future(Bad(b))
         }
         FutOr(_r)
      }

      def map[A2](f: G => A2): FutOr[A2, E] = {
         val _f = this.fut.map {
            case Good(b) => Good(f(b))
            case Bad(e) => Bad(e)
         }
         FutOr(_f)
      }


   }*/

}
