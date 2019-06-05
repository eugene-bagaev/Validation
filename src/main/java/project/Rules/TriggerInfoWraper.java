package project.Rules;
import java.util.ArrayList;
import java.util.List;

public class TriggerInfoWraper {
    String objName;
    List<String> triggerEvents;
    String helperName;

    public TriggerInfoWraper(String objName, List<String> triggerEvents, String helperName) {
        this.objName = objName;
        this.helperName = helperName;
        this.triggerEvents = new ArrayList<>();
        this.triggerEvents.addAll(triggerEvents);
    }
}
