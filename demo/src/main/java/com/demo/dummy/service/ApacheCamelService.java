package com.demo.dummy.service;

import com.jcraft.jsch.*;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Service
public class ApacheCamelService {

    Logger log = LoggerFactory.getLogger(ApacheCamelService.class);

    @Autowired
    private ProducerTemplate producerTemplate;
    public String callCamelApi() {
        return producerTemplate.requestBody("direct:httpCall", null, String.class);
    }

    public String callXmlApi() {
        return producerTemplate.requestBody("direct:getXML", null, String.class);
    }

    public String callSftp() {
        JSch jsch = new JSch();
        Session session = null;
        StringBuilder res = new StringBuilder();
        try {
            session = jsch.getSession("demo", "test.rebex.net", 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword("password");
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;

            try {
                InputStream stream = sftpChannel.get("/readme.txt");
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = br.readLine()) != null) {
                    res.append(line);
                }

            } catch (Exception e) {
                log.error("Exception occurred during reading file from SFTP server due to " + e.getMessage());
                return "Cannot read file from sftp server";
            }

            sftpChannel.exit();
            session.disconnect();

        } catch (JSchException e) {
            log.error(e.getMessage());
            return "Connection cannot be established";
        }

        log.info(res.toString());
        return res.toString();
    }
}
