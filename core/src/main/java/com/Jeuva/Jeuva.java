package com.Jeuva;

import com.Jeuva.models.LevelData;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.Jeuva.screens.GameScreen; // On va la créer juste après

public class Jeuva extends Game {
    // Le SpriteBatch est l'outil qui dessine les images.
    // On le met ici pour le partager entre tous les écrans et économiser de la mémoire.
    public SpriteBatch batch;

    @Override
    public void create() {
        // 1. On fabrique notre premier palier avec nos données
        LevelData palier1 = new LevelData(
            1,
            "images/mushroom-monster.png",
            "System.out.print(\"Boule de feu\");",
            new String[]{"\");", "System.out.print(\"", "Boule de feu"}
        );

        LevelData palier2 = new LevelData(
            2,
            "images/gobelin.png",
            "int degats = 50;",
            new String[]{"50;", "int ", "degats = "}
        );

        // 2. On lance l'écran de jeu en lui donnant ce palier !
        this.setScreen(new GameScreen(this, palier1));
    }

    @Override
    public void render() {
        // Très important : appelle le render() de la classe Game
        // pour qu'il délègue l'affichage à l'écran actif (GameScreen)
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        // On nettoie aussi l'écran actuel
        if (screen != null) screen.dispose();
    }
}
