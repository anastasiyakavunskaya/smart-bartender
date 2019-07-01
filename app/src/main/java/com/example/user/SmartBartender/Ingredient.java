package com.example.user.SmartBartender;

class Ingredient {
    final int id;
    final int value;
    final int layer;
    final String name;

    public Ingredient(int value, String name, int id, int layer) {
        this.id = id;
        this.value = value;
        this.name = name;
        this.layer = layer;
    }
}