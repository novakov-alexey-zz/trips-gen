package org.alexeyn

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor

object TripsImporter extends App {
  val xa = Transactor.fromDriverManager[IO]("org.postgresql.Driver", "jdbc:postgresql:trips", "postgres", "password")

  val drop =
    sql"""
    DROP TABLE IF EXISTS trip
  """.update.run

  val create =
    sql"""
    CREATE TABLE trip (
      trip_id   SERIAL,
      vehicle_type VARCHAR NOT NULL,
      city VARCHAR NOT NULL,
      state VARCHAR NOT NULL,
      zip INT NOT NULL,
      customer_id INT,
      customer_id INT,
      a_location VARCHAR NOT NULL,
      b_location VARCHAR NOT NULL,
      completed BOOLEAN NOT NULL,
      start_time TIMESTAMP,
      end_time TIMESTAMP,
      duration_min INT,
      cost_per_hour FLOAT,
      request_time TIMESTAMP,
      distance_km INT
    )
  """.update.run
}
