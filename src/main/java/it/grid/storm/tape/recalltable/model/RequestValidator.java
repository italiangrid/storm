package it.grid.storm.tape.recalltable.model;

@FunctionalInterface
public interface RequestValidator {

    public boolean validate();
}
