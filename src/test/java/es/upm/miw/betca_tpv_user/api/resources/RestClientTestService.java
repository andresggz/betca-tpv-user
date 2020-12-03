package es.upm.miw.betca_tpv_user.api.resources;

import es.upm.miw.betca_tpv_user.api.dtos.TokenDto;
import es.upm.miw.betca_tpv_user.api.http_errors.UnauthorizedException;
import es.upm.miw.betca_tpv_user.data.model.Role;
import es.upm.miw.betca_tpv_user.domain.services.JwtService;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Service
public class RestClientTestService {

    @Value("${server.servlet.contextPath}")
    private String contextPath;

    @Autowired
    private JwtService jwtService;

    private TokenDto tokenDto;

    private boolean isRole(Role role) {
        try {
            return this.tokenDto != null && jwtService.role(tokenDto.getToken()).equals(role.name());
        } catch (UnauthorizedException e) {
            LogManager.getLogger(this.getClass()).error("------- is role -----------");
        }
        return false;
    }

    private WebTestClient login(Role role, String mobile, WebTestClient webTestClient) {
        if (!this.isRole(role)) {
            return login(mobile, "6", webTestClient);
        } else {
            return webTestClient.mutate()
                    .defaultHeader("Authorization", "Bearer " + this.tokenDto.getToken()).build();
        }
    }

    public WebTestClient login(String mobile, String pass, WebTestClient webTestClient) {
        this.tokenDto = webTestClient
                .mutate().filter(basicAuthentication(mobile, pass)).build()
                .post().uri(contextPath + UserResource.USERS + UserResource.TOKEN)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TokenDto.class)
                .returnResult().getResponseBody();
        return webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer " + this.tokenDto.getToken()).build();
    }

    public WebTestClient loginAdmin(WebTestClient webTestClient) {
        return this.login(Role.ADMIN, "666666000", webTestClient);
    }

    public WebTestClient loginManager(WebTestClient webTestClient) {
        return this.login(Role.MANAGER, "666666001", webTestClient);
    }

    public WebTestClient loginOperator(WebTestClient webTestClient) {
        return this.login(Role.OPERATOR, "666666002", webTestClient);
    }

    public WebTestClient loginCustomer(WebTestClient webTestClient) {
        return this.login(Role.OPERATOR, "66", webTestClient);
    }

    public TokenDto getTokenDto() {
        return tokenDto;
    }

}
