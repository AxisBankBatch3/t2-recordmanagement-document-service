package com.delta.document




import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@SpringBootApplication
@OpenAPIDefinition(info = Info(title = "Employees API", version = "1.2.32", description = "Employees Information"))
open class DocumentServiceApplication

fun main(args: Array<String>) {
	runApplication<DocumentServiceApplication>(*args)
}
