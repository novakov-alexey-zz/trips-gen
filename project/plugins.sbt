logLevel := Level.Warn

resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.bintrayIvyRepo("rtimush", "sbt-plugin-snapshots")

addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.15")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.4")