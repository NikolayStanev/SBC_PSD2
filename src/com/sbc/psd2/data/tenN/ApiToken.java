
package com.sbc.psd2.data.tenN;

public class ApiToken {

    private String access_token;

    private Long expires_in;

    private String scope;

    private String token_type;

    public ApiToken () {

    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String accessToken) {
        this.access_token = accessToken;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expiresIn) {
        this.expires_in = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String tokenType) {
        this.token_type = tokenType;
    }

}
