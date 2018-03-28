package com.kimjio.easyadb.tool;

import com.sun.javafx.tk.Toolkit;

import java.io.InputStream;

public final class FontTools {

    public static String[] notoFonts = {
            "/font/notokr/NotoSansCJKkr-Regular.otf",
            "/font/notokr/NotoSansCJKkr-Medium.otf",
            "/font/notokr/NotoSansCJKkr-Light.otf",
            "/font/notokr/NotoSansCJKkr-Bold.otf",
            "/font/notokr/NotoSansCJKkr-Thin.otf"};

    public static String[] nanumCodingFonts = {
            "/font/nanum/coding/NanumGothicCoding.ttf",
            "/font/nanum/coding/NanumGothicCoding-Bold.ttf"
    };

    public static void loadFont(Class cla, double size, String... fonts) {
        for (String font : fonts) {
            loadFont(cla.getResourceAsStream(font), size);
        }
    }

    private static void loadFont(InputStream in, double size) {
        if (size <= 0) {
            size = getDefaultSystemFontSize();
        }
        Toolkit.getToolkit().getFontLoader().loadFont(in, size);
    }

    private static float defaultSystemFontSize = -1;
    private static float getDefaultSystemFontSize() {
        if (defaultSystemFontSize == -1) {
            defaultSystemFontSize =
                    Toolkit.getToolkit().getFontLoader().getSystemFontSize();
        }
        return defaultSystemFontSize;
    }
}
