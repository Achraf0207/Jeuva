package com.Jeuva.screens;

import com.Jeuva.Jeuva;
import com.Jeuva.utils.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {

    private final Jeuva game;
    private Stage stage;
    private BitmapFont titleFont;
    private BitmapFont buttonFont;
    private static final String PREFS_NAME = "jeuva_global_prefs";
    private static final String LAST_SLOT_KEY = "last_used_slot";

    // --- Les Textures ---
    private Texture bgTexture;
    private Texture btnUpTex;
    private Texture btnDownTex;
    private Texture btnHoverTex;
    private Texture titleBannerTex;
    private TextButton.TextButtonStyle buttonStyle;
    private NinePatchDrawable zoneDrawable;

    public MainMenuScreen(Jeuva game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        // 1. Les polices d'écriture
        titleFont = FontManager.generateCodeFont(80); // Un ÉNORME titre pour l'accueil
        buttonFont = FontManager.generateCodeFont(30);

        // 2. Le fond d'écran
        bgTexture = new Texture(Gdx.files.internal("images/Battleground.png"));
        Image background = new Image(bgTexture);
        background.setSize(1280, 720);
        stage.addActor(background);

        // 3. --- STYLE DES BOUTONS (KENNEY) ---
        btnUpTex = new Texture(Gdx.files.internal("images/UI/Double/button_brown.png"));
        btnDownTex = new Texture(Gdx.files.internal("images/UI/Double/button_grey.png"));
        btnHoverTex = new Texture(Gdx.files.internal("images/UI/Double/button_red.png"));
        titleBannerTex = new Texture(Gdx.files.internal("images/UI/Double/banner_classic_curtain.png"));

        com.badlogic.gdx.graphics.g2d.NinePatch titleBannerPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(titleBannerTex, 20, 20, 20, 20);
        com.badlogic.gdx.graphics.g2d.NinePatch btnUpPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(btnUpTex, 15, 15, 15, 15);
        com.badlogic.gdx.graphics.g2d.NinePatch btnDownPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(btnDownTex, 15, 15, 15, 15);
        com.badlogic.gdx.graphics.g2d.NinePatch btnHoverPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(btnHoverTex, 15, 15, 15, 15);

        com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable titleDrawable = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(titleBannerPatch);
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new NinePatchDrawable(btnUpPatch);
        buttonStyle.down = new NinePatchDrawable(btnDownPatch);
        buttonStyle.over = new NinePatchDrawable(btnHoverPatch);
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.valueOf("#5C4033"); // Marron bien lisible

        Table titleBannerTable = new Table();
        titleBannerTable.setBackground(titleDrawable);
        titleBannerTable.setSize(500, 150);

        // 👉 ON LA SUSPEND AU PLAFOND !
        // L'écran fait 720 de haut. On la place vers 600 pour que le haut de l'image
        // touche le bord de l'écran, ce qui donne l'effet "suspendu".
        // (Ajuste le 600 selon l'effet visuel que tu préfères !)
        titleBannerTable.setPosition((1280 - 500) / 2f, 570);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        Label titleLabel = new Label("JEUVA", titleStyle);
        titleBannerTable.add(titleLabel).padBottom(15); // padBottom pour remonter/descendre le texte dans la bannière

        // On l'ajoute directement sur le Stage (indépendante des boutons)
        stage.addActor(titleBannerTable);

        // 5. --- LA TABLE CENTRALE (Pour les boutons) ---
        Table menuTable = new Table();
        menuTable.setFillParent(true);
        menuTable.center().padTop(100);
        stage.addActor(menuTable);

        // --- 1. BOUTON CONTINUER (Dernier slot utilisé) ---
        TextButton btnContinuer = new TextButton("CONTINUER", buttonStyle);
        Preferences globalPrefs = Gdx.app.getPreferences(PREFS_NAME);
        String lastSlot = globalPrefs.getString(LAST_SLOT_KEY, "");

        boolean aUneSauvegarde = false;
        String slotACharger = lastSlot;

        // On inspecte les 3 slots pour voir si au moins un n'est pas vide
        for (int i = 1; i <= 3; i++) {
            Preferences slotPrefs = Gdx.app.getPreferences("slot" + i);
            if (slotPrefs.getInteger("maxLevel", 0) > 0) {
                aUneSauvegarde = true;

                // Si le dernier slot joué est vide (ex: l'enfant vient de le supprimer),
                // on redirige "Continuer" vers la première sauvegarde valide trouvée.
                if (slotACharger.isEmpty() || Gdx.app.getPreferences(slotACharger).getInteger("maxLevel", 0) == 0) {
                    slotACharger = "slot" + i;
                }
            }
        }

        // Si absolument TOUS les slots sont vides, on grise le bouton
        if (!aUneSauvegarde) {
            btnContinuer.setDisabled(true);
            btnContinuer.setColor(Color.GRAY);
        }

        menuTable.add(btnContinuer).size(400, 70).padBottom(15).row();

        // On crée une variable finale pour le Listener
        final String slotValideFinal = slotACharger;

        btnContinuer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!btnContinuer.isDisabled() && !slotValideFinal.isEmpty()) {
                    // On charge le bon slot et on lance la carte
                    game.setCurrentSlot(slotValideFinal);
                    game.setScreen(new MapScreen(game));
                    dispose();
                }
            }
        });

        // --- 2. BOUTON NOUVELLE PARTIE ---
        TextButton btnNouvelle = new TextButton("NOUVELLE PARTIE", buttonStyle);
        menuTable.add(btnNouvelle).size(400, 70).padBottom(15).row();

        btnNouvelle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // On redirige vers l'écran de choix de slot pour écraser/créer
                // Ou plus simple : on ouvre l'écran des sauvegardes avec un flag "mode création"
                showSaveSlotsPopup(true);
            }
        });

        // --- 3. BOUTON SAUVEGARDES (Gestion des slots) ---
        TextButton btnSauvegardes = new TextButton("SAUVEGARDES", buttonStyle);
        menuTable.add(btnSauvegardes).size(400, 70).padBottom(15).row();

        btnSauvegardes.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showSaveSlotsPopup(false);
            }
        });

        TextButton btnGrimoire = new TextButton("LE GRIMOIRE", buttonStyle);
        menuTable.add(btnGrimoire).size(400, 80).padBottom(20).row();
        btnGrimoire.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CourseScreen(game));
                dispose();
            }
        });

        // --- 4. BOUTON QUITTER ---
        TextButton btnQuitter = new TextButton("QUITTER", buttonStyle);
        menuTable.add(btnQuitter).size(400, 70);

        btnQuitter.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    private void showSaveSlotsPopup(boolean isNewGame) {
        // On crée un grand panneau au milieu
        Table savePanel = new Table();
        Texture panelTex = new Texture(Gdx.files.internal("images/UI/Double/panel_brown_damaged.png"));
        com.badlogic.gdx.graphics.g2d.NinePatch panelPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(panelTex, 15, 15, 15, 15);
        com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable popupBackground = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(panelPatch);

        savePanel.setBackground(popupBackground);
        savePanel.setSize(800, 500);
        savePanel.setPosition((1280 - 800) / 2f, (720 - 500) / 2f);

        // 👉 1. BLOQUER LES CLICS EN ARRIÈRE-PLAN
        savePanel.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.enabled);
        stage.addActor(savePanel);

        // 👉 2. LE STYLE MARRON POUR TOUT LE TEXTE DE LA POPUP
        Label.LabelStyle texteMarronStyle = new Label.LabelStyle(buttonFont, Color.valueOf("#5C4033"));

        Label title = new Label(isNewGame ? "CHOISIR UN EMPLACEMENT" : "VOS SAUVEGARDES", texteMarronStyle);
        savePanel.add(title).colspan(3).padBottom(40).row();

        for (int i = 1; i <= 3; i++) {
            final String slotName = "slot" + i;
            Preferences slotPrefs = Gdx.app.getPreferences(slotName);
            int level = slotPrefs.getInteger("maxLevel", 0);

            String info = (level == 0) ? "VIDE" : "Niveau " + level;

            // 👉 3. LE TEXTE MARRON ET CENTRÉ
            Label lblSlot = new Label("EMPLACEMENT " + i + "\n" + info, texteMarronStyle);
            lblSlot.setAlignment(Align.center);

            TextButton btnAction = new TextButton(isNewGame ? "CHOISIR" : (level == 0 ? "---" : "CHARGER"), buttonStyle);
            TextButton btnSupprimer = new TextButton("X", buttonStyle);

            // 👉 4. LES BONS ESPACEMENTS ET ALIGNEMENTS
            savePanel.add(lblSlot).width(200).padRight(30).padBottom(20);
            savePanel.add(btnAction).size(220, 60).padRight(15).padBottom(20);
            savePanel.add(btnSupprimer).size(60, 60).padBottom(20).row();

            // LOGIQUE CHARGER / CRÉER
            btnAction.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (isNewGame) {
                        // Reset du slot complet pour une nouvelle partie (efface l'ancien code joueur)
                        slotPrefs.clear();
                        slotPrefs.putInteger("maxLevel", 1);
                        slotPrefs.flush();
                    }
                    if (level > 0 || isNewGame) {
                        Gdx.app.getPreferences(PREFS_NAME).putString(LAST_SLOT_KEY, slotName).flush();
                        game.setCurrentSlot(slotName);
                        game.setScreen(new MapScreen(game));
                        dispose();
                    }
                }
            });

            // LOGIQUE SUPPRIMER
            btnSupprimer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    slotPrefs.clear();
                    slotPrefs.flush();
                    savePanel.remove();
                    showSaveSlotsPopup(isNewGame);
                }
            });
        }

        // Bouton fermer
        TextButton btnFermer = new TextButton("RETOUR", buttonStyle);
        savePanel.add(btnFermer).colspan(3).padTop(20);
        btnFermer.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { savePanel.remove(); }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
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
        if (buttonFont != null) buttonFont.dispose();

        // On n'oublie pas de nettoyer nos belles textures Kenney !
        if (bgTexture != null) bgTexture.dispose();
        if (btnUpTex != null) btnUpTex.dispose();
        if (btnDownTex != null) btnDownTex.dispose();
        if (btnHoverTex != null) btnHoverTex.dispose();
        if (titleBannerTex != null) titleBannerTex.dispose();
    }
}
