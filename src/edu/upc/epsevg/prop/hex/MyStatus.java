/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;

/**
 *
 * @author llucc
 */
public class MyStatus extends HexGameStatus {
    // crear una taula estatica amb size == hgs i per cada casella agafar un valor random per 0, 1, -1
    // recorrer la taula i per cada casella fer hash ^= taulaEstatica[i][j][z] (on < seria 0, 1, -1 o alguna cosa aixi)
    // i per cada place stone fer hash ^= taulaEstatica[placeStone.i][placeStone.j][placeStone.color]
    int hash = 0;
    
    public MyStatus(HexGameStatus hgs) {
        super(hgs);
        // calcul hash
    }
    
    public MyStatus(MyStatus hgs) {
        super(hgs);
        this.hash = hgs.hash;
    }
    
    @Override
    public void placeStone( Point point){
        super.placeStone(point);
        // recalcular hash
    }
    
}
