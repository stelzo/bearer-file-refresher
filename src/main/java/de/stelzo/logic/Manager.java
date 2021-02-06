package de.stelzo.logic;

import de.stelzo.client.KeycloakClient;
import de.stelzo.dto.KeycloakAuthenticateResponse;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Form;
import java.time.LocalDateTime;
import java.util.Base64;

@ApplicationScoped
public class Manager {
    private static final Logger log = Logger.getLogger(Manager.class.getName());

    private String refreshToken;
    private LocalDateTime refreshExpire;
    private LocalDateTime accessExpire;

    public Manager() {
        this.refreshExpire = LocalDateTime.now().minusDays(1);
        this.accessExpire = LocalDateTime.now().minusDays(1);
    }

    @Inject
    @RestClient
    KeycloakClient keycloakClient;

    @ConfigProperty(name = "keycloak.client.id")
    String clientId;

    @ConfigProperty(name = "keycloak.client.secret")
    String clientSecret;

    @ConfigProperty(name = "keycloak.user.username")
    String username;

    @ConfigProperty(name = "keycloak.user.password")
    String password;

    @Scheduled(every = "{refresh.interval}")
    public void refreshToken() {
        log.info(System.currentTimeMillis() + " - " + Manager.class.getName() + ": token refresh method @start");

        LocalDateTime now = LocalDateTime.now();

        // check if access can still be used
        if (accessExpire.isAfter(now)) {
            // not expired, no update
            log.info(System.currentTimeMillis() + " - " + Manager.class.getName() + ": access token not expired @end");
            return;
        }
        // access token expired

        log.info(System.currentTimeMillis() + " - " + Manager.class.getName() + ": access token expired, using refresh token.");

        Form request;
        // check if refresh token can be used
        if (refreshExpire.isAfter(now)) {
            // refresh not expired, use refresh token for getting new access token
            request = new Form()
                    .param("grant_type", "refresh_token")
                    .param("client_id", clientId)
                    .param("refresh_token", refreshToken);
        } else {
            // refresh token expired, make new session
            request = new Form()
                    .param("grant_type", "password")
                    .param("username", username)
                    .param("password", password);

        }

        KeycloakAuthenticateResponse token = keycloakClient.token(request, "Basic " + new String(Base64.getEncoder().encode(String.format("%s:%s", clientId, clientSecret).getBytes())));
        log.trace(System.currentTimeMillis() + " - " + Manager.class.getName() + ": got response from keycloak, saving tokens...");

        this.refreshExpire = now.plusSeconds(token.refresh_expires_in);
        this.accessExpire = now.plusSeconds(token.expires_in);

        this.refreshToken = token.refresh_token;
        FileRefresher.updateRefreshFile(token.access_token);

        log.info(System.currentTimeMillis() + " - " + Manager.class.getName() + ": token file refreshed @end");
    }


}
