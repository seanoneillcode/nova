package com.mygdx.game.core;

import java.lang.StringBuilder;
import java.util.List;
import java.util.Arrays;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Dialog {

    private static final float TIME_PER_CHAR = 20.0f;
    private static final int CHAR_PER_LINE = 36;

    public String line;
    float charTimer;
    int charIndex;
    TextureRegion image;
    boolean isLeft;

    public Dialog(String line, TextureRegion image, boolean isLeft) {
        this.line = parseLine(line);
        this.image = image;
        this.isLeft = isLeft;
        if (!isLeft) {
            this.image.flip(true, false);
        }
        charTimer = 0;
        charIndex = 0;
    }

    public TextureRegion getImage() {
        return image;
    }

    public boolean isLeft() {
        return isLeft;
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

    public void finish() {
        charIndex = line.length();
        charTimer = charIndex;
    }

    public boolean isFinished() {
        return charIndex >= line.length();
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
