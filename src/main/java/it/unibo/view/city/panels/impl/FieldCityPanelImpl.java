package it.unibo.view.city.panels.impl;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import it.unibo.view.battle.config.PathIconsConfiguration;
import it.unibo.view.battle.panels.entities.DrawPanel;
import it.unibo.view.city.panels.api.FieldCityPanel;
import it.unibo.view.utilities.ImageIconsSupplier;

public class FieldCityPanelImpl implements FieldCityPanel {

    private final JPanel mainpanel;
    private List<List<JButton>> buttonmap;
    private CityConfiguration gameConfiguration;
    private PathIconsConfiguration pathIconsConfiguration;
    

   

    public FieldCityPanelImpl(CityConfiguration gameConfiguration, PathIconsConfiguration pathIconsConfiguration){

        //GraphicUtils.resizeImage(new ImageIcon(),JButton.WIDTH,JButton.HEIGHT);
        this.gameConfiguration=gameConfiguration;
        this.pathIconsConfiguration=pathIconsConfiguration;
        this.mainpanel= new DrawPanel(ImageIconsSupplier.loadImage(pathIconsConfiguration.getBackgroundCity()),
            new Dimension(gameConfiguration.getWidth(), gameConfiguration.getHeight()));
        this.mainpanel.setLayout(new GridLayout(gameConfiguration.getWidth(),gameConfiguration.getHeight()));
        buttonmap= new ArrayList<>(gameConfiguration.getWidth()* gameConfiguration.getHeight());
        this.setfield(gameConfiguration.getWidth(), gameConfiguration.getHeight());
        
       

    }

    
    private void setfield(int width, int height) {
       
        
        for (int i = 0; i < width; i++) {
            List<JButton> cols= new ArrayList<>();
            for (int j = 0; j < height; j++) {
                /*ognuna di esse ha una determinata posizione -aggiungere un listener per ciascun bottone con la quale possa piazzare la struttura 
                */
                
                final JButton structure= new JButton();
                cols.add(j, structure);
                /*il campo viene creato per caso magari aggiungo un metodo che generi immagini e le piazzi per caso */
                structure.setOpaque(false);
                structure.setContentAreaFilled(false);
                this.mainpanel.add(structure);
                structure.setBorder(null);
            }
            buttonmap.add(i, cols);
    }
        
        }

    public JPanel getPanel(){
        return this.mainpanel;
    }


}