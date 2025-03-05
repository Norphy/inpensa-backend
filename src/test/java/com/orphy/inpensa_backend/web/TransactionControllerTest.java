package com.orphy.inpensa_backend.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.orphy.inpensa_backend.v1.config.SecurityConfiguration;
import com.orphy.inpensa_backend.v1.model.Role;
import com.orphy.inpensa_backend.v1.model.Transaction;
import com.orphy.inpensa_backend.v1.model.TransactionType;
import com.orphy.inpensa_backend.v1.model.dto.TransactionDto;
import com.orphy.inpensa_backend.v1.service.TransactionService;
import com.orphy.inpensa_backend.v1.web.controller.TransactionController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionController.class)
@ActiveProfiles(value = "test")
class TransactionControllerTest {

    private final String DEFAULT_UNAUTHORIZED_HEADER_VALUE = "The request requires higher privileges than provided by the access token.";
    private final String DEFAULT_TRANSACTION_ID = "6b246024-59b1-4716-b583-9a0c4d0e5195";
    private static final String EXPECTED_DEFAULT_SUB = "sub|1234abc";
    private static final String EXPECTED_ISSUER_URI = "https://random-issuer.com";
    protected static final String EXPECTED_AUD = "audience";
    private final String BASE_URL = "/transactions";

    @MockitoBean
    TransactionService transactionService;

    @Autowired
    @SuppressWarnings("UnusedDeclaration")
    private MockMvc mockMvc;

    @Autowired
    @SuppressWarnings("UnusedDeclaration")
    private ObjectMapper objectMapper;

    @Autowired
    JwtEncoder jwtEncoder;

    @Test
    void getAllTransactions_HappyPath() throws Exception {
        final List<Transaction> expectedList = List.of();
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.ADMIN_READ.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.getAllTransactions())
                .willReturn(expectedList);
        mockMvc.perform(get(BASE_URL + "/admin/all")
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAllTransactions_Unauthorized_UnHappyPath() throws Exception {
        final List<Transaction> expectedList = List.of();
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.USER_READ.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.getAllTransactions())
                .willReturn(expectedList);
        mockMvc.perform(get(BASE_URL + "/admin/all")
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void getAllTransactions_InvalidIssuer_UnHappyPath() throws Exception {
        final String invalidIssuer = "http://random.com";
        final List<Transaction> expectedList = List.of();
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.ADMIN_READ.getRoleValue());
            consumer.issuer(invalidIssuer);
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.getAllTransactions())
                .willReturn(expectedList);
        mockMvc.perform(get(BASE_URL + "/admin/all")
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", containsString("An error occurred while attempting to decode the Jwt: The iss claim is not valid")));
    }

    @Test
    void getAllTransactions_InvalidAud_UnHappyPath() throws Exception {
        final String invalidAud = "random-aud";
        final List<Transaction> expectedList = List.of();
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.ADMIN_READ.getRoleValue());
            consumer.audience(List.of(invalidAud));
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.getAllTransactions())
                .willReturn(expectedList);
        mockMvc.perform(get(BASE_URL + "/admin/all")
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", containsString("An error occurred while attempting to decode the Jwt: The aud claim is not valid")));
    }

    @Test
    void getAllTransactions_ExpiredJwtToken_UnHappyPath() throws Exception {
        final Instant issuedAt = Instant.now().minusSeconds(2);
        final Instant expiresAt = Instant.now().minusSeconds(1);
        final List<Transaction> expectedList = List.of();
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.ADMIN_READ.getRoleValue());
            consumer.issuedAt(issuedAt);
            consumer.expiresAt(expiresAt);
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.getAllTransactions())
                .willReturn(expectedList);
        mockMvc.perform(get(BASE_URL + "/admin/all")
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", containsString("An error occurred while attempting to decode the Jwt: The exp claim is not valid")));
    }

    @Test
    void getAllTransactionsByUser_HappyPath() throws  Exception{

        final List<Transaction> expectedList = List.of(getDefaultTransaction());
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.ADMIN_READ.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.getAllTransactionsByUser(EXPECTED_DEFAULT_SUB))
                .willReturn(expectedList);
        mockMvc.perform(get(BASE_URL + "/admin/user/{userId}", EXPECTED_DEFAULT_SUB)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(DEFAULT_TRANSACTION_ID));
    }

    @Test
    void getAllTransactionsByUser_Unauthorized_UnHappyPath() throws  Exception{

        final List<Transaction> expectedList = List.of(getDefaultTransaction());
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.USER_READ.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.getAllTransactionsByUser(EXPECTED_DEFAULT_SUB))
                .willReturn(expectedList);
        mockMvc.perform(get(BASE_URL + "/admin/user/{userId}", EXPECTED_DEFAULT_SUB)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void getAllTransactionsCurrentUser_HappyPath() throws Exception{

        final List<Transaction> expectedList = List.of(getDefaultTransaction());
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.USER_READ.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.getAllTransactionsByUser(EXPECTED_DEFAULT_SUB))
                .willReturn(expectedList);
        mockMvc.perform(get(BASE_URL)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(DEFAULT_TRANSACTION_ID));
    }

    @Test
    void getAllTransactionsCurrentUser_Unauthorized_UnHappyPath() throws Exception{

        final List<Transaction> expectedList = List.of(getDefaultTransaction());
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.USER_WRITE.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.getAllTransactionsByUser(EXPECTED_DEFAULT_SUB))
                .willReturn(expectedList);
        mockMvc.perform(get(BASE_URL)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void getTransactionById_HappyPath() throws Exception {

        final Transaction expectedTransaction = getDefaultTransaction();
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.USER_READ.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.getTransactionById(UUID.fromString(DEFAULT_TRANSACTION_ID)))
                .willReturn(expectedTransaction);
        mockMvc.perform(get(BASE_URL + "/{transactionId}", DEFAULT_TRANSACTION_ID)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(DEFAULT_TRANSACTION_ID));
    }

    @Test
    void getTransactionById_Unauthorized_UnHappyPath() throws Exception {

        final Transaction expectedTransaction = getDefaultTransaction();
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.USER_WRITE.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.getTransactionById(UUID.fromString(DEFAULT_TRANSACTION_ID)))
                .willReturn(expectedTransaction);
        mockMvc.perform(get(BASE_URL + "/{transactionId}", DEFAULT_TRANSACTION_ID)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void saveTransactionWithCurrentUser_HappyPath() throws Exception {

        final TransactionDto expectedTransactionDto = getDefaultTransactionDto();
        final String transactionDtoJsonStr = objectMapper.writeValueAsString(expectedTransactionDto);
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.USER_WRITE.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.saveTransaction(expectedTransactionDto))
                .willReturn(UUID.fromString(DEFAULT_TRANSACTION_ID));
        mockMvc.perform(post(BASE_URL)
                        .content(transactionDtoJsonStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(BASE_URL + "/" + DEFAULT_TRANSACTION_ID)))
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void saveTransactionWithCurrentUser_Unauthorized_UnHappyPath() throws Exception {

        final TransactionDto expectedTransactionDto = getDefaultTransactionDto();
        final String transactionDtoJsonStr = objectMapper.writeValueAsString(expectedTransactionDto);
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.USER_READ.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.saveTransaction(expectedTransactionDto))
                .willReturn(UUID.fromString(DEFAULT_TRANSACTION_ID));
        mockMvc.perform(post(BASE_URL)
                        .content(transactionDtoJsonStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(header().string("WWW-Authenticate", containsString(DEFAULT_UNAUTHORIZED_HEADER_VALUE)))
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void testSaveTransactionForUser_Admin_HappyPath() throws Exception {

        final TransactionDto expectedTransactionDto = getDefaultTransactionDto();
        final String transactionDtoJsonStr = objectMapper.writeValueAsString(expectedTransactionDto);
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.ADMIN_WRITE.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.saveTransaction(expectedTransactionDto, EXPECTED_DEFAULT_SUB))
                .willReturn(UUID.fromString(DEFAULT_TRANSACTION_ID));
        mockMvc.perform(post(BASE_URL + "/admin/user/{userId}", EXPECTED_DEFAULT_SUB)
                        .content(transactionDtoJsonStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString(BASE_URL + "/" + DEFAULT_TRANSACTION_ID)))
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void testSaveTransactionForUser_UnAuthorized_UnHappyPath() throws Exception {

        final TransactionDto expectedTransactionDto = getDefaultTransactionDto();
        final String transactionDtoJsonStr = objectMapper.writeValueAsString(expectedTransactionDto);
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.ADMIN_READ.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.saveTransaction(expectedTransactionDto, EXPECTED_DEFAULT_SUB))
                .willReturn(UUID.fromString(DEFAULT_TRANSACTION_ID));
        mockMvc.perform(post(BASE_URL + "/admin/user/{userId}", EXPECTED_DEFAULT_SUB)
                        .content(transactionDtoJsonStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(header().string("WWW-Authenticate", containsString(DEFAULT_UNAUTHORIZED_HEADER_VALUE)))
                .andExpect(jsonPath("$").doesNotExist());
    }


    @Test
    void testSaveTransactionForUser_UnAuthorized_Two_UnHappyPath() throws Exception {

        final TransactionDto expectedTransactionDto = getDefaultTransactionDto();
        final String transactionDtoJsonStr = objectMapper.writeValueAsString(expectedTransactionDto);
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.USER_WRITE.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.saveTransaction(expectedTransactionDto, EXPECTED_DEFAULT_SUB))
                .willReturn(UUID.fromString(DEFAULT_TRANSACTION_ID));
        mockMvc.perform(post(BASE_URL + "/admin/user/{userId}", EXPECTED_DEFAULT_SUB)
                        .content(transactionDtoJsonStr)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(header().string("WWW-Authenticate", containsString(DEFAULT_UNAUTHORIZED_HEADER_VALUE)))
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deleteTransaction_HappyPath() throws Exception {
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.USER_WRITE.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.deleteTransaction(UUID.fromString(DEFAULT_TRANSACTION_ID)))
                .willReturn(EXPECTED_DEFAULT_SUB);
        mockMvc.perform(delete(BASE_URL + "/{transactionId}", DEFAULT_TRANSACTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    void deleteTransaction_UnAuthorized_UnHappyPath() throws Exception {
        final String token = mint(consumer -> {
            consumer.claim("scp", Role.USER_READ.getRoleValue());
        });
        final String jwtToken = "Bearer " + token;
        given(transactionService.deleteTransaction(UUID.fromString(DEFAULT_TRANSACTION_ID)))
                .willReturn(EXPECTED_DEFAULT_SUB);
        mockMvc.perform(delete(BASE_URL + "/{transactionId}", DEFAULT_TRANSACTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
                        .header("Authorization", jwtToken))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(header().string("WWW-Authenticate", containsString(DEFAULT_UNAUTHORIZED_HEADER_VALUE)))
                .andExpect(jsonPath("$").doesNotExist());
    }

    private String mint(Consumer<JwtClaimsSet.Builder> consumer) {
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(100000))
                .subject(EXPECTED_DEFAULT_SUB)
                .issuer(EXPECTED_ISSUER_URI)
                .audience(List.of(EXPECTED_AUD))
                .claim("scp", Collections.singletonList(Role.USER_READ.getScope()));
        consumer.accept(builder);
        JwtEncoderParameters parameters = JwtEncoderParameters.from(builder.build());
        return this.jwtEncoder.encode(parameters).getTokenValue();
    }

    @TestConfiguration
    @Import(SecurityConfiguration.class)
    @SuppressWarnings("UnusedDeclaration")
    static class TestJwtConfiguration {
        @Bean
        @SuppressWarnings("UnusedDeclaration")
        JwtEncoder jwtEncoder(@Value("classpath:web/public_key.pem") RSAPublicKey pub,
                              @Value("classpath:web/private_key.pem") RSAPrivateKey pem) {
            RSAKey key = new RSAKey.Builder(pub).privateKey(pem).build();
            return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(key)));
        }

        @Bean
        @SuppressWarnings("UnusedDeclaration")
        JwtDecoder jwtDecoder(@Value("classpath:web/public_key.pem") RSAPublicKey pub) {
            NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(pub).build();
            OAuth2TokenValidator<Jwt> defaults = JwtValidators.createDefaultWithIssuer(EXPECTED_ISSUER_URI);
            OAuth2TokenValidator<Jwt> audience = new JwtClaimValidator<List<Object>>(JwtClaimNames.AUD,
                    (aud) -> !Collections.disjoint(aud, Collections.singleton(EXPECTED_AUD)));
            OAuth2TokenValidator<Jwt> expiresAt = new JwtClaimValidator<Instant>(JwtClaimNames.EXP,
                    (exp) -> exp.isAfter(Instant.now()));
            jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(defaults, audience, expiresAt));
            return jwtDecoder;
        }
    }

    private TransactionDto getDefaultTransactionDto() {
        return new TransactionDto( 1736154492435L, "Description", 56, TransactionType.EXPENSE, "Tag",
                UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5192"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5193"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5194"));
    }

    private Transaction getDefaultTransaction() {
        return new Transaction(UUID.fromString(DEFAULT_TRANSACTION_ID), 1736154492435L, 1736154492435L, "Description", 56, TransactionType.EXPENSE, "Tag",
                UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5192"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5193"), UUID.fromString("6b246024-59b1-4716-b583-9a0c4d0e5194"), "auth|1234567");
    }
}