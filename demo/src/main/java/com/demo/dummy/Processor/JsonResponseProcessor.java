package com.demo.dummy.Processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class JsonResponseProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        String xmlResponse = exchange.getMessage().getBody(String.class);

        XmlMapper xmlMapper = new XmlMapper();
        JsonNode jsonNode = xmlMapper.readTree(xmlResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(jsonNode);

        exchange.getMessage().setBody(jsonResponse);
    }
}
