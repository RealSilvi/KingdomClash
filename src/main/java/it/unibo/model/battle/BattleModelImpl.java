package it.unibo.model.battle;

import it.unibo.controller.battle.BattleController;
import it.unibo.controller.battle.BattleControllerImpl;
import it.unibo.controller.battle.Event;
import it.unibo.model.data.FightData;
import it.unibo.model.data.GameData;
import it.unibo.view.battle.Troop;

import java.util.*;

public class BattleModelImpl implements BattleModel{

    public static final int FIRST_TROOP = 0;
    private Optional<FightData> fightData;
    private BattleController battleController;

    int counted_round = 0;
    int botLife = 10;
    int playerLife = 10;


    public BattleModelImpl(GameData gameData){
        if(gameData.getFightData().isPresent()){
            this.fightData = gameData.getFightData();
        }
        this.battleController = new BattleControllerImpl(gameData);
    }

    @Override
    public void BattlePass() {

        fightData.get().getPlayerData().setClickedToChosen();

        //disablePassButton()
        //disablePlayerSlots()
        //spinBotFreeSlot()

        if(fightData.get().getPlayerData().getSelected().size() > 0) {
            fightData.get().getPlayerData().getSelected().forEach(x -> {

                if (!fightData.get().getBotData().isMatch(x)) {
                    if (fightData.get().getBotData().getNotSelected().contains(Troop.getNullable(x))) {
                        fightData.get().getBotData().AddBotTroop(fightData.get().getBotData().getKeyFromTroop(Troop.getNullable(x)));
                    } else {
                        if (fightData.get().getBotData().getSelected().size() < FightData.BOT_TROOPS) {
                            fightData.get().getBotData().AddBotTroop(fightData.get().getBotData().selectRandomTroop());
                        }
                    }
                }
            });
        }else{
            if (fightData.get().getBotData().getSelected().size() < FightData.BOT_TROOPS) {
                fightData.get().getBotData().AddBotTroop(fightData.get().getBotData().selectRandomTroop());
            }
        }

        counted_round++;
        if(counted_round >= 3){
            fightData.get().getBotData().setAllChosen();
            fightData.get().getPlayerData().setAllChosen();
            counted_round = 0;
            BattleCombat();
        }

        this.battleController.notify(Event.PASS);

    }

    @Override
    public void BattleSpin() {

        fightData.get().getPlayerData().changeNotSelectedTroop();

        this.battleController.notify(Event.SPIN);

    }

    @Override
    public void BattleCombat(){

        List<Optional<Troop>> playerField = fightData.get().getPlayerData().getOrderedField(fightData.get().getBotData());
        List<Optional<Troop>> botField = fightData.get().getBotData().getOrderedField(fightData.get().getPlayerData());

        playerField.forEach(x -> {

                if(botField.get(FIRST_TROOP).isPresent() && x.isPresent()){
                    if(x.get().getLevel() > botField.get(FIRST_TROOP).get().getLevel()){
                        if(!x.get().isDefense()){
                            if(botLife == 1){
                                botLife--;
                                //TODO player win
                            }else{
                                botLife--;
                            }
                        }
                    }else if(x.get().getLevel() < botField.get(FIRST_TROOP).get().getLevel()){
                        if(x.get().isDefense()){
                            if(playerLife == 1){
                                playerLife--;
                                //TODO bot win
                            }else{
                                playerLife--;
                            }
                        }
                    }
                }else if(botField.get(FIRST_TROOP).isEmpty() && x.isPresent() && (!x.get().isDefense())){
                    if(botLife == 1){
                        botLife--;
                        //TODO player win
                    }else{
                        botLife--;
                    }
                }else if(x.isEmpty() && botField.get(FIRST_TROOP).isPresent() && (!botField.get(FIRST_TROOP).get().isDefense())){
                    if(playerLife == 1){
                        playerLife--;
                        //TODO bot win
                    }else{
                        playerLife--;
                    }
                }

                botField.remove(FIRST_TROOP);

        });

        this.battleController.notify(Event.COMBAT);

    }



}