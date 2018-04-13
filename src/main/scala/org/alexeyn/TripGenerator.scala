package org.alexeyn

import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.time.temporal.ChronoUnit._

import org.alexeyn
import org.alexeyn.VehicleType._
import org.scalacheck.Gen

object TripGenerator {
  private val tripId = Gen.const(0L)
  private val vehicleType = Gen.oneOf(VehicleType.values.toSeq)
  private val stateCityZipCode = Gen.frequency(
    1 -> ("Baden-Württemberg", "Stuttgart", 72160),
    1 -> ("Bayern", "Munich", 80333),
    4 -> ("Berlin", "Berlin", 14167),
    1 -> ("Brandenburg", "Potsdam", 14469),
    1 -> ("Bremen", "Bremen", 28195),
    3 -> ("Hamburg", "Hamburg", 20095),
    2 -> ("Hessen", "Wiesbaden", 65185),
    1 -> ("Niedersachsen", "Hannover", 30159),
    1 -> ("Mecklenburg-Vorpommern", "Schwerin", 19055),
    3 -> ("Nordrhein-Westfalen (NRW)", "Düsseldorf", 40213),
    1 -> ("Rheinland-Pfalz", "Mainz", 55128),
    1 -> ("Saarland", "Saarbrücken", 66111),
    1 -> ("Sachsen", "Dresden", 1067),
    1 -> ("Sachsen-Anhalt", "Magdeburg", 39104),
    1 -> ("Schleswig-Holstein", "Kiel", 24103),
    1 -> ("Thüringen", "Erfurt", 99084)
  )
  private val customerId = Gen.choose(1, 1000L)
  private val location = for {
    street <- Gen.oneOf("Landstrasse", "Kettenhofweg", "Frankenallee", "Taunusstrasse")
    number <- Gen.choose(1, 100)
  } yield street + " " + number

  private val completed = Gen.frequency(4 -> true, 1 -> false)
  private val requestTime = localDateTimeGen
  private val waitingTimeMins = Gen.choose(0, 20)
  private val durationMins = Gen.choose(1, 1000L)
  private val costPerHourBike = Gen.choose(0.5, 2)
  private val costPerHourTaxi = Gen.choose(50, 100.0)
  private val costPerHourCarSharing = Gen.choose(15, 20.0)
  private val distanceKm = Gen.choose(1, 500)

  def tripGen: Gen[Trip] =
    for {
      tId <- tripId
      vehicle <- vehicleType
      (state, city, zip) <- stateCityZipCode
      cId <- customerId
      aLocation <- location
      bLocation <- location
      done <- completed
      rTime <- requestTime
      waitingTime <- waitingTimeMins
      dur <- durationMins
      startTime = rTime.plus(waitingTime.toLong, MINUTES)
      endTime = startTime.plus(dur.toLong, MINUTES)
      cost = getCost(vehicle)
      distance <- distanceKm
    } yield
      Trip(
        tId,
        vehicle,
        city,
        state,
        zip,
        cId,
        aLocation,
        bLocation,
        done,
        startTime,
        endTime,
        dur,
        cost,
        rTime,
        distance
      )

  private def getCost(vehicle: alexeyn.VehicleType.Value): Double = vehicle match {
    case `Bike` => costPerHourBike.sample.get
    case `Taxi` => costPerHourTaxi.sample.get
    case `CarSharing` => costPerHourCarSharing.sample.get
  }

  def localDateTimeGen: Gen[LocalDateTime] = {
    val rangeStart = LocalDateTime.now(UTC).minusMonths(6).toEpochSecond(UTC)
    val currentYear = LocalDateTime.now(UTC).getYear
    val rangeEnd = LocalDateTime.of(currentYear, 1, 1, 0, 0, 0).toEpochSecond(UTC)
    Gen.choose(rangeStart, rangeEnd).map(i => LocalDateTime.ofEpochSecond(i, 0, UTC))
  }
}
