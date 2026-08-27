package com.asada.security;

import com.asada.domain.Ruta;
import com.asada.service.RutaService;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad del sistema.
 *
 * Las rutas públicas básicas (login, recursos estáticos y páginas de error)
 * siempre están permitidas, sin depender de la base de datos. El resto de
 * las reglas de autorización se construyen dinámicamente a partir de la
 * tabla `ruta`, siguiendo el mismo enfoque que el proyecto de referencia
 * tienda_vm: cada fila de `ruta` indica un patrón de URL y, opcionalmente,
 * el rol requerido para acceder a él.
 *
 * Un mismo patrón de URL puede tener varias filas en `ruta`, cada una con
 * un rol distinto (por ejemplo, para permitir que tanto ADMIN como
 * FONTANERO vean la bitácora); en ese caso se combinan con "hasAnyRole".
 */
@Configuration
public class SecurityConfig {

    private static final String[] RUTAS_PUBLICAS = {
            "/",
            "/index",
            "/login",
            "/login/**",
            "/403",
            "/error",
            "/error/**",
            "/css/**",
            "/js/**",
            "/img/**",
            "/uploads/**",
            "/fav/**",
            "/webjars/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            @Lazy RutaService rutaService) throws Exception {

        http.authorizeHttpRequests(auth -> {

            auth.requestMatchers(RUTAS_PUBLICAS).permitAll();

            // Agrupa las reglas de la tabla `ruta` por patrón de URL, para
            // poder combinar varios roles sobre el mismo patrón.
            Map<String, Set<String>> rolesPorRuta = new LinkedHashMap<>();
            Set<String> rutasPublicasDinamicas = new LinkedHashSet<>();

            for (Ruta ruta : rutaService.getRutas()) {
                if (ruta.isRequiereRol() && ruta.getRol() != null) {
                    rolesPorRuta.computeIfAbsent(ruta.getRuta(), k -> new LinkedHashSet<>())
                            .add(ruta.getRol().getRol());
                } else {
                    rutasPublicasDinamicas.add(ruta.getRuta());
                }
            }

            for (Map.Entry<String, Set<String>> entry : rolesPorRuta.entrySet()) {
                auth.requestMatchers(entry.getKey()).hasAnyRole(entry.getValue().toArray(new String[0]));
            }

            if (!rutasPublicasDinamicas.isEmpty()) {
                auth.requestMatchers(rutasPublicasDinamicas.toArray(new String[0])).permitAll();
            }

            auth.anyRequest().authenticated();
        });

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", false)
                .failureUrl("/login?error=true")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
        );

        http.sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
        );

        http.exceptionHandling(exception -> exception
                .accessDeniedPage("/403")
        );

        return http.build();
    }

    /**
     * Ata el {@link UserDetailsService} propio (que consulta la tabla usuario)
     * y el codificador BCrypt al {@link AuthenticationManagerBuilder}.
     */
    @Autowired
    public void configurarAutenticacion(AuthenticationManagerBuilder builder,
            @Lazy PasswordEncoder passwordEncoder,
            @Lazy UserDetailsService userDetailsService) throws Exception {
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
