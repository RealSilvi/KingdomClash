package it.unibo.view.battle.panels.impl;

import it.unibo.view.battle.panels.api.ComandPanel;
import it.unibo.view.battle.panels.entities.impl.ButtonsPanelImpl;
import it.unibo.view.battle.panels.entities.impl.DrawPanel;
import it.unibo.view.battle.panels.entities.impl.LifePanelImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CommandPanelImpl implements ComandPanel {

    private final JPanel mainPanel;

    private final LifePanelImpl playerLivesPanel;
    private final LifePanelImpl botLivesPanel;
    private final ButtonsPanelImpl buttonsPanel;

    
    public CommandPanelImpl(final Dimension preferredDimension, final int numberOfLives) {
        this.mainPanel= new DrawPanel();
        this.botLivesPanel=new LifePanelImpl(numberOfLives);
        this.playerLivesPanel= new LifePanelImpl(numberOfLives);
        this.buttonsPanel= new ButtonsPanelImpl();

        this.mainPanel.setPreferredSize(preferredDimension);

        this.restart();
    }

    @Override
    public void restart() {
        this.mainPanel.setLayout(new BoxLayout(this.mainPanel,BoxLayout.Y_AXIS));
        this.mainPanel.add(botLivesPanel.getPanel());
        this.mainPanel.add(buttonsPanel.getPanel());
        this.mainPanel.add(playerLivesPanel.getPanel());
    }

    @Override
    public void disablePassButton(){
        this.buttonsPanel.disablePassButton();
    }

    @Override
    public void enablePassButton(){
        this.buttonsPanel.enablePassButton();
    }

    @Override
    public void disableSpinButton(){
        this.buttonsPanel.disableSpinButton();
    }

    @Override
    public void enableSpinButton(){
        this.buttonsPanel.enableSpinButton();
    }

    @Override
    public void decreasePlayerLive(){
        this.playerLivesPanel.decreaseLife();
    }
    
    @Override
    public void decreaseBotLive(){
        this.botLivesPanel.decreaseLife();
    }
    
    @Override
    public void setActionListenerPass(ActionListener actionListener){
        this.buttonsPanel.setActionListenerPass(actionListener);
    }
    
    @Override
    public void setActionListenerSpin(ActionListener actionListener){
        this.buttonsPanel.setActionListenerSpin(actionListener);
    }

    @Override
    public JPanel getPanel() {
        return this.mainPanel;
    }

}