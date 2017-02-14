package fr.gouv.service.provider.stub.api;

import static fr.gouv.service.provider.stub.util.ConfigUtil.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.gouv.service.provider.stub.util.ConfigUtil;

/**
 * Servlet that stub a callback URL after successful auth on OpenIdConnectProvider
 */
public class CallbackStub extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CallbackStub.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            final OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(req);

            if (logger.isDebugEnabled()) {
                logger.debug("authorization code" + oar.getCode());
            }

            // Check state
            final String currentState = req.getParameter("state");
            final String savedState = (String) req.getSession().getAttribute(ConfigUtil.USER_STATE);

            if (null == savedState || !savedState.equals(currentState)) {
                throw new ServletException("State has changed !");
            }

            // get access token
            final String tokenEndpoint = CONF.get(PROVIDER_TOKEN);
            String tokenUrl = ConfigUtil.getOpenIdConnectProviderUrlBase() + tokenEndpoint;

            final OAuthClientRequest authClientRequest = OAuthClientRequest
                    .tokenLocation(tokenUrl)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(CONF.get(CLIENT_ID))
                    .setClientSecret(CONF.get(CLIENT_SECRET))
                    .setRedirectURI(req.getServletPath() + CONF.get(REDIRECT_URL))
                    .setCode(oar.getCode())
                    .buildBodyMessage();

            final OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            final OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(authClientRequest);

            if (logger.isDebugEnabled()) {
                logger.debug("getAccessToken - tokenRequest=" + authClientRequest.getLocationUri());
                logger.debug("getAccessToken - response scope=" + oAuthResponse.getScope());
                logger.debug("getAccessToken - response expires=" + oAuthResponse.getExpiresIn());
                logger.debug("getAccessToken - response body=" + oAuthResponse.getBody());
            }

            final String result = oAuthResponse.getBody();
            req.getSession().setAttribute(ConfigUtil.USER_TOKEN, result);
            resp.sendRedirect(req.getContextPath() + "/info");
        } catch (final OAuthSystemException e) {
            throw new ServletException("Error during request for accessToken : ", e);
        } catch (final OAuthProblemException e) {
            throw new ServletException("Error during accessToken retrieving : ", e);
        }
    }
}
