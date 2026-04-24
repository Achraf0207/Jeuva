package com.Jeuva.screens;

import com.Jeuva.Jeuva;
import com.Jeuva.utils.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CourseScreen implements Screen {

    private final Jeuva game;
    private Stage stage;

    private BitmapFont titleFont, textFont, magicFont, buttonFont;
    private Texture bgTexture, bookTexture, scrollTexture;

    // Textures pour les boutons Kenney
    private Texture btnUpTex, btnDownTex, btnHoverTex;
    private TextButton.TextButtonStyle buttonStyle;

    private Label titreChapitreLabel, contenuChapitreLabel, astuceLabel;
    private Table pageDroiteTable;

    // L'ID du niveau depuis lequel on arrive (pour ouvrir la bonne page)
    private int targetLevelId = -1;

    // Constructeur 1 : Accès depuis le Menu Principal
    public CourseScreen(Jeuva game) {
        this.game = game;
    }

    // Constructeur 2 : Accès depuis le Game Over d'un niveau (Redirection intelligente)
    public CourseScreen(Jeuva game, int targetLevelId) {
        this.game = game;
        this.targetLevelId = targetLevelId;
    }

    @Override
    public void show() {
        initStage();
        initFonts();
        loadTextures();
        setupBackground();
        setupGrimoire();
        setupRetourButton();

        // 🎯 REDIRECTION INTELLIGENTE
        int chapitreAAfficher = 1; // Par défaut : Variables

        if (targetLevelId >= 1 && targetLevelId <= 3) chapitreAAfficher = 1;      // Niveaux 1 à 3 -> Variables
        else if (targetLevelId == 4) chapitreAAfficher = 2;                       // Niveau 4 -> Tableaux
        else if (targetLevelId == 5 || targetLevelId == 6) chapitreAAfficher = 3; // Niveaux 5 et 6 -> Conditions
        else if (targetLevelId == 7 || targetLevelId == 8) chapitreAAfficher = 4; // Niveaux 7 et 8 -> Boucles
        else if (targetLevelId == 9 || targetLevelId == 10) chapitreAAfficher = 5;// Niveaux 9 et 10 -> Classes et Objets

        chargerLecon(chapitreAAfficher);
    }

    private void initStage() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);
    }

    private void initFonts() {
        titleFont = FontManager.generateCodeFont(32);
        textFont = FontManager.generateCodeFont(19);
        magicFont = FontManager.generateCodeFont(17);
        buttonFont = FontManager.generateCodeFont(20);

        titleFont.getData().markupEnabled = true;
        textFont.getData().markupEnabled = true;
        magicFont.getData().markupEnabled = true;
    }

    private void loadTextures() {
        bgTexture = new Texture(Gdx.files.internal("images/Battleground.png"));
        bookTexture = new Texture(Gdx.files.internal("images/open-book.png"));
        scrollTexture = new Texture(Gdx.files.internal("images/scroll-bg.png"));

        // Textures des boutons Kenney
        btnUpTex = new Texture(Gdx.files.internal("images/UI/Double/button_brown.png"));
        btnDownTex = new Texture(Gdx.files.internal("images/UI/Double/button_grey.png"));
        btnHoverTex = new Texture(Gdx.files.internal("images/UI/Double/button_red.png"));

        NinePatch btnUpPatch = new NinePatch(btnUpTex, 15, 15, 15, 15);
        NinePatch btnDownPatch = new NinePatch(btnDownTex, 15, 15, 15, 15);
        NinePatch btnHoverPatch = new NinePatch(btnHoverTex, 15, 15, 15, 15);

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new NinePatchDrawable(btnUpPatch);
        buttonStyle.down = new NinePatchDrawable(btnDownPatch);
        buttonStyle.over = new NinePatchDrawable(btnHoverPatch);
        buttonStyle.font = buttonFont;
        buttonStyle.fontColor = Color.valueOf("#5C4033");
    }

    private void setupBackground() {
        Image background = new Image(bgTexture);
        background.setSize(1280, 720);
        background.setColor(0.35f, 0.35f, 0.35f, 1f); // On assombrit pour faire ressortir le livre
        stage.addActor(background);
    }

    private void setupGrimoire() {
        Table grimoire = new Table();
        grimoire.setBackground(new TextureRegionDrawable(bookTexture));
        grimoire.setSize(1100, 600);
        grimoire.setPosition(90, 60);
        stage.addActor(grimoire);

        Table pageGauche = createPageGauche();
        pageDroiteTable = createPageDroite();

        grimoire.add(pageGauche).width(550).fill();
        grimoire.add(pageDroiteTable).width(550).fill();
    }

    private Table createPageGauche() {
        Table pageGauche = new Table();
        // 👉 Modification du padding gauche de la page pour un meilleur alignement global (de 90 à 70)
        pageGauche.top().pad(70, 70, 40, 30); // Haut, Gauche, Bas, Droite

        Label sommaireTitre = new Label("[#4A2E19]~ INDEX DES SORTS ~", new Label.LabelStyle(titleFont, Color.WHITE));
        pageGauche.add(sommaireTitre).padBottom(30).row();

        Table boutonsTable = new Table();
        ajouterBoutonChapitre(boutonsTable, "1. Les Variables", 1);
        ajouterBoutonChapitre(boutonsTable, "2. Les Tableaux", 2);
        ajouterBoutonChapitre(boutonsTable, "3. Les Conditions", 3);
        ajouterBoutonChapitre(boutonsTable, "4. Les Boucles", 4);
        ajouterBoutonChapitre(boutonsTable, "5. Classes et Objets", 5);

        pageGauche.add(boutonsTable).left().expandX().row();

        Table astuceBox = new Table();
        astuceBox.setBackground(new TextureRegionDrawable(scrollTexture));

        astuceLabel = new Label("🦉 [#228B22]Maître Hibou :[]\nApprends les bases pour devenir un puissant mage !",
            new Label.LabelStyle(magicFont, Color.BLACK));
        astuceLabel.setWrap(true);
        astuceLabel.setAlignment(Align.center);

        // 👉 CORRECTION 1 : Augmentation du padding latéral pour contenir le texte dans le parchemin (pad(25) -> pad(25, 45, 25, 45))
        astuceBox.add(astuceLabel).width(320).pad(25, 45, 25, 45).padTop(35); // Haut, Gauche, Bas, Droite
        pageGauche.add(astuceBox).size(400, 160).bottom().padBottom(10);

        return pageGauche;
    }

    private Table createPageDroite() {
        Table pageDroite = new Table();
        // 👉 Modification du padding gauche de la page pour un meilleur alignement (de 30 à 50)
        pageDroite.top().pad(70, 50, 40, 90); // Haut, Gauche, Bas, Droite

        // 👉 CORRECTION 2 : Changement de couleur pour le titre pour une lisibilité parfaite (#B8860B -> #5C4033)
        Label.LabelStyle titreStyle = new Label.LabelStyle(titleFont, Color.valueOf("#5C4033"));
        titreChapitreLabel = new Label("TITRE DU CHAPITRE", titreStyle);
        titreChapitreLabel.setAlignment(Align.center);

        contenuChapitreLabel = new Label("", new Label.LabelStyle(textFont, Color.valueOf("#2C1E16")));
        contenuChapitreLabel.setWrap(true);
        contenuChapitreLabel.setAlignment(Align.topLeft);

        pageDroite.add(titreChapitreLabel).width(420).padBottom(25).row();
        pageDroite.add(contenuChapitreLabel).width(420).top().expand();

        return pageDroite;
    }

    private void setupRetourButton() {
        // Utilisation du même style de bouton que le reste du jeu
        TextButton btnRetour = new TextButton("RETOUR AU MENU", buttonStyle);
        btnRetour.setSize(250, 60);
        // Positionné en haut à gauche
        btnRetour.setPosition(100, 640);
        btnRetour.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(btnRetour);
    }

    private void ajouterBoutonChapitre(Table table, String texte, final int id) {
        TextButton btnChapitre = new TextButton(texte, buttonStyle);

        btnChapitre.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chargerLecon(id);
                // Animation douce pour la page de droite
                pageDroiteTable.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.4f)));
            }
        });

        table.add(btnChapitre).size(350, 55).padBottom(10).row();
    }

    private void chargerLecon(int id) {
        String titre = "", contenu = "", astuce = "";

        switch (id) {
            case 1: {
                titre = "SACS À CRISTAUX (VARIABLES)";
                contenu = "Les variables sont des sacs pour stocker tes trésors et caractéristiques :\n\n" +
                    "• [BLUE]int[] : Pour les nombres entiers.\n  [DARK_GRAY]Ex: int vie = 100;[]\n\n" +
                    "• [BLUE]String[] : Pour le texte (entre guillemets).\n  [DARK_GRAY]Ex: String nom = \"Merlin\";[]\n\n" +
                    "• [BLUE]boolean[] : Pour vrai ou faux.\n  [DARK_GRAY]Ex: boolean enVie = true;[]";
                astuce = "🦉 [#228B22]Maître Hibou :[]\nN'oublie jamais de donner une valeur à ta variable avec le signe '=' !";
                break;
            }
            case 2: {
                titre = "L'INVENTAIRE (TABLEAUX)";
                contenu = "Un tableau permet de stocker plusieurs objets du même type dans un seul grand sac.\n\n" +
                    "On ajoute des crochets [BLUE][][] après le type :\n\n" +
                    "[BLUE]String[] sorts = {\"Feu\", \"Glace\"};[]\n\n" +
                    "• Les éléments sont séparés par des virgules.\n" +
                    "• Ils sont entourés par des accolades [RED]{}[] !";
                astuce = "🦉 [#228B22]Maître Hibou :[]\nLes tableaux sont parfaits pour transporter tout ton grimoire de sorts d'un seul coup.";
                break;
            }
            case 3: {
                titre = "LE MIROIR (CONDITIONS)";
                contenu = "Le sort [PURPLE]if[] (SI) permet à ton mage de prendre des décisions selon la situation :\n\n" +
                    "[BLUE]if (vie < 50) {[]\n" +
                    "   boirePotion();\n" +
                    "[BLUE]} else {[]\n" +
                    "   attaquer();\n" +
                    "[BLUE]}[]\n\n" +
                    "Si la condition est vraie, on fait la première action, [PURPLE]else[] (sinon), on fait la deuxième !";
                astuce = "🦉 [#228B22]Maître Hibou :[]\nLa condition doit toujours être entre parenthèses '( )'.";
                break;
            }
            case 4: {
                titre = "SPIRALE (BOUCLES)";
                contenu = "Marre de répéter un sort ? Automatise-le !\n\n" +
                    "• La boucle [PURPLE]for[] compte un nombre de fois précis :\n" +
                    "[BLUE]for(int i=0; i<3; i++) {[] lancerFeu(); [BLUE]}[]\n\n" +
                    "• La boucle [PURPLE]while[] (tant que) tourne jusqu'à ce qu'une condition change :\n" +
                    "[BLUE]while(mana < 100) {[] mediter(); [BLUE]}[]";
                astuce = "🦉 [#228B22]Maître Hibou :[]\n'i++' est un raccourci magique qui signifie qu'on ajoute +1 à chaque tour !";
                break;
            }
            case 5: {
                titre = "L'ÂME (CLASSES ET OBJETS)";
                contenu = "Une [PURPLE]class[] (Classe) est le plan de construction de ton personnage.\n" +
                    "[BLUE]class Mage { int vie = 100; }[]\n\n" +
                    "Pour donner vie à ce plan dans le monde, tu dois l'instancier, c'est-à-dire créer un Objet avec le mot [PURPLE]new[] :\n\n" +
                    "[BLUE]Mage monHeros = new Mage();[]";
                astuce = "🦉 [#228B22]Maître Hibou :[]\nTout en Java est construit autour des Objets. Tu es maintenant un vrai Créateur !";
                break;
            }
        }

        titreChapitreLabel.setText(titre);
        contenuChapitreLabel.setText(contenu);
        astuceLabel.setText(astuce);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act(delta);
        stage.draw();
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
        if (textFont != null) textFont.dispose();
        if (magicFont != null) magicFont.dispose();
        if (buttonFont != null) buttonFont.dispose();
        if (bgTexture != null) bgTexture.dispose();
        if (bookTexture != null) bookTexture.dispose();
        if (scrollTexture != null) scrollTexture.dispose();
        if (btnUpTex != null) btnUpTex.dispose();
        if (btnDownTex != null) btnDownTex.dispose();
        if (btnHoverTex != null) btnHoverTex.dispose();
    }
}
