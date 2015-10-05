package com.saspes.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import static com.saspes.rest.TVCalendarUrl.URL;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@Path("/authentication")
public class AuthenticationService {

    private boolean login = false;
    private CloseableHttpClient httpClient = null;
    private String userName = null;

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @POST
    @Path("/login")
    public @ResponseBody
    String doLogin(@RequestBody String profile) throws IOException, URISyntaxException {
        if (isLogin()) {
            return Utils.messageJosn(Messages.INFO, "already login " + userName);
        }
        
        JsonElement jelement = new JsonParser().parse(profile);
        JsonObject jobject = jelement.getAsJsonObject();

        String username;
        try {
            username = jobject.get("username").getAsString();
            setUserName(username);
        } catch (Exception e) {
            return Utils.messageJosn(Messages.ERROR, "no username");
        }

        String password;
        try {
            password = jobject.get("password").getAsString();
        } catch (Exception e) {
            return Utils.messageJosn(Messages.ERROR, "no password");
        }

        String sub_login = "Account+Login";

        CookieStore cookieStore = new BasicCookieStore();
        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).setDefaultCookieStore(cookieStore).build();

        // POST URL
        HttpUriRequest loginPost = RequestBuilder.post()
                .setUri(new URI(URL))
                .addParameter("username", username)
                .addParameter("password", password)
                .addParameter("sub_login", sub_login)
                .build();
        CloseableHttpResponse response = httpClient.execute(loginPost);
        System.out.println("[ " + response.getStatusLine() + "] " + URL);
        try {
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("CAT_UID")) {
                setLogin(true);
                break;
            }
        }

        if (isLogin()) {
            return Utils.messageJosn(Messages.INFO, "login OK");
        } else {
            return Utils.messageJosn(Messages.ERROR, "login");
        }

    }

    @GET
    @Path("/logout")
    public @ResponseBody
    String doLogout(@RequestBody String profile) throws IOException, URISyntaxException {
        if (!isLogin()) {
            return Utils.messageJosn(Messages.INFO, "already logout");
        }
        
        try {
            httpClient.close();
            setLogin(false);
            setUserName(null);
            return Utils.messageJosn(Messages.INFO, "logout");
        } catch (Exception e) {
            return Utils.messageJosn(Messages.ERROR, "logout");
        }
    }
}
