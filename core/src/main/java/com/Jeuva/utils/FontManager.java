package com.Jeuva.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontManager {

    // Méthode statique pour générer facilement notre police
    public static BitmapFont generateCodeFont(int size) {
        // On charge le fichier .ttf depuis les assets
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/codefont.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();

        // On paramètre la taille et la couleur par défaut
        parameter.size = size;
        parameter.color = Color.WHITE;

        // Optionnel mais recommandé : lisser les bords pour un meilleur rendu
        parameter.minFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
        parameter.magFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;

        BitmapFont font = generator.generateFont(parameter);

        // TRÈS IMPORTANT : libérer le générateur de la mémoire une fois la police créée
        generator.dispose();

        return font;
    }
}
