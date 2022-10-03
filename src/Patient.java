
/*
This class represent a record in the data set
 */
public class Patient {
    private int id;
    private String gender;
    private double age;
    private boolean hypertension;
    private boolean heart_disease;
    private boolean ever_married;
    private String work_type;
    private String residence_type;
    private double avg_glucose_level;
    private double bmi;
    private String smoking_status;
    private boolean stroke;

    public Patient(String[] metadata) {
        this.id = Integer.parseInt(metadata[0]);
        this.gender = metadata[1];
        this.age = Double.parseDouble(metadata[2]);
        this.hypertension = metadata[3].equals("1");
        this.heart_disease = metadata[4].equals("1");
        this.ever_married = metadata[5].equals("Yes");
        this.work_type = metadata[6];
        this.residence_type = metadata[7];
        this.avg_glucose_level = Double.parseDouble(metadata[8]);
        this.bmi = Double.parseDouble(metadata[9]);
        this.smoking_status = metadata[10];
        this.stroke = metadata[11].equals("1");
    }

    public Patient(Patient other) {
        this.id = other.id;
        this.gender = new String(other.gender);
        this.age = other.age;
        this.hypertension = other.hypertension;
        this.heart_disease = other.heart_disease;
        this.ever_married = other.ever_married;
        this.work_type = new String(other.work_type);
        this.residence_type = new String(other.residence_type);
        this.avg_glucose_level = other.avg_glucose_level;
        this.bmi = other.bmi;
        this.smoking_status = new String(other.smoking_status);
        this.stroke = other.stroke;
    }

    public String toString() {
        return "Patient [id= " + this.id + ", gender= " + this.gender + ", age= " + this.age + ", hypertension= " + this.hypertension
                + ", heart disease= " + this.heart_disease + ", ever married= " + this.ever_married + ", work type= " + work_type
                + ", residence type= " + this.residence_type +  ", avg glucose level= " + this.avg_glucose_level + ", bmi= " + this.bmi
                + ", smoking status= " + this.smoking_status + ", stroke= " + this.stroke + "]";
    }

    public String[] toStringArray(){
        return new String[]{Integer.toString(id),gender,Double.toString(age),booleanToString(hypertension), booleanToString(heart_disease)
                             ,booleanToString(ever_married),work_type,residence_type,Double.toString(avg_glucose_level)
                             ,Double.toString(bmi),smoking_status,booleanToString(stroke)};
    }

    public String booleanToString(boolean bool){
        if(bool)
            return "1";
        return "0";
    }

    public boolean equals(Patient other) {
        return this.id == other.id && this.gender.equals(other.gender) && this.age == other.age && this.hypertension == other.hypertension
                && this.heart_disease == other.heart_disease && this.ever_married == other.ever_married && this.work_type.equals(other.work_type)
                && this.residence_type.equals(other.residence_type) && this.avg_glucose_level == other.avg_glucose_level && this.bmi == other.bmi
                && this.smoking_status.equals(other.smoking_status) && this.stroke == other.stroke;
    }

    public String getGender() {
        return gender;
    }
    public String getResidenceType() {
        return residence_type;
    }


    public double getAge() {
        return age;
    }

    public boolean isHypertension() {
        return hypertension;
    }

    public boolean isHeartDisease() {
        return heart_disease;
    }

    public boolean isEverMarried() {
        return ever_married;
    }

    public String getWorkType() {
        return work_type;
    }

    public double getAvgGlucoseLevel() {
        return avg_glucose_level;
    }

    public double getBmi() {
        return bmi;
    }

    public String getSmokingStatus() {
        return smoking_status;
    }
    public boolean isStroke() {
        return stroke;
    }

}
