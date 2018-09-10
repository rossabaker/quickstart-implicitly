package com.example.quickstart

import cats.effect.Sync
import cats.implicits._
import io.circe.Json
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes

object Edge {

  def jokeRoutes[F[_]](implicit F: Sync[F], J: JokeAlg[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.getJoke
          resp <- Ok(joke)
        } yield resp
    }
  }

  def helloWorldRoutes[F[_]](implicit F: Sync[F], H: HelloWorldAlg[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorldAlg.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
  }

}
