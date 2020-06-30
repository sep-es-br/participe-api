package br.gov.es.participe.util;

import com.google.common.collect.Lists;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;

@Component
public class ParticipeUtils {

    public String getServerBaseUrl(HttpServletRequest request) {
        String baseUrl = String.format("%s://%s:%d/participe", request.getScheme(), request.getServerName(), request.getServerPort());
        return baseUrl;
    }

    public RestTemplate htmlRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        HttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        List<HttpMessageConverter<?>> httpMessageConverter = Lists.newArrayList();

        httpMessageConverter.add(stringHttpMessageConverter);
        httpMessageConverter.add(new MappingJackson2HttpMessageConverter());
        restTemplate.setMessageConverters(httpMessageConverter);

        return restTemplate;
    }

    public HttpEntity<String> htmlEntityResponse() {
        HttpHeaders headers = new HttpHeaders();
        Charset utf8 = Charset.forName("UTF-8");
        MediaType mediaType = new MediaType("text", "html", utf8);
        headers.setContentType(mediaType);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        return entity;
    }

    public HashMap<String, String> convertQueryStringToHashMap(String source) {
        HashMap<String, String> data = new HashMap<String, String>();
        final String[] arrParameters = source.split("&");

        for (final String tempParameterString : arrParameters) {
            final String[] arrTempParameter = tempParameterString.split("=");
            final String parameterKey = arrTempParameter[0];

            if (arrTempParameter.length >= 2) {
                final String parameterValue = arrTempParameter[1];
                data.put(parameterKey, parameterValue);
            } else {
                data.put(parameterKey, "");
            }
        }

        return data;
    }

    public String normalize(String valor) {
        return Normalizer.normalize(valor, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
    }

}
