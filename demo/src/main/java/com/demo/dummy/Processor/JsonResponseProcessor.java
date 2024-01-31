package com.demo.dummy.Processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

@Component
public class JsonResponseProcessor implements Processor {

    @Value("${dummy.xml.travel.info.page}")
    private static String pagePath;

    @Value("${dummy.xml.travel.info.total.record}")
    private static String totalRecordPath;

    @Value("${dummy.xml.travel.info.travelers}")
    private static String travelerInfoPath;

    @Override
    public void process(Exchange exchange) throws Exception {
        String xmlResponse = exchange.getMessage().getBody(String.class);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xmlResponse));
            Document document = builder.parse(inputSource);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xPath = xPathFactory.newXPath();

            int page = Integer.parseInt(evaluateXPath(xPath, document, "/TravelerinformationResponse/page"));
            int totalRecord = Integer.parseInt(evaluateXPath(xPath, document, "/TravelerinformationResponse/totalrecord"));

            // Create a nested JSON object
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode jsonResult = objectMapper.createObjectNode();
            jsonResult.put("page", page);
            jsonResult.put("totalrecord", totalRecord);

            // Extract travelers information
            ArrayNode travelersArray = jsonResult.putArray("travelers");
            XPathExpression travelersExpression = xPath.compile("/TravelerinformationResponse/travelers/Travelerinformation");

            NodeList travelersList = (NodeList) travelersExpression.evaluate(document, XPathConstants.NODESET);
            for (int i = 0; i < travelersList.getLength(); i++) {
                Node travelerNode = travelersList.item(i);
                if (travelerNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element travelerElement = (Element) travelerNode;

                    ObjectNode travelerObject = objectMapper.createObjectNode();
                    travelerObject.put("id", Integer.parseInt(getElementValue(travelerElement, "id")));
                    travelerObject.put("name", getElementValue(travelerElement, "name"));
                    travelerObject.put("email", getElementValue(travelerElement, "email"));

                    travelersArray.add(travelerObject);
                }
            }

            //jsonResult.put("travelers", travelersArray);

            exchange.getMessage().setBody(jsonResult);

            //System.out.println(jsonResult.toString());

            //XML to Json Conversion
            /*XmlMapper xmlMapper = new XmlMapper();
            JsonNode jsonNode = xmlMapper.readTree(xmlResponse);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(jsonNode);*/

            //exchange.getMessage().setBody(jsonResponse);

        } catch (Exception e) {
            LoggerFactory.getLogger(JsonResponseProcessor.class).error("Error Occurred while Processing ", e);
        }
    }

    private static String evaluateXPath(XPath xPath, Document document, String expression) throws Exception {
        XPathExpression xPathExpression = xPath.compile(expression);
        return (String) xPathExpression.evaluate(document, XPathConstants.STRING);
    }

    private static String getElementValue(Element element, String tagName) {
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}
