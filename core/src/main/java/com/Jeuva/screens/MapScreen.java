package com.Jeuva.screens;

import com.Jeuva.Jeuva;
import com.Jeuva.models.LevelData;
import com.Jeuva.utils.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MapScreen implements Screen {

    private final Jeuva game;
    private Stage stage;
    private BitmapFont titleFont;
    private BitmapFont levelFont;
    private Texture bgTexture;
    private Texture nodeUnlockedTexture;
    private Texture nodeLockedTexture;
    private ShapeRenderer shapeRenderer; // 👉 L'outil pour dessiner le chemin !
    private Preferences sauvegarde;

    public MapScreen(Jeuva game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        shapeRenderer = new ShapeRenderer();

        titleFont = FontManager.generateCodeFont(48);
        levelFont = FontManager.generateCodeFont(32);

        sauvegarde = Gdx.app.getPreferences("jeuva_save");
        int niveauMaxDebloque = sauvegarde.getInteger("maxLevel", 1);

        // --- LE FOND ---
        bgTexture = new Texture(Gdx.files.internal("images/mist-forest.png"));
        Image background = new Image(bgTexture);
        background.setSize(1280, 720);
        background.setColor(0.5f, 0.5f, 0.5f, 1f);
        stage.addActor(background);

        // ...
        background.setColor(0.5f, 0.5f, 0.5f, 1f);
        stage.addActor(background);

        // 👉 LA CORRECTION EST ICI : On crée un Acteur spécial pour les lignes
        // On l'ajoute juste après le fond, donc il sera dessiné par-dessus la forêt, mais sous les ronds !
        com.badlogic.gdx.scenes.scene2d.Actor lignesChemin = new com.badlogic.gdx.scenes.scene2d.Actor() {
            @Override
            public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float parentAlpha) {
                batch.end(); // On met le pinceau à images en pause

                shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.valueOf("#DDDDDD"));

                // rectLine(x1, y1, x2, y2, epaisseur)
                shapeRenderer.rectLine(290, 240, 590, 440, 15); // Ligne Nv 1 -> Nv 2
                shapeRenderer.rectLine(590, 440, 890, 290, 15); // Ligne Nv 2 -> Nv 3

                shapeRenderer.end(); // On a fini les lignes

                batch.begin(); // On relance le pinceau à images pour la suite (les ronds et textes)
            }
        };
        stage.addActor(lignesChemin);
        // ... (la suite de ton code avec la création des ronds reste identique)

        // --- GÉNÉRATION DES "RONDS" (Nœuds) POUR LA CARTE ---
        // 1. Un beau rond Vert pour les niveaux débloqués
        Pixmap pixmap = new Pixmap(80, 80, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.valueOf("#55FF55"));
        pixmap.fillCircle(40, 40, 40);
        nodeUnlockedTexture = new Texture(pixmap);

        // 2. Un rond Gris foncé pour les niveaux bloqués
        pixmap.setColor(Color.valueOf("#555555"));
        pixmap.fillCircle(40, 40, 40);
        nodeLockedTexture = new Texture(pixmap);
        pixmap.dispose();

        // --- LE TITRE ET RETOUR ---
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.valueOf("#FFCC00"));
        Label titre = new Label("LE CHEMIN DU MAGE", titleStyle);
        titre.setPosition(400, 620);
        stage.addActor(titre);

        Label.LabelStyle btnStyle = new Label.LabelStyle(levelFont, Color.valueOf("#FF5555"));
        Label btnRetour = new Label("< MENU", btnStyle);
        btnRetour.setPosition(30, 650);
        stage.addActor(btnRetour);
        btnRetour.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // --- PLACEMENT DES NŒUDS SUR LA CARTE ---
        creerPointNiveau(1, "La Voix", 250, 200, niveauMaxDebloque);
        creerPointNiveau(2, "Bourse de Mana", 550, 400, niveauMaxDebloque);
        creerPointNiveau(3, "La Boucle", 850, 250, niveauMaxDebloque);
    }

    private void creerPointNiveau(int levelId, String nomNiveau, float x, float y, int maxLevel) {
        boolean estDebloque = (levelId <= maxLevel);

        // On crée un Groupe qui va contenir le rond ET le texte
        Group nodeGroup = new Group();
        nodeGroup.setPosition(x, y);
        nodeGroup.setSize(80, 80);

        // L'image du rond (Vert ou Gris)
        Image nodeImage = new Image(estDebloque ? nodeUnlockedTexture : nodeLockedTexture);
        nodeImage.setSize(80, 80);
        nodeGroup.addActor(nodeImage);

        // Le numéro du niveau par-dessus le rond
        Label.LabelStyle numStyle = new Label.LabelStyle(titleFont, Color.BLACK); // Texte noir sur le rond
        Label numLabel = new Label(estDebloque ? String.valueOf(levelId) : "🔒", numStyle);
        numLabel.setSize(80, 80);
        numLabel.setAlignment(Align.center);
        nodeGroup.addActor(numLabel);

        // Le nom du niveau en dessous du rond
        Label.LabelStyle nameStyle = new Label.LabelStyle(levelFont, estDebloque ? Color.WHITE : Color.GRAY);
        Label nameLabel = new Label(nomNiveau, nameStyle);
        nameLabel.setPosition(-20, -40); // On le décale un peu vers le bas
        nodeGroup.addActor(nameLabel);

        if (estDebloque) {
            nodeGroup.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    LevelData niveauChoisi = LevelData.getLevel(levelId);
                    if (niveauChoisi != null) {
                        game.setScreen(new GameScreen(game, niveauChoisi));
                        dispose();
                    }
                }
            });
        }
        stage.addActor(nodeGroup);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw(); // Le stage va tout dessiner dans le bon ordre maintenant !
    }

    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (titleFont != null) titleFont.dispose();
        if (levelFont != null) levelFont.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (nodeUnlockedTexture != null) nodeUnlockedTexture.dispose();
        if (nodeLockedTexture != null) nodeLockedTexture.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
