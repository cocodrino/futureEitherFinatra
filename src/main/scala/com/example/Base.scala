
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


   /*   case class doOrResp[+A,H <: HttpLogicResponses](fut : Future[Or[A,H]]) {
         def flatMap[A2](f: A => FOr[A2, H]): FOr[A2, H] = {
            val _r = fut.flatMap {
               case Good(r) => f(r).fut
               case Bad(b) => Future(Bad(b))
            }
            FOr(_r)
         }
   
         def map[A2](f: A => A2): FOr[A2, H] = {
            val _f = this.fut.map {
               case Good(b) => Good(f(b))
               case Bad(e) => Bad(e)
            }
            FOr(_f)
         }
   
   
         def build (r : ResponseBuilder)(block : (A)=> ResponseBuilder#EnrichedResponse) = {
            fut.map{
               case Good(v) => block(v)
               case Bad(e) => e.generateResponse(r)
            }
         }
      }*/

   case class Ba(f: Future[ErrorResponseBuilder])

   /*   case class FOr[A,H <: HttpLogicResponses](fut : Future[Or[A,H]]) {
         def flatMap[A2,B<:HttpLogicResponses](f: A => FOr[A2, B]): FOr[A2, B] = {
            val _r = fut.flatMap {
               case Good(r) => f(r).fut
               case Bad(b) => Future(Bad(b))
            }
            FOr(_r)
         }
   
         def map[A2](f: A => A2): FOr[A2, H] = {
            val _f = this.fut.map {
               case Good(b) => Good(f(b))
               case Bad(e) => Bad(e)
            }
            FOr(_f)
   
   
   
         def build(r : ResponseBuilder)(block : (A)=> ResponseBuilder#EnrichedResponse)= {
            fut.map{
               case Good(v) => block(v)
               case Bad(e) => e.generateResponse(r)
            }
         }
      }
      }*/


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

   /*
   @deprecated
   case class FutureOr[G, E](fut: Future[Or[G, E]]) {
     def flatMap[G2](f: G => FutureOr[G2, E]): FutureOr[G2, E] = {
        val _r = fut.flatMap {
           case Good(r) => f(r).fut
           case Bad(b) => Future(Bad(b))
        }
        FutureOr(_r)
     }
   
     def map[G2](f: G => G2): FutureOr[G2, E] = {
        val _f = this.fut.map {
           case Good(b) => Good(f(b))
           case Bad(e) => Bad(e)
        }
        FutureOr(_f)
     }
   
   
   }
     */
}


