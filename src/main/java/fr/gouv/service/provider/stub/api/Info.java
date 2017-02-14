package fr.gouv.service.provider.stub.api;

import static fr.gouv.service.provider.stub.util.ConfigUtil.CONF;
import static fr.gouv.service.provider.stub.util.ConfigUtil.PROVIDER_USERINFO;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.gouv.service.provider.stub.util.ConfigUtil;
import fr.gouv.service.provider.stub.util.SessionToken;

/**
 * Display user info fetched from OpenIdConnect Provider.
 */
public class Info extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(Info.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String token = (String) request.getSession().getAttribute(ConfigUtil.USER_TOKEN);

        // Fetch user info
        final String userInfo = getUserInfo(token);

        String output = ("<!doctype html>\n" + "\n") +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "\n" +
                "        <title>France Connect User Info page</title>\n" +
                "        <meta name=\"description\" content=\"User information\">\n" +
                "        <meta name=\"author\" content=\"PNDS\">\n" +
                "\n" +
                "    </head>\n" +
                "\n" +
                "    <body>\n" +
                "        <h1>Token info</h1>\n" +
                "        <pre>" +
                token +
                "</pre>\n" +
                "        <h1>User info</h1>\n" +
                "        <pre>" +
                userInfo +
                "</pre>\n" +
                "    </body>\n" +
                "</html>";

        response.getOutputStream().write(output.getBytes(UTF_8.displayName()));
    }

    private String getUserInfo(final String token) {
        final String info;

        if (StringUtils.isBlank(token)) {
            throw new RuntimeException("Empty token data !");
        }
        final Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        final SessionToken userToken = gson.fromJson(token, SessionToken.class);

        final OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        final OAuthClientRequest bearerClientRequest;
        try {
            final String path = CONF.get(PROVIDER_USERINFO);
            final String url = ConfigUtil.getOpenIdConnectProviderUrlBase() + path;
            bearerClientRequest = new OAuthBearerClientRequest(url)
                    .setAccessToken(userToken.getAccessToken())
                    .buildHeaderMessage();

            if (logger.isDebugEnabled()) {
                logger.debug("getUserInfo - request=" + bearerClientRequest.getLocationUri());
            }

            final OAuthResourceResponse resourceResponse = oAuthClient.resource(bearerClientRequest,
                    OAuth.HttpMethod.GET,
                    OAuthResourceResponse.class);
            if (logger.isDebugEnabled()) {
                logger.debug("getUserInfo - response=" + resourceResponse.getBody());
            }

            if (resourceResponse.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException(resourceResponse.getBody());
            }

            info = resourceResponse.getBody();
        } catch (final OAuthSystemException e) {
            throw new RuntimeException("Error during userInfo request building : ", e);
        } catch (final OAuthProblemException e) {
            throw new RuntimeException("Error during userInfo retrieving : ", e);
        }

        return info;
    }
}
