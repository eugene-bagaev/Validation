package project;

import project.Rules.Results;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserInfoWrapper {
    public List<Results> results;
    public List<String> loginHistoryList;
    public Map<String, List<ResultsWrapper>> metaDataResults;


    public UserInfoWrapper() {}

    public void addResults(Results results) {
        if (this.results == null) {
            this.results = new ArrayList<>();
        }
        this.results.add(results);
    }

    public void addLoginHistoryElement(String loginHistory) {
        if (this.loginHistoryList == null) {
            this.loginHistoryList = new ArrayList<>();
        }

        loginHistoryList.add(loginHistory);
    }


    public class ResultsWrapper {
        public String nameMetadata;
        public List<Results> results;
        public ResultsWrapper(String nameMetadata, List<Results> results){
            this.nameMetadata = nameMetadata;
            this.results = results;
        }

    }



}