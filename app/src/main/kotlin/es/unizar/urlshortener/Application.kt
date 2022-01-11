package es.unizar.urlshortener

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

/**
 * The marker that makes this project a Spring Boot application.
 */
@SpringBootApplication
class UrlShortenerApplication

/**
 * The main entry point.
 */
fun main(args: Array<String>) {
    runApplication<UrlShortenerApplication>(*args)
}
