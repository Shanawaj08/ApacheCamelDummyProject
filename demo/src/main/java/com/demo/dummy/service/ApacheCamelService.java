package com.demo.dummy.service;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApacheCamelService {

    @Autowired
    private ProducerTemplate producerTemplate;
    public String callCamelApi() {
        return producerTemplate.requestBody("direct:httpCall", null, String.class);
    }

    public String callXmlApi() {
        return producerTemplate.requestBody("direct:getXML", null, String.class);
    }
}
