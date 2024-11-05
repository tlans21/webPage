package HomePage.domain.model.enumData;

public enum LikeTargetType {
    RESTAURANT("R"),
    BOARD("B");

    private final String code;

    LikeTargetType(String code){
        this.code = code;
    }
    public String getCode(){
        return this.code;
    }
}
