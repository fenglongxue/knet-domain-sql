package cn.knet.enums;


public enum StatusEnum {
    ABLE("ABLE", "正常"),
    DISABLE("DISABLE", "停用");
    String value;
    String text;

    private StatusEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}