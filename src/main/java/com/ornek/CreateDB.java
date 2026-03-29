package com.ornek;
public class CreateDB {
    public static void main(String[] args) {
        DatabaseHelper.initializeDatabase();
        System.out.println("DB Olusturuldu!");
    }
}
