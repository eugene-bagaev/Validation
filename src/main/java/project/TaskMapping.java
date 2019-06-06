package project;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.StringWriter;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import com.google.gson.*;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.google.gson.reflect.*;
import com.google.gson.stream.JsonReader;
import org.mortbay.util.ajax.JSON;
import org.mortbay.util.ajax.JSONObjectConvertor;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import project.Rules.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class TaskMapping {

    public static double VERSION  = 45.0;
    public static String PathToXMLFile  = "src/main/resources/package.xml";
    public String nameTask  = "src/main/resources/package.xml";
    public Map<String, Map<String, Rule>> nameTask_mapResults   = new LinkedHashMap<>();

    public TaskMapping(String nameTask){
        this.nameTask = nameTask;
        this.nameTask_mapResults  = loadTaskMapping();
        generatePackageXML(this.nameTask_mapResults);
    }

    public Map<String, Map<String, Rule>> loadTaskMapping(){
        Map<String, Map<String, Rule>> nameTask_mapResults   = new LinkedHashMap<>();
        try{
            // System.out.println("METADATA_CHECK " + METADATA_CHECK);
            FileReader reader = new FileReader("src/main/resources/StorageTaskMapping.json");
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(reader);
            JSONArray tasksList = (JSONArray) obj;
            int index = 0;
            for (Object task: tasksList){
                index++;
                Map<String, Rule> mapRule = parseTaskObject((JSONObject)task);
                nameTask_mapResults.put("TASK_" + index, mapRule);
            }
        } catch(Exception e){
            System.out.println("oi kak hrenovo " + e.getMessage());
        }
        return nameTask_mapResults;
    }

    public static void generatePackageXML(Map<String, Map<String, Rule>> METADATA_CHECK){
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Package");
            doc.appendChild(rootElement);
            Attr attr = doc.createAttribute("xmlns");
            attr.setValue("http://soap.sforce.com/2006/04/metadata");
            rootElement.setAttributeNode(attr);

            Map<String, List<String>> metadataMembers = createMapForXML(METADATA_CHECK);
            for (String item :metadataMembers.keySet()) {

                if (metadataMembers.get(item).size() > 0){
                    Element types = doc.createElement("types");
                    rootElement.appendChild(types);
                    for(String m :metadataMembers.get(item)){
                        Element members = doc.createElement("members");
                        members.appendChild(doc.createTextNode(m));
                        types.appendChild(members);
                    }
                    Element nameMembers = doc.createElement("name");
                    nameMembers.appendChild(doc.createTextNode(item));
                    types.appendChild(nameMembers);
                }
            }
            Element version = doc.createElement("version");
            version.appendChild(doc.createTextNode(String.valueOf(VERSION)));
            rootElement.appendChild(version);
// save XML
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
//            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(new File(PathToXMLFile)));
//            System.out.println(writer.getBuffer().toString());
        } catch (ParserConfigurationException | TransformerException e ) {
            e.printStackTrace();
        }
    }

    private static Map<String, List<String>> createMapForXML(Map<String, Map<String, Rule>> mapping){
        Map<String, Rule> METADATA_CHECK = new HashMap<>();
        for ( Map<String, Rule> e: mapping.values()){
            METADATA_CHECK.putAll(e);
        }
        Map<String, List<String>> results = new HashMap<>();
        List<String> membersSobject = new ArrayList<>();
        List<String> membersApexClass = new ArrayList<>();
        List<String> membersTriggerClass = new ArrayList<>();
        List<String> membersVisualforcePage = new ArrayList<>();
        for (Rule rule : METADATA_CHECK.values()) {
//            String member = (item.contains(".")  ? item.substring(0, item.indexOf('.')) : item);
            if (rule instanceof sObjectRule){
                sObjectRule sObjRule = (sObjectRule)rule;
                membersSobject.add(sObjRule.nameFile);
            } else if (rule instanceof ApexClassRule){
                ApexClassRule apexRule = (ApexClassRule)rule;
                membersApexClass.add(apexRule.nameClass);
            } else if (rule instanceof ApexTriggerRule){
                ApexTriggerRule apexTrigger = (ApexTriggerRule)rule;
                membersTriggerClass.add(apexTrigger.triggerName);
            } else if (rule instanceof VisualforcePageRule){
                VisualforcePageRule VFpage = (VisualforcePageRule)rule;
                membersVisualforcePage.add(VFpage.nameFile);
            } else if (rule instanceof TestRule){
                TestRule test  = (TestRule)rule;
                membersApexClass.add(test.TestClass);
            }
        }
        if (!membersSobject.isEmpty()) {
            results.put("CustomObject", membersSobject);
        }
        if (!membersApexClass.isEmpty()) {
            results.put("ApexClass", membersApexClass);
        }
        if (!membersTriggerClass.isEmpty()) {
            results.put("ApexTrigger", membersTriggerClass);
        }
        if (!membersVisualforcePage.isEmpty()) {
            results.put("ApexPage", membersVisualforcePage);
        }
        return results;
    }

    public static void saveJsonFile(String jsonFile){
        try{
            System.out.println(jsonFile);
            FileWriter file = new FileWriter("src/main/resources/StorageTaskMapping.json");
            file.write(jsonFile);
            file.flush();
        } catch(Exception e){
            System.out.println("oi kak hrenovo " + e.getMessage());
        }
    }

    public static String getJsonFile(){
        try{
            FileReader reader = new FileReader("src/main/resources/StorageTaskMapping.json");
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(reader);
            JSONArray tasksList = (JSONArray) obj;
            return tasksList.toJSONString();
        } catch(Exception e){
            System.out.println("oi kak hrenovo " + e.getMessage());
        }
        return null;
    }


    private static  Map<String, Rule> parseTaskObject(JSONObject tasksList) {
        Map<String, Rule> taskMapping = new LinkedHashMap<>();
        JSONArray sObjectArr = (JSONArray) tasksList.get("sObjectTasks");
        sObjectArr.forEach( e -> {
            sObjectRule  sObjectrule = createSObjectTasks( (JSONObject)e);
            String name = sObjectrule.nameFile;// + ".object";
            taskMapping.put(name, sObjectrule);
        });
        JSONArray triggerArr = (JSONArray) tasksList.get("triggerTasks");
        triggerArr.forEach( e -> {
            ApexTriggerRule triggerRule = createsTriggerTasks( (JSONObject)e);
            String name = triggerRule.triggerName; //+ ".trigger";
            taskMapping.put(name, triggerRule);
        });
        JSONArray apexClassArr = (JSONArray) tasksList.get("apexClassTasks");
        apexClassArr.forEach( e -> {
            ApexClassRule apexClassrule = createsApexClassTasks( (JSONObject)e);
            String name = apexClassrule.nameClass; //+ ".cls";
            taskMapping.put(name, apexClassrule);
        });
        JSONArray apexPageArr = (JSONArray) tasksList.get("apexPageTasks");
        apexPageArr.forEach( e -> {
            VisualforcePageRule VFrule = createsApexPagesTasks((JSONObject)e);
            String name = VFrule.nameFile; //+ ".cls";
            taskMapping.put(name, VFrule);
        });
        JSONArray testArr = (JSONArray) tasksList.get("testTasks");
        testArr.forEach( e -> {
            TestRule test = createsTestTasks((JSONObject)e);
            String name = test.TestClass; //+ ".cls";
            taskMapping.put(name, test);
        });
        return taskMapping;
    }


    private static sObjectRule createSObjectTasks (JSONObject sObject) {
        String namesObject = (String)sObject.get("name");
        String labelsObject = (String)sObject.get("label");
//fields
        JSONArray fieldsRuleArr = (JSONArray) sObject.get("fieldsRule");
        List<sObjectRule.Property> fields = new ArrayList<>();
        fields.addAll(createssObjectRuleProperty(fieldsRuleArr));
//validationRules
        JSONArray validationRulesArr = (JSONArray) sObject.get("validationRule");
        fields.addAll(createssObjectValidationRules(validationRulesArr));
//label
        fields.add(new sObjectRule.labelInnerClass(labelsObject));
        return new sObjectRule(namesObject, fields);
    }

    private static List<sObjectRule.Property> createssObjectValidationRules( JSONArray validationRulesArr){
        List<sObjectRule.Property> validateRules = new ArrayList<>();
        Iterator<JSONObject> iterator = validationRulesArr.iterator();
        while(iterator.hasNext()) {
            JSONObject validRule = iterator.next();
            String ruleName = (String)validRule.get("name");
            Map<String, String> tagRule = new HashMap<>();
            JSONArray fieldskeyValue = (JSONArray) validRule.get("keyValue");
            Iterator<JSONObject> iteratorKeyVal = fieldskeyValue.iterator();
            while(iteratorKeyVal.hasNext()) {
                JSONObject tagRuleObj = iteratorKeyVal.next();
                tagRule.put((String)tagRuleObj.get("key"), (String)tagRuleObj.get("value"));
            }
            validateRules.add(new sObjectRule.validationRulesInnerClass(ruleName ,tagRule));
        }
        return validateRules;
    }

    private static List<sObjectRule.Property> createssObjectRuleProperty( JSONArray fieldsRuleArr){
        List<sObjectRule.Property> fields = new ArrayList<>();
        Iterator<JSONObject> iterator = fieldsRuleArr.iterator();
        while(iterator.hasNext()) {
            JSONObject field = iterator.next();
            String fieldName = (String)field.get("name");
            Map<String, String> tagRule = new HashMap<>();
            JSONArray fieldskeyValue = (JSONArray) field.get("keyValue");
            Iterator<JSONObject> iteratorKeyVal = fieldskeyValue.iterator();
            while(iteratorKeyVal.hasNext()) {
                JSONObject tagRuleObj = iteratorKeyVal.next();
                tagRule.put((String)tagRuleObj.get("key"), (String)tagRuleObj.get("value"));
            }
            fields.add(new  sObjectRule.FieldSObjectInnerClass(fieldName ,tagRule));
        }
        return fields;
    }

    private static  ApexTriggerRule createsTriggerTasks (JSONObject trigger) {
        String namesTrigger = (String)trigger.get("name");
        String helperMethod = (String)trigger.get("helperMethod");
        String objName = (String)trigger.get("objName");
        JSONArray events = (JSONArray) trigger.get("trigerEvents");
        List<String> triggerEvents = new ArrayList<>();
        events.forEach( e -> triggerEvents.add((String)e) );
        return new ApexTriggerRule(namesTrigger, new TriggerInfoWraper(objName, triggerEvents, helperMethod));
    }

    private static  ApexClassRule createsApexClassTasks (JSONObject apexClasses) {
        String namesApexClass = (String)apexClasses.get("name");
        List<String> methodsForSearch = new ArrayList<>();
        JSONArray methodsSearch = (JSONArray) apexClasses.get("methodForSearch");
        methodsSearch.forEach( e -> methodsForSearch.add((String)e) );
        JSONArray methodsExecute = (JSONArray) apexClasses.get("methodsForExecute");
        return new ApexClassRule( namesApexClass, methodsForSearch,createsApexClassCheckExecuteWraper(methodsExecute));
    }

    private static List<CheckExecuteMethodWraper> createsApexClassCheckExecuteWraper(JSONArray wrapers) {
        List<CheckExecuteMethodWraper> executedMethods = new ArrayList<>();
        Iterator<JSONObject> iterator = wrapers.iterator();
        while(iterator.hasNext()) {
            JSONObject executeMethod = iterator.next();
            String nameClass = (String)executeMethod.get("nameClass");
            String nameMethod = (String)executeMethod.get("nameMethod");
            String stringExecute = (String)executeMethod.get("stringExecute");
            executedMethods.add(new CheckExecuteMethodWraper(nameClass, nameMethod, stringExecute));
        }
        return executedMethods;
    }

    private static VisualforcePageRule createsApexPagesTasks(JSONObject apexPages) {
        Map<String, List<String>> tagValuesForSearchVF = new HashMap<>();
        String namesApexPages = (String)apexPages.get("name");
        JSONArray apexPagesRules = (JSONArray) apexPages.get("rules");
        Iterator<JSONObject> iterator = apexPagesRules.iterator();
        while(iterator.hasNext()) {
            JSONObject ruleApex = iterator.next();
            String nameTag = (String)ruleApex.get("nameTag");
            List<String> valuesTags = new ArrayList<>();
            JSONArray valuesTagsSearch = (JSONArray) ruleApex.get("searchStrings");
            valuesTagsSearch.forEach( e -> valuesTags.add((String)e) );
            tagValuesForSearchVF.put(nameTag, valuesTags);
        }
        return  new VisualforcePageRule(namesApexPages, tagValuesForSearchVF);
    }

    private static  TestRule createsTestTasks(JSONObject tests) {
        String namesTest = (String)tests.get("name");
        String namesTestingClass = (String)tests.get("testingClass");
        return new TestRule(namesTest, namesTestingClass);
    }



}
