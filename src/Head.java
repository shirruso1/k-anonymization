import java.util.ArrayList;
import java.util.List;

public class Head {
    private List<Integer> anonymization;
    private List<EquivalenceClass> inducedEquivalenceClasses;

    public Head(List<Integer> anonymization, List<EquivalenceClass> inducedEquivalenceClasses) {
        this.anonymization = anonymization;
        this.inducedEquivalenceClasses = inducedEquivalenceClasses;
    }

    public Head(Head other) {
        this.anonymization = new ArrayList<>(other.anonymization);
        this.inducedEquivalenceClasses = new ArrayList<>();
        for (EquivalenceClass ec : other.inducedEquivalenceClasses) {
            this.inducedEquivalenceClasses.add(new EquivalenceClass(ec));
        }
    }


    public List<EquivalenceClass> getInducedEquivalenceClasses() {
        return inducedEquivalenceClasses;
    }

    public List<Integer> getAnonymization() {
        return anonymization;
    }

    public void setEquivalenceClassList(List<EquivalenceClass> ecList) {
        this.inducedEquivalenceClasses = ecList;
    }

    public void addValue(int value) {
        this.anonymization.add(value);
    }
}
