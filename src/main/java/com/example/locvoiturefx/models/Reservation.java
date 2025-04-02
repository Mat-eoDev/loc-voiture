package com.example.locvoiturefx.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Reservation {
    private int id;
    private String clientName;
    private String immatriculation; // Utilisez "immatriculation" pour la plaque
    private String startDate;       // Date de début au format "yyyy-MM-dd"
    private String endDate;         // Date de fin au format "yyyy-MM-dd"

    public Reservation(int id, String clientName, String immatriculation, String startDate, String endDate) {
        this.id = id;
        this.clientName = clientName;
        this.immatriculation = immatriculation;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters et setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public String getImmatriculation() {
        return immatriculation;
    }
    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    // Méthode calculant le nombre de jours restants entre aujourd'hui et la date de fin
    public long getDaysRemaining() {
        try {
            LocalDate end = LocalDate.parse(endDate);
            LocalDate now = LocalDate.now();
            return ChronoUnit.DAYS.between(now, end);
        } catch (Exception e) {
            return 0;
        }
    }
}
