package com.thxago.anticheat.player;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerData {
    
    private long lastAttack;
    private long lastMove;
    private long lastPlace;
    private long lastBreak;
    private long lastEat;
    
    private double lastX;
    private double lastY;
    private double lastZ;
    
    private int airTicks;
    private int groundTicks;
    private int violationLevel;
    
    private List<Long> clickTimes; // Lista de tempos de clique para detecção de AutoClick
    
    public PlayerData() {
        this.lastAttack = 0;
        this.lastMove = 0;
        this.lastPlace = 0;
        this.lastBreak = 0;
        this.lastEat = 0;
        this.violationLevel = 0;
        this.clickTimes = new ArrayList<>();
    }
} 