package com.xound.config;

import com.xound.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(JwtFilter jwtFilter, CorsConfigurationSource corsConfigurationSource) {
        this.jwtFilter = jwtFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                .requestMatchers("/api/roles").permitAll()
                .requestMatchers("/api/events/share/**").permitAll()
                // Swagger UI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                // Búsqueda de acordes y letras (usuarios autenticados)
                .requestMatchers(HttpMethod.GET, "/api/chords/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/lyrics/**").authenticated()
                // Eventos publicados accesibles para músicos autenticados
                .requestMatchers(HttpMethod.GET, "/api/events/published").authenticated()
                // Usar código admin - cualquier autenticado (antes de la regla general)
                .requestMatchers(HttpMethod.POST, "/api/admin/use-admin-code").authenticated()
                // Super admin panel - todo lo demás
                .requestMatchers("/api/admin/**").hasRole("SUPER_ADMIN")
                // CRUD de eventos y canciones solo para ADMIN o SUPER_ADMIN
                .requestMatchers(HttpMethod.POST, "/api/events", "/api/songs").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/events/**", "/api/songs/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/events/**", "/api/songs/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                // Setlist management solo para ADMIN o SUPER_ADMIN
                .requestMatchers(HttpMethod.POST, "/api/events/*/setlist").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/events/*/setlist/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/events/*/setlist/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                // Band management - crear y gestionar banda solo ADMIN o SUPER_ADMIN
                .requestMatchers(HttpMethod.POST, "/api/bands").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/bands/regenerate-code").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/bands/*/members/*").hasAnyRole("ADMIN", "SUPER_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/bands/*/members/*").hasAnyRole("ADMIN", "SUPER_ADMIN")
                // Join band - cualquier usuario autenticado
                .requestMatchers(HttpMethod.POST, "/api/bands/join").authenticated()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
