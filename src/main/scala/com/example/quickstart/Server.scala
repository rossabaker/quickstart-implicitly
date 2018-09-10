package com.example.quickstart


import cats.effect.{ConcurrentEffect, Effect, ExitCode, IO, IOApp, Timer, ContextShift}
import cats.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import fs2.Stream
import scala.concurrent.ExecutionContext.global

import org.http4s.server.middleware.Logger

object Server {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, ExitCode] =
    BlazeClientBuilder[F](global).stream.flatMap { implicit client =>
      implicit val helloWorldAlg = HelloWorldAlg.impl[F]
      implicit val jokeAlg = JokeAlg.impl[F]

      // Combine Service Routes into an HttpApp
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      val httpApp = (
        Edge.helloWorldRoutes[F] <+> Edge.jokeRoutes[F]
      ).orNotFound

      // With Middlewares in place
      val finalHttpApp = Logger(true, true)(httpApp)

      BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    }
}
