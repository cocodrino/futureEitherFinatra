
package com.example

import com.twitter.finatra.http.response.ResponseBuilder
import org.scalactic
import org.scalactic._
import scala.reflect.macros.whitebox
import scala.util.Try
import com.twitter.util._

import javax.inject.Inject

object Base {

   abstract sealed class ErrorResponseBuilder(b: Any) {
      def generateResponse(r: ResponseBuilder): ResponseBuilder#EnrichedResponse
   }

   case class Forbidden(b: Any) extends ErrorResponseBuilder(b) {

      def generateResponse(r: ResponseBuilder) = r.forbidden(b)
   }

   case class BadRequest(b: Any) extends ErrorResponseBuilder(b) {
      def generateResponse(r: ResponseBuilder) = r.badRequest(b)
   }

   case class Unauthorized(b: Any) extends ErrorResponseBuilder(b) {
      def generateResponse(r: ResponseBuilder) = r.unauthorized(b)
   }

   case class Unavailable() extends ErrorResponseBuilder("") {
      def generateResponse(r: ResponseBuilder) = r.serviceUnavailable
   }

   case class NotImplemented() extends ErrorResponseBuilder("") {
      def generateResponse(r: ResponseBuilder) = r.notImplemented
   }

   type >>>[A] = Future[Either[ErrorResponseBuilder, A]]


   case class <<<[L, R](future: Future[Either[L, R]]) {

      def flatMap[R2](f: R => <<<[L, R2]): <<<[L, R2] = {
         val result = future.flatMap {
            case Right(r) => f(r).future
            case Left(l) => Future(Left(l))
         }
         <<<(result)
      }

      def map[R2](f: R => R2): <<<[L, R2] = {
         val result = future.map {
            case Right(r) => Right(f(r))
            case Left(l) => Left(l)
         }
         <<<(result)
      }

      def build(r: ResponseBuilder)(block: (R) => ResponseBuilder#EnrichedResponse)(implicit ev: L <:<
        ErrorResponseBuilder) = {
         future.map {
            case Right(v) => block(v)
            case Left(e) => e.generateResponse(r)
         }
      }


   }

 

   case class Ba(f: Future[ErrorResponseBuilder])

  


   implicit class FutureOrBuilder[+A](f: Future[A]) {
      def asOrWithBad[B](b: B) = {
         f.map(Good(_).asInstanceOf[Or[A, B]]).rescue {
            case e: Exception => Future(Bad(b).asInstanceOf[Or[A, B]])
         }
      }

      def orFLeft[B](b: B) = {
         f.map(Right(_).asInstanceOf[Either[B, A]]).rescue {
            case e: Exception => Future(Left(b).asInstanceOf[Either[B, A]])
         }
      }
   }

 


