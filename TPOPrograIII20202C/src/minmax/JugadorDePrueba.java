package minmax;

import java.util.Scanner;

import reversi.Celda;
import reversi.JugadorReversi;

public class JugadorDePrueba implements JugadorReversi {

	private Scanner in;
	
	public JugadorDePrueba(Scanner in) {
		this.in = in;
	}
	
	@Override
	public Celda devolverJugadaOptima(int[][] tablero) {
		in = new Scanner(System.in);
		System.out.print("Jugada de prueba para O (enter para pasar el turno si no hay movimientos posibles): ");

		String jugada = in.nextLine();

		return traducirJugada(jugada);
	}

	private Celda traducirJugada(String jugada) {

		int fila;
		int columna;

		if (jugada.equals("")) {
			// pasa el turno al adversario
			fila = -1;
			columna = -1;
		} else {
			// traduce a índice de la matriz (A1 -> fila 0 y columna 0)
			fila = ((int) jugada.charAt(0)) - 65;
			columna = ((int) jugada.charAt(1)) - 49;
		}
		return new Celda(fila, columna);
		
	}

}
