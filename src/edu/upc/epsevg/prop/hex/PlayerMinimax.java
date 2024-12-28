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
    public static int[][][] taulaHash;
    Heuristica h = new Heuristica();
    String name;
    int depth;
    long playsExplored;
    int color;

    public PlayerMinimax(int depth) {
        this.name = "NOMBRE";
        this.depth = depth;
        taulaHash = createHashingTable(11);
    }

    @Override
    public PlayerMove move(HexGameStatus hgs) {
        color = hgs.getCurrentPlayerColor();
        playsExplored = 0;
        MyStatus m = new MyStatus(hgs);
        h.heuristica(m.graf1, m.graf2, m.ini, m.end);
        return new PlayerMove(miniMax(m), playsExplored, depth, SearchType.MINIMAX);
    }

    @Override
    public void timeout() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getName() {
        return name;
    }

    public Point miniMax(MyStatus hgs) {
        long tiempoInicial = System.currentTimeMillis();
        int max = -300000;
        Point columnaJugar = new Point(0, 0);
        int alpha = Integer.MIN_VALUE, beta = Integer.MAX_VALUE;

        List<MoveNode> movimientos = h.obtenerJugadas(hgs);
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
        System.out.println("Tiempo: " + tiempo + " s");
        System.out.println(columnaJugar);
        return columnaJugar;
    }

    public int valorMax(MyStatus hgs, int prof, int alpha, int beta) {
        int max = -100000;

        if (hgs.isGameOver() && hgs.GetWinner() != null) {
            return max;
        } else if (prof == 0 || hgs.isGameOver()) {
            return 0; //heuristicaGlobal(t, colorNB);
        } else {
            // Con poda: genera y ordena jugadas
            List<MoveNode> movimientos = h.obtenerJugadas(hgs);
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

    
    public int valorMin(MyStatus hgs, int prof, int alpha, int beta) {
        int min = 100000;

        if (hgs.isGameOver() && hgs.GetWinner() != null) {
            return min;
        } else if (prof == 0 || hgs.isGameOver()) {
            return 0; //heuristicaGlobal(t, colorNB);
        } else {
            // Con poda: genera y ordena jugadas
            List<MoveNode> movimientos = h.obtenerJugadas(hgs);
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

    public static int[][][] createHashingTable(int size){
        int[][][] table = new int[size][size][3];
        Random ran = new Random();
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                for(int z = 0; z<3; z++){
                    table[i][j][z] = ran.nextInt();
                }
            }
        }
        return table;
    }
}
