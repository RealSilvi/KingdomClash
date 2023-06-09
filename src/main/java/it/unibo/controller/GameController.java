package it.unibo.controller;


import it.unibo.controller.base.BaseControllerImpl;
import it.unibo.controller.battle.BattleControllerImpl;
import it.unibo.model.GameModel;
import it.unibo.model.data.GameConfiguration;
import it.unibo.view.GameGui;

public class GameController {

    private GameModel gameModel;
    private GameGui gameGui;

    private Controller baseController;
    private Controller battleController;

    public GameController() {
        this.gameModel = new GameModel();

        this.battleController = new BattleControllerImpl(gameModel.getGameData());
        this.baseController = new BaseControllerImpl(gameModel.getGameData());

        this.gameGui = new GameGui(battleController.getGuiPanel(),baseController.getGuiPanel(), this.gameModel.getGameData().getGameConfiguration());

    }

    public static void main(final String... args) {
        new GameController();

    }

}