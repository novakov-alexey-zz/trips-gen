package org.alexeyn

import slick.dbio.{DBIO, DBIOAction}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object TripsImporter extends App {
  lazy val db: Database = Database.forConfig("Transportation")

  Await.result(initDatabase(), 5.seconds)

  val tripsCount = 1000000
  val batchSize = 10000
  val startTime = System.nanoTime()

  val (batches, batchCount) = (1 to tripsCount).toStream
    .grouped(batchSize)
    .map(_.map(_ => TripGen.tripGen.sample.get))
    .foldLeft(Seq[Future[Unit]]() -> 1) {
      case ((acc, i), s) =>
        println(s"generated batch of ${s.length} size")
        val f = insertTrip(s)
        f.onComplete(_.foreach(_ => println(s"batch completed $i")))
        (acc :+ f, i + 1)
    }

  println(s"$batchCount batches submitted")
  Await.result(Future.sequence(batches), Duration.Inf)

  val duration = (System.nanoTime() - startTime).nanos.toSeconds

  printf(s"\nTook time: %02d:%02d:%02d\n", duration / 3600, (duration % 3600) / 60, duration % 60)

  private def insertTrip(s: Stream[Trip]) = {
    db.run(DBIOAction.seq(TripDao.insert(s)))
  }

  private def initDatabase() = {
    db.run(DBIO.seq(TripDao.dropSchema().asTry.andFinally(TripDao.createSchema())))
  }
}
