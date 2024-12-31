/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.List;

/**
 *
 * @author llucc
 */
public class PlayerID implements IPlayer, IAuto {

    Heuristica h = Heuristica.getInstance();
    String name;
    boolean timeoutFlag;
    long playsExplored;
    int depth;
    int color;
    Point bestMove;

    public PlayerID() {
        this.name = "HEXpertos";
        h.taulaHash = Heuristica.createHashingTable(11); // sha de mirar com agafar la mida
    }

    @Override
    public PlayerMove move(HexGameStatus hgs) {
        timeoutFlag = false;
        color = hgs.getCurrentPlayerColor();
        playsExplored = 0;
        MyStatus m = new MyStatus(hgs);
        bestMove = null;
        depth = 1;

        while (!timeoutFlag) {
            try {
                iterativeMinimax(m, depth);
                System.out.println("Profundidad alcanzada: " + depth);
                depth++;
            } catch (TimeoutException e) {
                break;
            }
        }
        return new PlayerMove(bestMove, playsExplored, depth, SearchType.MINIMAX_IDS);
    }

    @Override
    public void timeout() {
        timeoutFlag = true;
    }

    @Override
    public String getName() {
        return name;
    }

    private void iterativeMinimax(MyStatus status, int depth) throws TimeoutException {
        int bestScore = Integer.MIN_VALUE;

        List<MoveNode> movimientos = h.obtenerJugadas(status);

        for (MoveNode move : movimientos) {
            if (!h.pruned.contains(status.getNewHash(move.getPoint()))) {
                checkTimeout();
                MyStatus newState = new MyStatus(status);
                newState.placeStone(move.getPoint());
                int score = valorMin(newState, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move.getPoint();
                }
            }
        }
    }

    private int valorMax(MyStatus status, int depth, int alpha, int beta) throws TimeoutException {
        checkTimeout();
        int maxScore = Integer.MIN_VALUE;
        if (status.isGameOver() && status.GetWinner() != null) {
            return maxScore;
        }
        if (depth == 0 || status.isGameOver()) {
            playsExplored++;
            return h.heuristica(status.graf1, status.graf2, status.ini, status.end);
        }
        
        List<MoveNode> movimientos = h.obtenerJugadas(status);

        for (MoveNode move : movimientos) {
            if (!h.pruned.contains(status.getNewHash(move.getPoint()))) {
                MyStatus newState = new MyStatus(status);
                newState.placeStone(move.getPoint());
                int score = valorMin(newState, depth - 1, alpha, beta);
                if (score < maxScore){
                    maxScore = score;
                    h.millorJugada.put(status.hash, move.getPoint());
                }
                alpha = Math.max(alpha, maxScore);
                if (alpha >= beta) {
                    h.pruned.add(status.hash);
                    break;
                }
            }
        }

        return maxScore;
    }

    private int valorMin(MyStatus status, int depth, int alpha, int beta) throws TimeoutException {
        checkTimeout();
        int minScore = Integer.MAX_VALUE;
        if (status.isGameOver() && status.GetWinner() != null) {
            return minScore;
        }
        if (depth == 0 || status.isGameOver()) {
            playsExplored++;
            return h.heuristica(status.graf1, status.graf2, status.ini, status.end);
        }
        
        List<MoveNode> movimientos = h.obtenerJugadas(status);

        for (MoveNode move : movimientos) {
            if (!h.pruned.contains(status.getNewHash(move.getPoint()))) {
                MyStatus newState = new MyStatus(status);
                newState.placeStone(move.getPoint());
                int score = valorMax(newState, depth - 1, alpha, beta);
                if (score < minScore){
                    minScore = score;
                    h.millorJugada.put(status.hash, move.getPoint());
                }
                beta = Math.min(beta, minScore);
                if (alpha >= beta) {
                    h.pruned.add(status.hash);
                    break;
                }
            }
        }

        return minScore;
    }

    private void checkTimeout() throws TimeoutException {
        if (timeoutFlag) {
            throw new TimeoutException("Timeout reached!");
        }
    }

    private static class TimeoutException extends Exception {

        public TimeoutException(String message) {
            super(message);
        }
    }
}
