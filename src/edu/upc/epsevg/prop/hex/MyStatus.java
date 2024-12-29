/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.AbstractMap;
import java.util.ArrayList;
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
    int myColor;
    int hash = 0;
    int valEstatic = 0;
    Map<Point, Map<Point, Integer>> graf1;
    Map<Point, Map<Point, Integer>> graf2;
    Point ini = new Point(0, -1);
    Point end = new Point(-1, 0);

    public MyStatus(HexGameStatus hgs) {
        super(hgs);
        myColor = getCurrentPlayerColor();
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                hash ^= PlayerMinimax.taulaHash[i][j][getPos(i, j) + 1];
                valEstatic += (10 - Math.abs(i - getSize() / 2) + Math.abs(j - getSize() / 2)) * getPos(i, j);
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
    }

    @Override
    public void placeStone(Point point) {
        super.placeStone(point);
        hash ^= PlayerMinimax.taulaHash[point.x][point.y][getPos(point.x, point.y) + 1];
        valEstatic += (10 - Math.abs(point.x - getSize() / 2) + Math.abs(point.y - getSize() / 2)) * getPos(point);
        graphsUpdate(point, -getCurrentPlayerColor());
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
                        }
                    }
                    if (i == getSize() - 1) {
                        map.get(p).put(end, 0);
                    }
                    if (i == getSize() - 2 && j != 0 && getPos(p) == 1) {
                        if (getPos(i + 1, j) == 0 && getPos(i + 1, j - 1) == 0) {
                            map.get(p).put(end, 1);
                        }
                    }
                } else {
                    if (j == 0) {
                        map.putIfAbsent(ini, new HashMap<>());
                        map.get(ini).put(p, getPos(p) == 1 ? 1000 : getPos(p) == 0 ? 2 : 0);
                    }
                    if (j == 1 && i != getSize() - 1 && getPos(p) == -1) {
                        if (getPos(i, j-1) == 0 && getPos(i + 1, j - 1) == 0) {
                            map.get(ini).put(p, 1);
                        }
                    }
                    if (j == getSize() - 1) {
                        map.get(p).put(end, 0);
                    }
                    if (j == getSize() - 2 && i != 0 && getPos(p) == -1) {
                        if (getPos(i, j - 1) == 0 && getPos(i + 1, j - 1) == 0) {
                            map.get(p).put(end, 1);
                        }
                    }
                }
            }
        }
        return map;
    }

    public Map<Point, Integer> getNeighbors(Point p, int col) {
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
                    }
                }
            }
            i++;
        }

        return ngbs;
    }

    public void graphsUpdate(Point p, int col) {
        int[][] directions = {
            {1, 0}, // Derecha
            {1, -1}, // Abajo-derecha
            {0, -1}, // Abajo-izquierda
            {-1, 0}, // Izquierda
            {-1, 1}, // Arriba-izquierda
            {0, 1} // Arriba-derecha
        };

        for (int[] dir : directions) {
            int x = p.x + dir[0];
            int y = p.y + dir[1];
            if (x >= 0 && y >= 0 && x < getSize() && y < getSize()) {

                int val1 = 2;
                int val2 = 2;
                if (getPos(p) == col) {
                    val1 = 0;
                    val2 = 1000;
                } else if (getPos(p) == -col) {
                    val1 = 1000;
                    val2 = 0;
                }

                graf1.get(new Point(x, y)).put(p, val1);
                //System.out.println(p + ":" + col + "->" + x + "." + y + ";" + val1 + " " + graf1.get(new Point(x, y)).get(p));
                graf2.get(new Point(x, y)).put(p, val2);
            }
        }
        int i = p.x, j = p.y;
        boolean g1 = graf1.get(ini).containsKey(p);
        System.out.println(i+":"+g1+"."+myColor);
            if (i == 0) {
                if (g1) graf1.get(ini).put(p, getPos(p) == -myColor ? 1000 : getPos(p) == 0 ? 2 : 0);
                else graf2.get(ini).put(p, getPos(p) == myColor ? 1000 : getPos(p) == 0 ? 2 : 0);
            }
            if (i == 1 && j != getSize() - 1 && getPos(p) == (g1 ? myColor : -myColor)) {
                if (getPos(i - 1, j) == 0 && getPos(i - 1, j + 1) == 0) {
                    if (g1) graf1.get(ini).put(p, 1);
                    else graf2.get(ini).put(p, 1);
                }
            }
            if (i == getSize() - 1) {
                if (g1) graf1.get(p).put(end, 0);
                else graf2.get(p).put(end, 0);
            }
            if (i == getSize() - 2 && j != 0 && getPos(p) == (g1 ? myColor : -myColor)) {
                if (getPos(i + 1, j) == 0 && getPos(i + 1, j - 1) == 0) {
                    if (g1) graf1.get(p).put(end, 1);
                    else graf2.get(p).put(end, 1);
                }
            }
      
            if (j == 0) {
                if (g1) graf2.get(ini).put(p, getPos(p) == myColor ? 1000 : getPos(p) == 0 ? 2 : 0);
                else graf1.get(ini).put(p, getPos(p) == -myColor ? 1000 : getPos(p) == 0 ? 2 : 0);
            }
            if (j == 1 && i != getSize() - 1 && getPos(p) == (g1 ? myColor : -myColor)) {
                if (getPos(i, j-1) == 0 && getPos(i + 1, j - 1) == 0) {
                    System.out.println("aaa");
                    if (g1) graf2.get(ini).put(p, 1);
                    else graf1.get(ini).put(p, 1);
                }
            }
            if (j == getSize() - 1) {
                if (g1) graf2.get(p).put(end, 0);
                else graf2.get(p).put(end, 0);
            }
            if (j == getSize() - 2 && i != 0 && getPos(p) == (g1 ? myColor : -myColor)) {
                if (getPos(i, j - 1) == 0 && getPos(i + 1, j - 1) == 0) {
                    if (g1) graf2.get(p).put(end, 1);
                    graf1.get(p).put(end, 1);
                }
            }
        
    }
}
