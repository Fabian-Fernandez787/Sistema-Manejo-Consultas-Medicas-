package clinic.cli;

import java.util.Scanner;

public class Main {

    // Displays the mode selection menu
    // Calls: System.out.println()
    // Called by: main()
    private static void showModeMenu() {
        System.out.println("\n=== SELECCIONAR MODO DE EJECUCION ===");
        System.out.println("1. Modo manual (ingresar datos paso a paso)");
        System.out.println("2. Modo automatico (datos y acciones de prueba)");
        System.out.println("0. Salir");
    }

    // Lets the user choose between ManualMode and AutomaticMode
    // Calls: Scanner(), showModeMenu(), sc.nextLine(), trim(), Integer.parseInt(), ManualMode.run(), AutomaticMode.run(), sc.close()
    // Called by: JVM when starting the application
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int mode = -1;

        while (mode != 0) {
            showModeMenu();
            System.out.print("Seleccione un modo: ");
            String input = sc.nextLine().trim();

            try {
                mode = Integer.parseInt(input);

                switch (mode) {
                    case 1:
                        ManualMode.run(sc);
                        break;
                    case 2:
                        AutomaticMode.run(sc);
                        break;
                    case 0:
                        System.out.println("Hasta luego.");
                        break;
                    default:
                        System.out.println("Modo no valido.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingrese un numero.");
            }
        }

        sc.close();
    }
}