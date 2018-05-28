package com.androidcommonlibrary.constants;


/**
 * This enum is tightly coupled with the enum in res/values/attrs.xml!
 * Make sure their orders stay the same **
 * fontValue - should be mapped with attr.xml enum values
 */
public enum FontConstants {
    ProximaNovaRegular("fonts/RobotoLight.ttf", 0), ProximaNovaSemibold(
            "fonts/RobotoBold.ttf", 1), ProximaNovaLight("fonts/RobotoMedium.ttf", 2),
    ProximaNovaBold("fonts/RobotoRegular.ttf", 3), MuseoSlab500("fonts/RobotoThin.ttf", 4);

    private String fontPathName;
    private int fontValue;

    FontConstants(String fontPathName, int fontValue) {
        this.fontPathName = fontPathName;
        this.fontValue = fontValue;
    }

    /**
     * @param value
     * @return Returns font's file location existing in assets folder by comparing with Font value. Return's default font "ProximaNova-Regular.otf" if fontValue not exist .
     */
    public static String getFontNameFromFontValue(int value) {

        for (FontConstants font : FontConstants.values()) {
            if (font.getFontValue() == value)
                return font.getFontPathName();
        }

        return ProximaNovaRegular.getFontPathName();
    }

    public int getFontValue() {
        return fontValue;
    }

    public String getFontPathName() {
        return fontPathName;
    }
}