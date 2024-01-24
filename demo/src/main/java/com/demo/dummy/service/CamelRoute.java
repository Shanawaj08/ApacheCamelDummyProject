package com.demo.dummy.service;

import com.demo.dummy.ErrorResponse;
import com.demo.dummy.Processor.CamelProcessor;
import com.demo.dummy.Processor.JsonResponseProcessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CamelRoute extends RouteBuilder {

    Logger logger = LoggerFactory.getLogger(CamelRoute.class);

    @Override
    public void configure() throws Exception {

        /*String url = "https://jsonplaceholder.typicode.com/todos";
        String xmlUrl = "http://restapi.adequateshop.com/api/Traveler";*/

        onException(HttpOperationFailedException.class).
                redeliveryDelay(1000).
                maximumRedeliveries(3).
                onRedelivery(exchange ->  {
                    logger.info("Retry...");
                }).
                log("Unable to connect the url").
                handled(true).
                process(exchange -> {
                    String failingUrl = exchange.getProperty(Exchange.TO_ENDPOINT, String.class);
                    exchange.setProperty("failingUrl", failingUrl);
                }).
                to("direct:errorResponse");

        onException(NullPointerException.class).
                log("No Object Found").
                handled(true).
                process(exchange -> {
                    String failingUrl = exchange.getProperty(Exchange.TO_ENDPOINT, String.class);
                    exchange.setProperty("failingUrl", failingUrl);
                }).
                to("direct:errorResponse");

        onException(IndexOutOfBoundsException.class).
                log("Index Out of Bound").
                handled(true).
                process(exchange -> {
                    String failingUrl = exchange.getProperty(Exchange.TO_ENDPOINT, String.class);
                    exchange.setProperty("failingUrl", failingUrl);
                }).
                to("direct:errorResponse");

        from("direct:httpCall")
                .to("https://jsonplaceholder.typicode.com/todos")
                .process(new CamelProcessor())
                .log("Response : ${body}");

        from("direct:getXML")
                .to("http://restapi.adequateshop.com/api/Traveler")
                .process(new JsonResponseProcessor())
                .log("Response : ${body}");

                    /*JSONObject json = XML.toJSONObject(xmlResponse);
                    String jsonResponse = json.toString(4);
                    exchange.getMessage().setBody(jsonResponse);*/

        from("direct:errorResponse")
                .process(exchange ->  {

                    Throwable exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                    String url = exchange.getProperty("failingUrl", String.class);

                    if (exception instanceof HttpOperationFailedException) {
                        ErrorResponse errorResponse = new ErrorResponse("Unable to Connect URL", url, 500);
                        exchange.getMessage().setBody(errorResponse);
                    } else if (exception instanceof NullPointerException) {
                        ErrorResponse errorResponse = new ErrorResponse("No Object Found", url, 404);
                        exchange.getMessage().setBody(errorResponse);
                    }
                    else if(exception instanceof IndexOutOfBoundsException) {
                        ErrorResponse errorResponse = new ErrorResponse("Index Out of Bound", url, 500);
                        exchange.getMessage().setBody(errorResponse);
                    }
                    exchange.getMessage().setHeader(Exchange.CONTENT_TYPE, "application/json");
                    exchange.getMessage().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.INTERNAL_SERVER_ERROR);
                });


    }
}
