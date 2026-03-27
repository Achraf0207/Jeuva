package com.Jeuva;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.Jeuva.screens.GameScreen; // On va la créer juste après

public class Jeuva extends Game {
    // Le SpriteBatch est l'outil qui dessine les images.
    // On le met ici pour le partager entre tous les écrans et économiser de la mémoire.
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // C'est ici qu'on définit l'écran de démarrage.
        // Pour l'instant, on lance directement l'écran de jeu (on fera le menu plus tard).
        this.setScreen(new GameScreen(this));
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
