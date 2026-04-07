package com.Jeuva.models; // Assure-toi que le package correspond au tien

public class LevelData {
    private int id;                 // Le numéro du palier (ex: 1)
    private String monsterImagePath;// Le chemin vers l'image du monstre (ex: "images/monstre.png")
    private String expectedCode;    // Le code parfait pour gagner (ex: "System.out.print(\"Boule de feu\");")
    private String[] availableBlocks; // Les morceaux de code mélangés qu'on donne au joueur
    private String hint;

    // Le Constructeur : c'est lui qui permet de fabriquer un niveau !
    public LevelData(int id, String monsterImagePath, String expectedCode, String[] availableBlocks, String hint) {
        this.id = id;
        this.monsterImagePath = monsterImagePath;
        this.expectedCode = expectedCode;
        this.availableBlocks = availableBlocks;
        this.hint = hint;
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

    public String getHint() { return hint; }


    // --- L'USINE À NIVEAUX --
    public static LevelData getLevel(int niveauId) {
        if (niveauId == 1) {
            return new LevelData(
                1,
                "images/mushroom-monster.png",
                "System.out.print(\"Boule de feu\");",
                new String[]{"\");", "System.out.print(\"", "Boule de feu"},
                "📖 GRIMOIRE : Pour parler, utilise le sort d'affichage.\n N'oublie pas la majuscule à 'System' et le point-virgule à la fin !"
            );
        }
        else if (niveauId == 2) {
            return new LevelData(
                2,
                "images/gobelin.png", // (J'ai mis ton gobelin !)
                "int mana = 100;",
                new String[]{"100;", "int ", "mana = "},
                 "📖 GRIMOIRE : Pour stocker un nombre entier (sans virgule),\nutilise la bourse de type 'int'."
            );
        }
        return null;
    }
}
