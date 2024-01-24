package com.demo.dummy.Controller;

import com.demo.dummy.service.ApacheCamelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApacheCamelController {

    @Autowired
    private ApacheCamelService apacheCamelService;

    @GetMapping("/callApi")
    public ResponseEntity<String> callCamelApi() {
        return new ResponseEntity<>(apacheCamelService.callCamelApi(), HttpStatus.OK);
    }

    @GetMapping("/callXmlApi")
    public ResponseEntity<String> callXmlApi() {
        return new ResponseEntity<>(apacheCamelService.callXmlApi(), HttpStatus.OK);
    }
}
