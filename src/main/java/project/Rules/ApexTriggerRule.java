package project.Rules;
import java.util.ArrayList;
import java.util.List;

public class ApexTriggerRule implements Rule {
    String triggerName;
    TriggerInfoWraper triggerInfo;

    public ApexTriggerRule(String triggerName, TriggerInfoWraper triggerInfo) {
        this.triggerName = triggerName;
        this.triggerInfo = triggerInfo;
    }

    public List<Results> checkCondition(String file) {
        List<Results> results = new ArrayList<>();
        if (Util.checkNesting(file, triggerInfo.objName) == 0) {
            results.add(new Results(triggerName, Constants.TRIGGER_OBJECT_SUCCESS_MESSAGE, true));
        } else {
            results.add(new Results(triggerName, Constants.TRIGGER_OBJECT_ERROR_MESSAGE, false));
        }

        for (String event : triggerInfo.triggerEvents) {
            if (Util.checkNesting(file, event) != 0) {
                results.add(new Results(
                        triggerName,
                        Constants.TRIGGER_EVENT_ERROR_MESSAGE + event,
                        false)
                );
            }
        }
        if (results.size() == 1) {
            results.add(new Results(
                    triggerName,
                    String.join(", ", triggerInfo.triggerEvents) + Constants.TRIGGER_EVENT_SUCCESS_MESSAGE,
                    true
            ));
        }
        if (Util.checkNesting(triggerName, triggerInfo.helperName) > 0) {
            results.add(new Results(triggerName, Constants.TRIGGER_HELPER_SUCCESS_MESSAGE, true));
        } else {
            results.add(new Results(triggerName, Constants.TRIGGER_HELPER_ERROR_MESSAGE, false));
        }
        return results;
    }

}