package org.alexeyn

import java.time.LocalDateTime

import org.alexeyn.VehicleType.VehicleType

case class Trip(
  tripId: Long,
  vehicleType: VehicleType,
  city: String,
  state: String,
  zip: Int,
  customerId: Long,
  aLocation: String,
  bLocation: String,
  completed: Boolean,
  startTime: LocalDateTime,
  endTime: Option[LocalDateTime] = None,
  durationInMin: Long,
  costPerHour: Double,
  requestTime: LocalDateTime,
  distance: Int
)

object VehicleType extends Enumeration {
  type VehicleType = Value
  val Bike, Taxi, CarSharing = Value
}
