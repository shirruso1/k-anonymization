import javafx.util.Pair;

/*
This class represent a record in the data set that has been generalized.
The first initialization represents the most generalized record
 */
public class Tuple {
    private Object[] record;

    /*
       Index   |  Attribute
         0         id
         1         gender
         2         age
         3         hypertension
         4         heart disease
         5         ever married
         6         work type
         7         residence type
         8         avg glucose level
         9         bmi
        10         smoking status
        11         stroke
     */
    public Tuple() {
        record = new Object[]{"*", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K"};
    }

    public Tuple(Tuple tuple) {
        record = new Object[tuple.record.length];
        for (int i = 0; i < tuple.record.length; i++) {
            record[i] = tuple.record[i];
        }
    }

    public Object[] getRecord() {
        return record;
    }

    public void setAttribute(int index, int value) {
        this.record[index] = value;
    }

    public String[] toStringArray(Object[] mapValueToNumber) {
        String[] output = new String[record.length];

        //id
        output[0] = "*";

        //gender
        if (record[1].equals("A"))
            output[1] = "A";
        else
            output[1] = (String) mapValueToNumber[(Integer) record[1]];

        //age
        if (record[2].equals("B"))
            output[2] = "B";
        else {
            Pair<Integer, Integer> pair = (Pair<Integer, Integer>) mapValueToNumber[(Integer) record[2]];
            output[2] = "[" + pair.getKey() + " , " + pair.getValue() + "]";
        }

        //hypertension
        if (record[3].equals("C"))
            output[3] = "C";
        else
            output[3] = booleanToString((Boolean) mapValueToNumber[(Integer) record[3]]);

        //heart disease
        if (record[4].equals("D"))
            output[4] = "D";
        else
            output[4] = booleanToString((Boolean) mapValueToNumber[(Integer) record[4]]);

        //ever married
        if (record[5].equals("E"))
            output[5] = "E";
        else
            output[5] = booleanToString((Boolean) mapValueToNumber[(Integer) record[5]]);

        //work type
        if (record[6].equals("F"))
            output[6] = "F";
        else
            output[6] = (String) mapValueToNumber[(Integer) record[6]];

        //residence type
        if (record[7].equals("G"))
            output[7] = "G";
        else
            output[7] = (String) mapValueToNumber[(Integer) record[7]];

        //avg glucose level
        if (record[8].equals("H"))
            output[8] = "H";
        else {
            Pair<Integer, Integer> pair = (Pair<Integer, Integer>) mapValueToNumber[(Integer) record[8]];
            output[8] = "[" + pair.getKey() + " , " + pair.getValue() + "]";
        }

        //bmi
        if (record[9].equals("I"))
            output[9] = "I";
        else {
            Pair<Integer, Integer> pair = (Pair<Integer, Integer>) mapValueToNumber[(Integer) record[9]];
            output[9] = "[" + pair.getKey() + " , " + pair.getValue() + "]";
        }

        //smoking status
        if (record[10].equals("J"))
            output[10] = "J";
        else
            output[10] = (String) mapValueToNumber[(Integer) record[10]];

        //stroke
        if (record[11].equals("K"))
            output[11] = "K";
        else
            output[11] = booleanToString((Boolean) mapValueToNumber[(Integer) record[11]]);
        return output;
    }

    public String booleanToString(boolean bool) {
        if (bool)
            return "1";
        return "0";
    }

}
