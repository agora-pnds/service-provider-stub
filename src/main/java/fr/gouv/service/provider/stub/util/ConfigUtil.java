package fr.gouv.service.provider.stub.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.gouv.service.provider.stub.exceptions.InvalidConfigurationException;

/**
 * Created by tchabaud on 06/01/2017.
 * Configuration singleton.
 */

public enum ConfigUtil {
    CONF;

    // Configuration props names
    public static final String CLIENT_SECRET = "stub.oidc.clientsecret";
    public static final String CLIENT_ID = "stub.oidc.clientid";

    public static final String PROVIDER_PROTOCOL = "stub.oidc.provider.protocol";
    public static final String PROVIDER_HOST = "stub.oidc.provider.hostname";
    public static final String PROVIDER_PORT = "stub.oidc.provider.port";
    public static final String PROVIDER_AUTH = "stub.oidc.authorize.endpoint";
    public static final String PROVIDER_TOKEN = "stub.oidc.token.endpoint";
    public static final String PROVIDER_USERINFO = "stub.oidc.userinfo.endpoint";
    public static final String PROVIDER_LOGOUT = "stub.oidc.logout.endpoint";
    public static final String PROVIDER_SCOPES = "stub.oidc.scopes";

    public static final String USER_TOKEN = "FS_TOKEN";
    public static final String USER_STATE = "FS_STATE";
    public static final String REDIRECT_URL = "stub.client.redirect.url";
    private static final String CONF_PROPERTY = "/config.properties";
    public static String USER_NONCE = "FS_NONCE";
    private final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

    /**
     * Configuration holder.
     */
    private final Properties config;

    ConfigUtil() {
        config = loadConfiguration();
    }

    public static String getServiceProviderBaseUrl(final HttpServletRequest req) {
        final String protocol = req.getScheme();
        final String hostname = req.getRemoteHost();
        final int port = req.getServerPort();
        return protocol + "://" + hostname + ':' + port;
    }

    public static String getOpenIdConnectProviderUrlBase() {
        final String protocol = CONF.get(PROVIDER_PROTOCOL);
        final String hostname = CONF.get(PROVIDER_HOST);
        final String port = CONF.get(PROVIDER_PORT);
        return protocol + "://" + hostname + ':' + port;
    }

    private Properties loadConfiguration() {
        final Properties cfg = new Properties();
        final InputStream inputStream = getClass().getResourceAsStream(CONF_PROPERTY);
        try {
            cfg.load(inputStream);
            logger.info("Loading configuration from {} file ...", CONF_PROPERTY);
            for (String property : cfg.stringPropertyNames()) {
                final String value = cfg.getProperty(property);
                logger.info("{}={}", property, value);
            }
            logger.info("Configuration loaded");
        } catch (NullPointerException | IOException e) {
            throw new InvalidConfigurationException("Unable to find configuration in classpath /"
                    + CONF_PROPERTY, e);
        }
        return cfg;
    }

    /**
     * @param pKey : property key
     * @return the property value
     */
    public String get(String pKey) {
        return config.getProperty(pKey);
    }

}
