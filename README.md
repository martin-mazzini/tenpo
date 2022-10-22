
# Challenge Tenpo
Hola! Soy Martín Mazzini, gracias por revisar este proyecto. Incluí este readme para ayudar con la revisión del código y sumar algunos comentarios extra.


## ¿Cómo probar la app?
  

 1. Clonar el repositorio
 2. Ejecutar el comando `docker-compose up`. 
 3. Para probar la API hay dos opciones:
	 a. Navegar a **[http://localhost:8080/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)**. 
	 b. Utilizar el collection de Postman adjunto. 

  
## Stack de desarrollo

 - Spring Boot + Java 11
 - Base de datos PostgreSQL para producción (H2 in-memory para testing)

  
## Seguridad  
La API REST se encuentra securizada mediante autenticación con **JWT** (usando **Spring Security** + **io.jsonwebtoken**). Por lo tanto, para probar cualquier endpoint de la API es necesario:

 - Crear un user
 - Login con las credenciales del user
 - Copiar el token en el header Authorization de todos los requests subsiguientes
  
## Documentación de la API  
La API está documentada utilizando la librería Springfox Swagger. 
 - http://localhost:8080/swagger-ui/index.html para UI interactiva
 - http://localhost:8080/v2/api-docs para documentación en formato JSON.    
  
## Guardado del historial de requests
Este feature está implementado en la clase **AsyncRequestProcessorFilter**, extendiendo de **OncePerRequestFilter**. Este filter pasa el request y el response a un **Thread Pool** que luego se encarga del guardado a la base de datos (para evitar sumar tiempo extra al request). En una arquitectura de microservicios real probablemente convendría externalizar y centralizar esta responsabilidad en otro componente (por ejemplo en un API Gateway, o utilizando un sidecar container + el ELK stack).


## SumService
Para implementar el feature de sumar dos números, primero hay que obtener el porcentaje del servicio externo (mockeado en la clase PercentageServiceMock).  Se consideraron dos alternativas:
 1. Utilizar una cache local para guardar los porcentajes, con una **key que dependa del momento** en que se procesa el request y que cambié cada media hora. De esta manera nos aseguraríamos de consultar al servicio externo una sola vez por cada período de media hora (en caso de un llamado exitoso).
 2. Utilizar un cron (@Scheduled) que corra cada media hora, que consulte al servicio externo y guarde el resultado obtenido localmente. Luego el servicio interesado sólo debe consultar este campo.
 
Se implementó la alternativa número 1. Una ventaja de este enfoque es que si no logramos obtener el porcentaje en un request, el siguiente request automáticamente va a reintentar. En el enfoque 2, no tendríamos dato nuevo hasta la siguiente media hora, al menos que implementos otra lógica adicional. El siguiente diagrama resume la lógica con más detalle.

<img width="866" alt="tenpo" src="https://user-images.githubusercontent.com/25701657/197357502-b8d438c6-f81f-402a-8b3b-473062e5d754.png">



Otras consideraciones que vale la pena mencionar.

 - **Cache externa:** se podría utilizar una cache externa (Redis o memcached por ejemplo) para guardar el porcentaje, dependiendo de los requerimientos. En comparación con una **cache local**, tiene sus ventajas y desventajas en cuanto a latencia, costo de mantenimiento, consistencia de los datos y problemas de cold-start.
 - **Retries:** para implementar la lógica de retry se utilizó la librería **resilience4j**. Adicionalmente se podría configurar un **circuit-breaker** si fuera necesario.
  
## Validación y errores
 - Se utilizaron las anotaciones de Bean Validation (JSR 380) para validar el formato del payload de los requests (ver directorio controller.request)
 - Se utilizaró el exception handling de Spring para mapear distintas excepciones a los códigos de respuesta apropiados (ver clase **RestExceptionHandler**)
  
## Testing  
Se incluyeron varios tests unitarios y otros de integración. Algunas menciones con respecto a los tests.
 - Se creó una clase **DatabaseResetter** para ayudar a resetear el estado de la DB entre tests, sin tener que recrear todo el contexto de Spring (que es lentísimo)
 - Para testear el guardado del historial de requests (en la clase **RequestLogControllerTest**) , se creó un mock del **Thread Pool**, el cuál ejecuta las tareas en el thread actual (clase **SameThreadExecutorService**). Esto es necesario para obtener resultados deterministas en el test (y que no dependa del timing)
 - En **UsersControllerTest** se incluyeron tests para el signup, login, y chequear que la autenticación funcione correctamente.
 - **PercentageRepositoryImplTest** incluye varios tests unitarios para comprobar que la obtención del porcentaje se comporta de acuerdo a los requerimientos (ir cada media hora, tomar el valor anterior si falla, retries hasta 3 veces, etc)
 - Se incluyeron algunos test parametrizados para testear lógica sencilla (ej: **TimeUtilsTest**)
 

  
