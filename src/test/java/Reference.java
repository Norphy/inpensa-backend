import com.sun.tools.jconsole.JConsoleContext;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Reference {

    @Test
    public void test() {
        JwtEncoderParameters params = mint(consumer -> {
            consumer.subject("hiii");
        });
        System.out.println("Params Issuer: " + params.getClaims().getIssuer());
        System.out.println("Params Sub: " + params.getClaims().getSubject());
    }


    private JwtEncoderParameters mint(Consumer<JwtClaimsSet.Builder> consumer) {
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(100000))
                .subject("sarah1")
                .issuer("http://localhost:9000")
                .audience(Arrays.asList("cashcard-client"))
                .claim("scp", Arrays.asList("cashcard:read", "cashcard:write"));
        consumer.accept(builder);
        JwtEncoderParameters parameters = JwtEncoderParameters.from(builder.build());
        System.out.println("Parameters: " + parameters);
        return parameters;
//        return this.jwtEncoder.encode(parameters).getTokenValue();
    }


//    @TestConfiguration
//    static class TestJwtConfiguration {
//        @Bean
//        JwtEncoder jwtEncoder(@Value("classpath:authz.pub") RSAPublicKey pub,
//                              @Value("classpath:authz.pem") RSAPrivateKey pem) {
//            RSAKey key = new RSAKey.Builder(pub).privateKey(pem).build();
//            return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
//        }
//    }
//
//
//
//    @TestConfiguration
//    static class TestJwtConfiguration {
//        @Bean
//        JwtEncoder jwtEncoder(@Value("classpath:authz.pub") RSAPublicKey pub,
//                              @Value("classpath:authz.pem") RSAPrivateKey pem) {
//            RSAKey key = new RSAKey.Builder(pub).privateKey(pem).build();
//            return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
//        }
//
//        @Bean
//        JwtDecoder jwtDecoder(@Value("classpath:authz.pub") RSAPublicKey pub) {
//            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(pub).build();
//            OAuth2TokenValidator<Jwt> defaults = JwtValidators.createDefaultWithIssuer("http://localhost:9000");
//            OAuth2TokenValidator<Jwt> audience = new JwtClaimValidator<List<Object>>(JwtClaimNames.AUD,
//                    (aud) -> !Collections.disjoint(aud, Collections.singleton("cashcard-client")));
//            jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaults, audience));
//            return jwtDecoder;
//        }
//    }
}
