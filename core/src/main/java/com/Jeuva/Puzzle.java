package com.Jeuva;

import java.util.List;

public class Puzzle {
    public final String title;
    public final String hint;
    public final List<String> correctOrder;

    public Puzzle(String title, String hint, List<String> correctOrder) {
        this.title = title;
        this.hint = hint;
        this.correctOrder = correctOrder;
    }
}
