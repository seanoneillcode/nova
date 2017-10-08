package com.mygdx.game.core;

import java.lang.StringBuilder;
import java.util.List;
import java.util.Arrays;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Dialog {

    private static final float TIME_PER_CHAR = 8.0f;
    private static final int CHAR_PER_LINE = 40;

    public String line;
    float charTimer;
    int charIndex;

    public Dialog(String line) {
        this.line = parseLine(line);
        charTimer = 0;
        charIndex = 0;
    }

    private String parseLine(String line) {
        int lineCount = 0;
        StringBuilder sb = new StringBuilder();
        List<String> words = Arrays.asList(line.split(" "));
        for (String word : words) {
            if (word.length() + lineCount > CHAR_PER_LINE) {
                sb.append("\r\n");
                lineCount = 0;
            }
            lineCount = lineCount + word.length();
            sb.append(word);
            sb.append(" ");
        }
        return sb.toString();
    }

    public void reset() {
        charIndex = 0;
        charTimer = 0;
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        charTimer = charTimer + (TIME_PER_CHAR * delta);
        charIndex = (int) charTimer;
        if (charIndex > line.length()) {
            charIndex = line.length();
        }
    }

    public String getLine() {
        return this.line.substring(0, charIndex);
    }
}
