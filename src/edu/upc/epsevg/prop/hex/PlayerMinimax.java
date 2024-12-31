/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.*;

/**
 *
 * @author llucc
 */
public class PlayerMinimax implements IPlayer, IAuto {

    /**
     * Instància de la classe Heuristica per avaluar l'estat del joc.
     */
    Heuristica h = Heuristica.getInstance();

    /**
     * Nom del jugador.
     */
    String name;

    /**
     * Profunditat màxima de l'arbre de decisions que explora Minimax.
     */
    int depth;

    /**
     * Nombre total de nodes explorats durant la cerca.
     */
    long playsExplored;

    /**
     * Color actual del jugador (1 o -1).
     */
    int color;

    /**
     * Constructor del jugador Minimax.
     *
     * @param depth Profunditat màxima per a l'algorisme Minimax.
     */
    public PlayerMinimax(int depth) {
        this.name = "HEXpertos";
        this.depth = depth;
        h.taulaHash = Heuristica.createHashingTable(11);
    }

    /**
     * Selecciona el millor moviment utilitzant l'algorisme Minimax.
     *
     * @param hgs Estat actual del joc (HexGameStatus).
     * @return Un objecte {@link PlayerMove} amb el millor moviment calculat.
     */
    @Override
    public PlayerMove move(HexGameStatus hgs) {
        color = hgs.getCurrentPlayerColor();
        playsExplored = 0;
        MyStatus m = new MyStatus(hgs);
        return new PlayerMove(miniMax(m), playsExplored, depth, SearchType.MINIMAX);
    }

    /**
     * Marca el jugador com a no disponible per gestionar el timeout.
     * (Actualment no implementat).
     */
    @Override
    public void timeout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Retorna el nom del jugador.
     *
     * @return Nom del jugador.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Cerca el millor moviment utilitzant l'algorisme Minimax amb poda
     * alfa-beta.
     *
     * @param hgs L'estat actual del joc.
     * @return El punt que representa el millor moviment calculat.
     */
    public Point miniMax(MyStatus hgs) {
        long tiempoInicial = System.currentTimeMillis();
        int max = -300000;
        Point columnaJugar = new Point(0, 0);
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;

        List<MoveNode> movimientos = hgs.obtenerJugadas();
        for (MoveNode jugada : movimientos) {
            MyStatus nova = new MyStatus(hgs);
            nova.placeStone(jugada.getPoint());
            int actual = valorMin(nova, depth - 1, alpha, beta);
            if (actual > max) {
                max = actual;
                columnaJugar = jugada.getPoint();
            }
        }

        long tiempoFinal = System.currentTimeMillis();
        double tiempo = (tiempoFinal - tiempoInicial) / 1000.0;
        System.out.println("Temps: " + tiempo + " s");
        return columnaJugar;
    }

    /**
     * Calcula el valor màxim de Minimax per a un estat donat.
     *
     * @param hgs L'estat actual del joc.
     * @param prof La profunditat restant per explorar.
     * @param alpha El millor valor garantit per al jugador MAX.
     * @param beta El millor valor garantit per al jugador MIN.
     * @return El valor màxim calculat per a l'estat actual.
     */
    public int valorMax(MyStatus hgs, int prof, int alpha, int beta) {
        int max = -100000;

        if (hgs.isGameOver() && hgs.GetWinner() != null) {
            return max;
        } else if (prof == 0) {
            playsExplored++;
            return hgs.calculHeuristica();
        } else {
            List<MoveNode> movimientos = hgs.obtenerJugadas();
            for (MoveNode jugada : movimientos) {
                MyStatus nova = new MyStatus(hgs);
                nova.placeStone(jugada.getPoint());
                int min = valorMin(nova, prof - 1, alpha, beta);
                max = Math.max(max, min);
                alpha = Math.max(alpha, max);
                if (alpha >= beta) {
                    break;
                }
            }
        }
        return max;
    }

    /**
     * Calcula el valor mínim de Minimax per a un estat donat.
     *
     * @param hgs L'estat actual del joc.
     * @param prof La profunditat restant per explorar.
     * @param alpha El millor valor garantit per al jugador MAX.
     * @param beta El millor valor garantit per al jugador MIN.
     * @return El valor mínim calculat per a l'estat actual.
     */
    public int valorMin(MyStatus hgs, int prof, int alpha, int beta) {
        int min = 100000;

        if (hgs.isGameOver() && hgs.GetWinner() != null) {
            return min;
        } else if (prof == 0) {
            return hgs.calculHeuristica();
        } else {
            List<MoveNode> movimientos = hgs.obtenerJugadas();
            for (MoveNode jugada : movimientos) {

                MyStatus nova = new MyStatus(hgs);
                nova.placeStone(jugada.getPoint());
                int max = valorMax(nova, prof - 1, alpha, beta);
                min = Math.min(min, max);
                beta = Math.min(beta, min);
                if (alpha >= beta) {
                    break;
                }
            }
        }
        return min;
    }
}
