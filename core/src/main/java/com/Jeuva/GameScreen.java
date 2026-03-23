package com.Jeuva;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameScreen implements Screen {

    private final Jeuva game;
    private Stage stage;
    private Skin skin;

    private Puzzle currentPuzzle;
    private List<String> dropZoneCodes;
    private DragAndDrop dnd;

    private Table sourceTable;
    private Table dropTable;

    private int puzzleIndex = 0;
    private List<Puzzle> puzzles;

    public GameScreen(Jeuva game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage   = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin    = new Skin(Gdx.files.internal("uiskin.json"));
        puzzles = PuzzleFactory.getAllPuzzles();
        loadPuzzle(0);
    }

    // ─────────────────────────────────────────────────────────────────
    private void loadPuzzle(int index) {
        puzzleIndex   = index;
        dropZoneCodes = new ArrayList<>();
        dnd           = new DragAndDrop();
        dnd.setDragTime(0);
        stage.clear();

        currentPuzzle = puzzles.get(index);

        List<String> shuffled = new ArrayList<>(currentPuzzle.correctOrder);
        Collections.shuffle(shuffled);

        buildUI(shuffled);
    }

    // ─────────────────────────────────────────────────────────────────
    private void buildUI(List<String> shuffled) {

        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(skin.newDrawable("white", new Color(0.08f, 0.08f, 0.18f, 1f)));
        root.pad(24);
        stage.addActor(root);

        // ── Titre ────────────────────────────────────────────────────
        Label title = new Label("Puzzle " + (puzzleIndex + 1), skin);
        title.setFontScale(1.8f);
        title.setColor(Color.CYAN);
        root.add(title).colspan(3).padBottom(6).row();

        Label counter = new Label(puzzles.size() + " puzzles au total", skin);
        counter.setColor(Color.LIGHT_GRAY);
        root.add(counter).colspan(3).padBottom(20).row();

        // ── En-têtes ──────────────────────────────────────────────────
        Label srcHeader  = new Label("Blocs disponibles", skin);
        Label dropHeader = new Label("Sortilège (glisse ici)", skin);
        srcHeader.setColor(Color.YELLOW);
        dropHeader.setColor(Color.YELLOW);
        root.add(srcHeader).width(380).padRight(20);
        root.add(new Label("", skin)).width(130);
        root.add(dropHeader).width(380).row();

        // ── Table SOURCE ──────────────────────────────────────────────
        sourceTable = new Table();
        sourceTable.top().left().pad(10);
        sourceTable.setBackground(
            skin.newDrawable("white", new Color(0.12f, 0.12f, 0.25f, 1f)));

        for (String code : shuffled) {
            Container<Label> bloc = makeBlockLabel(code, new Color(0.25f, 0.25f, 0.5f, 1f));
            sourceTable.add(bloc).width(360).height(50).padBottom(8).fill().row();
            registerAsSource(bloc, code);
        }

        ScrollPane srcScroll = new ScrollPane(sourceTable, skin);
        srcScroll.setScrollingDisabled(true, true);
        srcScroll.setTouchable(Touchable.childrenOnly);

        // ── Table DROP ────────────────────────────────────────────────
        dropTable = new Table();
        dropTable.top().left().pad(10);
        dropTable.setBackground(
            skin.newDrawable("white", new Color(0.05f, 0.2f, 0.1f, 1f)));

        // ✅ Stack superpose dropTable + acteur cible invisible
        Stack dropStack = new Stack();
        dropStack.add(dropTable);

        Actor dropTargetActor = new Actor();
        dropStack.add(dropTargetActor);

        registerDropTarget(dropTargetActor);

        // ── Boutons ───────────────────────────────────────────────────
        Table btnCol = new Table();

        TextButton verifyBtn = new TextButton("Verifier", skin);
        verifyBtn.getLabel().setFontScale(1.15f);
        verifyBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                checkAnswer();
            }
        });

        TextButton resetBtn = new TextButton("Reset", skin);
        resetBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                loadPuzzle(puzzleIndex);
            }
        });

        btnCol.add(verifyBtn).width(120).height(50).padBottom(12).row();
        btnCol.add(resetBtn).width(120).height(40);

        // ── Layout ────────────────────────────────────────────────────
        root.add(srcScroll).width(380).height(420).top().padRight(20);
        root.add(btnCol).width(130).top().padTop(10).padRight(20);
        root.add(dropStack).width(380).height(420).top().row();

        // ── Navigation ───────────────────────────────────────────────
        Table nav = new Table();
        if (puzzleIndex > 0) {
            TextButton prev = new TextButton("< Precedent", skin);
            prev.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    loadPuzzle(puzzleIndex - 1);
                }
            });
            nav.add(prev).padRight(20);
        }
        if (puzzleIndex < puzzles.size() - 1) {
            TextButton next = new TextButton("Suivant >", skin);
            next.addListener(new ClickListener() {
                @Override public void clicked(InputEvent e, float x, float y) {
                    loadPuzzle(puzzleIndex + 1);
                }
            });
            nav.add(next);
        }
        root.add(nav).colspan(3).padTop(16);
    }

    // ─────────────────────────────────────────────────────────────────
    private Container<Label> makeBlockLabel(String code, Color bgColor) {
        Label lbl = new Label("  " + code, skin);
        lbl.setColor(Color.WHITE);

        Container<Label> container = new Container<>(lbl);
        container.setBackground(skin.newDrawable("white", bgColor));
        container.fill();
        container.left();
        return container;
    }

    // ─────────────────────────────────────────────────────────────────
    private void registerAsSource(Actor actor, String code) {
        dnd.addSource(new DragAndDrop.Source(actor) {

            @Override
            public DragAndDrop.Payload dragStart(InputEvent event,
                                                 float x, float y, int pointer) {
                Label ghost = new Label("  " + code, skin);
                ghost.setColor(Color.YELLOW);

                Container<Label> ghostContainer = new Container<>(ghost);
                ghostContainer.setBackground(
                    skin.newDrawable("white", new Color(0.35f, 0.35f, 0.7f, 0.95f)));
                ghostContainer.setSize(360, 50);
                ghostContainer.fill();
                ghostContainer.left();

                DragAndDrop.Payload payload = new DragAndDrop.Payload();
                payload.setObject(code);
                payload.setDragActor(ghostContainer);

                actor.setColor(new Color(1f, 1f, 1f, 0.3f));
                return payload;
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer,
                                 DragAndDrop.Payload payload, DragAndDrop.Target target) {
                if (target == null) {
                    actor.setColor(Color.WHITE);
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────
    private void registerDropTarget(Actor dropTargetActor) {
        dnd.addTarget(new DragAndDrop.Target(dropTargetActor) {

            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload,
                                float x, float y, int pointer) {
                dropTable.setBackground(
                    skin.newDrawable("white", new Color(0.1f, 0.45f, 0.15f, 1f)));
                return true;
            }

            @Override
            public void reset(DragAndDrop.Source source, DragAndDrop.Payload payload) {
                dropTable.setBackground(
                    skin.newDrawable("white", new Color(0.05f, 0.2f, 0.1f, 1f)));
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload,
                             float x, float y, int pointer) {
                dropTable.setBackground(
                    skin.newDrawable("white", new Color(0.05f, 0.2f, 0.1f, 1f)));

                String code = (String) payload.getObject();
                dropZoneCodes.add(code);

                Container<Label> dropped = makeBlockLabel(code,
                    new Color(0.1f, 0.38f, 0.15f, 1f));
                dropTable.add(dropped).width(360).height(50).padBottom(8).fill().row();
                dropTable.invalidateHierarchy();

                source.getActor().setVisible(false);
                source.getActor().setTouchable(Touchable.disabled);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────
    private void checkAnswer() {
        List<String> correct = currentPuzzle.correctOrder;

        if (dropZoneCodes.size() != correct.size()) {
            showDialog("Hibou",
                "Il manque des blocs !\nPlace les " + correct.size() +
                    " blocs avant de verifier.\n\nIndice : " + currentPuzzle.hint,
                "Reessayer", false);
            return;
        }

        for (int i = 0; i < correct.size(); i++) {
            if (!dropZoneCodes.get(i).equals(correct.get(i))) {
                showDialog("Indice du hibou",
                    "Mauvais ordre !\n\nIndice : " + currentPuzzle.hint,
                    "Reessayer", false);
                return;
            }
        }

        boolean isLast = puzzleIndex >= puzzles.size() - 1;
        showDialog("Bravo !",
            "Parfait ! Le sort est lance !\n\n" +
                (isLast ? "Tu as termine tous les puzzles !" : "Pret pour le suivant ?"),
            isLast ? "Terminer" : "Suivant >",
            !isLast);
    }

    // ─────────────────────────────────────────────────────────────────
    private void showDialog(String title, String message,
                            String btnText, boolean goNext) {
        Dialog d = new Dialog(title, skin) {
            @Override protected void result(Object obj) {
                if ((Boolean) obj && goNext) loadPuzzle(puzzleIndex + 1);
            }
        };
        Label msg = new Label(message, skin);
        msg.setWrap(true);
        msg.setColor(Color.WHITE);
        d.getContentTable().add(msg).width(420).pad(20);
        d.button(btnText, true);
        d.show(stage);
    }

    // ─────────────────────────────────────────────────────────────────
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.18f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) {
        stage.getViewport().update(w, h, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
