package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.tiles.Tile;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;

import java.awt.*;
import java.util.Random;

import static it.polimi.ingsw.Const.*;
import static it.polimi.ingsw.Const.RESET_COLOR;

public class MainSceneController implements GUIController{
    private GUIManager guiManager;
    private static final String IMAGEPATH = "/fxml/graphics/item_tiles/";
    @FXML
    private GridPane boardGrid;
    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public GUIManager getGuiManager(){return this.guiManager;}
    public void printBoard(){
        String[][] board = guiManager.getModelView().getGame().getBoard().getBoardforGUI();
        Random random = new Random();
        for (int i = 0; i < MAXBOARDDIM; i++) {
            for (int j = 0; j < MAXBOARDDIM; j++) {
                if(!(board[i][j].equals("BLANK")||board[i][j].equals("UNAVAILABLE"))){
                    Tile tile = new Tile(i, j, this);
                    tile.setFill(
                            new ImagePattern(
                                    new Image(GUI.class.getResourceAsStream(IMAGEPATH
                                                                                +board[i][j]
                                                                                +random.nextInt(1,4)
                                                                                +".png"))));
                    boardGrid.add(tile, i, j);
                }

            }
        }
    }
}
