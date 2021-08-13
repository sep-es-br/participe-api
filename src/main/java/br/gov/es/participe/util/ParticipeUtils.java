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
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.*;

@Component
public class ParticipeUtils {

    public boolean isPosClosure(Date endDate) {
        return endDate != null && (new Date()).after(endDate);
    }

    public boolean isPreOpening(Date beginDate) {
        return beginDate != null && (new Date()).before(beginDate);
    }

    public boolean isActive(Date beginDate, Date endDate) {
        Date today = new Date();
        if (beginDate != null && endDate != null) {
            return today.after(beginDate) && today.before(endDate);
        }
        else if (beginDate == null && endDate != null) {
            return today.before(endDate);
        }
        else if (beginDate != null) {
            return today.after(beginDate);
        }
        return true;
    }


    public String getServerBaseUrl(HttpServletRequest request) {
        if (request.getServerPort() > 0 && request.getServerPort() != 443) {
            return String.format("%s://%s:%d/participe", request.getScheme(), request.getServerName(), request.getServerPort());
        } else {
            return String.format("%s://%s/participe", request.getScheme(), request.getServerName());
        }
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
        return new HttpEntity<>("parameters", headers);
    }

    public Map<String, String> convertQueryStringToHashMap(String source) {
        Map<String, String> data = new HashMap<>();
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

    public String normalize(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
    }

}
