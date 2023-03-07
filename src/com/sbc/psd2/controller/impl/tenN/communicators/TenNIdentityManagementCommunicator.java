package com.sbc.psd2.controller.impl.tenN.communicators;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;

import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.controller.CoreSystemCommunicator;
import com.sbc.psd2.controller.IdentityManagementCommunicator;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.tenN.ApiToken;
import com.sbc.psd2.data.tenN.TenNUserInfo;
import com.sbc.psd2.rest.util.HttpClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class TenNIdentityManagementCommunicator implements IdentityManagementCommunicator {

    public static String apiToken;
    public static String tokenType;

    @Override
    public UserInfo getUserByToken(String token, String eIDASThumbprint) throws ApplicationException{
        LogManager.trace(TenNIdentityManagementCommunicator.class, "getUserByToken()", token);

        try {
            URL getTokenURL = new URL("https://crm-api-test.10npay.com/api/v1/Individual/");

            HashMap<String, String> pathParams = new HashMap<>();
            pathParams.put("id", token);

            HashMap<String, String> headers = new HashMap<>();

            HttpClient httpClient = new HttpClient(getTokenURL,"application/json",null,pathParams,headers,null);

            TenNUserInfo tenNUserInfo = httpClient.doGet(TenNUserInfo.class);

            //TODO: Finish Implementation when TenN provide the required API
            UserInfo userInfo = new UserInfo();


            return userInfo;

        }catch (Exception e) {
            LogManager.log(TenNIdentityManagementCommunicator.class, e);

            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, e.getMessage());
        }
    }

    public void getApiToken () throws MalformedURLException {
        LogManager.trace(TenNIdentityManagementCommunicator.class, "getApiToken()");
        String identityManagementCommunicatorEndpoint = AbstractCommunicatorFactory.getInstance().getIdentityManagementCommunicatorEndPoint();

        //TODO: Make it easy to change.
        String requestBody = "client_id=Sirma.OpenBanking&grant_type=client_credentials&scope=Tenn.CRM%20IdentityServerApi&IdentityServer:MTLS:X509Certificate=MIIGWTCCBUECCQDabrs2xRPBTTANBgkqhkiG9w0BAQsFADBlMQswCQYDVQQGEwJCRzEOMAwGA1UECAwFU29maWExGjAYBgNVBAoMEUFsYXJpYyBTZWN1cml0aWVzMRUwEwYDVQQLDAwxMG4gUGF5bWVudHMxEzARBgNVBAMMCjEwbnBheS5jb20wIBcNMjIxMjA1MTU0ODA3WhgPMjA1MDA0MjExNTQ4MDdaMHYxCzAJBgNVBAYTAkJHMQ4wDAYDVQQIDAVTb2ZpYTEOMAwGA1UEBwwFU29maWExGjAYBgNVBAoMEUFsYXJpYyBTZWN1cml0aWVzMRUwEwYDVQQLDAwxMG4gUGF5bWVudHMxFDASBgNVBAMMC3Npcm1hLmxvY2FsMIIEIjANBgkqhkiG9w0BAQEFAAOCBA8AMIIECgKCBAEAql%2B0v3NbcQysxcIUDbynBBSCExFQ5mn5qMQPMrpIrOBMCKjJhVKM%2FCL6PnUDqLVlX6jeYWKPceRxx0EcwVrPTGWhN9MWibb%2FcsRssUDysMbiRUPP6dY7RiD5ofX%2B5g%2BV0Bjfcx1LxA2m8uXeUcmCt82hL4q75k1OzzuBpEtM3wqAIjUucB5DeTGlzsuX4QKq%2FmfXHAbW%2FOtdr0NwC90EHvzAN%2Bty7yFpcI%2Fc5qSvJTUVSuH2KAuVESBO7t2IgzzPdYR4h7%2Fk7Zx0dcfVK2NZU6jiFZPt2%2FDqIvALXXxl6zGLaSnPppCdl7nY%2Fh5OH%2FgIXVV3OgM5cOc9qfXyzEhNcdgcNkrAqeP5LDdWBKdliJabunv0Z9JjYpnaoieGInFQGVqsp1Do2kP3zott6MnEyVBIPPexrV6lkzzZrCVAxf3%2BttIYMSikh2PcNA2f%2B4WPK2jw6Mb08nB9DgrmXDJVIBE%2BJbkmNfVhqdaBasag8Es9geFVYwpSuGBNK9DFg1iuQN95%2BWBHnWEwvwr3MAfJz2BUFqqx6LvgXWQ1pJm5b%2B2rIdc49G2RV%2F80m3J0OXkKAapzf%2B%2BOpP4rtroFK7DO1vbhg%2FjKDeTHSS221KEKXJPur6XtSW0mPL8oKEtIr4z8tmUAmUzfqPa7YkSlGEw%2BuKsli%2FCDaw26jkyG1psokSloVmH30OGVedQ0SvxzH4y5Xap282xLnEAE3ZDQODbpWA4BWv4Zumg6PlxQ7gQELDEW5Bhufc3PDxVWSH%2FoaPEkLhmkehnL1eHrewlheGKh%2Bp1wNK%2FhR86CkyHuw8ePar9EZaSkw0WNh1rAGtzNwH0x4sHai%2Bqf7Yx8bpa0n%2FSZkGwjOc2uvDjs%2FWj5lgwxIC4moEfE5bGxVw0F47RQFsyPOD%2BAd3jAM1WuJ2%2FZLhxecAinL1DWcv7jUnz%2Bbw5V5uo4MNmT2lzevlJuSvzx83iveXchiR95%2FZbMKFEUMJJKvP8Fo2qQot1k%2FtSC3Ru8WR72WetwSviVkOPUd%2BSmMer%2FY0cuSn5raY5xQQ9A2L1do0zAMWxs4O%2FzHsEa2eSHJmb2HmyYDWPS1fNbmayy%2BLZ6X8uieWDKlXN1pHuQMZtpm%2FVppKBNAO0w1eo%2BnydnJ%2Fn4EAOzAUaED30weTkIba%2B1tYpqCmnGnTiPu5AtzUIp%2FpZYxfJiWTBsoWfsxiSoebPidsSFYnpE8dXvz8r0QpxlSKkEbBpNZrJ7TnLDm3rquzx1vUP7DWxlacU2AJ1wCIvk5WcoCwFFDkJahRdqrO8lEvBTmdfzERombUu28t%2FIUStr%2F%2BPPs6oFLrueqK3e%2Bh%2BZ%2Fr4oSk0wxFYkHXVWEDLE8SO7TV6AAgRFQwKhCkuNlwIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQA78xj%2FeKoUWPh4vKa%2FcxAaagQnRbqTFxDLiQ67G4uQubIEfqjlpgZ9dva61NFAl45tomRfo597Jxmym%2FHqKxV2f%2BibzEcERjqDJw9%2BX5WCYb2Rbo8zRVp4aKllLthB01gtKP5kj4a4aZBoz5fDheRMLC4R40KQD9mhQtI95dXpI1qiE%2BkSzqD80Df6Zxv9FllS%2BAK%2ByqvT868K9IPuC7UfJ423pN1i0aQAYfXbc3fwtLbZNcdE3WF6JcMiUi1T5D%2BhspLGm3vkvSTb9BhuiW5R0s%2BkdGurcjYocxcY3jmrupxbaSwF2mM6VcHtSP4nrHczddPwxNSf2mGDfvSwY8kV";
        URL tokenURL = new URL (identityManagementCommunicatorEndpoint + "/mtls/token");

        HttpClient httpClient = new HttpClient(tokenURL, "application/x-www-form-urlencoded", requestBody);

        ApiToken token = httpClient.doPost(ApiToken.class);
        apiToken = token.getAccess_token();
        tokenType = token.getToken_type();

    }

    public static void main (String[] args) {

        TenNIdentityManagementCommunicator communicator = new TenNIdentityManagementCommunicator();
        try {
            communicator.getApiToken();
            CoreSystemCommunicator coreSystemCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();

            ArrayList<CoreSystemAccountInfo> accountInfos = coreSystemCommunicator.getAccounts(new UserInfo("R01001221", "user@gmail.com", "username", "1"));

            CoreSystemAccountInfo coreSystemAccountInfo = accountInfos.get(0);

            CoreSystemAccountInfo coreSystemAccountInfo2 = coreSystemCommunicator.getAccountDetails(coreSystemAccountInfo.getIban());

            coreSystemCommunicator.getAccountBalances(coreSystemAccountInfo2.getIban());


        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(apiToken);
    }
}
