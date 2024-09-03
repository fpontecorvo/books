# Seed-poc

Esta semilla tiene como objetivo convertir a código los conceptos implementados en *Funding*.
Además, se deja la posibilidad de implementar esta semilla usando GraalVM para la compilación nativa del mismo.

### Stack:

- Java 21
- Kotlin 1.9
- Spring Boot 3
- MongoDB
- Redis
- Kafka
- Arrow-kt

***

## Arquitectura

Está basada en una arquitectura hexagonal clásica, pero de manera más pragmática.

- Dentro de la carpeta de *adapter* lo subdividimos en *in* y *out* para especificar las implementaciones de salida.
- No se generó duplicidad en las clases de *dominio* para sus contrapartes de *adapters*.
- Todos los *usecase* reciben como mínimo los requisitos para su ejecución y devuelven la entidad de dominio generada.
- Todas los puertos de salida son *functional interface* que son inyectadas en los *usecase/service*, pero la
  implementación de ellas es a través de una *internal interface* que las clusteriza.

***

## Branching

Esta semilla cuenta con 2 branches principales, *main* y *reactive*, utilizando spring-web y spring-webflux
respectivamente.
Así proporcionar opciones para dos tipos de enfoque.

El branch *reactive* utiliza el modificador de función *suspend* de kotlin a lo largo de todo el código para el manejo
non-blocking de la app,
a su vez también la versiones de Mongo y Redis utilizadas son su contraparte reactivas implementadas con Reactor.
Kotlin maneja de forma nativa un interface para reactor donde: *Mono<T\> => T?* y *Flux<T\> => Flow<T\>*.
Además se utiliza el DSL de ruteo funcional en vez del approach clásico de @RestController utilizado en *main*.

***

## Arrow-kt

Se decidió utilizar Arrow-kt para el manejo de errores de la aplicación.
Arrow dispone de un conjunto de monadas y funciones que permiten un uso más simple de patrones funcionales.
El más usado por nosotros es Either<L, R> que permite envolver en la monada el resultado de una función ya sea que haya
sido exitoso o fallido, y continuar con solo flujo lógico sin importarnos este.
Solo al final del flujo se evalúa el resultado de la monada lanzando una excepción si el resultado fue un error o
retornando el valor exitoso.

***

## Persistencia asíncrona

La persistencia de las creaciones o cambios en la entidad se hacen de manera asíncrona, a través de eventos en kafka.
Por ejemplo al solicitar la creación de un libro, este produce un evento en el tópico *created.book* para que la
velocidad de la respuesta no esté atada a la velocidad de persistencia en base de datos.

***

## Cache

Para el uso de la cache se utilizó Redis. Cada vez que se genere un cambio o se cree una nueva entidad, antes de
producir el mensaje, se actualiza la cache de manera manual.
No se utilizó @Cacheable ya que la interacción con Arrow genera problemas.

***

## Tracing

Micrometer para el traceo. El TraceId se propaga por clientes y consumer, al ingresar un flujo hijo cambian el SpanId.

***

## Docker

El dockerfile agrega los agentes de opentelemetry y abren el acceso a java.lang que a partir de java 17 es explicito.


