package com.allan.climberanalyzer.config;

// import java.security.KeyManagementException;
// import java.security.NoSuchAlgorithmException;
// import java.security.cert.X509Certificate;

// import javax.net.ssl.HostnameVerifier;
// import javax.net.ssl.HttpsURLConnection;
// import javax.net.ssl.SSLContext;
// import javax.net.ssl.TrustManager;
// import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableWebSecurity
public class AppConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(customizer -> customizer.disable()).authorizeHttpRequests(request -> request

                .anyRequest().permitAll()).

                sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).build();
    }
}
