/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saspes.rest;

import static com.saspes.rest.TVCalendarUrl.URL_TODAY;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * REST Web Service
 *
 * @author SasPes
 */
@Path("shows")
public class ShowsService {

//    @GET
//    @Path("/today")
//    public @ResponseBody
//    String getToday(@RequestBody String profile) throws IOException, URISyntaxException {
//        // GET URL_TODAY
//        HttpGet httpGet = new HttpGet(URL_TODAY);
//        CloseableHttpResponse today = httpClient.execute(httpGet, httpContext);
//        try {
//            System.out.println("[ " + today.getStatusLine() + "] " + URL_TODAY);
//            HttpEntity entity = today.getEntity();
//            String entityContents = EntityUtils.toString(entity);
//
//            Document doc = Jsoup.parse(entityContents);
//            shows = doc.select("div.contbox.ovbox");
//            if (shows.toString().equals("")) {
//                return errorJosn(Messages.INFO, "no tv shows");
//            }
////                for (Element show : shows) {
////                    System.out.println(show.text());
////                }
//
//        } finally {
//            today.close();
//        }
//        
//        return shows.toString();
//
//    }
}
