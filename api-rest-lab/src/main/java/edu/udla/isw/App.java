package edu.udla.isw;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App extends RouteBuilder {
        private static final Map<String, Envio> envios = new HashMap<>();
        private static final ObjectMapper objectMapper = new ObjectMapper();

        public static class Envio {
                private String id;
                private String destinatario;
                private String direccion;
                private String estado;

                public Envio() {
                }

                public Envio(String destinatario, String direccion) {
                        this.id = UUID.randomUUID().toString();
                        this.destinatario = destinatario;
                        this.direccion = direccion;
                        this.estado = "Pendiente";
                }

                // Getters and Setters
                public String getId() {
                        return id;
                }

                public void setId(String id) {
                        this.id = id;
                }

                public String getDestinatario() {
                        return destinatario;
                }

                public void setDestinatario(String destinatario) {
                        this.destinatario = destinatario;
                }

                public String getDireccion() {
                        return direccion;
                }

                public void setDireccion(String direccion) {
                        this.direccion = direccion;
                }

                public String getEstado() {
                        return estado;
                }

                public void setEstado(String estado) {
                        this.estado = estado;
                }
        }

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

                onException(Exception.class)
                                .handled(true)
                                .setHeader("Content-Type", constant("application/json"))
                                .setHeader("CamelHttpResponseCode", constant(500))
                                .setBody(simple("{\"error\": \"${exception.message}\"}"));

                rest("/envios").description("Gestión de Envíos")
                                .get().to("direct:listarEnvios")
                                .post().type(String.class).to("direct:crearEnvio");

                rest("/envios/{id}")
                                .get().to("direct:obtenerEnvio")
                                .put().type(String.class).to("direct:actualizarEnvio")
                                .delete().to("direct:eliminarEnvio");

                rest("/health")
                                .get().to("direct:health");

                from("direct:listarEnvios")
                                .process(exchange -> {
                                        List<Envio> listaEnvios = new ArrayList<>(envios.values());
                                        exchange.getMessage().setBody(objectMapper.writeValueAsString(listaEnvios));
                                });

                from("direct:crearEnvio")
                                .process(exchange -> {
                                        String body = exchange.getMessage().getBody(String.class);
                                        Envio nuevoEnvio = objectMapper.readValue(body, Envio.class);
                                        nuevoEnvio.setId(UUID.randomUUID().toString());
                                        nuevoEnvio.setEstado("Pendiente");
                                        envios.put(nuevoEnvio.getId(), nuevoEnvio);
                                        exchange.getMessage().setHeader("CamelHttpResponseCode", 201);
                                        exchange.getMessage().setBody(objectMapper.writeValueAsString(nuevoEnvio));
                                });

                from("direct:obtenerEnvio")
                                .process(exchange -> {
                                        String id = exchange.getMessage().getHeader("id", String.class);
                                        Envio envio = envios.get(id);
                                        if (envio == null) {
                                                exchange.getMessage().setHeader("CamelHttpResponseCode", 404);
                                                exchange.getMessage().setBody("{\"error\": \"Envío no encontrado\"}");
                                        } else {
                                                exchange.getMessage().setBody(objectMapper.writeValueAsString(envio));
                                        }
                                });

                from("direct:actualizarEnvio")
                                .process(exchange -> {
                                        String id = exchange.getMessage().getHeader("id", String.class);
                                        String body = exchange.getMessage().getBody(String.class);
                                        if (!envios.containsKey(id)) {
                                                exchange.getMessage().setHeader("CamelHttpResponseCode", 404);
                                                exchange.getMessage().setBody("{\"error\": \"Envío no encontrado\"}");
                                                return;
                                        }
                                        Envio actualizacion = objectMapper.readValue(body, Envio.class);
                                        Envio envioExistente = envios.get(id);
                                        envioExistente.setDestinatario(actualizacion.getDestinatario());
                                        envioExistente.setDireccion(actualizacion.getDireccion());
                                        envioExistente.setEstado(actualizacion.getEstado());
                                        exchange.getMessage().setBody(objectMapper.writeValueAsString(envioExistente));
                                });

                from("direct:eliminarEnvio")
                                .process(exchange -> {
                                        String id = exchange.getMessage().getHeader("id", String.class);
                                        if (envios.remove(id) == null) {
                                                exchange.getMessage().setHeader("CamelHttpResponseCode", 404);
                                                exchange.getMessage().setBody("{\"error\": \"Envío no encontrado\"}");
                                        } else {
                                                exchange.getMessage().setHeader("CamelHttpResponseCode", 204);
                                        }
                                });

                from("direct:health")
                                .setBody(constant("{\"status\":\"UP\"}"));
        }
}
