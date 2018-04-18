package org.alexeyn

import java.sql.Timestamp
import java.time.{LocalDateTime, ZoneOffset}

import org.alexeyn.VehicleType.VehicleType
import slick.dbio.{Effect, NoStream}
import slick.jdbc.PostgresProfile.api._
import slick.sql.FixedSqlAction

trait DAO[T] {

  def createSchema(): FixedSqlAction[Unit, NoStream, Effect.Schema]

  def dropSchema(): FixedSqlAction[Unit, NoStream, Effect.Schema]

  def insert(elements: Seq[T]): FixedSqlAction[Option[Int], NoStream, Effect.Write]
}

object TripDao extends DAO[Trip] {

  implicit val vehicleTypeEnumMapper =
    MappedColumnType.base[VehicleType, String](_.toString, VehicleType.withName)

  implicit val localDateTimeColumnType = MappedColumnType
    .base[LocalDateTime, Timestamp](d => Timestamp.from(d.toInstant(ZoneOffset.ofHours(0))), _.toLocalDateTime)

  class Trips(tag: Tag) extends Table[Trip](tag, "trip") {
    def tripId = column[Long]("tripId", O.AutoInc, O.PrimaryKey)

    def vehicleType = column[VehicleType]("vehicleType")

    def city = column[String]("city")

    def state = column[String]("state")

    def zip = column[Int]("zip")

    def customerId = column[Long]("customerId")

    def aLocation = column[String]("a_location")

    def bLocation = column[String]("b_location")

    def completed = column[Boolean]("completed")

    def startTime = column[LocalDateTime]("start_time")

    def endTime = column[Option[LocalDateTime]]("end_time", O.Default(None))

    def durationMin = column[Long]("duration_min")

    def costPerHour = column[Double]("cost_per_hour")

    def requestTime = column[LocalDateTime]("request_time")

    def distanceKm = column[Int]("distance_km")

    def * =
      (
        tripId,
        vehicleType,
        city,
        state,
        zip,
        customerId,
        aLocation,
        bLocation,
        completed,
        startTime,
        endTime,
        durationMin,
        costPerHour,
        requestTime,
        distanceKm
      ) <>
        (Trip.tupled, Trip.unapply)
  }

  val trips = TableQuery[Trips]

  override def createSchema(): FixedSqlAction[Unit, NoStream, Effect.Schema] = trips.schema.create

  override def dropSchema(): FixedSqlAction[Unit, NoStream, Effect.Schema] = trips.schema.drop

  override def insert(elements: Seq[Trip]): FixedSqlAction[Option[Int], NoStream, Effect.Write] =
    trips ++= elements
}
