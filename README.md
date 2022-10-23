

# Challenge Tenpo
Hola! Soy Martín, gracias por revisar este proyecto. Incluí un readme para ayudar con la revisión del código y sumar algunos comentarios extra.


## ¿Cómo probar la app?
  

 1. Clonar el repositorio
 2. Ejecutar el comando `docker-compose up`. 
 3. Para probar la API hay dos opciones:
	 1. Navegar a **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)** y utilizar la UI interactiva.
	 2. Utilizar el collection de Postman adjunto. 

## ¿Cómo loguearse?

Para probar los endpoints, primero hay que:

 1. Crear un user
 2. Realizar el login
 3. Copiar el token en el authorization header a los otros endpoints (que requieren autenticación)

Para los pasos 1 y 2 los datos de un user se encuentran pre-cargado (tanto en la UI como en la colección de Postman). Luego es necesario copiar el token del response a los otros endpoints. 

 1. Si se utiliza la UI, navegar al botón "Authorize" y pegar el token ahí. 
 2. Si es con Postman, pegarlo directamente en el header.

## Stack de desarrollo

 - Spring Boot + Java 11
 - Base de datos PostgreSQL para producción (H2 in-memory para testing)

  
## Seguridad  
Para la API REST se utilizó autenticación con **JWT** (usando **Spring Security** + **io.jsonwebtoken**).
  
## Documentación de la API  
La API está documentada utilizando la librería Springfox Swagger. 
 - http://localhost:8080/swagger-ui/index.html para UI interactiva
 - http://localhost:8080/v2/api-docs para documentación en formato JSON.    
  
## Guardado del historial de requests
 - Este feature está implementado en la clase **AsyncRequestProcessorFilter**, extendiendo de **OncePerRequestFilter**. Este filter pasa los datos del request y el response a un **Thread Pool** que luego se encarga del guardado a la base de datos (para evitar sumar tiempo extra al request).
 - En una arquitectura de microservicios real probablemente convendría
   externalizar y centralizar esta responsabilidad en otro componente
   (por ejemplo en un API Gateway, o utilizando un sidecar container +
   el ELK stack).

  

## SumService
El servicio externo de porcentajes se mockeo en la clase **PercentageServiceMock** (devuelve un valor random entre 10 y 12).  Para obtener el porcentaje, de acuerdo a los requerimientos, se consideraron dos alternativas:
 1. Cachear el último porcentaje localmente, con una **key que dependa del momento** en que se procesa el request, de forma que la key varie cada media hora. De esta manera nos aseguramos de consultar al servicio externo una sola vez por cada media hora (en caso de llamado exitoso).
 2. Utilizar un cron (@Scheduled) que corra cada media hora, que consulte al servicio externo y guarde el resultado obtenido localmente. Luego el servicio interesado sólo debe consultar este campo.
 
Se implementó la alternativa número 1. El siguiente diagrama resume la lógica con más detalle.
<img width="847" alt="tenpo" src="https://user-images.githubusercontent.com/25701657/197362364-36060d0f-fd47-4a65-8634-cbe9ab905464.png">



Otras consideraciones que vale la pena mencionar.

 - **Cache utilizada:** Cómo solo nos importa el último valor obtenido, no hizo falta utilizar ninguna librería externa (tipo Guava / Caffeine)
 - **Cache externa:** se podría utilizar una cache externa (Redis o memcached por ejemplo) para guardar el porcentaje, dependiendo de los requerimientos. Hay diferencias en cuanto a latencia, costo de mantenimiento, consistencia de los datos y problemas de cold-start con respecto a utilizar una **cache local**.
 - **Retries:** para implementar la lógica de retry se utilizó la librería **resilience4j**. Adicionalmente se podría configurar un **circuit-breaker** si fuera necesario.
  
## Validación y errores
 - Se utilizaron las anotaciones de **Bean Validation** (JSR 380) para validar el formato del payload de los requests (ver directorio controller.request)
 - Se utilizó el exception handling de Spring para mapear distintas excepciones a los códigos de respuesta apropiados (ver clase **RestExceptionHandler**)
  
## Testing  
Se incluyeron varios tests unitarios y otros de integración. Algunas menciones con respecto a los tests.
 - Se creó una clase **DatabaseResetter** para ayudar a resetear el estado de la DB entre tests, sin tener que recrear todo el contexto de Spring (que es lentísimo)
 - Para testear el guardado del historial de requests (en la clase **RequestLogControllerTest**) , se creó un mock del **Thread Pool**, el cuál ejecuta las tareas en el current thread (clase **SameThreadExecutorService**). Esto es necesario para obtener resultados deterministas en el test (y que no dependa del timing de los hilos)
 - En **UsersControllerTest** se incluyeron tests para el signup, login, y chequear que la autenticación funcione correctamente.
 - **PercentageRepositoryImplTest** incluye varios tests unitarios para comprobar que la obtención del porcentaje se comporta de acuerdo a los requerimientos (ir cada media hora al servicio, tomar el valor anterior si falla, retries hasta 3 veces, etc)
 - Se incluyeron algunos test parametrizados para testear lógica sencilla (ej: **TimeUtilsTest**)
 

  
