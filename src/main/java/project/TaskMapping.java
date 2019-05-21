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

    public static Map<String, Rule> METADATA_CHECK   = new HashMap<>();
    public static Map<String, String> TEST_CLASSES   = new HashMap<>();
    public static List<String> tasks                 = Arrays.asList(
            "Product__c.object",
            "ProductTablePage.page",
            "ProductTablePageController.cls",
            "ProductTrigger.trigger",
            "TestProductTablePageController.cls",
            "TestProductTrigger.cls",
            "TestProductTriggerHelper.cls"
    );


    public static double VERSION  = 45.0;
    public static String PathToXMLFile  = "src/main/resources/package.xml";
    static {


        loadTaskMapping();


//        List<sObjectRule.Property> fields = new ArrayList<>();
////        •  Поле для хранения цены на продукт Field Name = ImageURL
//        Map<String, String> keyValue = new HashMap<>();
//        keyValue.put("type", "Url"); // Type = URL
//        keyValue.put("label", "ImageURL"); // Label = ImageURL
//        fields.add(new sObjectRule.FieldSObjectInnerClass("ImageURL__c",keyValue));
////        •  Поле для хранения цены на продукт Field Name = UnitPrice
//        Map<String, String> keyValueTwo = new HashMap<>();
//        keyValueTwo.put("type", "Currency");  //Type = Currency
//        keyValueTwo.put("label", "UnitPrice"); //Label = UnitPrice
//        fields.add(new sObjectRule.FieldSObjectInnerClass("UnitPrice__c",keyValueTwo));
////        • Поле для хранения количества продуктов Field Name = UnitsAvailable
//        Map<String, String> keyValueThree = new HashMap<>();
//        keyValueThree.put("type", "Number");  //Type = Number
//        keyValueThree.put("label", "UnitsAvailable"); //Label = UnitsAvailable
//        fields.add(new sObjectRule.FieldSObjectInnerClass("UnitsAvailable__c",keyValueThree));
////        • Поле для хранения количества продуктов Field Name = UnitsAvailable
//        Map<String, String> keyValueFour = new HashMap<>();
//        keyValueFour.put("type", "LongTextArea");  //Type = Number
//        keyValueFour.put("label", "Description"); //Label = UnitsAvailable
//        fields.add(new sObjectRule.FieldSObjectInnerClass("Description__c",keyValueFour));
////        • Поле для хранения даты добавления продукта Field Name = AddedDate
//        Map<String, String> keyValueFive = new HashMap<>();
//        keyValueFive.put("type", "DateTime");  //Type = Number
//        keyValueFive.put("label", "AddedDate"); //Label = UnitsAvailable
//        fields.add(new sObjectRule.FieldSObjectInnerClass("AddedDate__c",keyValueFive));
////        Создать Validation Rule, который не будет позволять создавать записи Product, если
//        Map<String, String> keyValueValidRule = new HashMap<>();
//        keyValueValidRule.put("errorConditionFormula", "UnitPrice__c");
//        fields.add(new sObjectRule.validationRulesInnerClass("CorrectPriceValidation",keyValueValidRule));
////          Label = Product
//        fields.add(new sObjectRule.labelInnerClass("Product"));
//
//        METADATA_CHECK.put("Product__c.object", new sObjectRule("Product__c", fields));
//
////      Trigger
//        List<String> triggerEvents = new ArrayList<>();
//        triggerEvents.add("before insert");
//        triggerEvents.add("before update");
//        METADATA_CHECK.put("ProductTrigger.trigger", new ApexTriggerRule("ProductTrigger", new TriggerInfoWraper("Product__c", triggerEvents, "ProductTriggerHelper")));
//
////      Apex class
//        List<CheckExecuteMethodWraper> executedMethods = new ArrayList<>();
//        executedMethods.add(new CheckExecuteMethodWraper(
//                "ProductTablePageController",
//                "getProducts",
//                "ProductTablePageController cl = new ProductTablePageController();"
//                        + " List<Product__c> executeMethod = cl.getProducts();"
//                        + " Integer checkOnNull = executeMethod.size();"));
//        METADATA_CHECK.put("ProductTablePageController.cls", new ApexClassRule( "ProductTablePageController", Arrays.asList("getProducts"), executedMethods));
//
//// use key-word "button", "table" for search values in this tags
////      VisualforcePage
//        Map<String, List<String>> tagValuesForSearchVF = new HashMap<>();
//        ArrayList<String> searchInTable = new ArrayList<String>() {
//            {
//                add("Image");
//                add("Name");
//                add("Description");
//                add("Price");
//                add("Available Units");
//            }
//        };
//        tagValuesForSearchVF.put("table", searchInTable);
//        tagValuesForSearchVF.put("apex:page", new ArrayList<String>() {{ add("ProductTablePageController");}});
//        tagValuesForSearchVF.put("button", new ArrayList<String>() {{ add("New");add("Save");}});
//        METADATA_CHECK.put("ProductTablePage.page", new VisualforcePageRule("ProductTablePage", tagValuesForSearchVF));

        // tests: Test Class => Class тестируемый
        TEST_CLASSES.put("TestProductTablePageController", "ProductTablePageController");
        TEST_CLASSES.put("TestProductTrigger", "ProductTrigger");
        TEST_CLASSES.put("TestProductTriggerHelper", "ProductTriggerHelper");

//        METADATA_CHECK.put("TestProductTablePageController.cls", new TestRule("TestProductTablePageController", "ProductTablePageController"));
//        METADATA_CHECK.put("TestProductTrigger.cls", new TestRule("TestProductTrigger", "ProductTrigger"));
//        METADATA_CHECK.put("TestProductTriggerHelper.cls", new TestRule("TestProductTriggerHelper","ProductTriggerHelper" ));


    }

    public static void generatePackageXML(){
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

            Map<String, List<String>> metadataMembers = createMapForXML();
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

    private static Map<String, List<String>> createMapForXML(){
        Map<String, List<String>> results = new HashMap<>();
        List<String> membersSobject = new ArrayList<>();
        List<String> membersApexClass = new ArrayList<>();
        List<String> membersTriggerClass = new ArrayList<>();
        List<String> membersVisualforcePage = new ArrayList<>();
        for (String item : METADATA_CHECK.keySet()) {
            String member = (item.contains(".")  ? item.substring(0, item.indexOf('.')) : item);
            if (METADATA_CHECK.get(item) instanceof sObjectRule){
                membersSobject.add(member);
            } else if (METADATA_CHECK.get(item) instanceof ApexClassRule){
                membersApexClass.add(member);
            } else if (METADATA_CHECK.get(item) instanceof ApexTriggerRule){
                membersTriggerClass.add(member);
            } else if (METADATA_CHECK.get(item) instanceof VisualforcePageRule){
                membersVisualforcePage.add(member);
            } else if (METADATA_CHECK.get(item) instanceof TestRule){
                membersApexClass.add(member);
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
//            System.out.println(tasksList);
//            tasksList.forEach( emp -> parseTaskObject( (JSONObject) emp ) );
        } catch(Exception e){
            System.out.println("oi kak hrenovo " + e.getMessage());
        }
        return null;
    }


    public static void loadTaskMapping(){
        try{
            Map<String, Rule> meta   = new HashMap<>();
            System.out.println("METADATA_CHECK " + METADATA_CHECK);
            FileReader reader = new FileReader("src/main/resources/StorageTaskMapping.json");
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(reader);
            JSONArray tasksList = (JSONArray) obj;
            tasksList.forEach( emp -> meta.putAll(parseTaskObject( (JSONObject) emp) ) );

            METADATA_CHECK = meta;
        } catch(Exception e){
            System.out.println("oi kak hrenovo " + e.getMessage());
        }
    }




    private static Map<String, Rule> parseTaskObject(JSONObject tasksList) {
        Map<String, Rule> metaCheck   = new HashMap<>();
        List<Map<String, Rule>> TASK = new ArrayList<>();

        JSONArray sObjectArr = (JSONArray) tasksList.get("sObjectTasks");
        sObjectArr.forEach( e -> metaCheck.putAll(createSObjectTasks( (JSONObject)e)) );
        System.out.println(sObjectArr);
        JSONArray triggerArr = (JSONArray) tasksList.get("triggerTasks");
        triggerArr.forEach( e -> metaCheck.putAll(createsTriggerTasks( (JSONObject)e)) );

        JSONArray apexClassArr = (JSONArray) tasksList.get("apexClassTasks");
        apexClassArr.forEach( e -> metaCheck.putAll(createsApexClassTasks( (JSONObject)e)) );

        JSONArray apexPageArr = (JSONArray) tasksList.get("apexPageTasks");
        apexPageArr.forEach( e -> metaCheck.putAll(createsApexPagesTasks( (JSONObject)e)) );

        JSONArray testArr = (JSONArray) tasksList.get("testTasks");
        testArr.forEach( e -> metaCheck.putAll(createsTestTasks( (JSONObject)e)) );
        return metaCheck;
    }


    private static Map<String, Rule> createSObjectTasks (JSONObject sObject) {
        Map<String, Rule> metaCheck   = new HashMap<>();
        String namesObject = (String)sObject.get("name");
        System.out.println(namesObject);

        String labelsObject = (String)sObject.get("label");
        System.out.println(labelsObject);
//fields
        JSONArray fieldsRuleArr = (JSONArray) sObject.get("fieldsRule");
        System.out.println(fieldsRuleArr);
        List<sObjectRule.Property> fields = new ArrayList<>();
        fields.addAll(createssObjectRuleProperty(fieldsRuleArr));
//validationRules
        JSONArray validationRulesArr = (JSONArray) sObject.get("validationRule");
        fields.addAll(createssObjectValidationRules(validationRulesArr));
//label
        fields.add(new sObjectRule.labelInnerClass(labelsObject));
        metaCheck.put(namesObject + ".object", new sObjectRule(namesObject, fields));
        return metaCheck;
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
            System.out.println("fieldName> " + fieldName);
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

    private static Map<String, Rule> createsTriggerTasks (JSONObject trigger) {
        Map<String, Rule> metaCheck   = new HashMap<>();
       String namesTrigger = (String)trigger.get("name");
       String helperMethod = (String)trigger.get("helperMethod");
        String objName = (String)trigger.get("objName");
        System.out.println(namesTrigger);

        JSONArray events = (JSONArray) trigger.get("trigerEvents");
        List<String> triggerEvents = new ArrayList<>();
        events.forEach( e -> triggerEvents.add((String)e) );
        metaCheck.put(namesTrigger +".trigger",
                new ApexTriggerRule(namesTrigger,
                        new TriggerInfoWraper(objName,
                                triggerEvents,
                                helperMethod)
                )
        );
        return metaCheck;
    }


    private static Map<String, Rule> createsApexClassTasks (JSONObject apexClasses) {
        Map<String, Rule> metaCheck   = new HashMap<>();
        String namesApexClass = (String)apexClasses.get("name");
        List<String> methodsForSearch = new ArrayList<>();
        JSONArray methodsSearch = (JSONArray) apexClasses.get("methodForSearch");
        methodsSearch.forEach( e -> methodsForSearch.add((String)e) );
        JSONArray methodsExecute = (JSONArray) apexClasses.get("methodsForExecute");
        metaCheck.put( namesApexClass + ".cls", new ApexClassRule( namesApexClass, methodsForSearch,createsApexClassCheckExecuteWraper(methodsExecute)));
        return metaCheck;
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

     private static  Map<String, Rule> createsApexPagesTasks(JSONObject apexPages) {
         Map<String, Rule> metaCheck   = new HashMap<>();
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
             System.out.println(nameTag);
             System.out.println(valuesTags);
         }
         metaCheck.put(namesApexPages+".page", new VisualforcePageRule(namesApexPages, tagValuesForSearchVF));
        return metaCheck;
    }

    private static  Map<String, Rule> createsTestTasks(JSONObject tests) {
        Map<String, Rule> metaCheck   = new HashMap<>();
        String namesTest = (String)tests.get("name");
        String namesTestingClass = (String)tests.get("testingClass");
        metaCheck.put(namesTest + ".cls", new TestRule(namesTest, namesTestingClass));
        return metaCheck;
    }

}
