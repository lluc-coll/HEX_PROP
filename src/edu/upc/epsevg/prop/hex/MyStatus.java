/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author llucc
 */
public class MyStatus extends HexGameStatus {

    // crear una taula estatica amb size == hgs i per cada casella agafar un valor random per 0, 1, -1
    // recorrer la taula i per cada casella fer hash ^= taulaEstatica[i][j][z] (on < seria 0, 1, -1 o alguna cosa aixi)
    // i per cada place stone fer hash ^= taulaEstatica[placeStone.i][placeStone.j][placeStone.color]
    Heuristica h = Heuristica.getInstance();
    int myColor;
    int hash = 0;
    int valEstatic = 0;
    Map<Point, Map<Point, Integer>> graf1;
    Map<Point, Map<Point, Integer>> graf2;
    Point ini = new Point(0, -1);
    Point end = new Point(-1, 0);
    Map<Point, Integer> tuples1 = new HashMap<>();
    Map<Point, Integer> tuples2 = new HashMap<>();

    public MyStatus(HexGameStatus hgs) {
        super(hgs);
        myColor = getCurrentPlayerColor();
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                hash ^= h.taulaHash[i][j][getPos(i, j) + 1];
                valEstatic += (10 - (Math.abs(i - getSize() / 2) + Math.abs(j - getSize() / 2))) * getPos(i, j);
            }
        }
        graf1 = getTableGraph(myColor);
        graf2 = getTableGraph(-myColor);
    }

    public MyStatus(MyStatus hgs) {
        super(hgs);
        this.hash = hgs.hash;
        this.valEstatic = hgs.valEstatic;
        this.graf1 = hgs.graf1;
        this.graf2 = hgs.graf2;
        this.ini = hgs.ini;
        this.end = hgs.end;
        this.myColor = hgs.myColor;
    }

    @Override
    public void placeStone(Point point) {
        super.placeStone(point);
        hash ^= h.taulaHash[point.x][point.y][getPos(point.x, point.y) + 1];
        valEstatic += (10 - (Math.abs(point.x - getSize() / 2) + Math.abs(point.y - getSize() / 2))) * getPos(point);
        graphsUpdate(point, -getCurrentPlayerColor());
    }

    public int calculHeuristica() {
        int val = 0;
        val += h.heuristica(graf1, graf2, ini, end)*3;
        val += valEstatic;
        // falta posar el valor sumatori de les tuples.
        val += (900-(tuples1.values().stream().mapToInt(Integer::intValue).sum()) - (900-tuples2.values().stream().mapToInt(Integer::intValue).sum()));
        return val;
    }

    public int getNewHash(Point p) {
        return hash ^ h.taulaHash[p.x][p.y][getPos(p.x, p.y) + 1];
    }

    public int getNewValEstatic(Point p) {
        return valEstatic + (10 - (Math.abs(p.x - getSize() / 2) + Math.abs(p.y - getSize() / 2))) * getPos(p);
    }

    public Map<Point, Map<Point, Integer>> getTableGraph(int col) {
        Map<Point, Map<Point, Integer>> map = new HashMap<>();
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                Point p = new Point(i, j);
                map.put(p, getNeighbors(p, col));
                if (col == 1) {
                    if (i == 0) {
                        map.putIfAbsent(ini, new HashMap<>());
                        map.get(ini).put(p, getPos(p) == -1 ? 1000 : getPos(p) == 0 ? 2 : 0);
                    }
                    if (i == 1 && j != getSize() - 1 && getPos(p) == 1) {
                        if (getPos(i - 1, j) == 0 && getPos(i - 1, j + 1) == 0) {
                            map.get(ini).put(p, 1);
                            (myColor == col ? tuples1 : tuples2).merge(ini, 1, Integer::sum);
                        }
                    }
                    if (i == getSize() - 1) {
                        map.get(p).put(end, 0);
                    }
                    if (i == getSize() - 2 && j != 0 && getPos(p) == 1) {
                        if (getPos(i + 1, j) == 0 && getPos(i + 1, j - 1) == 0) {
                            map.get(p).put(end, 1);
                            (myColor == col ? tuples1 : tuples2).merge(p, 1, Integer::sum);
                        }
                    }
                } else {
                    if (j == 0) {
                        map.putIfAbsent(ini, new HashMap<>());
                        map.get(ini).put(p, getPos(p) == 1 ? 1000 : getPos(p) == 0 ? 2 : 0);
                    }
                    if (j == 1 && i != getSize() - 1 && getPos(p) == -1) {
                        if (getPos(i, j - 1) == 0 && getPos(i + 1, j - 1) == 0) {
                            map.get(ini).put(p, 1);
                            (myColor == -col ? tuples1 : tuples2).merge(ini, 1, Integer::sum);
                        }
                    }
                    if (j == getSize() - 1) {
                        map.get(p).put(end, 0);
                    }
                    if (j == getSize() - 2 && i != 0 && getPos(p) == -1) {
                        if (getPos(i, j + 1) == 0 && getPos(i - 1, j + 1) == 0) {
                            map.get(p).put(end, 1);
                            (myColor == col ? tuples1 : tuples2).merge(p, 1, Integer::sum);
                        }
                    }
                }
            }
        }
        return map;
    }

    public Map<Point, Integer> getNeighbors(Point p, int col) {
        (myColor == col ? tuples1 : tuples2).remove(p);
        Map<Point, Integer> ngbs = new HashMap<>();
        int[][] directions = {
            {-1, 0}, // Izquierda
            {-1, 1}, // Arriba-izquierda
            {0, 1}, // Arriba-derecha
            {1, 0}, // Derecha
            {1, -1}, // Abajo-derecha
            {0, -1}, // Abajo-izquierda
            {-2, 1}, // Arriba doble
            {-1, 2}, // Arriba-derecha doble
            {1, 1}, // Abajo-derecha doble
            {2, -1}, // Abajo doble
            {1, -2}, // Abajo-izquierda doble
            {-1, -1} // Arriba-izquierda doble
        };
        int i = 0;
        for (int[] dir : directions) {
            int x = p.x + dir[0];
            int y = p.y + dir[1];
            if (x >= 0 && y >= 0 && x < getSize() && y < getSize()) {
                if (i < 6) {
                    int val = 2;
                    if (getPos(x, y) == col) {
                        val = 0;
                    } else if (getPos(x, y) == -col) {
                        val = 1000;
                    }
                    ngbs.put(new Point(x, y), val);
                } else {
                    Point p1 = new Point(p.x + directions[i % 6][0], p.y + directions[i % 6][1]);
                    Point p2 = new Point(p.x + directions[(i + 1) % 6][0], p.y + directions[(i + 1) % 6][1]);
                    if (getPos(p) == col && getPos(x, y) == col && getPos(p1) == 0 && getPos(p2) == 0) {
                        ngbs.put(new Point(x, y), 1);
                        (myColor == col ? tuples1 : tuples2).merge(p, 1, Integer::sum);
                    }
                }
            }
            i++;
        }

        return ngbs;
    }

    public void graphsUpdate(Point p, int col) {
        int[][] directions = {
            {-1, 0}, // Izquierda
            {-1, 1}, // Arriba-izquierda
            {0, 1}, // Arriba-derecha
            {1, 0}, // Derecha
            {1, -1}, // Abajo-derecha
            {0, -1}, // Abajo-izquierda
            {-2, 1}, // Arriba doble
            {-1, 2}, // Arriba-derecha doble
            {1, 1}, // Abajo-derecha doble
            {2, -1}, // Abajo doble
            {1, -2}, // Abajo-izquierda doble
            {-1, -1} // Arriba-izquierda doble
        };
        graf1.put(p, getNeighbors(p, myColor));
        graf2.put(p, getNeighbors(p, -myColor));
        for (int[] dir : directions) {
            int x = p.x + dir[0];
            int y = p.y + dir[1];
            Point aux = new Point(x, y);
            if (x >= 0 && y >= 0 && x < getSize() && y < getSize()) {
                graf1.put(aux, getNeighbors(aux, myColor));
                graf2.put(aux, getNeighbors(aux, -myColor));
            }
        }

        // Comprovaciones para updatear los nodos auxiliares 
        graf1.put(ini, new HashMap<>());
        graf2.put(ini, new HashMap<>());
        tuples1.remove(ini);
        tuples2.remove(ini);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < getSize(); j++) {
                Point ij = new Point(i, j);
                Point imj = new Point(getSize() - 2, j);
                Point ji = new Point(j, i);
                Point jim = new Point(j, getSize() - 2);

                if (i == 0) {
                    (myColor == 1 ? graf1 : graf2).get(ini).put(ij, getPos(ij) == -1 ? 1000 : getPos(ij) == 0 ? 2 : 0);
                    (myColor == -1 ? graf1 : graf2).get(ini).put(ji, getPos(ji) == 1 ? 1000 : getPos(ji) == 0 ? 2 : 0);
                    (myColor == 1 ? graf1 : graf2).get(new Point(getSize() - 1, j)).put(end, 0);
                    (myColor == -1 ? graf1 : graf2).get(new Point(j, getSize() - 1)).put(end, 0);
                }
                if (i == 1 && j != getSize() - 1) {
                    if (getPos(ij) == 1 && getPos(i - 1, j) == 0 && getPos(i - 1, j + 1) == 0) {
                        (myColor == 1 ? graf1 : graf2).get(ini).put(ij, 1);
                        (myColor == 1 ? tuples1 : tuples2).merge(ini, 1, Integer::sum);
                    }
                    if (getPos(ji) == -1 && getPos(j, i - 1) == 0 && getPos(j + 1, i - 1) == 0) {
                        (myColor == -1 ? graf1 : graf2).get(ini).put(ji, 1);
                        (myColor == -1 ? tuples1 : tuples2).merge(ini, 1, Integer::sum);
                    }
                }
                if (i == 1 && j != 0) {
                    if (graf1.get(imj).containsKey(end)) {
                        graf1.get(imj).remove(end);
                        tuples1.merge(imj, 1, (oldValue, newValue) -> Math.max(oldValue - newValue, 0));
                    }
                    if (graf2.get(imj).containsKey(end)) {
                        graf2.get(imj).remove(end);
                        tuples2.merge(imj, 1, (oldValue, newValue) -> Math.max(oldValue - newValue, 0));
                    }
                    if (graf1.get(jim).containsKey(end)) {
                        graf1.get(jim).remove(end);
                        tuples1.merge(jim, 1, (oldValue, newValue) -> Math.max(oldValue - newValue, 0));
                    }
                    if (graf2.get(jim).containsKey(end)) {
                        graf2.get(jim).remove(end);
                        tuples2.merge(jim, 1, (oldValue, newValue) -> Math.max(oldValue - newValue, 0));
                    }

                    if (getPos(imj) == 1 && getPos(getSize() - 1, j) == 0 && getPos(getSize() - 1, j - 1) == 0) {
                        (myColor == 1 ? graf1 : graf2).get(imj).put(end, 1);
                        (myColor == 1 ? tuples1 : tuples2).merge(imj, 1, Integer::sum);
                    }
                    if (getPos(jim) == -1 && getPos(j, getSize() - 1) == 0 && getPos(j - 1, getSize() - 1) == 0) {
                        (myColor == -1 ? graf1 : graf2).get(jim).put(end, 1);
                        (myColor == -1 ? tuples1 : tuples2).merge(jim, 1, Integer::sum);
                    }
                }
            }
        }
    }

    public List<MoveNode> obtenerJugadas() {
        List<MoveNode> l = getMoves();
        l.sort((a, b) -> {
            int scoreA = getNewValEstatic(a.getPoint());
            int scoreB = getNewValEstatic(b.getPoint());
            return Integer.compare(scoreB, scoreA); // Sort Descending
        });
        return l;
    }
}