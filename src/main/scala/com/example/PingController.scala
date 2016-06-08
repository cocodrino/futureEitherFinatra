package com.example

import com.sun.xml.internal.ws.client.sei.ResponseBuilder
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.{response, Controller}

import scala.util.Random._


import com.twitter.util.Future

import org.scalactic._
import com.example.Base._

import scala.util.Try

case class User(name: String, lang: String)

object Cases {


   def getUser(name: String): >>>[User] = Future {
      //simulating retrieving from async db
      if (name == "donald")
         Left(Unauthorized("you can't access here"))
      else
         Right(User(name, "scala,clojure,js"))
   }

   def getFriendsCountFromExternalServer(name: String): >>>[Int] = Future {
      //simulating contact external server 20% fails
      if (shuffle(1 to 5).head == 5)
         Left(Unavailable())
      else Right(shuffle(0 to 3).head)
   }

   def notImplementedTest: >>>[String] = Future {
      //just for fun  10% fails
      if (shuffle(1 to 10).head == 10) Left(NotImplemented()) else Right("OK")
   }


   def notZero(v: Int): >>>[Int] = Future {
      //util for wrap when futures can fail....
      10 / v
   }.orFLeft(BadRequest("not friends?"))
}


class PingController extends Controller {

   import Cases._

   get("/blah") { r: Request =>
      (for {
         v <- <<<(getUser(r.getParam("name")))
         _ <- <<<(notZero(r.getIntParam("number")))

         _ <- <<<(notImplementedTest)
         n <- <<<(getFriendsCountFromExternalServer(v.name))

      } yield v).build(response) { r => response.ok.json(Map("user" -> r)) }

   }


}
