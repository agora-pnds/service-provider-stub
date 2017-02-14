package fr.gouv.service.provider.stub.api;

import static fr.gouv.service.provider.stub.util.ConfigUtil.*;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;

import fr.gouv.service.provider.stub.util.ConfigUtil;

/**
 * Sample Login stub.
 */
public class Login extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String redirectPath = req.getContextPath() + CONF.get(REDIRECT_URL);
        final String authPath = CONF.get(PROVIDER_AUTH);
        final String scopes = URLEncoder.encode(CONF.get(PROVIDER_SCOPES), UTF_8.displayName());
        final String nonce = RandomStringUtils.randomAlphabetic(14);
        final String state = RandomStringUtils.randomAlphabetic(32);
        req.getSession().setAttribute(ConfigUtil.USER_STATE, state);
        final String clientId = CONF.get(CLIENT_ID);

        String redirectUrl = ConfigUtil.getOpenIdConnectProviderUrlBase() + authPath +
                '?' +
                "scope" + "=" + scopes + '&' +
                "response_type" + "=" + "code" + '&' +
                "nonce" + "=" + nonce + '&' +
                "redirect_uri" + "=" + ConfigUtil.getServiceProviderBaseUrl(req) +
                redirectPath + '&' +
                "client_id" + '=' + clientId + '&' +
                "state" + '=' + state;

        resp.sendRedirect(redirectUrl);
    }
}
