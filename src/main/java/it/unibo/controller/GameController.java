package it.unibo.controller;


import it.unibo.controller.base.BaseControllerImpl;
import it.unibo.controller.battle.BattleControllerImpl;
import it.unibo.model.GameModel;
import it.unibo.view.GameGui;

import java.awt.event.ActionListener;

public class GameController {

    private static final int START_ACTIVE_LEVEL = 1;
    private static final int START_BEATEN_LEVEL = 0;

    GameModel gameModel;
    GameGui gameGui;

    Controller battleController;
    Controller baseController;

    ActionListener toMainPanel;

    public GameController() {

        this.gameModel = new GameModel();

        this.battleController = new BattleControllerImpl(gameModel.getGameData());
        this.baseController = new BaseControllerImpl(gameModel.getGameData());

        this.gameGui = new GameGui(battleController.getGuiPanel(), baseController.getGuiPanel(), gameModel.getGameData().getGameConfiguration());

        this.toMainPanel = gameGui.getActionListenerMap();
        battleController.setReturnActionListener(toMainPanel);
        baseController.setReturnActionListener(toMainPanel);

        this.setActionListenerNewGame();
        this.setActionListenerBattle();
        this.setActionListenerMap();
        this.setActionListenerCity();

        gameGui.setBeatenLevels(START_BEATEN_LEVEL);
        gameGui.setActivateBattle(START_ACTIVE_LEVEL);
    }

    private void setActionListenerNewGame() {
        ActionListener actionListener = e -> this.gameGui.showCity();
        this.gameGui.setActionListenerNewGame(actionListener);
    }

    private void setActionListenerCity() {
        this.gameGui.setActionListenerCity(e -> this.gameGui.showCity());
    }

    private void setActionListenerMap() {
        this.gameGui.setActionListenerMap(e -> this.gameGui.showMap());
    }

    private void setActionListenerBattle() {
        this.gameGui.setActionListenerBattle(e -> this.gameGui.showBattle());
    }

}