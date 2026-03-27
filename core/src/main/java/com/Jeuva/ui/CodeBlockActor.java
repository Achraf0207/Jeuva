package com.Jeuva.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class CodeBlockActor extends Table {

    private String codeText;

    // On ajoute un paramètre "background" au constructeur
    public CodeBlockActor(String text, BitmapFont font, Drawable background) {
        this.codeText = text;

        Label.LabelStyle style = new Label.LabelStyle(font, Color.valueOf("#E5C07B"));
        Label label = new Label(text, style);

        this.add(label).pad(10, 20, 10, 20); // Marges internes (haut, gauche, bas, droite)

        // On applique le fond visuel au bloc
        this.setBackground(background);
    }

    public String getCodeText() {
        return codeText;
    }
}
