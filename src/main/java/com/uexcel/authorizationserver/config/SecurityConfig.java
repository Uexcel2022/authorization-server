package com.uexcel.authorizationserver.config;

import static org.springframework.security.config.Customizer.withDefaults;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
        httpSecurity.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(withDefaults());
        httpSecurity.exceptionHandling(
                e -> e.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        return httpSecurity.build();

    }

    @Bean
    @Order(2)
    SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers(HttpMethod.POST, "/registration").permitAll()
                                .anyRequest().authenticated())
                .formLogin(withDefaults())
                // .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .build();
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            Authentication principal = context.getPrincipal();
            if (context.getTokenType().getValue().equals("id_token")) {
                context.getClaims().claim("Test", "Test Id Token");
            }
            if (context.getTokenType().getValue().equals("access_token")) {
                context.getClaims().claim("Test", "Test Access Token");
                Set<String> authorities = principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
                context.getClaims().claim("authorities", authorities)
                        .claim("user", principal.getName());
            }
        };
    }

    @Bean
    JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    public static RSAKey generateRsa() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
    }

    static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}

// import static org.springframework.security.config.Customizer.withDefaults;

// import java.security.KeyPair;
// import java.security.KeyPairGenerator;
// import java.security.interfaces.RSAPrivateKey;
// import java.security.interfaces.RSAPublicKey;
// import java.time.Duration;
// import java.util.Set;
// import java.util.UUID;
// import java.util.stream.Collectors;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.annotation.Order;
// import org.springframework.http.HttpMethod;
// import
// org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import
// org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import
// org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
// import
// org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.oauth2.core.AuthorizationGrantType;
// import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
// import org.springframework.security.oauth2.core.oidc.OidcScopes;
// import org.springframework.security.oauth2.jwt.JwtDecoder;
// import
// org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
// import
// org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
// import
// org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
// import
// org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
// import
// org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
// import
// org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
// import
// org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
// import
// org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
// import
// org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
// import
// org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
// import
// org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
// import
// org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
// import
// org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
// import
// org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
// import
// org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
// import org.springframework.security.web.SecurityFilterChain;
// import
// org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

// import com.nimbusds.jose.jwk.JWKSet;
// import com.nimbusds.jose.jwk.RSAKey;
// import com.nimbusds.jose.jwk.source.JWKSource;
// import com.nimbusds.jose.proc.SecurityContext;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

// @Bean
// @Order(1)
// SecurityFilterChain webFilterChainForOAuth(HttpSecurity httpSecurity) throws
// Exception {
// OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
// httpSecurity.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
// .oidc(withDefaults());
// httpSecurity.exceptionHandling(
// e -> e.authenticationEntryPoint(new
// LoginUrlAuthenticationEntryPoint("/login")));

// return httpSecurity.build();

// }

// @Bean
// @Order(2)
// SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws
// Exception {
// return http
// .csrf(AbstractHttpConfigurer::disable)
// .authorizeHttpRequests(
// auth -> auth.requestMatchers(HttpMethod.POST, "/registration").permitAll()
// .anyRequest()
// .authenticated())
// .formLogin(withDefaults())
// // .authorizeHttpRequests(authorize ->
// authorize.anyRequest().authenticated())
// .build();

// }

// // @Bean
// // UserDetailsService userDetailsService() {
// // var user1 = User.withUsername("user")
// // .password(passwordEncoder().encode("password"))
// // .authorities("read")
// // .build();
// // return new InMemoryUserDetailsManager(user1);
// // }

// @Bean
// BCryptPasswordEncoder passwordEncoder() {
// return new BCryptPasswordEncoder();
// }

// @Bean
// RegisteredClientRepository registeredClientRepository() {
// RegisteredClient registeredClient =
// RegisteredClient.withId(UUID.randomUUID().toString())
// .clientId("client")
// .clientSecret(passwordEncoder().encode("secret"))
// .scope("read")
// .scope(OidcScopes.OPENID)
// .scope(OidcScopes.PROFILE)
// .redirectUri("http://insomnia")
// .redirectUri("http://127.0.0.1:8080/login/oauth2/code/client")
// .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
// .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
// .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
// .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
// .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
// .tokenSettings(tokenSettings())
// .clientSettings(clientSettings())
// .build();

// return new InMemoryRegisteredClientRepository(registeredClient);
// }

// @Bean
// TokenSettings tokenSettings() {
// return TokenSettings.builder()
// .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
// .accessTokenTimeToLive(Duration.ofDays(1))
// .build();
// }

// @Bean
// ClientSettings clientSettings() {
// return ClientSettings.builder()
// .requireProofKey(false)
// .requireAuthorizationConsent(false)
// .build();
// }

// // @Bean
// // AuthorizationServerSettings authorizationServerSettings() {
// // return AuthorizationServerSettings.builder().build();
// // }

// // @Bean
// // OAuth2AuthorizationService authorizationService() {
// // return new InMemoryOAuth2AuthorizationService();
// // }

// // @Bean
// // OAuth2AuthorizationConsentService authorizationConsentService() {
// // return new InMemoryOAuth2AuthorizationConsentService();
// // }

// @Bean
// OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
// return context -> {
// Authentication principal = context.getPrincipal();
// if (context.getTokenType().getValue().equals("id_token")) {
// context.getClaims().claim("Test", "Test Id Token");
// }
// if (context.getTokenType().getValue().equals("access_token")) {
// context.getClaims().claim("Test", "Test Access Token");
// Set<String> authorities = principal.getAuthorities().stream()
// .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
// context.getClaims().claim("authorities", authorities)
// .claim("user", principal.getName());
// }

// };
// }

// @Bean
// JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
// return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
// }

// @Bean
// JWKSource<SecurityContext> jwkSource() {
// RSAKey rsaKey = generateRsa();
// JWKSet jwkSet = new JWKSet(rsaKey);
// return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
// }

// public static RSAKey generateRsa() {
// KeyPair keyPair = generateRsaKey();
// RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
// RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
// return new
// RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
// }

// static KeyPair generateRsaKey() {
// KeyPair keyPair;
// try {
// KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
// keyPairGenerator.initialize(2048);
// keyPair = keyPairGenerator.generateKeyPair();
// } catch (Exception ex) {
// throw new IllegalStateException(ex);
// }
// return keyPair;
// }

// }