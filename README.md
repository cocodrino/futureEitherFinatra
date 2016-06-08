this is a small example about how compose futures and either monads in finatra/scala enabling a nice syntax sugar and terse error handling

this includes its own syntax for future either monad and some implementations of http error codes

for instance 

Left(Unauthorized("you can't access here")) will return a 401 code with the text you can't access

also extend Futures with orFLeft (check the notZero method)

**WHY TE UGLY ARROWS >>> 
Because I love back to future and it looks a bit like the arrow in the movie...and you know..back to FUTURE...FUTURE monad...


![movie-title] (https://upload.wikimedia.org/wikipedia/id/7/71/Back_to_the_Future_logo.png)




```scala

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


```