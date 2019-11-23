package SDA.model;

public enum GenderEnum {

    F, M;


    public static GenderEnum getGenderByValue(String value) {
        for (GenderEnum genderEnum : values()) {
            if (genderEnum.name().equalsIgnoreCase(value)) {
                return genderEnum;
            }
        }
        return null;
    }


}
