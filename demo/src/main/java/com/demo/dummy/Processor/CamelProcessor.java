package com.demo.dummy.Processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class CamelProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String body = exchange.getMessage().getBody(String.class);
        String[] records = body.split("\\},\\s*\\{");

        StringBuilder processedRecords = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            String record = records[i];
            record = record + "}";
            record = "{" + record;
            processedRecords.append(record).append("\n");
        }
        exchange.getMessage().setBody(processedRecords.toString());
    }
}
