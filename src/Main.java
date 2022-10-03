

import com.opencsv.CSVWriter;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main {
    // holds all the equivalence classes that are induced by the current anonymization
    //private static List<EquivalenceClass> equivalence_classes_list = new ArrayList<>();
    //hold the current best anonymization
    private static Head best_anonymization;
    // map each value to its attribute
    private static int[] mapValueToAttribute;
    // map each value of an attribute to a number
    private static Object[] mapValueToNumber;
    private static int k;
    private static int dataSetSize;
    private static List<Patient> dataSet;


    public static void main(String[] args) {
        //read from keyboard k and number of records the user want to upload
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter k: ");
        k = sc.nextInt();
        System.out.print("Enter the number of records you want to upload: ");
        dataSetSize = sc.nextInt();

        dataSet = new ArrayList<>();
        EquivalenceClass most_general_ec = new EquivalenceClass();
        List<EquivalenceClass> equivalence_classes_list = new ArrayList<>();
        equivalence_classes_list.add(most_general_ec);
        best_anonymization = new Head(new ArrayList<>(), equivalence_classes_list);
        String line = "";
        String splitBy = ",";
        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(".\\src\\healthcare-dataset-stroke-data.csv"));
            int counter = dataSetSize;
            br.readLine();
            //parse data set
            while ((line = br.readLine()) != null && counter > 0)   //returns a Boolean value
            {
                String[] attributes = line.split(splitBy);    // use comma as separator
                /*create the first equivalence class that contains all the records in the dataset,
                 That is, the most generalized equivalence class*/
                Patient patient = new Patient(attributes);
                Pair<Patient, Tuple> pair = new Pair<>(patient, new Tuple());
                most_general_ec.addTuple(pair);
                dataSet.add(patient);
                counter--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
           Indexes    |  Attribute
             0 - 1        gender
             2 - 4        age
             5 - 6        hypertension
             7- 8         heart disease
             9- 10        ever married
            11 - 15       work type
            16 - 17       residence type
            18 - 20       avg glucose level
             21- 23       bmi
            24 - 27       smoking status
            28 - 29       stroke

         */
        mapValueToNumber = new Object[]{"Male", "Female"
                , new Pair<>(0, 27), new Pair<>(28, 55), new Pair<>(56, 82)
                , true, false
                , true, false
                , true, false
                , "children", "Govt_job", "Never_worked", "Private", "Self-employed"
                , "Rural", "Urban"
                , new Pair<>(55, 127), new Pair<>(128, 200), new Pair<>(201, 272)
                , new Pair<>(10, 39), new Pair<>(40, 69), new Pair<>(70, 98)
                , "formerly smoked", "never smoked", "smokes", "Unknown"
                , true, false};
        // maps each value to its attribute
        mapValueToAttribute = new int[]
                {1, 1,
                        2, 2, 2,
                        3, 3,
                        4, 4,
                        5, 5,
                        6, 6, 6, 6, 6,
                        7, 7,
                        8, 8, 8,
                        9, 9, 9,
                        10, 10, 10, 10,
                        11, 11};

        kOptimizeMain(k);
        anonymizationToStringArray();
        writeDataLineByLine(".\\src\\output.csv");

    }

    //this function call KOptimize inorder to find the best anonymization.
    public static int kOptimizeMain(int k) {
        List<Integer> sigmaAll = IntStream.range(0, mapValueToAttribute.length)
                .boxed()
                .collect(Collectors.toList());
        return KOptimize(k, best_anonymization, sigmaAll, Integer.MAX_VALUE);

    }

    //this function returns the lowest cost of any anonymization within the subtree rooted at 'head' that has a cost less than 'bestCost'.
    //otherwise,it returns 'bestCost'.
    public static int KOptimize(int k, Head head, List<Integer> tail, int bestCost) {
        tail = pruneUselessValues(head, tail);
        int c_optional = computeCost(head);

        /* if the cost of head is lower than the current best cost, then this head is better
        anonymization than the current best anonymization.
        Therefore, need to update 'best_anonymization' and 'bestCost'.
        */
        if (c_optional < bestCost) {
            best_anonymization = head;
            bestCost = c_optional;
        }
        tail = prune(head,tail,bestCost);
        tail = reorderTail(head, tail);

        //iterate over all direct children of the parent node - head in order to find the best anonymization with the best cost
        while (tail.size() > 0) {
            int value = tail.remove(0);
            List<Integer> value_list = new ArrayList<>();
            value_list.add(value);
            List<EquivalenceClass> new_ec_list = updateEquivalenceClasses(head, value_list);
            List<Integer> new_anonymization = new ArrayList<>(head.getAnonymization());
            new_anonymization.add(value);
            Head new_head = new Head(new_anonymization, new_ec_list);
            bestCost = KOptimize(k, new_head, tail, bestCost);
            //if 'bestCost' have been changed following the previous call, then attempt to prune more values from the tail.
            tail = prune(head, tail, bestCost);

        }
        return bestCost;
    }

    /*The purpose of the function is to prune all the tail values that induced equivalence classes that are smaller than k.
        these values are called "useless values". */
    public static List<Integer> pruneUselessValues(Head head, List<Integer> tail) {
        List<Integer> new_tail = new ArrayList<>();
        for (Integer value : tail) {
            List<Integer> valueList = new ArrayList<>();
            valueList.add(value);
            List<EquivalenceClass> new_equivalence_classes_list = updateEquivalenceClasses(head, valueList);
            if (!isUselessValue(new_equivalence_classes_list)) {
                new_tail.add(value);
            }
        }
        return new_tail;
    }

    /* create a new equivalence classes list resulting from adding "value" to the current anonymization */
    public static List<EquivalenceClass> updateEquivalenceClasses(Head head, List<Integer> values) {
        Head head_copy = new Head(new ArrayList<>(head.getAnonymization()), head.getInducedEquivalenceClasses());
        List<EquivalenceClass> new_equivalence_classes_list = new ArrayList<>();
        for (Integer value : values) {
            new_equivalence_classes_list = new ArrayList<>();
            for (EquivalenceClass ec : head_copy.getInducedEquivalenceClasses()) {
                Pair<EquivalenceClass, EquivalenceClass> induced_ec = ec.induceEC(value, mapValueToAttribute[value], mapValueToNumber);
                if (induced_ec.getValue().size() > 0) {
                    new_equivalence_classes_list.add(induced_ec.getValue());
                }
                if (induced_ec.getKey().size() > 0) {
                    new_equivalence_classes_list.add(induced_ec.getKey());
                }
            }
            head_copy.addValue(value);
            List<EquivalenceClass> new_ec_copy = new ArrayList<>(new_equivalence_classes_list);
            head_copy.setEquivalenceClassList(new_ec_copy);
        }
        return new_equivalence_classes_list;
    }

    /*  If the size of one of the new equivalence classes created by adding "value" to the head is less than k,
       then "value" is called a useless value. Therefore, in this case the function return true, otherwise false.
    */
    public static boolean isUselessValue(List<EquivalenceClass> ec_list) {
        for (EquivalenceClass ec : ec_list) {
            if (ec.size() < k)
                return true;
        }
        return false;
    }

    /*this function computes the following formula:
      Sigma|E|^2 (forall |E| >= then k) + Sigma|D||E| (forall |E| < k)
      In this expression,the sets E refer to the equivalence classes of tuples in D induced by the anonymization H.
      The first sum computes penalties for each non-suppressed tuple, the second for suppressed tuples.
     */
    public static int computeCost(Head head) {
        int c = 0;
        int ecSize;
        for (EquivalenceClass ec : head.getInducedEquivalenceClasses()) {
            ecSize = ec.size();
            if (ecSize >= k) {
                c += ecSize * ecSize;
            } else {
                c += dataSetSize * ecSize;
            }
        }
        return c;
    }


    public static int computeLowerBound(Head head, List<Integer> tail) {
        int sum = 0;
        //list of all equivalence classes induced by all_set A (union of head and tail)
        List<EquivalenceClass> ec_list = updateEquivalenceClasses(head, tail);
        for (Patient patient : dataSet) {
            for (EquivalenceClass ec_head : head.getInducedEquivalenceClasses()) {
                //find the equivalence class induced by head that contain patient
                if (ec_head.containsPatient(patient)) {
                    //check if this record is suppressed by H
                    if (ec_head.size() < k)
                        sum += dataSetSize;
                        // the record is not suppressed by H
                    else {
                        //find the equivalence class induced by the all_set that contain patient
                        for (EquivalenceClass ec_all_set : ec_list) {
                            if (ec_all_set.containsPatient(patient))
                                sum += Math.max(ec_all_set.size(), k);
                        }
                    }
                }
            }
        }
        return sum;
    }

    /*this function creates and returns a new tail set by removing values from T that can not
    lead to anonymization with cost lower than best_cost
    */
    public static List<Integer> prune(Head head, List<Integer> tail, int best_cost) {
        List <Integer> filtered_tail = new ArrayList<>();
        List <Integer> tail_copy = new ArrayList<>(tail);
        if(computeLowerBound(head, tail) >= best_cost){
            return filtered_tail;
        }
        for(Integer value:tail){
            List<Integer> value_list = new ArrayList<>();
            tail_copy.remove(0);
            value_list.add(value);
            List<EquivalenceClass> new_ec_list = updateEquivalenceClasses(head, value_list);
            List<Integer> new_anonymization = new ArrayList<>(head.getAnonymization());
            new_anonymization.add(value);
            Head new_head = new Head(new_anonymization, new_ec_list);
            if (computeLowerBound(new_head, tail) < best_cost) {
                filtered_tail.add(value);
            }
        }
        return filtered_tail;
    }

    //reorder the tail values  in a manner that vastly increases pruning opportunities.
    public static List<Integer> reorderTail(Head head, List<Integer> tail) {
        List<Integer> new_tail = new ArrayList<>();
        List<Pair<Integer, Integer>> s = new ArrayList<>();
        for (Integer value : tail) {
            int counter = countSplitting(head, value);
            s.add(new Pair<>(value, counter));
        }
        s.sort(Comparator.comparingInt(Pair::getValue));
        for (Pair<Integer, Integer> pair : s) {
            new_tail.add(pair.getKey());
        }
        return new_tail;
    }

    //the function counts the number of equivalence classes induced by head that are split by specializing on value.
    public static int countSplitting(Head head, int value) {
        List<Integer> value_list = new ArrayList<>();
        value_list.add(value);
        List<EquivalenceClass> new_ec_list = updateEquivalenceClasses(head, value_list);
        return new_ec_list.size() - head.getInducedEquivalenceClasses().size();
    }

    public static void writeDataLineByLine(String filePath) {
        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter output_file = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(output_file);

            for (EquivalenceClass ec : best_anonymization.getInducedEquivalenceClasses()) {
                // adding header to csv
                String[] header = {"id", "gender", "age", "hypertension", "heart disease", "ever married", "work type", "residence type", "avg glucose level", "bmi", "smoking status", "stroke"};
                writer.writeNext(header);
                Tuple t = ec.getTuple_list().get(0).getValue();
                writer.writeNext(t.toStringArray(mapValueToNumber));
                for (Pair<Patient, Tuple> tuple : ec.getTuple_list()) {
                    Patient patient = tuple.getKey();
                    writer.writeNext(patient.toStringArray());
                }
                writer.writeNext(new String[0]);
            }

            // closing writer connection
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
               Indexes    |  Attribute
             0 - 1        gender
              2 - 10       age
            11 - 12       hypertension
            13 - 14       heart disease
            15 - 16       ever married
            17 - 21       work type
            22 - 23       residence type
            24 - 30       avg glucose level
            31 - 37       bmi
            38 - 41       smoking status
            42 - 43       stroke
     */
    private static void anonymizationToStringArray() {
        System.out.println("\nThe anonymization is:");
        for (Integer val : best_anonymization.getAnonymization()) {
            if (val >= 0 && val <= 1) {
                System.out.println("gender: " + mapValueToNumber[val]);
            } else if (val >= 2 && val <= 4) {
                Pair<Integer, Integer> pair = (Pair<Integer, Integer>) mapValueToNumber[val];
                System.out.println("age: [" + pair.getKey() + " , " + pair.getValue() + "]");
            } else if (val >= 5 && val <= 6) {
                System.out.println("hypertension: " + booleanToString((Boolean) mapValueToNumber[val]));
            } else if (val >= 7 && val <= 8) {
                System.out.println("heart disease: " + booleanToString((Boolean) mapValueToNumber[val]));
            } else if (val >= 9 && val <= 10) {
                System.out.println("ever married: " + booleanToString((Boolean) mapValueToNumber[val]));
            } else if (val >= 11 && val <= 15) {
                System.out.println("work type: " + mapValueToNumber[val]);
            } else if (val >= 16 && val <= 17) {
                System.out.println("residence type: " + mapValueToNumber[val]);
            } else if (val >= 18 && val <= 20) {
                Pair<Integer, Integer> pair = (Pair<Integer, Integer>) mapValueToNumber[val];
                System.out.println("avg glucose level: [" + pair.getKey() + " , " + pair.getValue() + "]");
            } else if (val >= 21 && val <= 23) {
                Pair<Integer, Integer> pair = (Pair<Integer, Integer>) mapValueToNumber[val];
                System.out.println("bmi: [" + pair.getKey() + " , " + pair.getValue() + "]");
            } else if (val >= 24 && val <= 27) {
                System.out.println("smoking status: " + mapValueToNumber[val]);
            } else {
                System.out.println("stroke: " + booleanToString((Boolean) mapValueToNumber[val]));
            }
        }
    }

    private static String booleanToString(boolean bool) {
        if (bool)
            return "1";
        return "0";
    }


}

