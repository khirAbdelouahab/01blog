package com.blogger._blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@Component
public class CustomErrorController extends BasicErrorController {
    public CustomErrorController(ErrorAttributes errorAttributes) {
        super(errorAttributes, new ErrorProperties());
    }
    @Override
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String,Object>> error(HttpServletRequest request) {
        Map<String,Object> body = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        HttpStatus status = getStatus(request);
        body.put("customMessage","This is mrigla");
        body.put("timestamp",new Date());
        return new ResponseEntity<>(body,status);
    }

}
