package com.Jeuva;

import com.Jeuva.models.LevelData;
import com.Jeuva.screens.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.Jeuva.screens.GameScreen; // On va la créer juste après

public class Jeuva extends Game {
    // Le SpriteBatch est l'outil qui dessine les images.
    // On le met ici pour le partager entre tous les écrans et économiser de la mémoire.
    public SpriteBatch batch;
    private String currentSlot = "slot1";

    @Override
    public void create() {
        this.setScreen(new MainMenuScreen(this));
    }

    public void setCurrentSlot(String slot) { this.currentSlot = slot; }
    public String getCurrentSlot() { return this.currentSlot; }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (screen != null) screen.dispose();
    }
}
