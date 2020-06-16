package controllers

import com.typesafe.scalalogging.LazyLogging
import glsf.{User, UserRepository}
import javax.inject.{Inject, Singleton}
import play.api.libs.Files
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents,
  MultipartFormData
}
import util.ResultCont

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ForwardController @Inject()(cc: ControllerComponents,
                                  userRepository: UserRepository,
                                  implicit val ec: ExecutionContext)
    extends AbstractController(cc)
    with LazyLogging {

  private def findUser(to: String): ResultCont[User] =
    ResultCont.fromFuture(userRepository.findBy(to)).getOrResult {
      logger.info(s"Message come to unknown mail: $to")
      Ok
    }

  private def notifySlack(user: User,
                          subject: String,
                          text: String): ResultCont[Unit] = ResultCont.pure {
    // TODO
    logger.info(s"TODO Send messag to Slack: $user $subject $text")
  }

  def post: Action[MultipartFormData[Files.TemporaryFile]] =
    Action.async(cc.parsers.multipartFormData) { implicit request =>
      // ref. https://sendgrid.com/docs/for-developers/parsing-email/setting-up-the-inbound-parse-webhook/
      val data = request.body.dataParts
      (for {
        user <- findUser(data("to").head)
        _ <- notifySlack(user, data("subject").head, data("text").head)
      } yield Ok).run_
    }
}
