package project.Rules;

import java.util.List;

public interface Rule {
    List<Results> checkCondition(String str, String userName);
}
