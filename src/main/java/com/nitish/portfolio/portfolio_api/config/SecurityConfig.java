//package com.nitish.portfolio.portfolio_api.config;
//
//import com.nitish.portfolio.portfolio_api.security.JwtAuthFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//public class SecurityConfig {
//
//    @Autowired
//    private JwtAuthFilter jwtAuthFilter;
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/login").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}



package com.nitish.portfolio.portfolio_api.config;

import com.nitish.portfolio.portfolio_api.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration; // NEW IMPORT
import org.springframework.web.cors.CorsConfigurationSource; // NEW IMPORT
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // NEW IMPORT
import java.util.Arrays; // NEW IMPORT

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    // NEW BEAN: Global CORS Configuration Source
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow ONLY the React development origin (http://localhost:5173)
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "https://nyt1sh.netlify.app/"
        ));

        // Allow all necessary methods, including PUT and DELETE used by the Admin UI
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowedHeaders(Arrays.asList("*"));

        configuration.setExposedHeaders(Arrays.asList("Authorization")); // ye naya lagaye hein
        // Allow credentials (necessary for JWT in case cookies were involved, good practice)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all paths
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1. Enable and configure CORS globally
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Disable CSRF (standard for JWT APIs)
                .csrf(csrf -> csrf.disable())

                // 3. Configure authorization rules
//                .authorizeHttpRequests(auth -> auth
//                        // Public routes
//                        .requestMatchers("/api/auth/login").permitAll()
//                        .requestMatchers("/api/health").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/content/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/projects").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/contact/request-otp").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/api/contact/verify-otp").permitAll()
//
//                        // All other requests must be authenticated
//
//                        .anyRequest().authenticated()
//                )

                .authorizeHttpRequests(auth -> auth
                        // Public routes
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/content/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/projects").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/contact").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/contact/request-otp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/contact/verify-otp").permitAll()

                        // ADMIN routes – need ROLE_ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )


                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}