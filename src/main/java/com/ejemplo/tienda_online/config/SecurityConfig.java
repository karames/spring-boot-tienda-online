package com.ejemplo.tienda_online.config;

import com.ejemplo.tienda_online.security.JwtAuthFilter;
import com.ejemplo.tienda_online.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    @SuppressWarnings("deprecation")
    public org.springframework.security.authentication.dao.DaoAuthenticationProvider daoAuthenticationProvider() {
        var provider = new org.springframework.security.authentication.dao.DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/auth/**", "/auth/**")
                .disable()
            )
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/**", // Permitir endpoints de autenticación
                    "/auth/**",
                    "/setup/admin", // Permitir configuración inicial
                    "/dev/diagnostico/**", // Endpoints de diagnóstico
                    "/index.html", "/", "/login.html", "/register.html", "/error.html",
                    "/documentacion.html", // Permitir acceso a documentación sin autenticación
                    "/css/**", "/js/**", "/img/**", "/favicon.ico", "/favicon.svg",
                    "/error",
                    "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**" // Swagger UI y OpenAPI
                ).permitAll()
                // Endpoints de productos
                .requestMatchers(HttpMethod.GET, "/api/productos", "/api/productos/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                // Endpoints de pedidos
                .requestMatchers(HttpMethod.GET, "/api/pedidos").hasRole("ADMIN") // Ver todos los pedidos
                .requestMatchers(HttpMethod.GET, "/api/pedidos/mios").hasRole("CLIENTE") // Ver mis pedidos
                .requestMatchers(HttpMethod.POST, "/api/pedidos").hasRole("CLIENTE") // Crear pedido
                .requestMatchers(HttpMethod.PUT, "/api/pedidos/**").hasRole("ADMIN") // Cambiar estado
                // Páginas web
                .requestMatchers("/admin.html", "/admin_db.html").hasRole("ADMIN")
                .requestMatchers("/productos.html", "/pedidos.html", "/carrito.html").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(eh -> eh
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    if (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json")) {
                        // Para solicitudes API que esperan JSON
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"No tienes permiso para acceder a este recurso\"}");
                    } else {
                        // Para solicitudes web, redirigir a la página de error
                        response.sendRedirect("/error.html");
                    }
                })
                .authenticationEntryPoint((request, response, authException) -> {
                    if (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json")) {
                        // Para solicitudes API que esperan JSON
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Debes iniciar sesión para acceder a este recurso\"}");
                    } else {
                        // Para solicitudes web, redirigir a la página de login
                        response.sendRedirect("/login.html");
                    }
                })
            )
            .authenticationProvider(daoAuthenticationProvider())
            .addFilterAfter(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
