package com.Jeuva.screens;

import com.Jeuva.Jeuva;
import com.Jeuva.models.LevelData;
import com.Jeuva.ui.AnimatedActor;
import com.Jeuva.ui.CodeBlockActor;
import com.Jeuva.utils.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class GameScreen implements Screen {

    private final Jeuva game;
    private LevelData currentLevel;
    private Stage stage;
    private BitmapFont codeFont;
    private Texture coeurTexture;
    private Image[] coeursUI = new Image[3];
    private Image dragonHeart; // Le coeur du dragon
    private int viesJoueur = 3;
    private Table reserveZone;
    private Table spellZone;
    private com.badlogic.gdx.scenes.scene2d.ui.ScrollPane scrollReserve;
    private com.badlogic.gdx.scenes.scene2d.ui.ScrollPane scrollSpell;

    // Les nouvelles fenêtres de notification
    private Table victoryTable;
    private Table gameOverTable;
    private Table hitPopupTable;

    private Texture bgTexture;
    private Texture zoneColorTexture;
    private Texture blockColorTexture;

    // --- Variables d'animation du Mage ---
    private Texture mageIdleSheet;
    private Texture mageAttackSheet;
    private Texture mageDeathSheet;
    private Animation<TextureRegion> mageIdleAnimation;
    private Animation<TextureRegion> mageAttackAnimation;
    private Animation<TextureRegion> mageDeathAnimation;
    private Texture scrollKnobTex;

    // --- Variables d'animation du Dragon ---
    private Animation<TextureRegion> dragonIdleAnim;
    private Animation<TextureRegion> dragonAttackAnim;
    private Animation<TextureRegion> dragonDeathAnim;
    private com.badlogic.gdx.utils.Array<Texture> dragonTextures = new com.badlogic.gdx.utils.Array<>();

    // --- Variables des Boules de feu animées ---
    private com.badlogic.gdx.utils.Array<Texture> mageFireballTextures = new com.badlogic.gdx.utils.Array<>();
    private Animation<TextureRegion> mageFireballAnim;
    private com.badlogic.gdx.utils.Array<Texture> dragonFireballTextures = new com.badlogic.gdx.utils.Array<>();
    private Animation<TextureRegion> dragonFireballAnim;


    public GameScreen(Jeuva game, LevelData level) {
        this.game = game;
        this.currentLevel = level;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        codeFont = FontManager.generateCodeFont(24);

        bgTexture = new Texture(Gdx.files.internal("images/Battleground.png"));
        Image background = new Image(bgTexture);
        background.setSize(1280, 720);
        stage.addActor(background);

        coeurTexture = new Texture(Gdx.files.internal("images/heart.png"));

        // Coeurs du Mage (Au-dessus de sa tête)
        for (int i = 0; i < 3; i++) {
            coeursUI[i] = new Image(coeurTexture);
            coeursUI[i].setSize(40, 40);
            coeursUI[i].setPosition(190 + (i * 45), 400);
            stage.addActor(coeursUI[i]);
        }

        // Coeur du Dragon (Au-dessus de sa tête)
        dragonHeart = new Image(coeurTexture);
        dragonHeart.setSize(40, 40);
        dragonHeart.setPosition(930, 400);
        stage.addActor(dragonHeart);

        // --- 2. CHARGEMENT DES FONDS UI (NinePatch) ---
        // Le NinePatch coupe l'image à 15 pixels des bords pour ne pas déformer les coins !

        // A. Le fond des Zones (Réserve et Sort)
        Texture panelTex = new Texture(Gdx.files.internal("images/UI/Double/panel_brown_corners_a.png"));
        com.badlogic.gdx.graphics.g2d.NinePatch panelPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(panelTex, 15, 15, 15, 15);
        com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable zoneDrawable = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(panelPatch);

        // B. Le fond des Blocs de code
        Texture blockTex = new Texture(Gdx.files.internal("images/UI/Double/button_grey.png"));
        com.badlogic.gdx.graphics.g2d.NinePatch blockPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(blockTex, 10, 10, 10, 10);
        com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable blockDrawable = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(blockPatch);

        // C. Le style des Boutons
        Texture btnUpTex = new Texture(Gdx.files.internal("images/UI/Double/button_brown.png"));
        Texture btnDownTex = new Texture(Gdx.files.internal("images/UI/Double/button_grey.png"));
        Texture btnHoverTex = new Texture(Gdx.files.internal("images/UI/Double/button_red.png"));
        com.badlogic.gdx.graphics.g2d.NinePatch btnUpPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(btnUpTex, 15, 15, 15, 15);
        com.badlogic.gdx.graphics.g2d.NinePatch btnDownPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(btnDownTex, 15, 15, 15, 15);
        com.badlogic.gdx.graphics.g2d.NinePatch btnHoverPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(btnHoverTex, 15, 15, 15, 15);

        com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle buttonStyle = new com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle();
        buttonStyle.up = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(btnUpPatch);
        buttonStyle.down = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(btnDownPatch);
        buttonStyle.over = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(btnHoverPatch);
        buttonStyle.font = codeFont;
        buttonStyle.fontColor = Color.valueOf("#5C4033");

        // --- BOUTON RETOUR (En haut à gauche) ---
        com.badlogic.gdx.scenes.scene2d.ui.TextButton btnRetour = new com.badlogic.gdx.scenes.scene2d.ui.TextButton("FUIR LE COMBAT", buttonStyle);
        btnRetour.setSize(220, 50);
        btnRetour.setPosition(20, 650);
        stage.addActor(btnRetour);

        btnRetour.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });

        // ==========================================
        // --- LE POPUP INDICE (GRIMOIRE EN BANNIÈRE) ---
        // ==========================================

        // 1. On charge l'image de la bannière spécifique pour les indices
        Texture hintBannerTex = new Texture(Gdx.files.internal("images/UI/Double/banner_modern.png"));
        // Ajuste les marges (20, 20, 20, 20) selon l'épaisseur des bords de ton image Kenney
        com.badlogic.gdx.graphics.g2d.NinePatch hintBannerPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(hintBannerTex, 20, 20, 20, 20);
        com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable hintDrawable = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(hintBannerPatch);

        Table hintTable = new Table();
        hintTable.setBackground(hintDrawable); // 👉 ON APPLIQUE LA BANNIÈRE ICI !
        hintTable.setSize(600, 200);
        hintTable.setPosition((1280 - 600) / 2f, 450);
        hintTable.setVisible(false);

        // 2. Le texte de l'indice
        Label.LabelStyle hintStyle = new Label.LabelStyle(codeFont, Color.valueOf("#5C4033"));
        Label hintLabel = new Label(currentLevel.getHint(), hintStyle);
        hintLabel.setWrap(true);
        hintLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

        // On ajoute le texte centré
        hintTable.add(hintLabel).width(500).pad(20); // J'ai un peu réduit le width (500) pour bien rentrer dans la bannière
        stage.addActor(hintTable);

        // --- LE BOUTON POUR OUVRIR LE GRIMOIRE ---
        com.badlogic.gdx.scenes.scene2d.ui.TextButton btnAide = new com.badlogic.gdx.scenes.scene2d.ui.TextButton("AIDE", buttonStyle);
        btnAide.setSize(220, 50);
        btnAide.setPosition(20, 580);
        stage.addActor(btnAide);

        btnAide.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hintTable.setVisible(!hintTable.isVisible());
            }
        });

        // ==========================================
        // --- 3. CHARGEMENT DU MAGE ---
        // ==========================================

        mageIdleSheet = new Texture(Gdx.files.internal("images/mage/Idle.png"));
        int IDLE_COLS = 7;
        TextureRegion[][] tmpIdle = TextureRegion.split(mageIdleSheet, mageIdleSheet.getWidth() / IDLE_COLS, mageIdleSheet.getHeight() / 1);
        TextureRegion[] idleFrames = new TextureRegion[IDLE_COLS];
        for (int i = 0; i < IDLE_COLS; i++) idleFrames[i] = tmpIdle[0][i];
        mageIdleAnimation = new Animation<TextureRegion>(0.12f, idleFrames);
        mageIdleAnimation.setPlayMode(Animation.PlayMode.LOOP);

        mageAttackSheet = new Texture(Gdx.files.internal("images/mage/Attack_2.png"));
        int ATTACK_COLS = 4;
        TextureRegion[][] tmpAttack = TextureRegion.split(mageAttackSheet, mageAttackSheet.getWidth() / ATTACK_COLS, mageAttackSheet.getHeight() / 1);
        TextureRegion[] attackFrames = new TextureRegion[ATTACK_COLS];
        for (int i = 0; i < ATTACK_COLS; i++) attackFrames[i] = tmpAttack[0][i];
        mageAttackAnimation = new Animation<TextureRegion>(0.08f, attackFrames);
        mageAttackAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        // 💀 --- MORT DU MAGE ---
        mageDeathSheet = new Texture(Gdx.files.internal("images/mage/Dead.png"));
        int DEATH_COLS_MAGE = 5; // 🛑 METTRE LE VRAI NOMBRE DE FRAMES !
        TextureRegion[][] tmpDeath = TextureRegion.split(mageDeathSheet, mageDeathSheet.getWidth() / DEATH_COLS_MAGE, mageDeathSheet.getHeight() / 1);
        TextureRegion[] mDeathFrames = new TextureRegion[DEATH_COLS_MAGE];
        for (int i = 0; i < DEATH_COLS_MAGE; i++) mDeathFrames[i] = tmpDeath[0][i];
        mageDeathAnimation = new Animation<TextureRegion>(0.1f, mDeathFrames);
        mageDeathAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        AnimatedActor mageCorps = new AnimatedActor(mageIdleAnimation);
        mageCorps.setSize(mageIdleSheet.getWidth() / IDLE_COLS, mageIdleSheet.getHeight());
        mageCorps.setScale(3f);
        mageCorps.setPosition(100, 200);
        stage.addActor(mageCorps);

        // ==========================================================
        // 🌳 NOUVEAU MONSTRE : L'HOMME-ARBRE
        // ==========================================================
        Texture monstreTex = new Texture(Gdx.files.internal("images/TreManAnimatedAsepriteSheet.png"));

        // On découpe l'image en une grille de 7 colonnes par 6 lignes
        com.badlogic.gdx.graphics.g2d.TextureRegion[][] tmpGrille = com.badlogic.gdx.graphics.g2d.TextureRegion.split(
            monstreTex,
            monstreTex.getWidth() / 7,
            monstreTex.getHeight() / 6
        );

        // On "aplatit" la grille en un seul grand tableau 1D pour piocher facilement par index (de 0 à 41)
        com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.TextureRegion> toutesLesFrames = new com.badlogic.gdx.utils.Array<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                // 👉 C'EST ICI QU'ON RETOURNE L'IMAGE HORIZONTALEMENT (true pour X, false pour Y)
                tmpGrille[i][j].flip(true, false);
                toutesLesFrames.add(tmpGrille[i][j]);
            }
        }

        // 1. Animation IDLE (Repos) : Ligne 1 -> Index 0 à 6
        com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.TextureRegion> idleFrames_t = new com.badlogic.gdx.utils.Array<>();
        for (int i = 0; i <= 6; i++) idleFrames_t.add(toutesLesFrames.get(i));
        dragonIdleAnim = new com.badlogic.gdx.graphics.g2d.Animation<>(0.15f, idleFrames_t, com.badlogic.gdx.graphics.g2d.Animation.PlayMode.LOOP);

        // 2. Animation ATTAQUE : Ligne 3 (frames 15 à 21) -> Index 14 à 20
        com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.TextureRegion> attackFrames_t = new com.badlogic.gdx.utils.Array<>();
        for (int i = 14; i <= 20; i++) attackFrames_t.add(toutesLesFrames.get(i));
        dragonAttackAnim = new com.badlogic.gdx.graphics.g2d.Animation<>(0.1f, attackFrames_t, com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL);

        // 3. Animation MORT : Frames 34 à 37 -> Index 33 à 36
        com.badlogic.gdx.utils.Array<com.badlogic.gdx.graphics.g2d.TextureRegion> deathFrames = new com.badlogic.gdx.utils.Array<>();
        for (int i = 33; i <= 36; i++) deathFrames.add(toutesLesFrames.get(i));
        dragonDeathAnim = new com.badlogic.gdx.graphics.g2d.Animation<>(0.2f, deathFrames, com.badlogic.gdx.graphics.g2d.Animation.PlayMode.NORMAL);

        // On applique l'animation de base au monstre

        // Ajuste la taille si l'arbre te paraît trop gros ou trop petit !

        AnimatedActor monstre = new AnimatedActor(dragonIdleAnim);
        //monstre.setSize(dragonTextures.get(0).getWidth(), dragonTextures.get(0).getHeight());
        monstre.setSize(800, 800);
        monstre.setScale(0.4f);
        monstre.setPosition(775, 130);
        stage.addActor(monstre);

        // ==========================================
        // --- 4. CHARGEMENT DES BOULES DE FEU ANIMÉES ---
        // ==========================================

        int NB_FRAMES_MAGE_FB = 7; // 🛑 Mettre le vrai nombre
        com.badlogic.gdx.utils.Array<TextureRegion> mFbFrames = new com.badlogic.gdx.utils.Array<>();
        for (int i = 1; i <= NB_FRAMES_MAGE_FB; i++) {
            Texture tex = new Texture(Gdx.files.internal("images/fireballs/Blue_fireball_" + i + ".png"));
            mageFireballTextures.add(tex);
            mFbFrames.add(new TextureRegion(tex));
        }
        mageFireballAnim = new Animation<TextureRegion>(0.08f, mFbFrames);
        mageFireballAnim.setPlayMode(Animation.PlayMode.LOOP);

        int NB_FRAMES_DRAGON_FB = 11; // 🛑 Mettre le vrai nombre
        com.badlogic.gdx.utils.Array<TextureRegion> dFbFrames = new com.badlogic.gdx.utils.Array<>();
        for (int i = 1; i <= NB_FRAMES_DRAGON_FB; i++) {
            Texture tex = new Texture(Gdx.files.internal("images/fireballs/Yellow_fireball_" + i + ".png"));
            dragonFireballTextures.add(tex);
            TextureRegion reg = new TextureRegion(tex);
            reg.flip(true, false);
            dFbFrames.add(reg);
        }
        dragonFireballAnim = new Animation<TextureRegion>(0.08f, dFbFrames);
        dragonFireballAnim.setPlayMode(Animation.PlayMode.LOOP);


        // ==========================================

        Group combatUI = new Group();
        combatUI.setVisible(false);
        stage.addActor(combatUI);

        scrollKnobTex = new Texture(Gdx.files.internal("images/UI/Double/scrollbar_grey_small.png"));
        com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable scrollKnobDrawable = new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(new TextureRegion(scrollKnobTex));

        // 2. On configure le style
        com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle scrollStyle = new com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = scrollKnobDrawable;
        scrollStyle.background = zoneDrawable;

        reserveZone = new Table();
        reserveZone.top().pad(10); // Aligne les blocs en haut avec une marge

        scrollReserve = new com.badlogic.gdx.scenes.scene2d.ui.ScrollPane(reserveZone, scrollStyle);
        scrollReserve.setSize(400, 400);
        scrollReserve.setPosition(220, 200);
        scrollReserve.setScrollingDisabled(true, false);
        scrollReserve.setFadeScrollBars(false);
        scrollReserve.setOverscroll(false, false);
        scrollReserve.setFlickScroll(false);
        scrollReserve.setVariableSizeKnobs(false);
        combatUI.addActor(scrollReserve);

        // --- 2. ZONE DE SORT (Conteneur avec Scroll) ---
        spellZone = new Table();
        spellZone.top().pad(10);

        scrollSpell = new com.badlogic.gdx.scenes.scene2d.ui.ScrollPane(spellZone, scrollStyle);
        scrollSpell.setSize(400, 400);
        scrollSpell.setPosition(660, 200);
        scrollSpell.setScrollingDisabled(true, false);
        scrollSpell.setFadeScrollBars(false);
        scrollSpell.setOverscroll(false, false);
        scrollSpell.setFlickScroll(false);
        scrollSpell.setVariableSizeKnobs(false);
        combatUI.addActor(scrollSpell);

        com.badlogic.gdx.scenes.scene2d.ui.TextButton btnValider = new com.badlogic.gdx.scenes.scene2d.ui.TextButton("LANCER LE SORT !", buttonStyle);
        btnValider.setSize(250, 60);
        btnValider.setPosition((1280 - 250) / 2f, 120);
        combatUI.addActor(btnValider);

        com.badlogic.gdx.scenes.scene2d.ui.TextButton btnFermer = new com.badlogic.gdx.scenes.scene2d.ui.TextButton("X", buttonStyle);
        btnFermer.setSize(50, 50);
        btnFermer.setPosition(1010, 560);
        combatUI.addActor(btnFermer);

        /*com.badlogic.gdx.scenes.scene2d.ui.TextButton btnReset = new com.badlogic.gdx.scenes.scene2d.ui.TextButton("REJOUER", buttonStyle);
        btnReset.setSize(180, 60);
        btnReset.setPosition((1280 - 180) / 2f, 120);
        btnReset.setVisible(false);
        combatUI.addActor(btnReset);*/

        // --- POPUP AVEC BANNIÈRE ---
        // --- LES 3 NOUVELLES NOTIFICATIONS ---
        Texture bannerTex = new Texture(Gdx.files.internal("images/UI/Double/banner_modern.png"));
        com.badlogic.gdx.graphics.g2d.NinePatch bannerPatch = new com.badlogic.gdx.graphics.g2d.NinePatch(bannerTex, 20, 20, 20, 20);
        com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable bannerDrawable = new com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable(bannerPatch);

        // 1. Popup de Hit (dégâts)
        hitPopupTable = new Table();
        hitPopupTable.setBackground(bannerDrawable);
        hitPopupTable.setSize(450, 100);
        hitPopupTable.setPosition((1280 - 450) / 2f, 550);
        Label hitLabel = new Label("Aïe ! Le monstre t'a touché !", new Label.LabelStyle(codeFont, Color.WHITE));
        hitPopupTable.add(hitLabel).center();
        hitPopupTable.setVisible(false);
        stage.addActor(hitPopupTable);

        // 2. Panel de Victoire
        victoryTable = new Table();
        victoryTable.setBackground(zoneDrawable);
        victoryTable.setSize(600, 300);
        victoryTable.setPosition((1280 - 600) / 2f, (720 - 300) / 2f);

        // 👉 CORRECTION ICI : Police réduite à 30, setWrap(true) et limitation de la largeur à 500
        Label.LabelStyle victoryStyle = new Label.LabelStyle(FontManager.generateCodeFont(30), Color.valueOf("#5C4033"));
        Label victoryLabel = new Label("🏆 LE MONSTRE EST VAINCU ! 🏆", victoryStyle);
        victoryLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        victoryLabel.setWrap(true);

        victoryTable.add(victoryLabel).width(500).padBottom(40).row();

        com.badlogic.gdx.scenes.scene2d.ui.TextButton btnVictoireSuivant = new com.badlogic.gdx.scenes.scene2d.ui.TextButton("NIVEAU SUIVANT", buttonStyle);
        victoryTable.add(btnVictoireSuivant).size(300, 70);
        victoryTable.setVisible(false);
        stage.addActor(victoryTable);

        // Action du bouton victoire (Même code que ton ancien btnNextLevel)
        btnVictoireSuivant.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int prochainId = currentLevel.getId() + 1;

                // 👉 1. LA SAUVEGARDE DANS LE BON SLOT
                String slotActuel = game.getCurrentSlot();
                if (slotActuel != null && !slotActuel.isEmpty()) {
                    Preferences sauvegarde = Gdx.app.getPreferences(slotActuel);
                    int niveauMaxActuel = sauvegarde.getInteger("maxLevel", 1);

                    // Si le prochain niveau est plus grand que ce qu'on avait débloqué, on met à jour !
                    if (prochainId > niveauMaxActuel) {
                        sauvegarde.putInteger("maxLevel", prochainId);
                        sauvegarde.flush(); // 💾 C'est cette ligne qui grave la donnée !
                    }
                }

                // 👉 2. LE CHANGEMENT D'ÉCRAN
                LevelData prochainNiveau = LevelData.getLevel(prochainId);
                if (prochainNiveau != null) {
                    game.setScreen(new GameScreen(game, prochainNiveau));
                } else {
                    game.setScreen(new MapScreen(game));
                }
                dispose();
            }
        });

        // 3. Panel de Game Over
        gameOverTable = new Table();
        gameOverTable.setBackground(zoneDrawable);
        gameOverTable.setSize(650, 350);
        gameOverTable.setPosition((1280 - 650) / 2f, (720 - 350) / 2f);
        Label goLabel = new Label("Tu as perdu toutes tes vies...\nIl est temps de réviser !", new Label.LabelStyle(codeFont, Color.valueOf("#5C4033")));
        goLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
        gameOverTable.add(goLabel).padBottom(40).colspan(2).row();

        com.badlogic.gdx.scenes.scene2d.ui.TextButton btnAllerCours = new com.badlogic.gdx.scenes.scene2d.ui.TextButton("📖 GRIMOIRE", buttonStyle);
        com.badlogic.gdx.scenes.scene2d.ui.TextButton btnRejouerGO = new com.badlogic.gdx.scenes.scene2d.ui.TextButton("🔄 REJOUER", buttonStyle);
        gameOverTable.add(btnAllerCours).size(250, 70).padRight(30);
        gameOverTable.add(btnRejouerGO).size(250, 70);
        gameOverTable.setVisible(false);
        stage.addActor(gameOverTable);

        btnRejouerGO.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { game.setScreen(new GameScreen(game, currentLevel)); dispose(); }
        });
        btnAllerCours.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // 👉 On envoie l'ID du niveau actuel au Grimoire !
                game.setScreen(new CourseScreen(game, currentLevel.getId()));
                dispose();
            }
        });

        com.badlogic.gdx.scenes.scene2d.ui.TextButton btnCommencer = new com.badlogic.gdx.scenes.scene2d.ui.TextButton("COMBATTRE LE MONSTRE", buttonStyle);
        btnCommencer.setSize(350, 70);
        btnCommencer.setPosition((1280 - 350) / 2f, 150);
        stage.addActor(btnCommencer);

        btnCommencer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnCommencer.setVisible(false);
                combatUI.setVisible(true);
            }
        });

        btnFermer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                combatUI.setVisible(false);
                btnCommencer.setVisible(true); // On fait réapparaître le bouton central
            }
        });

        DragAndDrop dragAndDrop = new DragAndDrop();
        String[] textesSort = currentLevel.getAvailableBlocks();
        java.util.List<CodeBlockActor> tousLesBlocs = new java.util.ArrayList<>();
        java.util.List<String> listeMelangee = java.util.Arrays.asList(textesSort);
        java.util.Collections.shuffle(listeMelangee);

        /*btnReset.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnReset.setVisible(false);
                btnValider.setVisible(true);
                reserveZone.clearChildren();
                spellZone.clearChildren();
                for (CodeBlockActor bloc : tousLesBlocs) {
                    reserveZone.add(bloc).pad(5).row(); // Remettre la largeur fixe ici aussi
                }
                // Reset du scroll en haut
                scrollReserve.setScrollY(0);
                scrollSpell.setScrollY(0);
            }
        });*/

        for (String texte : listeMelangee) {
            CodeBlockActor bloc = new CodeBlockActor(texte, codeFont, blockDrawable);
            bloc.pack();
            reserveZone.add(bloc).pad(10).row();
            tousLesBlocs.add(bloc);

            dragAndDrop.addSource(new Source(bloc) {
                @Override
                public Payload dragStart(InputEvent event, float x, float y, int pointer) {
                    Payload payload = new Payload();
                    payload.setObject(bloc);
                    CodeBlockActor dragActor = new CodeBlockActor(bloc.getCodeText(), codeFont, blockDrawable);
                    dragActor.pack();
                    payload.setDragActor(dragActor);
                    bloc.setVisible(false);
                    return payload;
                }
                @Override
                public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
                    if (target == null) bloc.setVisible(true);
                }
            });
        }

        // 👉 ON CIBLE 'scrollSpell'
        dragAndDrop.addTarget(new Target(scrollSpell) {
            @Override public boolean drag(Source source, Payload payload, float x, float y, int pointer) { return true; }
            @Override public void drop(Source source, Payload payload, float x, float y, int pointer) {
                CodeBlockActor droppedBlock = (CodeBlockActor) payload.getObject();
                droppedBlock.setVisible(true);
                // Mais on ajoute bien le bloc dans la 'spellZone' !
                spellZone.add(droppedBlock).pad(10).row();
            }
        });

        // 👉 ON CIBLE 'scrollReserve'
        dragAndDrop.addTarget(new Target(scrollReserve) {
            @Override public boolean drag(Source source, Payload payload, float x, float y, int pointer) { return true; }
            @Override public void drop(Source source, Payload payload, float x, float y, int pointer) {
                CodeBlockActor droppedBlock = (CodeBlockActor) payload.getObject();
                droppedBlock.setVisible(true);
                reserveZone.add(droppedBlock).pad(10).row();
            }
        });

        // ==========================================================
        // --- 7. VALIDATION ET COMBAT ---
        // ==========================================================
        btnValider.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                combatUI.setVisible(false);

                StringBuilder sortJoueur = new StringBuilder();
                for (com.badlogic.gdx.scenes.scene2d.Actor acteur : spellZone.getChildren()) {
                    if (acteur instanceof CodeBlockActor) {
                        sortJoueur.append(((CodeBlockActor) acteur).getCodeText());
                    }
                }

                String sortAttendu = currentLevel.getExpectedCode();
                String reponseJoueurPure = sortJoueur.toString().replaceAll("\\s+", "");
                String reponseAttenduePure = sortAttendu.replaceAll("\\s+", "");
                // =================================================
                // 🏆 CAS 1 : VICTOIRE (MORT DU DRAGON)
                // =================================================
                if (reponseJoueurPure.toString().equals(reponseAttenduePure)) {

                    mageCorps.setAnimation(mageAttackAnimation);

                    AnimatedActor mFireball = new AnimatedActor(mageFireballAnim);
                    mFireball.setSize(100, 100);
                    mFireball.setPosition(250, 230);

                    stage.addAction(Actions.sequence(
                        Actions.delay(0.4f),
                        Actions.run(() -> {
                            stage.addActor(mFireball);
                            mFireball.addAction(Actions.sequence(
                                Actions.moveTo(850, 230, 0.4f),
                                Actions.run(() -> {
                                    mFireball.remove();

                                    dragonHeart.setVisible(false); // Le dragon perd son coeur
                                    monstre.clearActions();
                                    monstre.setAnimation(dragonDeathAnim);

                                    // 👉 PAUSE DE 2.5s AVANT D'AFFICHER LE PANEL
                                    stage.addAction(Actions.sequence(
                                        Actions.delay(2.5f),
                                        Actions.run(() -> {
                                            victoryTable.setVisible(true);
                                        })
                                    ));
                                })
                            ));
                        }),
                        Actions.delay(0.2f),
                        Actions.run(() -> mageCorps.setAnimation(mageIdleAnimation))
                    ));

                } else {
                    // =================================================
                    // ❌ CAS 2 : ÉCHEC (ATTAQUE DU DRAGON)
                    // =================================================
                    viesJoueur--;
                    // On garde en mémoire quel cœur doit disparaître pour l'utiliser plus tard
                    final int indexCoeur = viesJoueur;

                    AnimatedActor dFireball = new AnimatedActor(dragonFireballAnim);
                    dFireball.setSize(120, 120);

                    if (viesJoueur > 0) {
                        // --- 2A : IL RESTE DES VIES ---

                        // 👉 CORRECTION : La notification attend que la boule touche (1.4s préparation + 0.4s vol = 1.8s)
                        hitPopupTable.clearActions();
                        hitPopupTable.addAction(Actions.sequence(
                            Actions.delay(1.8f),
                            Actions.alpha(0), Actions.visible(true), Actions.fadeIn(0.3f),
                            Actions.delay(2f), Actions.fadeOut(0.5f), Actions.visible(false)
                        ));

                        stage.addAction(Actions.sequence(
                            Actions.run(() -> monstre.setAnimation(dragonAttackAnim)),
                            Actions.delay(1.4f),
                            Actions.run(() -> {
                                dFireball.setPosition(850, 230);
                                stage.addActor(dFireball);

                                dFireball.addAction(Actions.sequence(
                                    Actions.moveTo(250, 230, 0.4f),
                                    Actions.run(() -> {
                                        dFireball.remove();

                                        // 👉 CORRECTION : LE CŒUR DISPARAÎT SEULEMENT À L'IMPACT !
                                        if (indexCoeur >= 0) coeursUI[indexCoeur].setVisible(false);

                                        mageCorps.addAction(Actions.sequence(
                                            Actions.color(Color.RED, 0.1f), Actions.color(Color.WHITE, 0.1f)
                                        ));
                                    })
                                ));
                            }),
                            Actions.delay(0.5f),
                            Actions.run(() -> monstre.setAnimation(dragonIdleAnim)),

                            Actions.delay(2.5f),
                            Actions.run(() -> {
                                combatUI.setVisible(true);
                                reserveZone.setVisible(true);
                                spellZone.setVisible(true);
                                //btnValider.setVisible(false);
                                //btnReset.setVisible(true);
                            })
                        ));
                    } else {
                        // --- 2B : GAME OVER ! MORT DU MAGE 💀 ---

                        stage.addAction(Actions.sequence(
                            Actions.run(() -> monstre.setAnimation(dragonAttackAnim)),
                            Actions.delay(1.4f),
                            Actions.run(() -> {
                                dFireball.setPosition(850, 230);
                                stage.addActor(dFireball);

                                dFireball.addAction(Actions.sequence(
                                    Actions.moveTo(250, 230, 0.4f),
                                    Actions.run(() -> {
                                        dFireball.remove();

                                        // 👉 CORRECTION : LE DERNIER CŒUR DISPARAÎT ICI AUSSI !
                                        if (indexCoeur >= 0) coeursUI[indexCoeur].setVisible(false);

                                        mageCorps.clearActions();
                                        mageCorps.setAnimation(mageDeathAnimation);
                                        mageCorps.addAction(Actions.color(Color.RED, 0.2f));
                                    })
                                ));
                            }),
                            Actions.delay(2.5f),
                            Actions.run(() -> {
                                gameOverTable.setVisible(true);
                            })
                        ));
                    }
                }
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
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (codeFont != null) codeFont.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (zoneColorTexture != null) zoneColorTexture.dispose();
        if (blockColorTexture != null) blockColorTexture.dispose();

        if (mageIdleSheet != null) mageIdleSheet.dispose();
        if (mageAttackSheet != null) mageAttackSheet.dispose();
        if (mageDeathSheet != null) mageDeathSheet.dispose();

        for (Texture tex : dragonTextures) if (tex != null) tex.dispose();
        for (Texture tex : mageFireballTextures) if (tex != null) tex.dispose();
        for (Texture tex : dragonFireballTextures) if (tex != null) tex.dispose();

        if (coeurTexture != null) coeurTexture.dispose();
        if (scrollKnobTex != null) scrollKnobTex.dispose();
    }
}
