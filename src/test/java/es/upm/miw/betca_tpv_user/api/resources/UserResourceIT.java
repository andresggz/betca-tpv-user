package es.upm.miw.betca_tpv_user.api.resources;

import es.upm.miw.betca_tpv_user.api.dtos.UserDto;
import es.upm.miw.betca_tpv_user.data.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static es.upm.miw.betca_tpv_user.api.resources.UserResource.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ApiTestConfig
class UserResourceIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RestClientTestService restClientTestService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void testLogin() {
        this.restClientTestService.loginAdmin(this.webTestClient);
        assertTrue(this.restClientTestService.getTokenDto().getToken().length() > 10);
    }

    @Test
    void testReadUser() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .get().uri(contextPath + USERS + MOBILE_ID,"666666003")
                .exchange().expectStatus().isOk()
                .expectBody(UserDto.class)
                .value(user -> assertEquals("c1", user.getFirstName()));
    }

    @Test
    void testReadUserNotFound() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .get().uri(contextPath + USERS + MOBILE_ID,"999666999")
                .exchange().expectStatus().isNotFound();
    }

    @Test
    void testReadUserForbidden() {
        this.restClientTestService.loginCustomer(this.webTestClient)
                .get().uri(contextPath + USERS + MOBILE_ID,"999666999")
                .exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testCreateUser() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + USERS)
                .body(Mono.just(UserDto.builder().mobile("666000666").firstName("daemon").build()), UserDto.class)
                .exchange().expectStatus().isOk();
    }

    @Test
    void testCreateUserUnauthorized() {
        this.webTestClient
                .post().uri(contextPath + USERS)
                .body(Mono.just(UserDto.builder().mobile("666000666").firstName("daemon").build()), UserDto.class)
                .exchange().expectStatus().isUnauthorized();
    }

    @Test
    void testCreateUserForbidden() {
        this.restClientTestService.loginManager(this.webTestClient)
                .post().uri(contextPath + USERS)
                .body(Mono.just(UserDto.builder().mobile("666000666").firstName("daemon").role(Role.ADMIN).build()),
                        UserDto.class)
                .exchange().expectStatus().isForbidden();
    }

    @Test
    void testCreateUserConflict() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + USERS)
                .body(Mono.just(UserDto.builder().mobile("666666000").firstName("daemon").build()), UserDto.class)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void testCreateFullUser() {
        this.restClientTestService.loginManager(this.webTestClient)
                .post().uri(contextPath + USERS)
                .body(Mono.just(UserDto.builder().mobile("666001666").firstName("daemon").familyName("family")
                        .address("address").password("123").dni("dni").email("email@gmail.com").build()), UserDto.class)
                .exchange().expectStatus().isOk();
    }

    @Test
    void testCreateUserBadNumber() {
        this.restClientTestService.loginOperator(this.webTestClient)
                .post().uri(contextPath + USERS)
                .body(Mono.just(UserDto.builder().mobile("6").firstName("kk").build()), UserDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateUserWithoutNumber() {
        this.restClientTestService.loginAdmin(this.webTestClient)
                .post().uri(contextPath + USERS)
                .body(Mono.just(UserDto.builder().mobile(null).firstName("kk").build()), UserDto.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testReadAllOperator() {
        this.restClientTestService.loginOperator(this.webTestClient)
                .get().uri(contextPath + USERS)
                .exchange().expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .value(users -> assertTrue(users.stream().allMatch(user -> null != user.getActive())))
                .value(users -> assertTrue(users.stream().allMatch(UserDto::getActive)))
                .value(users -> assertTrue(users.stream().noneMatch(user -> "adm".equals(user.getFirstName()))))
                .value(users -> assertTrue(users.stream().noneMatch(user -> "man".equals(user.getFirstName()))))
                .value(users -> assertTrue(users.stream().noneMatch(user -> "ope".equals(user.getFirstName()))))
                .value(users -> assertTrue(users.stream().anyMatch(user -> "c1".equals(user.getFirstName()))));
    }

    @Test
    void testSearch() {
        this.restClientTestService.loginOperator(this.webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(contextPath + USERS + SEARCH)
                        .queryParam("mobile", "6")
                        .queryParam("firstName", "c")
                        .queryParam("dni", "e").build())
                .exchange().expectStatus().isOk()
                .expectBodyList(UserDto.class)
                .value(users -> assertTrue(users.stream().anyMatch(user -> "c1".equals(user.getFirstName()))));
    }

}
