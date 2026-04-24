package com.Jeuva.models;

import java.util.HashMap;
import java.util.Map;

public class LevelData {
    private int id;
    private String title;
    private String expectedCode;
    private String hint;
    private String[] availableBlocks;

    private static Map<Integer, LevelData> levels = new HashMap<>();

    public LevelData(int id, String title, String expectedCode, String hint, String[] availableBlocks) {
        this.id = id;
        this.title = title;
        this.expectedCode = expectedCode;
        this.hint = hint;
        this.availableBlocks = availableBlocks;
    }

    static {
        // --- NIVEAU 1 : Déclaration d'un int ---
        levels.put(1, new LevelData(1, "Les Points de Vie",
            "int vie = 100;",
            "Pour créer un nombre entier en Java, on utilise le mot-clé 'int'. Donne une 'vie' de 100 à ton mage et n'oublie pas le point-virgule !",
            new String[]{"int", "vie", "=", "100;"}));

        // --- NIVEAU 2 : Déclaration d'un String ---
        levels.put(2, new LevelData(2, "Le Nom du Héros",
            "String nom = \"Merlin\";",
            "Une chaîne de caractères (du texte) se déclare avec 'String' (avec une majuscule !). Assigne le nom \"Merlin\" entre guillemets.",
            new String[]{"String", "nom", "=", "\"Merlin\";"}));

        // --- NIVEAU 3 : Déclaration d'un boolean ---
        levels.put(3, new LevelData(3, "L'État du Mage",
            "boolean enVie = true;",
            "Un 'boolean' ne peut être que vrai (true) ou faux (false). Déclare que ton mage est 'enVie' en lui donnant la valeur 'true'.",
            new String[]{"boolean", "enVie", "=", "true;"}));

        // --- NIVEAU 4 : Déclaration d'un Tableau ---
        levels.put(4, new LevelData(4, "Le Grimoire (Tableau)",
            "String[] sorts = {\"Feu\", \"Glace\"};",
            "Pour stocker plusieurs éléments, on ajoute des crochets '[]' après le type. Mets tes sorts entre des accolades '{}'.",
            new String[]{"String[]", "sorts", "=", "{\"Feu\", \"Glace\"};"}));

        // --- NIVEAU 5 : Condition IF ---
        levels.put(5, new LevelData(5, "Le Réflexe de Survie",
            "if (vie < 50) { boirePotion(); }",
            "L'instruction 'if' vérifie une condition entre parenthèses '()'. Si la 'vie' est sous 50, le code entre les accolades '{}' s'exécute.",
            new String[]{"if", "(vie < 50)", "{", "boirePotion();", "}"}));

        // --- NIVEAU 6 : Condition IF / ELSE ---
        levels.put(6, new LevelData(6, "Le Choix Crucial",
            "if (enVie) { attaquer(); } else { fuir(); }",
            "Ajoute un 'else' après le 'if'. Cela veut dire \"sinon\". Si tu es 'enVie' tu attaques, sinon (else) tu fuis !",
            new String[]{"if (enVie)", "{ attaquer(); }", "else", "{ fuir(); }"}));

        // --- NIVEAU 7 : Boucle FOR ---
        levels.put(7, new LevelData(7, "L'Attaque Multiple",
            "for (int i=0; i<3; i++) { lancerFeu(); }",
            "La boucle 'for' répète une action. Elle utilise un compteur 'i' qui commence à 0, s'arrête avant 3, et augmente de 1 ('i++').",
            new String[]{"for", "(int i=0; i<3; i++)", "{", "lancerFeu();", "}"}));

        // --- NIVEAU 8 : Boucle WHILE ---
        levels.put(8, new LevelData(8, "La Concentration",
            "while (mana < 100) { mediter(); }",
            "La boucle 'while' signifie \"Tant que\". Tant que la condition 'mana < 100' est vraie, on répète l'action de méditer.",
            new String[]{"while", "(mana < 100)", "{", "mediter();", "}"}));

        // --- NIVEAU 9 : Déclaration d'une Classe ---
        levels.put(9, new LevelData(9, "Le Plan de Construction",
            "class Mage { int vie = 100; }",
            "Une 'class' est le plan de ton personnage. Crée la classe 'Mage' et intègre sa 'vie' à l'intérieur de ses accolades.",
            new String[]{"class", "Mage", "{", "int vie = 100;", "}"}));

        // --- NIVEAU 10 : Instanciation d'un Objet ---
        levels.put(10, new LevelData(10, "L'Invocation Finale",
            "Mage monHeros = new Mage();",
            "Pour donner vie à ton plan, il faut créer un objet ! Utilise le mot-clé 'new' suivi du nom de ta classe et de parenthèses.",
            new String[]{"Mage", "monHeros", "=", "new", "Mage();"}));
    }

    public static LevelData getLevel(int id) {
        return levels.get(id);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getExpectedCode() { return expectedCode; }
    public String getHint() { return hint; }
    public String[] getAvailableBlocks() { return availableBlocks; }
}
