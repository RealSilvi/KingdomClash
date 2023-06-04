package it.unibo.model.base.exceptions;

import it.unibo.view.battle.Troop;

public class InvalidTroopLevelException extends TroopException {
    public InvalidTroopLevelException(Troop troopType, int level) {
        //TODO: Ouptut max level to string when ready
        super("Level "+level+" is invalid for troop: "
        +troopType.name()+" Max level is: TO BE ADDED");
    }
    public InvalidTroopLevelException(String msg) {
        super(msg);
    }
    public InvalidTroopLevelException(String msg, Throwable trace) {
        super(msg, trace);
    }
}