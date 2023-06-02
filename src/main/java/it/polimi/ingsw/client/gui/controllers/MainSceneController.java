package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.tiles.Tile;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.control.Label;
import javafx.scene.Cursor;

import java.util.ArrayList;

import static it.polimi.ingsw.Const.*;

public class MainSceneController implements GUIController{
    private GUIManager guiManager;
    private static final String IMAGEPATH = "/fxml/graphics/item_tiles/";
    private static final int MAX_CHOOSABLE_TILES = 3;
    private Tile[] chosenTiles = new Tile[MAX_CHOOSABLE_TILES];
    private  int lastChosenTile = 0;

    @FXML
    private GridPane boardGrid;
    @FXML
    private GridPane chosenTilesGrid;
    @FXML
    private Label turn;
    @FXML
    private Label username;
    @FXML ImageView chair;

    @FXML
    Button confirmTilesBtn;
    @FXML Button cancelTilesChoiseBtn;
    @FXML Button confirmTilesChoiseBtn;
    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public GUIManager getGuiManager(){return this.guiManager;}
    public void printBoard(){
        String[][] board = guiManager.getModelView().getGame().getBoard().getBoardforGUI();
        //Random random = new Random();
        int imageChoise;
        for (int i = 0; i < MAXBOARDDIM; i++) {
            for (int j = 0; j < MAXBOARDDIM; j++) {
                if(!(board[i][j].equals("BLANK")||board[i][j].equals("UNAVAILABLE"))){
                    Tile tile = new Tile(i,j, this);
                    //imageChoise = random.nextInt(1,4);
                    imageChoise = guiManager.getModelView().getGame().getNumOfPlayers()-1;
                    tile.setFill(
                            new ImagePattern(
                                    new Image(GUI.class.getResourceAsStream(IMAGEPATH
                                                                                +board[i][j]
                                                                                +imageChoise
                                                                                +".png"))));
                    boardGrid.add(tile,j,i);
                    tile.setOnMouseClicked(mouseEvent -> chooseTile(tile));
                }
            }
        }
    }

    private void chooseTile(Tile chosenTile){
        /*if(chosenTiles[lastChosenTile]!=null)
            boardGrid.add(chosenTiles[lastChosenTile],chosenTiles[lastChosenTile].getOrdinate(),chosenTiles[lastChosenTile].getAbscissa());
        chosenTiles[lastChosenTile]=chosenTile;
        chosenTilesGrid.add(chosenTile, lastChosenTile, 0);*/

            chosenTiles[lastChosenTile]=chosenTile;
            chosenTilesGrid.add(chosenTile, lastChosenTile, 0);
            lastChosenTile++;
            boardGrid.getChildren().remove(chosenTile);
            confirmTilesChoiseBtn.setVisible(true);
            cancelTilesChoiseBtn.setVisible(true);
        if(lastChosenTile==3)
            boardGrid.setDisable(true);


        //lastChosenTile = (lastChosenTile + 1) == MAX_CHOOSABLE_TILES ? 0: lastChosenTile +1;



    }

    public void cancelTilesChoise(){
        Tile tileToRemove;
        for (int i=0;i<MAX_CHOOSABLE_TILES; i++){
            if(chosenTiles[i]!=null){
                tileToRemove = chosenTiles[i];
                boardGrid.add(tileToRemove, tileToRemove.getOrdinate(), tileToRemove.getAbscissa());
                chosenTilesGrid.getChildren().remove(tileToRemove);
                chosenTiles[i] = null;
                lastChosenTile--;
            }

        }
        boardGrid.setDisable(false);
        confirmTilesChoiseBtn.setVisible(false);
        cancelTilesChoiseBtn.setVisible(false);
    }


    public void confirmTilesChoise(){
        if(chosenTilesGrid.getChildren()!=null){
            String numOfTilesToPick;
            switch (chosenTilesGrid.getChildren().size()){
                case 1: numOfTilesToPick = "ONE"; break;
                case 2: numOfTilesToPick = "TWO"; break;
                case 3: numOfTilesToPick = "THREE"; break;
                default: numOfTilesToPick = "ERROR";
            }
            StringBuilder cmd = new StringBuilder("PICKTILES ");
            cmd.append(numOfTilesToPick);
            for (Node n:chosenTilesGrid.getChildren()) {
                cmd.append(" " + ((Tile)n).getAbscissa() + " " + ((Tile)n).getOrdinate());
            }
            System.out.println(chosenTilesGrid.getChildren().size());
            System.out.println(cmd.toString());
            this.getGuiManager().firePC("action", null, cmd.toString());
        }

    }

    public void allowPickTiles(){
        boardGrid.setDisable(true);
    }

    public void disablePickTiles(){
        boardGrid.setDisable(true);
    }
    public void updateTurn(String turn){
        this.turn.setText(turn);
    }

    public void showChair(){
        chair.setVisible(true);
    }

    public void hideChair(){
        chair.setVisible(false);
    }

    public void setUsername(String username){
        this.username.setText(username);
    }
}
