package main;

import java.util.Scanner;

//import minmax.JugadorDePrueba;
import minmax.JugadorReversiImpl;
import reversi.JugadorReversi;
import reversi.Reversi;

public class Main {

	public static void main(String[] args) throws Exception {

		Scanner in = new Scanner(System.in);
		try {
			// Clase JugadorDePrueba es únicamente para probar el juego,
			// esta línea de código no va en el TPO final
			//JugadorReversi jugadorReversi = new JugadorDePrueba(in);

			// La clase JugadorMinMaxImpl es la que deben implementar y usar acá
			JugadorReversi jugadorReversi = new JugadorReversiImpl();

			Reversi reversi = new Reversi(jugadorReversi, in);
			reversi.jugar();
			in.close();
			
		} catch (Exception e) {
			
			in.close();
			System.err.print("Error en el juego:\n" + e.getMessage());
			
		}
	}

}
