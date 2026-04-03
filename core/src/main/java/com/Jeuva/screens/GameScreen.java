package com.Jeuva.screens;

import com.Jeuva.Jeuva;
import com.Jeuva.ui.CodeBlockActor;
import com.Jeuva.utils.FontManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class GameScreen implements Screen {

    private final Jeuva game;
    private Stage stage;
    private BitmapFont codeFont;

    // Nouvelles textures pour le visuel
    private Texture bgTexture;
    private Texture zoneColorTexture;
    private Texture blockColorTexture;
    private Texture magicienTexture;
    private Texture monstreTexture;


    public GameScreen(Jeuva game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        codeFont = FontManager.generateCodeFont(24);

        // --- 1. LE FOND D'ÉCRAN (LA FORÊT) ---
        bgTexture = new Texture(Gdx.files.internal("images/mist-forest.png"));
        Image background = new Image(bgTexture);
        background.setSize(1280, 720);
        stage.addActor(background);

        // --- 2. CRÉATION DES COULEURS D'INTERFACE ---
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

        pixmap.setColor(new Color(0f, 0f, 0f, 0.6f));
        pixmap.fill();
        zoneColorTexture = new Texture(pixmap);
        TextureRegionDrawable zoneDrawable = new TextureRegionDrawable(zoneColorTexture);

        pixmap.setColor(new Color(0.2f, 0.25f, 0.35f, 1f));
        pixmap.fill();
        blockColorTexture = new Texture(pixmap);
        TextureRegionDrawable blockDrawable = new TextureRegionDrawable(blockColorTexture);

        pixmap.dispose();

        // --- 3. LES PERSONNAGES AU SOL ---
        magicienTexture = new Texture(Gdx.files.internal("images/mage-dark.png"));
        Image magicien = new Image(magicienTexture);
        magicien.setSize(100, 130);
        magicien.setPosition(250, 100); // Au niveau du sol
        magicien.addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0, 10, 1f), Actions.moveBy(0, -10, 1f)
        )));
        stage.addActor(magicien);

        monstreTexture = new Texture(Gdx.files.internal("images/mushroom-monster.png"));
        Image monstre = new Image(monstreTexture);
        monstre.setSize(120, 120);
        monstre.setPosition(850, 100); // Au niveau du sol
        monstre.addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0, 10, 0.8f), Actions.moveBy(0, -10, 0.8f)
        )));
        stage.addActor(monstre);

        // --- 4. LE GROUPE D'INTERFACE DE COMBAT (Caché au début) ---
        Group combatUI = new Group();
        combatUI.setVisible(false); // Invisible par défaut
        stage.addActor(combatUI);

        Table reserveZone = new Table();
        reserveZone.setSize(400, 400);
        reserveZone.setPosition(100, 250); // Remonté pour ne pas cacher les persos
        reserveZone.setBackground(zoneDrawable);
        reserveZone.setTouchable(Touchable.enabled);
        reserveZone.top().padTop(20);
        combatUI.addActor(reserveZone); // Ajouté au GROUPE

        Table spellZone = new Table();
        spellZone.setSize(400, 400);
        spellZone.setPosition(700, 250);
        spellZone.setBackground(zoneDrawable);
        spellZone.setTouchable(Touchable.enabled);
        spellZone.top().padTop(20);
        combatUI.addActor(spellZone); // Ajouté au GROUPE

        Label.LabelStyle btnStyle = new Label.LabelStyle(codeFont, Color.WHITE);
        Label btnValider = new Label("LANCER LE SORT !", btnStyle);
        btnValider.setPosition(520, 160);
        combatUI.addActor(btnValider); // Ajouté au GROUPE

        // --- LE BOUTON RÉINITIALISER ---
        Label.LabelStyle resetStyle = new Label.LabelStyle(codeFont, Color.valueOf("#AAAAFF")); // Un bleu clair
        Label btnReset = new Label("↺ REJOUER", resetStyle);
        btnReset.setPosition(560, 160); // Au même endroit !
        btnReset.setVisible(false); // Il est caché au début
        combatUI.addActor(btnReset);

        // --- LE POPUP DE MESSAGE
        Label.LabelStyle popupStyle = new Label.LabelStyle(codeFont, Color.WHITE);
        Label popupLabel = new Label("", popupStyle);
        // On le centre bien haut dans l'écran pour qu'il soit bien visible (ex: Y = 600)
        popupLabel.setPosition(350, 600);
        popupLabel.setVisible(false);
        stage.addActor(popupLabel);

        // --- 5. LE BOUTON "COMMENCER" ---
        Label.LabelStyle startStyle = new Label.LabelStyle(codeFont, Color.valueOf("#55FF55"));
        Label btnCommencer = new Label("> COMBATTRE LE MONSTRE <", startStyle);
        btnCommencer.setPosition(450, 360); // Au centre de l'écran
        stage.addActor(btnCommencer); // Lui, il est sur le stage principal !

        // Logique d'apparition du combat
        btnCommencer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnCommencer.setVisible(false); // Disparaît
                combatUI.setVisible(true);      // Fait apparaître l'interface de magie !
            }
        });

        // --- 6. LE DRAG & DROP ET LES BLOCS ---
        DragAndDrop dragAndDrop = new DragAndDrop();
        String[] textesSort = { "\");", "System.out.print(\"", "Boule de feu" };
        java.util.List<CodeBlockActor> tousLesBlocs = new java.util.ArrayList<>();

        // Logique bouton Reset
        btnReset.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnReset.setVisible(false); // On se recache
                btnValider.setVisible(true); // On réaffiche "Lancer le sort"

                // On vide les deux zones
                reserveZone.clearChildren();
                spellZone.clearChildren();

                // On remet tous nos blocs sauvegardés dans la réserve (à gauche) !
                for (CodeBlockActor bloc : tousLesBlocs) {
                    reserveZone.add(bloc).pad(10).row();
                }
            }
        });

        for (String texte : textesSort) {
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

        dragAndDrop.addTarget(new Target(spellZone) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) { return true; }
            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                CodeBlockActor droppedBlock = (CodeBlockActor) payload.getObject();
                droppedBlock.setVisible(true);
                spellZone.add(droppedBlock).pad(10).row();
            }
        });

        dragAndDrop.addTarget(new Target(reserveZone) {
            @Override
            public boolean drag(Source source, Payload payload, float x, float y, int pointer) { return true; }
            @Override
            public void drop(Source source, Payload payload, float x, float y, int pointer) {
                CodeBlockActor droppedBlock = (CodeBlockActor) payload.getObject();
                droppedBlock.setVisible(true);
                reserveZone.add(droppedBlock).pad(10).row();
            }
        });

        // --- 7. LOGIQUE DE VALIDATION ET ANIMATION ---
        btnValider.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                btnValider.setVisible(false);

                StringBuilder sortJoueur = new StringBuilder();
                for (com.badlogic.gdx.scenes.scene2d.Actor acteur : spellZone.getChildren()) {
                    if (acteur instanceof CodeBlockActor) {
                        sortJoueur.append(((CodeBlockActor) acteur).getCodeText());
                    }
                }

                String sortAttendu = "System.out.print(\"Boule de feu\");";

                if (sortJoueur.toString().equals(sortAttendu)) {
                    // --- 1. LE POPUP DE SUCCÈS ---
                    popupLabel.setText("SUCCES !");
                    popupLabel.setColor(Color.valueOf("#55FF55")); // Vert
                    popupLabel.clearActions(); // On annule les anciennes animations si on a rejoué
                    popupLabel.addAction(Actions.sequence(
                        Actions.alpha(0), // On le rend transparent
                        Actions.visible(true), // On l'active
                        Actions.fadeIn(0.5f), // Il apparaît en douceur (0.5s)
                        Actions.delay(2f), // Il reste affiché 2 secondes
                        Actions.fadeOut(0.5f), // Il disparaît en douceur
                        Actions.visible(false)
                    ));

                    // (Ici tu gardes ton code actuel pour la boule de feu fireball.addAction(...) )
                    Texture fireballTex = new Texture(Gdx.files.internal("images/fireball.png"));
                    Image fireball = new Image(fireballTex);
                    fireball.setSize(60, 60);
                    fireball.setPosition(300, 130);
                    stage.addActor(fireball);

                    fireball.addAction(Actions.sequence(
                        Actions.moveTo(850, 130, 0.4f),
                        Actions.run(() -> {
                            fireball.remove();
                            fireballTex.dispose();

                            monstre.addAction(Actions.sequence(
                                Actions.color(Color.RED, 0.1f),
                                Actions.color(Color.WHITE, 0.1f),
                                Actions.color(Color.RED, 0.1f),
                                Actions.color(Color.WHITE, 0.1f),
                                Actions.run(() -> btnReset.setVisible(true))
                            ));
                        })
                    ));

                } else {
                    // --- 2. LE POPUP D'ÉCHEC ---
                    popupLabel.setText("ÉCHEC !");
                    popupLabel.setColor(Color.valueOf("#FF5555")); // Rouge
                    popupLabel.clearActions();
                    popupLabel.addAction(Actions.sequence(
                        Actions.alpha(0),
                        Actions.visible(true),
                        Actions.fadeIn(0.3f), // Apparaît plus vite (car c'est une alerte)
                        Actions.delay(2f),
                        Actions.fadeOut(0.5f),
                        Actions.visible(false)
                    ));

                    // (Ici tu gardes ton code actuel pour l'attaque du monstre monstre.addAction(...) )
                    monstre.addAction(Actions.sequence(
                        Actions.moveBy(-500, 0, 0.2f),
                        Actions.run(() -> {
                            magicien.addAction(Actions.sequence(
                                Actions.color(Color.RED, 0.1f),
                                Actions.color(Color.WHITE, 0.1f)
                            ));
                        }),
                        Actions.moveBy(500, 0, 0.3f),
                        Actions.run(() -> btnReset.setVisible(true))
                    ));
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

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (codeFont != null) codeFont.dispose();
        // TRÈS IMPORTANT : On détruit nos textures personnalisées pour éviter les fuites de mémoire
        if (bgTexture != null) bgTexture.dispose();
        if (zoneColorTexture != null) zoneColorTexture.dispose();
        if (blockColorTexture != null) blockColorTexture.dispose();
        if (magicienTexture != null) magicienTexture.dispose();
        if (monstreTexture != null) monstreTexture.dispose();
    }
}
