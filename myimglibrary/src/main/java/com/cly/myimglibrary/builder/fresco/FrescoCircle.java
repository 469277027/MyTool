package com.cly.myimglibrary.builder.fresco;

/**
 * Created by 丛龙宇 on 2017/1/12.
 */

public class FrescoCircle {

    private int borderWidth;
    private String borderColor;

    private FrescoCircle(Builder builder) {
        this.borderWidth = builder.width;
        this.borderColor = builder.color;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public String getBorderColor() {
        return borderColor;
    }

    @Override
    public String toString() {
        return "FrescoCircle{" +
                "borderWidth=" + borderWidth +
                ", borderColor='" + borderColor + '\'' +
                '}';
    }

    public static class Builder {

        private int width = 0;
        private String color = "#00000000";

        public Builder borderWidth(int width) {
            this.width = width;
            return this;
        }

        public Builder borderColor(String color) {
            this.color = color;
            return this;
        }

        public FrescoCircle build() {
            return new FrescoCircle(this);
        }

    }
}
