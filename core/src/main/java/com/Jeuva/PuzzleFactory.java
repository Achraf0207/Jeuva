package com.Jeuva;

import java.util.Arrays;
import java.util.List;

public class PuzzleFactory {

    public static List<Puzzle> getAllPuzzles() {
        return Arrays.asList(

            new Puzzle(
                "Puzzle 1",
                "Ce code calcule la factorielle d'un nombre",
                Arrays.asList(
                    "int n = 5;",
                    "int result = 1;",
                    "for (int i = 1; i <= n; i++)",
                    "{",
                    "    result *= i;",
                    "}"
                )
            ),

            new Puzzle(
                "Puzzle 2",
                "Ce code trouve le plus grand entre deux nombres",
                Arrays.asList(
                    "int a = 8;",
                    "int b = 3;",
                    "if (a > b)",
                    "    System.out.println(a);",
                    "else",
                    "    System.out.println(b);"
                )
            ),

            new Puzzle(
                "Puzzle 3",
                "Ce code échange les valeurs de deux variables",
                Arrays.asList(
                    "int x = 10;",
                    "int y = 20;",
                    "int temp = x;",
                    "x = y;",
                    "y = temp;"
                )
            ),

            new Puzzle(
                "Puzzle 4",
                "Ce code additionne tous les entiers de 1 à N",
                Arrays.asList(
                    "int n = 10;",
                    "int sum = 0;",
                    "int i = 1;",
                    "while (i <= n)",
                    "{",
                    "    sum += i;",
                    "    i++;",
                    "}"
                )
            ),

            new Puzzle(
                "Puzzle 5",
                "Ce code vérifie si un nombre est pair ou impair",
                Arrays.asList(
                    "int n = 7;",
                    "if (n % 2 == 0)",
                    "    System.out.println(\"Pair\");",
                    "else",
                    "    System.out.println(\"Impair\");"
                )
            ),

            new Puzzle(
                "Puzzle 6",
                "Ce code parcourt un mot lettre par lettre",
                Arrays.asList(
                    "String mot = \"magique\";",
                    "int count = 0;",
                    "for (char c : mot.toCharArray())",
                    "{",
                    "    if (\"aeiouy\".indexOf(c) >= 0)",
                    "        count++;",
                    "}"
                )
            ),

            new Puzzle(
                "Puzzle 7",
                "Ce code calcule 2 exposant N",
                Arrays.asList(
                    "int n = 6;",
                    "int result = 1;",
                    "for (int i = 0; i < n; i++)",
                    "{",
                    "    result *= 2;",
                    "}"
                )
            ),

            new Puzzle(
                "Puzzle 8",
                "Ce code retourne un mot à l'envers",
                Arrays.asList(
                    "String mot = \"sort\";",
                    "String inverse = \"\";",
                    "for (int i = mot.length() - 1; i >= 0; i--)",
                    "{",
                    "    inverse += mot.charAt(i);",
                    "}"
                )
            )
        );
    }
}
