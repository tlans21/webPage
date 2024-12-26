package HomePage.domain.model.enumData;

public enum LikeTargetType {
    RESTAURANT("RESTAURANT"),
    BOARD("BOARD"),
    COMMENT("COMMENT"),
    REVIEW("REVIEW");



    private final String code;

    LikeTargetType(String code){
        this.code = code;
    }
    public String getCode(){
        return this.code;
    }
}
