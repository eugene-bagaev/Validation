package project.Rules;

import java.io.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import javax.xml.xpath.*;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.w3c.dom.NodeList;


public class sObjectRule implements  Rule {

    public String nameFile;
    public List<Property> properties;

    public sObjectRule(String nameFile,  List<Property> properties){
        this.nameFile = nameFile;
        this.properties = properties;
    }
    private static Document getDocument(String fileName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(fileName)));
        doc.getDocumentElement().normalize();
        return doc;
    }

    public List<Results> checkCondition (String file, String userName){
        List<Results> results = new ArrayList<>();
        try {
            Document doc = getDocument(file);
            for (Property pr :properties){
                results.addAll(pr.checkProperty(doc, nameFile));
            }
        }
        catch (Exception e) {
            System.out.println("!!!!!!!!error sObject checkCondition " + e.getMessage());
        }
        return results;
    }

    public interface Property {
        List<Results> checkProperty(Document doc, String fileName);
    }

    public static class FieldSObjectInnerClass implements  Property {
        public String name;
        public Map<String, String> keyValue;
        public  FieldSObjectInnerClass(String name, Map<String, String> keyValue) {
            this.name = name;
            this.keyValue = keyValue;
        }
        public List<Results> checkProperty(Document doc, String fileName) {
            List<Results> results = new ArrayList<>();
            try {
                XPath xpath = XPathFactory.newInstance().newXPath();
                XPathExpression expr = xpath.compile("//fields[fullName='"+ name +"']");
                Node fieldNode = (Node)expr.evaluate(doc, XPathConstants.NODE);
                if (fieldNode == null) {
                    results.add( new Results(fileName, MessageFormat.format(Constants.SOBJECT_NOT_FOUND_FIELD, name, fileName), false));
                    return results;
                }
                results.add( new Results(fileName,  MessageFormat.format(Constants.SOBJECT_FOUND_FIELD, name, fileName), true));
                final Node nodeClone = fieldNode.cloneNode(true);
                for (String key : keyValue.keySet()) {
                    XPathExpression expClone = xpath.compile("//" + key);
                    String fieldKey = (String)expClone.evaluate(nodeClone, XPathConstants.STRING);
                    if (fieldKey.equals(keyValue.get(key))){
                        results.add( new Results(fileName,  MessageFormat.format(Constants.SOBJECT_FOUND__PROPERTY, key, name, fileName), true));
                    } else {
                        results.add( new Results(fileName,  MessageFormat.format(Constants.SOBJECT_NOT_FOUND__PROPERTY, key, name, fileName),  false));
                    }
                }
            }
            catch (Exception e) {
                System.out.println("!!!!!!!!error FieldSObjectInnerClass" + e.getMessage());
            }
            return results;
        }
    }


    public static class validationRulesInnerClass implements sObjectRule.Property {
        public String name;
        public Map<String, String> keyValue;
        public  validationRulesInnerClass(String name, Map<String, String> keyValue) {
            this.name = name;
            this.keyValue = keyValue;
        }
        public List<Results> checkProperty(Document doc, String fileName) {
            List<Results> results = new ArrayList<>();
            try {
                XPath xpath = XPathFactory.newInstance().newXPath();
                XPathExpression expr = xpath.compile("//validationRules[fullName='"+ name +"']");
                Node validationRulesNode = (Node)expr.evaluate(doc, XPathConstants.NODE);
                if (validationRulesNode == null) {
                    results.add( new Results(fileName, MessageFormat.format(Constants.SOBJECT_NOT_FOUND_VALIDATIONRULES, name, fileName), false));
                    return results;
                }
                results.add( new Results(fileName,  MessageFormat.format(Constants.SOBJECT_FOUND_VALIDATIONRULES, name, fileName), true));
                final Node nodeClone = validationRulesNode.cloneNode(true);
                for (String key : keyValue.keySet()) {
                    XPathExpression expClone = xpath.compile("//" + key);
                    String fieldKey = (String)expClone.evaluate(nodeClone, XPathConstants.STRING);
                    if (!fieldKey.contains(keyValue.get(key))){
                        results.add( new Results(fileName,  MessageFormat.format(Constants.SOBJECT_WRONG_VALIDATIONRULES_FORMULA, name, keyValue.get(key)), false));
                    }
                }
            }
            catch (Exception e) {
                System.out.println("!error FieldSObjectInnerClass" + e.getMessage());
            }
            return results;
        }
    }


    public static class labelInnerClass implements sObjectRule.Property {
        public String value;
        public  labelInnerClass(String value) {
            this.value = value;
        }
        public List<Results> checkProperty(Document doc, String fileName) {
            List<Results> results = new ArrayList<>();
            try {
                XPath xpath = XPathFactory.newInstance().newXPath();
                XPathExpression expr = xpath.compile("/CustomObject/label");
                Node validationLabel = (Node)expr.evaluate(doc, XPathConstants.NODE);
                if (validationLabel.getTextContent().equals(value)) {
                    results.add( new Results(fileName, MessageFormat.format(Constants.SOBJECT_FOUND_LABEL, value, fileName), true));
                } else {
                    results.add( new Results(fileName, MessageFormat.format(Constants.SOBJECT_NOT_FOUND_LABEL, value, fileName), false));
                }
                return results;
            }
            catch (Exception e) {
                System.out.println("!error FieldSObjectInnerClass" + e.getMessage());
            }
            return results;
        }
    }


}
