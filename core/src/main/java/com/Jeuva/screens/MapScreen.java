package com.Jeuva.screens;

import com.Jeuva.Jeuva;
import com.Jeuva.models.LevelData;
import com.Jeuva.ui.AnimatedActor;
import com.Jeuva.utils.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class MapScreen implements Screen {

    private final Jeuva game;
    private Stage stage;
    private BitmapFont titleFont;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;

    // --- UI et Textures ---
    private Texture bgTexture;
    private Texture btnUpTex;
    private Texture btnDownTex;
    private Texture btnHoverTex;
    private Texture levelBtnTex; // 👉 NOUVEAU : Pour les boutons ronds des niveaux
    private Texture bannerTex;   // 👉 NOUVEAU : Pour le titre
    private Texture lockTex;
    private Texture mageIdleSheet;

    // --- Sauvegarde ---
    private int maxLevelUnlocked;
    private final int TOTAL_LEVELS = 10;

    // --- Pour tracer le chemin ---
    private Vector2[] levelCenters;

    public MapScreen(Jeuva game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
        shapeRenderer = new ShapeRenderer();
        levelCenters = new Vector2[TOTAL_LEVELS];

        // 1. Charger la sauvegarde
        Preferences sauvegarde = Gdx.app.getPreferences(game.getCurrentSlot());
        maxLevelUnlocked = sauvegarde.getInteger("maxLevel", 1);

        // 2. Polices et Fond
        titleFont = FontManager.generateCodeFont(50);
        font = FontManager.generateCodeFont(30);

        bgTexture = new Texture(Gdx.files.internal("images/Battleground.png"));
        Image background = new Image(bgTexture);
        background.setSize(1280, 720);
        stage.addActor(background);

        // ==========================================
        // 3. LA BANNIÈRE DU TITRE
        // ==========================================
        bannerTex = new Texture(Gdx.files.internal("images/UI/Double/banner_hanging.png"));
        com.badlogic.gdx.graphics.g2d.NinePatch bannerPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(bannerTex, 20, 20, 20, 20);
        NinePatchDrawable bannerDrawable = new NinePatchDrawable(bannerPatch);

        Table titleTable = new Table();
        titleTable.setBackground(bannerDrawable);
        titleTable.setSize(500, 130);
        titleTable.setPosition((1280 - 500) / 2f, 600); // Suspendue en haut !

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label titleLabel = new Label("CARTE DU MONDE", titleStyle);
        titleTable.add(titleLabel).padTop(10);
        stage.addActor(titleTable);

        // ==========================================
        // 4. STYLES DES BOUTONS (Rectangles et Ronds)
        // ==========================================

        // A. Style standard (Bouton RECTANGLE pour le retour)
        btnUpTex = new Texture(Gdx.files.internal("images/UI/Double/button_brown.png"));
        btnDownTex = new Texture(Gdx.files.internal("images/UI/Double/button_grey.png"));
        btnHoverTex = new Texture(Gdx.files.internal("images/UI/Double/button_red.png"));

        com.badlogic.gdx.graphics.g2d.NinePatch btnUpPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(btnUpTex, 15, 15, 15, 15);
        com.badlogic.gdx.graphics.g2d.NinePatch btnDownPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(btnDownTex, 15, 15, 15, 15);
        com.badlogic.gdx.graphics.g2d.NinePatch btnHoverPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(btnHoverTex, 15, 15, 15, 15);

        TextButton.TextButtonStyle standardButtonStyle = new TextButton.TextButtonStyle();
        standardButtonStyle.up = new NinePatchDrawable(btnUpPatch);
        standardButtonStyle.down = new NinePatchDrawable(btnDownPatch);
        standardButtonStyle.over = new NinePatchDrawable(btnHoverPatch);
        standardButtonStyle.font = font;
        standardButtonStyle.fontColor = Color.valueOf("#5C4033");

        // B. Style pour les Niveaux (Bouton ROND/CERCLE)
        // On n'utilise pas de NinePatch ici pour ne pas déformer le cercle !
        levelBtnTex = new Texture(Gdx.files.internal("images/UI/Double/round_brown.png"));
        TextureRegionDrawable levelDrawable = new TextureRegionDrawable(new TextureRegion(levelBtnTex));

        TextButton.TextButtonStyle levelButtonStyle = new TextButton.TextButtonStyle();
        levelButtonStyle.up = levelDrawable;
        levelButtonStyle.down = levelDrawable; // Tu peux ajouter un btn_level_down.png plus tard si tu veux
        levelButtonStyle.font = font;
        levelButtonStyle.fontColor = Color.valueOf("#5C4033");

        TextButton.TextButtonStyle lockedLevelStyle = new TextButton.TextButtonStyle(levelButtonStyle);
        lockedLevelStyle.fontColor = Color.GRAY;

        // ==========================================
        // 5. LA GRILLE DES NIVEAUX (Centrée)
        // ==========================================
        int cols = 5;
        float spacingX = 220f;
        float spacingY = 200f;

        // Formule pour un centrage horizontal parfait
        float startX = (1280 - (100 + (cols - 1) * spacingX)) / 2f;
        float startY = 400f; // Assez bas pour ne pas toucher le titre

        Vector2 magePosition = new Vector2(0, 0);
        lockTex = new Texture(Gdx.files.internal("images/lock.png"));

        for (int i = 0; i < TOTAL_LEVELS; i++) {
            int levelId = i + 1;
            int row = i / cols;
            int col = i % cols;

            // Effet Serpentin (Snake)
            if (row % 2 != 0) {
                col = (cols - 1) - col;
            }

            float x = startX + col * spacingX;
            float y = startY - row * spacingY;

            levelCenters[i] = new Vector2(x + 50, y + 40);

            boolean isUnlocked = (levelId <= maxLevelUnlocked);

            // 👉 On utilise le nouveau levelButtonStyle !
            TextButton btnLevel = new TextButton(String.valueOf(levelId), isUnlocked ? levelButtonStyle : lockedLevelStyle);
            btnLevel.setSize(100, 80);
            btnLevel.setPosition(x, y);

            if (isUnlocked) {
                btnLevel.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float px, float py) {
                        LevelData data = LevelData.getLevel(levelId);
                        if(data != null) {
                            game.setScreen(new GameScreen(game, data));
                            dispose();
                        }
                    }
                });

                if (levelId == maxLevelUnlocked) {
                    magePosition.set(x, y);
                    btnLevel.addAction(Actions.forever(Actions.sequence(
                        Actions.color(Color.LIGHT_GRAY, 0.5f),
                        Actions.color(Color.WHITE, 0.5f)
                    )));
                }
            } else {
                btnLevel.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);
                btnLevel.setColor(Color.DARK_GRAY);

                Image lockIcon = new Image(lockTex);
                lockIcon.setSize(40, 40);
                lockIcon.setPosition(x + 30, y - 45); // Cadenas décalé sous le rond
                stage.addActor(lockIcon);
            }

            stage.addActor(btnLevel);
        }

        // ==========================================
        // 6. CHARGEMENT DU MAGE
        // ==========================================
        mageIdleSheet = new Texture(Gdx.files.internal("images/mage/Idle.png"));
        int IDLE_COLS = 7;
        TextureRegion[][] tmpIdle = TextureRegion.split(mageIdleSheet, mageIdleSheet.getWidth() / IDLE_COLS, mageIdleSheet.getHeight() / 1);
        TextureRegion[] idleFrames = new TextureRegion[IDLE_COLS];
        for (int i = 0; i < IDLE_COLS; i++) idleFrames[i] = tmpIdle[0][i];

        Animation<TextureRegion> mageIdleAnimation = new Animation<TextureRegion>(0.12f, idleFrames);
        mageIdleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        AnimatedActor mageMap = new AnimatedActor(mageIdleAnimation);
        mageMap.setSize(mageIdleSheet.getWidth() / IDLE_COLS, mageIdleSheet.getHeight());
        mageMap.setScale(1.5f);
        // Le mage est placé PILE sur le rond (décalé à gauche et vers le haut)
        mageMap.setPosition(magePosition.x - 30, magePosition.y + 80);
        stage.addActor(mageMap);

        // ==========================================
        // 7. BOUTON RETOUR MENU
        // ==========================================
        // 👉 On utilise le standardButtonStyle (rectangulaire)
        TextButton btnRetour = new TextButton("MENU", standardButtonStyle);
        btnRetour.setSize(250, 60);
        btnRetour.setPosition(30, 30);
        stage.addActor(btnRetour);

        btnRetour.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.valueOf("#8B5A2B"));

        for (int i = 0; i < TOTAL_LEVELS - 1; i++) {
            shapeRenderer.rectLine(levelCenters[i], levelCenters[i+1], 12f);
        }
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (titleFont != null) titleFont.dispose();
        if (font != null) font.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();

        if (bgTexture != null) bgTexture.dispose();
        if (btnUpTex != null) btnUpTex.dispose();
        if (btnDownTex != null) btnDownTex.dispose();
        if (btnHoverTex != null) btnHoverTex.dispose();
        if (levelBtnTex != null) levelBtnTex.dispose(); // Nouveau
        if (bannerTex != null) bannerTex.dispose();     // Nouveau
        if (lockTex != null) lockTex.dispose();
        if (mageIdleSheet != null) mageIdleSheet.dispose();
    }
}
