package fr.gouv.service.provider.stub.api;

import static fr.gouv.service.provider.stub.util.ConfigUtil.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fr.gouv.service.provider.stub.util.ConfigUtil;

/**
 * Sample logout stub.
 */
public class Logout extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String logoutPath = CONF.get(PROVIDER_LOGOUT);
        final String token = CONF.get(PROVIDER_TOKEN);

        resp.sendRedirect(ConfigUtil.getOpenIdConnectProviderUrlBase()
                + logoutPath
                + "?id_token_hint="
                + token
                + "&post_logout_redirect_uri="
                + ConfigUtil.getServiceProviderBaseUrl(req) + req.getContextPath() + "/logout.jsp");
    }
}
