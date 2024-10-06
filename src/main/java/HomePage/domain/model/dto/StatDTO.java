package HomePage.domain.model.dto;

public class StatDTO {
    private String title;
    private long value;
    private double change;
    private String icon;

    public StatDTO(String title, long value, double change, String icon) {
        this.title = title;
        this.value = value;
        this.change = change;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
