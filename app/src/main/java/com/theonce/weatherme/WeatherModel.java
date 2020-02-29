package com.theonce.weatherme;

public class WeatherModel {

    private String Cuaca,Kota,deskripsi,negara;
    private double suhu,shuhuMin,suhuMax,angin,awan,kelembapan;
    private long sunrise,sunset;


    public long getSunrise() {
        return sunrise;
    }

    public double getAwan() {
        return awan;
    }

    public double getKelembapan() {
        return kelembapan;
    }

    public void setKelembapan(double kelembapan) {
        this.kelembapan = kelembapan;
    }

    public void setAwan(double awan) {
        this.awan = awan;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public double getShuhuMin() {
        return shuhuMin;
    }

    public void setShuhuMin(double shuhuMin) {
        this.shuhuMin = shuhuMin;
    }

    public double getSuhuMax() {
        return suhuMax;
    }

    public void setSuhuMax(double suhuMax) {
        this.suhuMax = suhuMax;
    }

    public double getAngin() {
        return angin;
    }

    public void setAngin(double angin) {
        this.angin = angin;
    }

    public String getCuaca() {
        return Cuaca;
    }

    public double getSuhu() {
        return suhu;
    }

    public void setSuhu(double suhu) {
        this.suhu = suhu;
    }

    public void setCuaca(String cuaca) {
        Cuaca = cuaca;
    }

    public String getKota() {
        return Kota;
    }

    public void setKota(String kota) {
        Kota = kota;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getNegara() {
        return negara;
    }

    public void setNegara(String negara) {
        this.negara = negara;
    }
}
