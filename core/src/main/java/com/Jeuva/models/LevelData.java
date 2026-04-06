package com.Jeuva.models; // Assure-toi que le package correspond au tien

public class LevelData {
    private int id;                 // Le numéro du palier (ex: 1)
    private String monsterImagePath;// Le chemin vers l'image du monstre (ex: "images/monstre.png")
    private String expectedCode;    // Le code parfait pour gagner (ex: "System.out.print(\"Boule de feu\");")
    private String[] availableBlocks; // Les morceaux de code mélangés qu'on donne au joueur

    // Le Constructeur : c'est lui qui permet de fabriquer un niveau !
    public LevelData(int id, String monsterImagePath, String expectedCode, String[] availableBlocks) {
        this.id = id;
        this.monsterImagePath = monsterImagePath;
        this.expectedCode = expectedCode;
        this.availableBlocks = availableBlocks;
    }

    // --- Les Getters (pour que GameScreen puisse lire ces infos) ---

    public int getId() {
        return id;
    }

    public String getMonsterImagePath() {
        return monsterImagePath;
    }

    public String getExpectedCode() {
        return expectedCode;
    }

    public String[] getAvailableBlocks() {
        return availableBlocks;
    }
}
