

# Challenge Tenpo
Hola! Soy Martín, gracias por revisar este proyecto. Incluí un readme para ayudar con la revisión del código y sumar algunos comentarios extra.


## ¿Cómo probar la app?
  

 1. Clonar el repositorio
 2. Ejecutar el comando `docker-compose up`. 
 3. Para probar la API se recomienda:
	 1. Utilizar la collection de Postman (en el directorio raíz de este repositorio) para probar los endpoints. Ver la sección que sigue para autenticación.
	 2. Navegar a **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)** para explorar la documentación de la API. La UI también permite interactuar con la API.
	 

## ¿Cómo loguearse?

Para probar los endpoints:

 1. Crear un user (primer request en la collection)
 2. Realizar el login (segundo request en la collection)
 3. Copiar el token del response del paso 2 en el header Authorization de los otros endpoints (que requieren autenticación)

La UI también permite autenticarse, arriba a la derecha hay un botón "Authorize" donde pegar el token (con prefijo Bearer incluido).

## Stack de desarrollo

 - Spring Boot + Java 11
 - Base de datos PostgreSQL para producción (H2 in-memory para testing)

  
## Seguridad  
Para la API REST se utilizó autenticación con **JWT** (usando **Spring Security** + **io.jsonwebtoken**).
  
## Documentación de la API  
La API está documentada utilizando la librería Springfox Swagger, incluyendo los códigos de respuesta y potenciales errores.
 - http://localhost:8080/swagger-ui/index.html para UI interactiva
 - http://localhost:8080/v2/api-docs para documentación en formato JSON.    
  
## Guardado del historial de requests
 - Este feature está implementado en la clase **AsyncRequestProcessorFilter**, extendiendo de **OncePerRequestFilter**. Este filter pasa los datos del request y el response a un **Thread Pool** que luego se encarga del guardado a la base de datos (para evitar que el llamado a la base sume tiempo extra al request).
 - En una arquitectura de microservicios real probablemente convendría
   externalizar y centralizar esta responsabilidad en otro componente
   (por ejemplo en un API Gateway, o utilizando un sidecar container +
   el ELK stack).

  

## SumService
El servicio externo de porcentajes se mockeo en la clase **PercentageServiceMock** (devuelve un valor random entre 10 y 12).  Para obtener el porcentaje, de acuerdo a los requerimientos, se consideraron dos alternativas, ambas cacheando el valor de forma local:
 1. Lazy loading: ir a buscar el valor del porcentaje a la cache, y si no está, recién ahi ir al servicio externo. Si cacheamos el valor con una **key que dependa del momento** en que se procesa el request, de forma que la key cambie cada media hora, nos aseguraríamos de consultar al servicio externo una sola vez por cada media hora, asumiendo un request exitoso.
 2. Eager loading: Utilizar un cron (@Scheduled) que corra cada media hora, consulte al servicio externo y guarde el resultado obtenido. Luego el servicio interesado sólo debe consultar este valor, sin usar ninguna key.
 
Se implementó la alternativa número 1. El siguiente diagrama resume la lógica:

<img width="930" alt="tenpo" src="https://user-images.githubusercontent.com/25701657/197373558-7df5fafc-199d-4ba2-b2ed-fdcd9023f578.png">


Otras consideraciones que vale la pena mencionar.

 - **Cache utilizada:** Cómo solo nos importa el último valor obtenido, no hizo falta utilizar ninguna librería externa (tipo Guava / Caffeine). Simplemente se almacenan el porcentaje y la key (de modo thread safe)
 - **Cache externa:** se podría utilizar una cache externa (Redis o memcached por ejemplo) para guardar el porcentaje, dependiendo de los requerimientos reales (en cuanto a latencia, costo de mantenimiento, y si necesario un valor consistente del % entre las distintas instancias de la app)
 - **Retries:** para implementar la lógica de retry se utilizó la librería **resilience4j**. Adicionalmente se podría configurar un **circuit-breaker** si fuera necesario.
 - **Validez del valor cacheado:** cómo el valor cacheado no tiene ningún tipo de expiración o TTL, se podría chequear que su antigüedad no supere cierto umbral, luego de lo cuál se lo podría invalidar y devolver un error.
  
## Validación y errores
 - Se utilizaron las anotaciones de **Bean Validation** (JSR 380) para validar el formato del payload de los requests (ver directorio controller.request)
 - Se utilizaron los exception handler de Spring para mapear distintas excepciones a los códigos de respuesta apropiados (ver clase **RestExceptionHandler**)
  
## Testing  
Se incluyeron varios tests unitarios y otros de integración. Algunas menciones con respecto a los tests.
 - Se creó una clase **DatabaseResetter** para ayudar a resetear el estado de la DB entre tests, sin tener que recrear todo el contexto de Spring (que es lentísimo)
 - Para testear el guardado del historial de requests (en la clase **RequestLogControllerTest**) , se creó un mock del **Thread Pool**, el cuál ejecuta las tareas en el current thread (clase **SameThreadExecutorService**). Esto es necesario para obtener resultados deterministas en el test (y que no dependa del timing de los hilos)
 - En **UsersControllerTest** se incluyeron tests para el signup, login, y chequear que la autenticación funcione correctamente.
 - **PercentageRepositoryImplTest** incluye varios tests unitarios para comprobar que la obtención del porcentaje se comporta de acuerdo a los requerimientos (ir cada media hora al servicio, tomar el valor anterior si falla, retries hasta 3 veces, etc)
 - Se incluyeron algunos test parametrizados para testear lógica sencilla (ej: **TimeUtilsTest**)




  
