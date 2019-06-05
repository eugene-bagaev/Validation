package project.Rules;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class VisualforcePageRule implements Rule {

    public String nameFile;
    public Map<String, List<String>> tagValuesSearch;

    public VisualforcePageRule(String nameFile, Map<String, List<String>> tagValuesSearch){
        this.nameFile = nameFile;
        this.tagValuesSearch = tagValuesSearch;
    }

    public List<Results> checkCondition(String file, String userName) {
        List<Results> results = new ArrayList<>();
        Document doc = Jsoup.parse(file);
        for(String tag : tagValuesSearch.keySet()) {
            results.addAll(searchValuesInTag(tag, tagValuesSearch.get(tag), doc));
        }
        return results;

    }

     private List<Results> searchValuesInTag(String tag, List<String> values, Document doc) {
         List<Results> results = new ArrayList<>();
         String resTag = tag;
         tag = (tag.contains(":")  ? tag.replace(":", "|") : tag);
         ArrayList<String> text = new ArrayList<>();
             if (tag.equals("button")){
                 text.addAll( searchElements("apex|commandButton", doc));
                 text.addAll( searchElements("button", doc));
             } else if (tag.equals("table")){
                 text.addAll( searchElements("apex|pageBlockTable", doc));
                 text.addAll( searchElements("apex|dataTable", doc));
                 text.addAll( searchElements("table", doc));
             } else {
                 text.addAll( searchElements(tag, doc));
             }
//         System.out.println(text);
//         System.out.println(text.isEmpty());
            if(text.isEmpty()){
                // not find teg
                 results.add(new Results(this.nameFile,  MessageFormat.format(Constants.VF_NOT_FOUND_TAG, this.nameFile, resTag),  false));
                return results;
            }
            for (String searchVal :values){
                long count = text.stream().filter(e -> e.toLowerCase().contains(searchVal.toLowerCase())).count();
                System.out.println(count);
                if (count == 0)  {
                    results.add(new Results(this.nameFile,  MessageFormat.format(Constants.VF_NOT_FOUND_VALUE_IN_TAG, this.nameFile, resTag, searchVal),  false));
                } else {
                    results.add(new Results(this.nameFile,  MessageFormat.format(Constants.VF_FOUND_VALUE_IN_TAG, this.nameFile, resTag, searchVal),true));
                }
            }

         return results;
     }


    private  ArrayList<String>  searchElements(String tag, Document doc) {
        ArrayList<String>  text =  new ArrayList<>();
        Elements elements = doc.select(tag);
        for (Element element : elements) {
            text.add(element.getAllElements().toString());
        }
        return text;
    }



}
