package com.mygdx.game.core;

import java.lang.StringBuilder;
import java.lang.Exception;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Conversation {

    private List<Dialog> dialogs;
    private int dialogIndex;

    public Conversation(List<Dialog> dialogs) {
        this.dialogs = dialogs;
        dialogIndex = 0;
    }

    public void reset() {
        dialogIndex = 0;
        for (Dialog dialog : dialogs) {
            dialog.reset();
        }
    }

    public void update() {
        getCurrentDialog().update();
    }

    public boolean isFinished() {
        return dialogIndex == dialogs.size() - 1 && getCurrentDialog().isFinished();
    }

    public Dialog getCurrentDialog() {
        return dialogs.get(dialogIndex);
    }

    public void handleInput() {
        if (!getCurrentDialog().isFinished()) {
            getCurrentDialog().finish();
        } else {
            dialogIndex++;
            if (dialogIndex > dialogs.size() - 1) {
                dialogIndex = dialogs.size() - 1;
            }
        }
    }

    public static class Builder {

        List<Dialog> dialogs;

        public Builder() {
            dialogs = new ArrayList<Dialog>();
        }

        public Builder dialog(Dialog dialog) {
            dialogs.add(dialog);
            return this;
        }

        public Conversation build() {
            if (dialogs == null) {
                throw new RuntimeException("failed to build conversation");
            }
            return new Conversation(this.dialogs);
        }
    }
}
