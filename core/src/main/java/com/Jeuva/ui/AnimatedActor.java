package com.Jeuva.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AnimatedActor extends Actor {
    private Animation<TextureRegion> animation;
    private float stateTime;

    public AnimatedActor(Animation<TextureRegion> animation) {
        this.animation = animation;
        this.stateTime = 0f;
        TextureRegion firstFrame = animation.getKeyFrame(0);
        setSize(firstFrame.getRegionWidth(), firstFrame.getRegionHeight());
    }

    // 👉 NOUVELLE MÉTHODE : Permet de changer l'animation en cours de jeu
    public void setAnimation(Animation<TextureRegion> newAnimation) {
        this.animation = newAnimation;
        this.stateTime = 0f; // On remet le chrono à zéro pour lire depuis le début
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        // 👉 CORRECTION : On enlève le "true" pour que le PlayMode (Loop ou Normal) soit respecté !
        TextureRegion currentFrame = animation.getKeyFrame(stateTime);

        batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(),
            getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }
}
