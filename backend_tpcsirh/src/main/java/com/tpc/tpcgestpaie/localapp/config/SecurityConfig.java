package com.tpc.tpcgestpaie.localapp.config;
import com.tpc.tpcgestpaie.localapp.filter.JwtFilter;
import com.tpc.tpcgestpaie.localapp.security.CustomAccessDeniedHandler;
import com.tpc.tpcgestpaie.localapp.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Autoriser le CORS (utile en dev, pas obligatoire en prod)
               /* .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(true);
                    config.addAllowedOrigin("http://localhost:5173"); // frontend en dev
                    config.setAllowedOriginPatterns(List.of(
                            "http://localhost:5173",
                            "http://localhost:5174",
                            "http://localhost:3700",
                            "http://localhost:*"
                    ));
                    config.addAllowedHeader("*");
                    config.addAllowedMethod("*"); // GET, POST, PUT, etc.
                    return config;
                }))*/
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowCredentials(false); // obligatoire avec *
                    config.addAllowedOrigin("*"); // toutes les origines
                    config.addAllowedHeader("*"); // tous les headers
                    config.addAllowedMethod("*"); // GET, POST, PUT, DELETE, OPTIONS...
                    return config;
                }))
                // Désactiver CSRF si tu utilises JWT
                .csrf(AbstractHttpConfigurer::disable)
                // Gestion des exceptions personnalisée
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                // Autorisations
                .authorizeHttpRequests(auth -> auth
                        // Frontend React (build copié dans /static)
                        .requestMatchers("/", "/index.html", "/static/**", "/assets/**","/logo/**","/lottiesFiles/**","/images/**","/icons/**","/document-templates/**","/fonts/**", "/favicon.ico").permitAll()
                        .requestMatchers("/{path:[^\\.]*}").permitAll()
                        // Fichiers uploadés accessibles publiquement
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/api/hr-events/**").permitAll()
                        .requestMatchers("/api/hr-alert-settings/**").permitAll()
                        .requestMatchers("/api/hr-event-types/**").permitAll()
                        .requestMatchers("/api/settings/color").permitAll()



                        // Auth publique
                        .requestMatchers("/api/auth/**", "/test/**", "/api/categories_employes/**",
                                "/api/common/tva/**", "/api/common/activity-areas/**",
                                "/api/paie/heures-supplementaires/**",  "/api/etats/**",
                                "/api/etat-charges/**","/api/apercu-avant/**","/api/apercu-apres/**","/api/employee-documents/**").permitAll()

                        // Swagger
                        .requestMatchers(
                                "/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs/**", "/v3/api-docs.yaml",
                                "/swagger-resources/**", "/webjars/**"
                        ).permitAll()

                        .requestMatchers("/api/installation/**").permitAll()
                        .requestMatchers("/api/debug/python-config","/api/digitalization/process").permitAll()
                        .requestMatchers("/api/admin/licenses/**").permitAll() // ← AJOUTEZ CETTE LIGNE
                        .requestMatchers("/api/license/**","api/license/verify","/api/license/activate","/api/license-token/info","/api/license-token/status").permitAll()
                        // Rôles spécifiques
                        .requestMatchers("/api/admin/**").hasAnyRole("SUPER_ADMIN")
                        .requestMatchers("/api/common/notification/**", "/api/employes/**", "/api/diplomes/**",
                                "/api/liens_parente/**", "/api/arh/employes/enfant/**",
                                "/api/arh/employes/personnes-a-prevenir/**", "/api/traitement-salaire/**"
                        ).hasAnyRole("USER", "ADMIN", "SUPER_ADMIN", "MANAGER", "HR","EMPLOYEE")
                        .requestMatchers("/admin/arh/**", "/api/arh/departements/**", "/api/paie/**","/api/alertes-systeme/**"
                        ).hasAnyRole("ADMIN", "SUPER_ADMIN", "MANAGER", "HR")

                        .requestMatchers("/user/**", "/api/user/**").hasRole("USER")
                        .requestMatchers("/api/espace-employe/**","/api/employes/","/api/common/notification/**").hasAnyRole("EMPLOYEE","COMPTABLE","HR","MANAGER")
                        .requestMatchers("/api/companies/by/user/visible")
                        .hasAnyRole("SUPER_ADMIN", "COMPANY_ADMIN", "HR", "MANAGER", "COMPTABLE")
                        .requestMatchers("/api/companies/by/user/my-client-company", "/api/companies/by/user/my-company-license","/api/settings/**","/api/documents-categories/**","/api/document-models/**","/api/documents/**")
                        .hasAnyRole("SUPER_ADMIN", "COMPANY_ADMIN")
                        //Employé
                        .requestMatchers("/api/espace-employe/**")
                        .hasAnyRole("EMPLOYEE")
                        .requestMatchers("/api/users/management/**")
                        .authenticated()
                        // Tout le reste nécessite une auth
                        .anyRequest().authenticated()
                )
                // Login par défaut (utile si tu testes sans JWT)
                .formLogin(AbstractHttpConfigurer::disable)
                // Ajouter ton filtre JWT
                .addFilterBefore(new JwtFilter(jwtUtils, customUserDetailsService),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
