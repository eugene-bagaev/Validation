package project;

import com.sforce.soap.apex.Connector;
import com.sforce.soap.apex.ExecuteAnonymousResult;
import com.sforce.soap.apex.LogCategory;
import com.sforce.soap.apex.LogCategoryLevel;
import com.sforce.soap.apex.LogInfo;
import com.sforce.soap.apex.LogType;
import com.sforce.soap.apex.SoapConnection;
import com.sforce.soap.apex.*;
import com.sforce.soap.metadata.*;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.soap.tooling.*;
import com.sforce.soap.tooling.RunTestsRequest;
import com.sforce.soap.tooling.RunTestsResult;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.mortbay.util.ajax.JSON;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import project.Rules.Results;
import project.Rules.VisualforcePageRule;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolingHelper {

    private String username;
    public MetadataConnection metadataConnection;
    public ToolingHelper(String username) {
        this.username = username;
    }


    public List<Results>  validateApexMethod() {
        List<Results> res = new ArrayList<>();

        return res;
    }




    public void createApexClass(String classBody) {

        ConnectorConfig soapConfig = new ConnectorConfig();
        soapConfig.setAuthEndpoint(MetadataLoginUtil.mapUserToLoginResult.get(this.username).getServerUrl());
        soapConfig.setServiceEndpoint(MetadataLoginUtil.mapUserToLoginResult.get(this.username).getServerUrl());
        soapConfig.setSessionId(MetadataLoginUtil.mapUserToSessionId.get(this.username));



        try {


//        ApexClass apexClass = new ApexClass();
//        apexClass.setBody(classBody);
//        ApexClass[] classes = { apexClass };
//           PartnerConnection connection = new PartnerConnection(soapConfig);
//            MetadataConnection connection = new MetadataConnection(soapConfig);
//            SoapConnection connection = new SoapConnection(soapConfig);
//            MetadataContainer Container = new MetadataContainer();
//            Container.setName("SampleContainer2");
//            MetadataContainer[] Containers = { Container };
////            SaveResult[] containerResults = connection.
            SoapConnection con =  com.sforce.soap.apex.Connector.newConnection(soapConfig);
            MetadataContainer Container = new MetadataContainer();
            Container.setName("SampleContainer2");

            MetadataContainer[] Containers = { Container };
            String[] classes = { classBody };
            CompileClassResult[] containerResults = con.compileClasses( classes );

//            containerResults.
//            MetadataContainer container = new MetadataContainer();
//            container.setName("dsfsd");

//            Metadata meta = new Metadata();
//            meta.
//            SaveResult[] saveResult = metadataConnection.createMetadata();
//
//            ApexClassMember createClassMember = new ApexClassMember();
//            createClassMember.setFullName("Messages");
//            createClassMember.setBody(classBody);
//            ApexClassMember[] createClassMembers = { createClassMember };
//            connection.createMetadata(createClassMembers);
            // create an ApexClass object and set the body

//            ApexClass apexClass = new ApexClass();
//            apexClass.setBody(classBody);
//            ApexClass[] classes = { apexClass };



//            createClassMember.se

//                    com.sforce.soap.partner.SaveResult saveResult = connection.create(new SObject[][] { classes });

        } catch (ConnectionException ex) {
            System.out.println( this.username + " .ToolingHelper >>executeAnonymousWithReturnStringDebug>> Connection Exception: " + ex);
        }

//// create an ApexClass object and set the body
//        ApexClass apexClass = new ApexClass();
//        apexClass.Body = classBody;
//
//        ApexClass[] classes = { apexClass };
//// call create() to add the class
//        SaveResult[] saveResults = sforce.create(classes);
//        for (int i = 0; i < saveResults.Length; i++)
//        {
//            if (saveResults[i].success)
//            {
//                Console.WriteLine("Successfully created Class: " +
//                        saveResults[i].id);
//            }
//            else
//            {
//                Console.WriteLine("Error: could not create Class ");
//                Console.WriteLine("   The error reported was: " +
//                        saveResults[i].errors[0].message + "\n");
//            }
//        }
    }






//        public static void generatePackageXML(String typesMeta, String memberMeta){}


}











//    public SObject[] runQuery(String query) {
//        ConnectorConfig soapConfig = new ConnectorConfig();
//        soapConfig.setAuthEndpoint(MetadataLoginUtil.mapUserToLoginResult.get(this.username).getServerUrl());
//        soapConfig.setServiceEndpoint(MetadataLoginUtil.mapUserToLoginResult.get(this.username).getServerUrl());
//        soapConfig.setSessionId(MetadataLoginUtil.mapUserToSessionId.get(this.username));
//        System.out.println("********");
//        System.out.println(MetadataLoginUtil.mapUserToSessionId.get(this.username));
//        System.out.println(MetadataLoginUtil.mapUserToLoginResult.get(this.username).getServerUrl());
//        try {
//            PartnerConnection connection = new PartnerConnection(soapConfig);
//            com.sforce.soap.partner.QueryResult result = connection.query(query);
//            System.out.println("getSize");
//            System.out.println(result.getSize());
//            for (SObject record : result.getRecords()) {
//                System.out.println("###### record.Id: " + (String)record.getField("Id"));
//                System.out.println("###### record.Name: " + (String)record.getField("Name"));
////                System.out.println("###### record.Markup: " + (String)record.getField("Markup"));
//            }
//            return result.getRecords();
//        } catch (ConnectionException ex) {
//            System.out.println( this.username + ".ToolingHelper >>runQuery>> Connection Exception: " + ex);
//        }
//        return null;
//    }
//
//    public Map<String, String> getApexPagesAndLink() {
//        Map<String, String> pageLink = new HashMap();
//        try {
//            ListMetadataQuery query = new ListMetadataQuery();
//            query.setType("ApexPage");
//            FileProperties[] listMeta = metadataConnection.listMetadata(new ListMetadataQuery[]{query},TaskMapping.VERSION);
//
//            com.sforce.soap.metadata.SessionHeader_element ee = metadataConnection.getSessionHeader();
//            String sessia = ee.getSessionId();
//
//            String link = MetadataLoginUtil.mapUserToLoginResult.get(this.username).getServerUrl();
//            String linkSubstr = link.substring(0,link.indexOf("/services"));
//            String url =  linkSubstr + "/secur/frontdoor.jsp?sid=" + sessia + "&retURL=" + linkSubstr + "/apex/";
//            for (FileProperties fp : listMeta) {
//                pageLink.put(fp.getFullName(), url + fp.getFullName());
//            }
//        } catch (ConnectionException ce) {
//            ce.printStackTrace();
//        }
//        return pageLink;
//    }
//
//    public String getApexPageMetadata(String page) {
//        String query = "SELECT Id, Name, Markup FROM ApexPage WHERE Name ='" + page + "'";
//        System.out.println(query);
//        SObject[] apexPage = runQuery(query);
//        String markupApexPage = "";
//        if (apexPage.length > 0){
//            markupApexPage = (String)apexPage[0].getField("Markup");
//        }
//        return markupApexPage;
//    }
//
