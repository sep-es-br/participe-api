package br.gov.es.participe;

import java.io.IOException;
//import java.util.Objects;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import br.gov.es.participe.controller.ResourceExceptionHandler;
import br.gov.es.participe.util.dto.MessageDto;

@SpringBootTest
public class ResourceExceptionHandlerTest {

    @Autowired
    private ResourceExceptionHandler resourceExceptionHandler;

    @Test
    public void sholdHandlerException() {
        ResponseEntity<MessageDto> responseEntity = resourceExceptionHandler
                .handleException(new Exception("Exception Error"), null);
        Assert.assertNotNull(responseEntity);
        MessageDto dto = responseEntity.getBody();
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getCode());
        Assert.assertEquals(Integer.valueOf("500"), dto.getCode());
    }

    @Test
    public void sholdHandlerIllegalArgumentException() {
        ResponseEntity<MessageDto> responseEntity = resourceExceptionHandler
                .handleException(new IllegalArgumentException("IllegalArgumentException Error"), null);
        Assert.assertNotNull(responseEntity);
        MessageDto dto = responseEntity.getBody();
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getCode());
        Assert.assertEquals(Integer.valueOf("400"), dto.getCode());
    }

    @Test
    public void sholdHandlerIOException() {
        ResponseEntity<MessageDto> responseEntity = resourceExceptionHandler
                .handleException(new IOException("IOException Error"), null);
        Assert.assertNotNull(responseEntity);
        MessageDto dto = responseEntity.getBody();
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getCode());
        Assert.assertEquals(Integer.valueOf("500"), dto.getCode());
    }

    @Test
    public void sholdHandlerRuntimeException() {
        ResponseEntity<MessageDto> responseEntity = resourceExceptionHandler
                .handleException(new RuntimeException("RuntimeException Error"), null);
        MessageDto msg = new MessageDto();
        msg.setCode(500);
        msg.setMessage("RuntimeException Error");
        Assert.assertNotNull(responseEntity);
        MessageDto dto = responseEntity.getBody();
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getCode());
        Assert.assertEquals(Integer.valueOf("500"), dto.getCode());
        Assert.assertEquals(msg.getMessage(), dto.getMessage());

    }

}
