package controllers

import play.api.mvc._
import com.mongodb.casbah.Imports._
import play.twirl.api.Html
import com.mongodb.DBObject
import play.api.libs.json._

import scala.collection.mutable.ArrayBuffer

object XSSController extends Controller {

  def index = Action {
    Ok(views.html.comment())
  }

  def addComment = Action(parse.urlFormEncoded) {

    implicit request =>

      // Connect to Mongo DB
      val client   = MongoClient()
      val db       = client("mp")
      val coll     = db("comments")

      val comment = MongoDBObject("comment" -> request.body.get("comment").get.head)

      coll.insert(comment)

      // Cleanly Close MongoDB Connection
      client.close()

      // Render the Results
      Redirect(routes.XSSController.comments())
  }

  def comments = Action {
    implicit request =>

      // Connect to Mongo DB
      val client   = MongoClient()
      val db       = client("mp")
      val coll     = db("comments")

      var goodCommentArray = ArrayBuffer[String]()
      coll.find().foreach { comment =>
        goodCommentArray += comment.get("comment").toString()
      }

      var badCommentArray = ArrayBuffer[Html]()
      coll.find().foreach { comment =>
        badCommentArray += Html(comment.get("comment").toString())
      }

      // Cleanly Close MongoDB Connection
      client.close()

      // Render the Results
      Ok(views.html.comments(goodCommentArray)(badCommentArray))

  }
}