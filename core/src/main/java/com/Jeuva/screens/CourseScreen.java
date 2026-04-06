package com.Jeuva.screens;

import com.Jeuva.Jeuva;
import com.Jeuva.utils.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CourseScreen implements Screen {

    private final Jeuva game;
    private Stage stage;
    private BitmapFont titleFont;
    private BitmapFont textFont;
    private Texture bgTexture;
    private Texture overlayTexture;

    // Les labels qui vont changer quand on clique sur un chapitre
    private Label titreChapitreLabel;
    private Label contenuChapitreLabel;

    public CourseScreen(Jeuva game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        titleFont = FontManager.generateCodeFont(36);
        textFont = FontManager.generateCodeFont(24);

        // --- 1. LE FOND ---
        bgTexture = new Texture(Gdx.files.internal("images/mist-forest.png"));
        Image background = new Image(bgTexture);
        background.setSize(1280, 720);
        stage.addActor(background);

        // --- 2. LE GRIMOIRE (La grande boîte) ---
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.85f));
        pixmap.fill();
        overlayTexture = new Texture(pixmap);
        pixmap.dispose();

        Table grimoire = new Table();
        grimoire.setBackground(new TextureRegionDrawable(overlayTexture));
        grimoire.setSize(1100, 550);
        grimoire.setPosition(90, 80);
        stage.addActor(grimoire);

        // --- 3. MISE EN PAGE : GAUCHE (Sommaire) / DROITE (Texte) ---
        Table sommaireTable = new Table();
        sommaireTable.top().pad(30); // On met un peu plus de marge interne

        Table contenuTable = new Table();
        contenuTable.top().pad(30);

        // 👉 CORRECTION DE L'ESPACE : On fixe la largeur des colonnes et on ajoute "padRight(60)" pour les espacer !
        // Le grimoire fait 1100px. On donne 300 à gauche, 60 d'espace, et 680 à droite (300+60+680 = 1040).
        grimoire.add(sommaireTable).width(300).padRight(60).expandY().fillY();
        grimoire.add(contenuTable).width(680).expandY().fillY();

        // --- 4. LA PAGE DE DROITE (Le texte dynamique) ---
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.valueOf("#FFCC00"));
        Label.LabelStyle textStyle = new Label.LabelStyle(textFont, Color.WHITE);

        titreChapitreLabel = new Label("SÉLECTIONNE UN CHAPITRE", titleStyle);
        // 👉 CORRECTION MAJEURE : On force le titre à revenir à la ligne s'il est trop long !
        titreChapitreLabel.setWrap(true);
        titreChapitreLabel.setAlignment(Align.topLeft);

        contenuChapitreLabel = new Label("Ouvre l'esprit, jeune apprenti. Les secrets de la Forêt de Java t'attendent dans les pages de gauche...", textStyle);
        contenuChapitreLabel.setWrap(true);
        contenuChapitreLabel.setAlignment(Align.topLeft);

        // 👉 CORRECTION DE LA LARGEUR : On force les labels à ne jamais dépasser la taille de la colonne (680)
        contenuTable.add(titreChapitreLabel).width(680).padBottom(30).row();
        contenuTable.add(contenuChapitreLabel).width(680).expandY().fillY().row();

        // --- 5. LA PAGE DE GAUCHE (Les boutons du Sommaire) ---
        Label.LabelStyle menuTitleStyle = new Label.LabelStyle(titleFont, Color.valueOf("#AAAAAA"));
        sommaireTable.add(new Label("--- INDEX ---", menuTitleStyle)).padBottom(30).row();

        ajouterBoutonChapitre(sommaireTable, "> Chapitre 1 : La Voix", 1);
        ajouterBoutonChapitre(sommaireTable, "> Chapitre 2 : Les Bourses", 2);
        ajouterBoutonChapitre(sommaireTable, "> Chapitre 3 : Les Boucles", 3);

        // --- 6. LE BOUTON RETOUR ---
        Label.LabelStyle btnStyle = new Label.LabelStyle(titleFont, Color.valueOf("#FF5555"));
        Label btnRetour = new Label("< FERMER LE GRIMOIRE", btnStyle);
        btnRetour.setPosition(50, 660);
        stage.addActor(btnRetour);

        btnRetour.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
    }

    // --- MÉTHODE POUR CRÉER LES BOUTONS DU SOMMAIRE ---
    private void ajouterBoutonChapitre(Table table, String texte, final int chapitreId) {
        Label.LabelStyle btnStyle = new Label.LabelStyle(textFont, Color.valueOf("#55FF55"));
        Label bouton = new Label(texte, btnStyle);

        bouton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                chargerChapitre(chapitreId); // Change le texte à droite quand on clique !
            }
        });

        table.add(bouton).padBottom(20).align(Align.left).row();
    }

    // --- MÉTHODE MAGIQUE QUI CONTIENT L'HISTOIRE ---
    private void chargerChapitre(int id) {
        switch (id) {
            case 1:
                titreChapitreLabel.setText("CHAPITRE 1 : LA VOIX MAGIQUE");
                contenuChapitreLabel.setText(
                    "Pour interagir avec les créatures de la Forêt, un mage doit projeter sa voix. " +
                        "En langage Java, on n'utilise pas ses cordes vocales, mais le sort d'affichage.\n\n" +
                        "Formule : System.out.print(\"Ton message\");\n\n" +
                        "Règles du sortilège :\n" +
                        "1. Invoque le grand 'S' majuscule au début.\n" +
                        "2. Enferme tes mots entre les guillemets (\").\n" +
                        "3. Scelle ton sort avec un point-virgule (;) à la fin, sinon la magie s'échappe !"
                );
                break;
            case 2:
                titreChapitreLabel.setText("CHAPITRE 2 : BOURSES DE MANA (Variables)");
                contenuChapitreLabel.setText(
                    "Un mage ne peut pas tout retenir de tête. Pour stocker de l'énergie, des points de vie ou des noms, on utilise des 'Bourses magiques' appelées Variables.\n\n" +
                        "Formule : int mana = 100;\n\n" +
                        "Ici, tu as créé une bourse appelée 'mana', et tu y as glissé 100 cristaux d'énergie. Le mot 'int' signifie 'Entier', car on ne coupe pas un cristal en deux !\n" +
                        "Si tu veux stocker un mot magique, utilise une bourse de type 'String' (chaîne de caractères)."
                );
                break;
            case 3:
                titreChapitreLabel.setText("CHAPITRE 3 : LES BOUCLES TEMPORELLES");
                contenuChapitreLabel.setText(
                    "Face à une armée de gobelins, jeter un sort 100 fois t'épuiserait. Heureusement, tu peux créer une anomalie temporelle : la boucle 'for' !\n\n" +
                        "Formule :\nfor(int i = 0; i < 3; i++) {\n   System.out.print(\"Feu !\");\n}\n\n" +
                        "Cette incantation va tirer 3 boules de feu automatiquement. La magie opère entre les accolades { ... } !"
                );
                break;
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
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
        if (bgTexture != null) bgTexture.dispose();
        if (overlayTexture != null) overlayTexture.dispose();
    }
}
