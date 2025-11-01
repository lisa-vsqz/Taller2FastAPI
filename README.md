## Descripción del Proyecto

API REST desarrollada con Apache Camel para gestionar envíos de una empresa de logística. El proyecto implementa operaciones CRUD básicas siguiendo los principios RESTful y está documentado con OpenAPI/Swagger.

## Características

- ✅ Consulta de todos los envíos (`GET /envios`)
- ✅ Consulta de envío por ID (`GET /envios/{id}`)
- ✅ Registro de nuevos envíos (`POST /envios`)
- ✅ Endpoint de salud (`GET /health`)
- ✅ Documentación OpenAPI 3.0
- ✅ Logs detallados de operaciones
- ✅ Soporte CORS habilitado

## Tecnologías Utilizadas

- **Java 17**
- **Apache Maven 3.9+**
- **Apache Camel 4.7.0**
  - camel-core
  - camel-main
  - camel-rest
  - camel-netty-http
  - camel-openapi-java
- **SLF4J 2.0.13** (logging)

## Requisitos Previos

- Java JDK 17 o superior
- Apache Maven 3.9+
- Postman (opcional, para pruebas)
- Docker (opcional, para ejecución containerizada)

## Estructura del Proyecto

```
api-rest-lab/
├── src/
│   └── main/
│       ├── java/
│       │   └── edu/udla/isw/
│       │       └── App.java
│       └── resources/
│           └── openapi.yaml
├── pom.xml
├── Dockerfile
└── README.md
```

## Instalación y Ejecución

### Opción 1: Ejecución Local con Maven

1. **Clonar el repositorio**

```bash
git clone <url-repositorio>
cd api-rest-lab
```

2. **Compilar el proyecto**

```bash
mvn clean package
```

3. **Ejecutar la aplicación**

```bash
mvn exec:java
```

O alternativamente:

```bash
java -cp target/api-rest-lab-1.0-SNAPSHOT.jar edu.udla.isw.App
```

### Opción 2: Ejecución con Docker

1. **Construir la imagen**

```bash
mvn clean package
docker build -t api-rest-lab .
```

2. **Ejecutar el contenedor**

```bash
docker run -p 8080:8080 api-rest-lab
```

## Endpoints Disponibles

### Base URL

```
http://localhost:8080/api
```

### 1. Listar todos los envíos

```http
GET /api/envios
```

**Respuesta exitosa (200)**

```json
[
  {
    "id": "001",
    "destinatario": "Juan Pérez",
    "direccion": "Calle 1",
    "estado": "En tránsito"
  }
]
```

### 2. Obtener envío por ID

```http
GET /api/envios/{id}
```

**Ejemplo:**

```http
GET /api/envios/001
```

**Respuesta exitosa (200)**

```json
{
  "id": "001",
  "destinatario": "Cliente X",
  "direccion": "C/ Ejemplo",
  "estado": "Entregado"
}
```

### 3. Crear nuevo envío

```http
POST /api/envios
Content-Type: application/json
```

**Body:**

```json
{
  "id": "002",
  "destinatario": "María López",
  "direccion": "Av. Central 123",
  "estado": "Registrado"
}
```

**Respuesta exitosa (201)**

```json
{
  "mensaje": "Envío registrado correctamente"
}
```

### 4. Health Check

```http
GET /api/health
```

**Respuesta exitosa (200)**

```json
{
  "status": "UP"
}
```

### 5. Documentación Swagger

```
http://localhost:8080/api/api-doc
```

## Pruebas con Postman

1. Importar la colección Postman incluida en el proyecto
2. Los ejemplos de respuesta ya están guardados en la colección
3. Verificar que el servidor esté corriendo en `localhost:8080`
4. Ejecutar las peticiones en el siguiente orden recomendado:
   - Health Check
   - GET /envios
   - GET /envios/001
   - POST /envios

## Logs de Aplicación

La aplicación genera logs informativos para cada operación:

```
[edu.udla.isw.App.main()] INFO org.apache.camel.impl.engine.AbstractCamelContext - Apache Camel 4.7.0 (camel-1) started in 536ms
[Camel (camel-1) thread #4 - NettyConsumerExecutorGroup] INFO route2 - Listando envíos
[Camel (camel-1) thread #4 - NettyConsumerExecutorGroup] INFO route3 - Nuevo envío recibido: {...}
[Camel (camel-1) thread #4 - NettyConsumerExecutorGroup] INFO route4 - Consultando envío con ID: 001
```

## Configuración del Proyecto

### Puerto y Context Path

- **Puerto**: 8080
- **Context Path**: `/api`
- **API Documentation**: `/api-doc`

### CORS

CORS está habilitado para permitir peticiones desde cualquier origen (`*`)

## Resolución de Problemas

### El servidor no inicia

- Verificar que el puerto 8080 no esté ocupado
- Confirmar que Java 17+ está instalado: `java -version`
- Verificar que Maven está correctamente configurado: `mvn -version`

### Error de dependencias

```bash
mvn clean install -U
```

### Caracteres especiales en logs

Los logs pueden mostrar caracteres incorrectos (ej: `env??o` en lugar de `envío`). Esto es un problema de encoding pero no afecta la funcionalidad de la API.

## Mejoras Futuras

- [ ] Persistencia de datos (base de datos)
- [ ] Validación de entrada con Bean Validation
- [ ] Manejo de errores HTTP (404, 400, 500)
- [ ] Autenticación y autorización
- [ ] Paginación para GET /envios
- [ ] Tests unitarios y de integración
- [ ] Actualización de envíos (PUT)
- [ ] Eliminación de envíos (DELETE)

## Reflexión Individual

### ¿Qué ventajas ofrece una API REST bien diseñada frente a los enfoques tradicionales de integración?

1. **Interoperabilidad**: APIs REST pueden ser consumidas por cualquier cliente que soporte HTTP, independientemente del lenguaje o plataforma

2. **Escalabilidad**: El modelo stateless facilita el escalamiento horizontal

3. **Desacoplamiento**: Cliente y servidor evolucionan independientemente

4. **Documentación estándar**: OpenAPI/Swagger proporciona documentación viva y probada

5. **Simplicidad**: Uso de verbos HTTP estándar hace la API intuitiva

6. **Cache**: Soporte nativo de HTTP para cacheo mejora el rendimiento

7. **Versionado**: Permite evolución controlada de la API sin romper clientes existentes

## Autor

Proyecto desarrollado como parte del Taller 02 - Diseño e Implementación de una API REST con OpenAPI/Swagger

## Bibliografía

- Hohpe, G. & Woolf, B. (2004). _Enterprise Integration Patterns_. Addison-Wesley.
- Apache Camel Documentation – https://camel.apache.org
- OpenAPI Specification – https://swagger.io/specification/

---

**Nota**: Este proyecto es con fines educativos y no está diseñado para entornos de producción sin las mejoras de seguridad y persistencia correspondientes.
