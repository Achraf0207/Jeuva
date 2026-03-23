package com.Jeuva;

import com.badlogic.gdx.Game;

public class Jeuva extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}
