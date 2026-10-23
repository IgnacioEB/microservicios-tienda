package com.example.gateway_service.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    // Headers con la identidad ya validada, que reciben los servicios internos
    private static final String HEADER_ID = "X-User-Id";
    private static final String HEADER_EMAIL = "X-User-Email";
    private static final String HEADER_ROL = "X-User-Rol";

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().value();
        HttpMethod method = exchange.getRequest().getMethod();

        // Rutas publicas: pasan sin token (pero nunca con headers de identidad inventados)
        if (HttpMethod.POST.equals(method)
                && (path.equals("/api/auth/login") || path.equals("/api/auth/registro"))) {
            ServerHttpRequest limpia = exchange.getRequest().mutate()
                    .headers(h -> {
                        h.remove(HEADER_ID);
                        h.remove(HEADER_EMAIL);
                        h.remove(HEADER_ROL);
                    })
                    .build();
            return chain.filter(exchange.mutate().request(limpia).build());
        }
        if (HttpMethod.GET.equals(method) && path.startsWith("/docs/")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return responder(exchange, HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);

        if (!jwtService.esTokenValido(token)) {
            return responder(exchange, HttpStatus.UNAUTHORIZED);
        }

        String email = jwtService.extraerEmail(token);
        String rol = jwtService.extraerRol(token);
        String uid = jwtService.extraerUid(token);

        // Autorizacion por rol: autenticado pero sin permiso -> 403
        if (requiereAdmin(method, path) && !"ADMIN".equals(rol)) {
            return responder(exchange, HttpStatus.FORBIDDEN);
        }

        // Se pisan los headers de identidad con los datos del token (el cliente no puede falsificarlos)
        ServerHttpRequest autenticada = exchange.getRequest().mutate()
                .headers(h -> {
                    h.remove(HEADER_ID);
                    h.remove(HEADER_EMAIL);
                    h.remove(HEADER_ROL);
                    h.add(HEADER_EMAIL, email);
                    if (rol != null) {
                        h.add(HEADER_ROL, rol);
                    }
                    if (uid != null) {
                        h.add(HEADER_ID, uid);
                    }
                })
                .build();

        return chain.filter(exchange.mutate().request(autenticada).build());
    }

    // Usuarios: todo solo ADMIN. Productos: leer cualquiera, escribir solo ADMIN.
    private boolean requiereAdmin(HttpMethod method, String path) {
        if (path.startsWith("/api/usuario")) {
            return true;
        }
        if (path.startsWith("/api/producto")) {
            return !HttpMethod.GET.equals(method);
        }
        return false;
    }

    private Mono<Void> responder(ServerWebExchange exchange, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
