
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/*
This class holds all the tuples that induced by a specific anonymization
 */
public class EquivalenceClass {
    private List<Pair<Patient, Tuple>> tuple_list;

    public EquivalenceClass() {
        tuple_list = new ArrayList<>();
    }

    public EquivalenceClass(EquivalenceClass ec) {
        this.tuple_list = new ArrayList<>();
        for (Pair<Patient, Tuple> tuple : ec.tuple_list) {
            Pair<Patient, Tuple> pair = new Pair<>(new Patient(tuple.getKey()), new Tuple(tuple.getValue()));
            this.tuple_list.add(pair);
        }
    }

    public void addTuple(Pair<Patient, Tuple> tuple) {
        this.tuple_list.add(tuple);
    }

    /* Adding "value" to the current anonymization can split the equivalence class into two equivalence classes,
        this function returns these two equivalence classes. It is possible that one of these equivalence classes is empty.
        In this case, "value" has no effect on this equivalence class.
     */
    public Pair<EquivalenceClass, EquivalenceClass> induceEC(int value, int attribute_index, Object[] mapValueToNumber) {
        EquivalenceClass ec1 = new EquivalenceClass();
        EquivalenceClass ec2 = new EquivalenceClass();
        for (Pair<Patient, Tuple> tuple : tuple_list) {
            Tuple tupleCopy = new Tuple(tuple.getValue());
            Pair pairCopy = new Pair<>(tuple.getKey(), tupleCopy);
            boolean condition = false;
            switch (attribute_index) {
                case 1://gender
                    condition = tuple.getKey().getGender().equals((String) mapValueToNumber[value]);
                    break;
                case 2://age
                    condition = tuple.getKey().getAge() >= (double) ((((Pair<Integer, Integer>) (mapValueToNumber[value])).getKey()).intValue())
                            && tuple.getKey().getAge() <= (double) ((((Pair<Integer, Integer>) (mapValueToNumber[value])).getValue()).intValue());
                    break;
                case 3://hypertension
                    condition = tuple.getKey().isHypertension() == (Boolean) mapValueToNumber[value];
                    break;
                case 4: //heart disease
                    condition = tuple.getKey().isHeartDisease() == (Boolean) mapValueToNumber[value];
                    break;
                case 5: //ever married
                    condition = tuple.getKey().isEverMarried() == (Boolean) mapValueToNumber[value];
                    break;
                case 6: //work type
                    condition = tuple.getKey().getWorkType().equals((String) mapValueToNumber[value]);
                    break;
                case 7: //residence type
                    condition = tuple.getKey().getResidenceType().equals((String) mapValueToNumber[value]);
                    break;
                case 8: //avg glucose level
                    condition = tuple.getKey().getAvgGlucoseLevel() >= (double) ((((Pair<Integer, Integer>) (mapValueToNumber[value])).getKey()).intValue())
                            && tuple.getKey().getAvgGlucoseLevel() <= (double) ((((Pair<Integer, Integer>) (mapValueToNumber[value])).getValue()).intValue());
                    break;
                case 9: //bmi
                    condition = tuple.getKey().getBmi() >= (double) ((((Pair<Integer, Integer>) (mapValueToNumber[value])).getKey()).intValue())
                            && tuple.getKey().getBmi() <= (double) ((((Pair<Integer, Integer>) (mapValueToNumber[value])).getValue()).intValue());
                    break;
                case 10: //smoking status
                    condition = tuple.getKey().getSmokingStatus().equals((String) mapValueToNumber[value]);
                    break;
                case 11: //stroke
                    condition = tuple.getKey().isStroke() == (Boolean) mapValueToNumber[value];
                    break;
            }
            checkConditionAndUpdate(condition, tupleCopy, ec1, ec2, attribute_index, value, pairCopy);
        }
        return new Pair<>(ec1, ec2);
    }

    public void checkConditionAndUpdate(boolean condition, Tuple tuple, EquivalenceClass ec1, EquivalenceClass ec2, int attribute_index, int value, Pair pair) {
        if (condition) {
            tuple.setAttribute(attribute_index, value);
            ec1.addTuple(pair);
        } else {
            ec2.addTuple(pair);
        }
    }

    public boolean containsPatient (Patient patient){
        for(Pair<Patient,Tuple> tuple: this.tuple_list){
            if(tuple.getKey().equals(patient))
                return true;
        }
        return false;
    }

    public int size() {
        return this.tuple_list.size();
    }

    public List<Pair<Patient, Tuple>> getTuple_list() {
        return tuple_list;
    }
}
