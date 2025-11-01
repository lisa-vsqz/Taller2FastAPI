package edu.udla.isw;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

public class App extends RouteBuilder {
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(new App());
        main.run(args);
    }

    @Override
    public void configure() {
        restConfiguration()
                .component("netty-http")
                .port(8080)
                .contextPath("/api")
                .apiContextPath("/api-doc")
                .apiProperty("api.title", "API de Envíos")
                .apiProperty("api.version", "1.0.0")
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin", "*");

        // Rutas REST
        rest("/envios").description("Gestión de Envíos")
                .get().to("direct:listarEnvios")
                .post().type(String.class).to("direct:crearEnvio");

        rest("/envios/{id}")
                .get().to("direct:obtenerEnvio");

        rest("/health")
                .get().to("direct:health");

        from("direct:listarEnvios")
                .log("Listando envíos")
                .setBody(constant(
                        "[{\"id\":\"001\",\"destinatario\":\"Juan Pérez\",\"direccion\":\"Calle 1\",\"estado\":\"En tránsito\"}]"));

        from("direct:crearEnvio")
                .log("Nuevo envío recibido: ${body}")
                .setHeader("CamelHttpResponseCode", constant(201))
                .setBody(constant("{\"mensaje\":\"Envío registrado correctamente\"}"));

        from("direct:obtenerEnvio")
                .log("Consultando envío con ID: ${header.id}")
                .setBody(simple(
                        "{\"id\":\"${header.id}\",\"destinatario\":\"Cliente X\",\"direccion\":\"C/ Ejemplo\",\"estado\":\"Entregado\"}"));

        from("direct:health")
                .setBody(constant("{\"status\":\"UP\"}"));
    }
}
