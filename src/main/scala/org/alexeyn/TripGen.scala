package org.alexeyn

import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.time.temporal.ChronoUnit._

import org.alexeyn.VehicleType._
import org.scalacheck.Gen

object TripGen {
  private val tripId = Gen.const(0L)
  private val vehicleType = Gen.frequency(5 -> Bike, 3 -> Taxi, 2 -> CarSharing)
  private val stateCityZipCode = Gen.frequency(
    1 -> ("Baden-Württemberg", "Stuttgart", 72160),
    4 -> ("Bayern", "Munich", 80333),
    5 -> ("Berlin", "Berlin", 14167),
    1 -> ("Brandenburg", "Potsdam", 14469),
    3 -> ("Bremen", "Bremen", 28195),
    4 -> ("Hamburg", "Hamburg", 20095),
    2 -> ("Hessen", "Wiesbaden", 65185),
    3 -> ("Niedersachsen", "Hannover", 30159),
    1 -> ("Mecklenburg-Vorpommern", "Schwerin", 19055),
    4 -> ("Nordrhein-Westfalen (NRW)", "Düsseldorf", 40213),
    1 -> ("Rheinland-Pfalz", "Mainz", 55128),
    3 -> ("Saarland", "Saarbrücken", 66111),
    2 -> ("Sachsen", "Dresden", 1067),
    1 -> ("Sachsen-Anhalt", "Magdeburg", 39104),
    1 -> ("Schleswig-Holstein", "Kiel", 24103),
    1 -> ("Thüringen", "Erfurt", 99084)
  )
  private val customerId = Gen.choose(1, 1000L)
  private val location = for {
    street <- Gen.oneOf("Landstrasse", "Kettenhofweg", "Frankenallee", "Taunusstrasse", "Ohmstrasse", "Goethestrasse")
    number <- Gen.choose(1, 100)
  } yield street + " " + number

  private val completed = Gen.frequency(10 -> true, 1 -> false)
  private val requestTime = localDateTimeGen
  private val rangeStart = LocalDateTime.now(UTC).minusMonths(6).toEpochSecond(UTC)
  private val currentYear = LocalDateTime.now(UTC).getYear
  private val rangeEnd = LocalDateTime.of(currentYear, 1, 1, 0, 0, 0).toEpochSecond(UTC)

  private val bikeWaitingTimeMins = Gen.choose(0, 1)
  private val taxiWaitingTimeMins = Gen.choose(0, 20)
  private val carWaitingTimeMins = Gen.choose(0, 2)

  private val bikeDurationMins = Gen.choose(1, 250L)
  private val taxiDurationMins = Gen.choose(1, 120L)
  private val carDurationMins = Gen.choose(1, 300L)

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
      waitingTime <- getWaitingTime(vehicle)
      duration <- getDuration(vehicle)
      startTime = rTime.plus(waitingTime.toLong, MINUTES)
      endTime = getEndTime(done, duration, startTime)
      cost <- getCost(vehicle)
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
        duration,
        cost,
        rTime,
        distance
      )

  private def getEndTime(completed: Boolean, durationMin: Long, startTime: LocalDateTime) = {
    if (completed) Some(startTime.plus(durationMin, MINUTES))
    else None
  }

  private def getWaitingTime(vehicle: VehicleType) = vehicle match {
    case Bike => bikeWaitingTimeMins
    case Taxi => taxiWaitingTimeMins
    case CarSharing => carWaitingTimeMins
  }

  private def getDuration(vehicle: VehicleType) = vehicle match {
    case Bike => bikeDurationMins
    case Taxi => taxiDurationMins
    case CarSharing => carDurationMins
  }

  private def getCost(vehicle: VehicleType) = vehicle match {
    case Bike => costPerHourBike
    case Taxi => costPerHourTaxi
    case CarSharing => costPerHourCarSharing
  }

  private def localDateTimeGen: Gen[LocalDateTime] = {
    Gen.choose(rangeStart, rangeEnd).map(i => LocalDateTime.ofEpochSecond(i, 0, UTC))
  }
}
