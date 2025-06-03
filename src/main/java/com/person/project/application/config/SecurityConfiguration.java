package com.person.project.application.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import static com.person.project.domain.enums.RoleUserEnum.ADMIN;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = { "/api/v1/auth/**" };

    private final JwtAuthenticationFilter jwtAuthFilter;
    // Usa el bean reactivo que configuras previamente
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    // Asegúrate de que este bean implemente ServerLogoutHandler (p. ej., SecurityContextServerLogoutHandler)
    private final ServerLogoutHandler logoutHandler;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                // Deshabilitar CSRF para APIs stateless
                .csrf(csrf -> csrf.disable())
                // Al no usar sesiones, indicamos que el repositorio de contexto de seguridad no haga persistencia
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(WHITE_LIST_URL).permitAll()
                        .pathMatchers("/test").hasAnyRole(ADMIN.name())
                        .anyExchange().authenticated()
                )
                // Usamos el reactive authentication manager
                .authenticationManager(reactiveAuthenticationManager)
                // Agregamos nuestro filtro JWT reactivo en la posición de autenticación
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                // Configuramos el logout
                .logout(logout -> logout
                        .logoutUrl("/api/v1/auth/logout")
                        .logoutHandler(logoutHandler)
                        .logoutSuccessHandler((exchange, authentication) -> {
                            // Para logout reactivo, basta con devolver un Mono vacío
                            return Mono.empty();
                        })
                )
                .build();
    }
}
