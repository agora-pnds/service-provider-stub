package fr.gouv.service.provider.stub.util;

/**
 * Bean permettant de gérer les identifiants communiqués par le provider OpenIdConnect.
 */
public class SessionToken {

    private String accessToken = null;
    private String idToken = null;


    /**
     * @return l'identifiant du jeton de session.
     */
    public String getIdToken() {
        return idToken;
    }

    /**
     * @param idToken the idToken to set
     */
    public void setIdToken(final String idToken) {
        this.idToken = idToken;
    }

    /**
     * @return the accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken the accessToken to set
     */
    public void setAccessToken(final String accessToken) {
        this.accessToken = accessToken;
    }

}
