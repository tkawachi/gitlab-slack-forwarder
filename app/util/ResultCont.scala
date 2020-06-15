package util

import play.api.mvc.Result

import scala.concurrent.Future

case class ResultCont[A](run: (A => Future[Result]) => Future[Result]) {
  def map[B](f: A => B): ResultCont[B] = ResultCont(cb => run(a => cb(f(a))))

  def flatMap[B](f: A => ResultCont[B]): ResultCont[B] =
    ResultCont(cb => run(a => f(a).run(cb)))

  def withFilter(predicate: A => Boolean): ResultCont[A] =
    ResultCont { cb =>
      run { a =>
        if (predicate(a)) {
          cb(a)
        } else {
          throw new NoSuchElementException(
            "ResultCont.withFilter predicate is not satisfied"
          )
        }
      }
    }

  def getOrResult[B](
    ifEmpty: => Result
  )(implicit ev: A <:< Option[B]): ResultCont[B] =
    ResultCont(cb => run(a => a.map(cb).getOrElse(Future.successful(ifEmpty))))

  def run_(implicit ev: A <:< Result): Future[Result] =
    run(a => Future.successful(a))

}

object ResultCont {
  def pure[A](a: A): ResultCont[A] = ResultCont(cb => cb(a))
}
