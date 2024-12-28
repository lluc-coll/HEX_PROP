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
    int hash = 0;
    int valEstatic = 0;
    Map<Point, List<Map.Entry<Point, Integer>>> graf1;
    Map<Point, List<Map.Entry<Point, Integer>>> graf2;
    Point ini = new Point(0, -1);
    Point end = new Point(-1, 0);

    public MyStatus(HexGameStatus hgs) {
        super(hgs);
        
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                hash ^= PlayerMinimax.taulaHash[i][j][getPos(i,j)+1];
                valEstatic += (10 - Math.abs(i - getSize()/2) + Math.abs(j - getSize()/2))*getPos(i, j);
            }
        }
        graf1 = getTableGraph(getCurrentPlayerColor());
        graf2 = getTableGraph(-getCurrentPlayerColor());
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
        hash ^= PlayerMinimax.taulaHash[point.x][point.y][getPos(point.x, point.y)+1];
        valEstatic += (10 - Math.abs(point.x - getSize()/2) + Math.abs(point.y - getSize()/2))*getPos(point);
        // recalcular grafs
    }

    public Map<Point, List<Map.Entry<Point, Integer>>> getTableGraph(int col) {
        Map<Point, List<Map.Entry<Point, Integer>>> map = new HashMap<>();
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                Point p = new Point(i, j);
                map.put(p, getNeighbors(p, col));
                if (col == 1) {
                    if (i == 0) {
                        map.putIfAbsent(ini, new ArrayList<>());
                        map.get(ini).add(new AbstractMap.SimpleEntry<>(p, getPos(p)==-1 ? 1000 : 0));
                    }
                    if (i == getSize() - 1) {
                        map.get(p).add(new AbstractMap.SimpleEntry<>(end, 0));
                    }
                } else {
                    if (j == 0) {
                        map.putIfAbsent(ini, new ArrayList<>());
                        map.get(ini).add(new AbstractMap.SimpleEntry<>(p, getPos(p)==1 ? 1000 : 0));
                    }
                    if (j == getSize() - 1) {
                        map.get(p).add(new AbstractMap.SimpleEntry<>(end, 0));
                    }
                }
            }
        }
        return map;
    }

    public List<Map.Entry<Point, Integer>> getNeighbors(Point p, int col) {
        List<Map.Entry<Point, Integer>> ngbs = new ArrayList<>();
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
                int val = 2;
                if (getPos(x, y) == col) {
                    val = 0;
                } else if (getPos(x, y) == -col) {
                    val = 1000;
                }
                ngbs.add(new AbstractMap.SimpleEntry<>(new Point(x, y), val));
            }
        }

        return ngbs;
    }
}
