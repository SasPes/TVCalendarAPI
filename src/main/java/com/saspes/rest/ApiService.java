package com.saspes.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import static com.saspes.rest.TVCalendarUrl.URL;
import static com.saspes.rest.TVCalendarUrl.URL_TODAY;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Component
@Path("/api")
public class ApiService {

    private final Auth auth = new Auth();
    private Elements shows;

    @POST
    @Path("/login")
    public @ResponseBody
    String doLogin(@RequestBody String profile) throws IOException, URISyntaxException {
        if (auth.isLogin()) {
            return Utils.messageJosn(MessageTags.INFO, "already login " + auth.getUserName());
        }

        JsonElement jelement;
        JsonObject jobject;
        try {
            jelement = new JsonParser().parse(profile);
            jobject = jelement.getAsJsonObject();
        } catch (Exception e) {
            return Utils.messageJosn(MessageTags.ERROR, "body is not valid json");
        }

        String username;
        try {
            username = jobject.get("username").getAsString();
            auth.setUserName(username);
        } catch (Exception e) {
            System.err.println(e);
            return Utils.messageJosn(MessageTags.ERROR, "no username");
        }

        String password;
        try {
            password = jobject.get("password").getAsString();
        } catch (Exception e) {
            System.err.println(e);
            return Utils.messageJosn(MessageTags.ERROR, "no password");
        }

        String sub_login = "Account+Login";

        // POST URL
        HttpUriRequest loginPost = RequestBuilder.post()
                .setUri(new URI(URL))
                .addParameter("username", username)
                .addParameter("password", password)
                .addParameter("sub_login", sub_login)
                .build();
        CloseableHttpResponse response = auth.getHttpClient().execute(loginPost);
        System.out.println("[ " + response.getStatusLine() + "] " + URL);
        try {
            HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        List<Cookie> cookies = auth.getCookieStore().getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("CAT_UID")) {
                auth.setLogin(true);
                break;
            }
        }

        if (auth.isLogin()) {
            return Utils.messageJosn(MessageTags.INFO, "login OK");
        } else {
            return Utils.messageJosn(MessageTags.ERROR, "login");
        }

    }

    @GET
    @Path("/logout")
    public @ResponseBody
    String doLogout(@RequestBody String profile) throws IOException, URISyntaxException {
        if (!auth.isLogin()) {
            return Utils.messageJosn(MessageTags.INFO, "already logout");
        }

        try {
            auth.getHttpClient().close();
            auth.setLogin(false);
            auth.setUserName(null);
            return Utils.messageJosn(MessageTags.INFO, "logout");
        } catch (Exception e) {
            System.err.println(e);
            return Utils.messageJosn(MessageTags.ERROR, "logout");
        }
    }

    @GET
    @Path("/today")
    public @ResponseBody
    String getToday(@RequestBody String profile) throws IOException, URISyntaxException {
        // GET URL_TODAY
        HttpGet httpGet = new HttpGet(URL_TODAY);
        CloseableHttpResponse today = auth.getHttpClient().execute(httpGet, auth.getHttpContext());
        try {
            System.out.println("[ " + today.getStatusLine() + "] " + URL_TODAY);
            HttpEntity entity = today.getEntity();
            String entityContents = EntityUtils.toString(entity);

            Document doc = Jsoup.parse(entityContents);
            shows = doc.select("div.contbox.ovbox");
            if (shows.toString().equals("")) {
                return Utils.messageJosn(MessageTags.INFO, "no tv shows");
            }
//                for (Element show : shows) {
//                    System.out.println(show.text());
//                }

        } finally {
            today.close();
        }

        return shows.toString();

    }
}
