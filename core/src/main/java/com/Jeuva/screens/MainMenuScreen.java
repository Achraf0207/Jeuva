package com.Jeuva.screens;

import com.Jeuva.Jeuva;
import com.Jeuva.models.LevelData;
import com.Jeuva.utils.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {

    private final Jeuva game;
    private Stage stage;
    private BitmapFont font;
    private BitmapFont titleFont;
    private Texture bgTexture;

    public MainMenuScreen(Jeuva game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        // On génère deux tailles de police : une pour le titre, une pour les boutons
        titleFont = FontManager.generateCodeFont(72); // Un gros titre !
        font = FontManager.generateCodeFont(36);

        // --- LE FOND ---
        bgTexture = new Texture(Gdx.files.internal("images/mist-forest.png"));
        Image background = new Image(bgTexture);
        background.setSize(1280, 720);
        // On peut assombrir un peu le fond pour faire ressortir le menu
        background.setColor(0.5f, 0.5f, 0.5f, 1f);
        stage.addActor(background);

        // --- LE TITRE ---
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.valueOf("#FFCC00")); // Doré
        Label titre = new Label("JEUVA : LE MAGE CODEUR", titleStyle);
        titre.setPosition(200, 500); // Centré en haut
        stage.addActor(titre);

        // --- BOUTON 1 : JOUER ---
        Label.LabelStyle btnStyle = new Label.LabelStyle(font, Color.WHITE);
        Label btnJouer = new Label("> ENTRER DANS LE DONJON <", btnStyle);
        btnJouer.setPosition(380, 350);
        stage.addActor(btnJouer);

        btnJouer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // On crée le palier 1 et on lance le jeu !
                LevelData palier1 = new LevelData(
                    1,
                    "images/mushroom-monster.png",
                    "System.out.print(\"Boule de feu\");",
                    new String[]{"\");", "System.out.print(\"", "Boule de feu"}
                );
                game.setScreen(new GameScreen(game, palier1));
                dispose(); // On détruit le menu pour libérer la mémoire
            }
        });

        // --- BOUTON 2 : COURS (Le Grimoire) ---
        Label btnCours = new Label("> CONSULTER LE GRIMOIRE (Cours) <", btnStyle);
        btnCours.setPosition(320, 250);
        stage.addActor(btnCours);

        btnCours.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // On lance l'écran du Grimoire !
                game.setScreen(new CourseScreen(game));
                dispose(); // On détruit le menu principal actuel
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (font != null) font.dispose();
        if (titleFont != null) titleFont.dispose();
        if (bgTexture != null) bgTexture.dispose();
    }
}
