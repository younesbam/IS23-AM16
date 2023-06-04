package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.tiles.Tile;
import it.polimi.ingsw.common.exceptions.NotEmptyColumnException;
import it.polimi.ingsw.model.BookShelf;
import it.polimi.ingsw.model.Cell;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
    private  int numOfChosenTile = 0;

    @FXML
    private GridPane boardGrid;
    @FXML
    private GridPane chosenTilesGrid;
    @FXML
    private Label turn;
    @FXML
    private Label username;
    @FXML
    private ImageView chair;
    @FXML
    private Button cancelTilesChoiseBtn;
    @FXML
    private Button confirmTilesChoiseBtn;
    @FXML
    private GridPane bookShelfGrid;
    @FXML
    private AnchorPane highlightPane;

    private final double bookShelfStartX = 560.0;
    private final double booShelfEndX = 900.0;
    private final double bookShelfStartY = 120.0;
    private final double booShelfEndY = 550;

    private final double dropSpace = 70.0;
    private int imageChoise;
    private BookShelf bookShelf;
    private int columnChosen;
    private int numOfTilesPlaced;
    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public GUIManager getGuiManager(){return this.guiManager;}
    public void printBoard(){
        imageChoise = guiManager.getModelView().getGame().getNumOfPlayers()-1;
        String[][] board = guiManager.getModelView().getGame().getBoard().getBoardforGUI();
        //Random random = new Random();
        for (int i = 0; i < MAXBOARDDIM; i++) {
            for (int j = 0; j < MAXBOARDDIM; j++) {
                if(!(board[i][j].equals("BLANK")||board[i][j].equals("UNAVAILABLE"))){
                    Tile tile = new Tile(i,j, this);
                    //imageChoise = random.nextInt(1,4);
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

    public void printBookShelf(){
        bookShelf = guiManager.getModelView().getGame().getCurrentPlayer().getBookShelf();
        Cell[][] grid = bookShelf.getGrid();
        for (int i = 0; i < MAXBOOKSHELFROW; i++) {
            for (int j = 0; j < MAXBOOKSHELFCOL; j++) {
                if(!(grid[i][j].getTile().name().equals("BLANK")||grid[i][j].getTile().name().equals("UNAVAILABLE"))){
                    Tile tile = new Tile(i,j, this);
                    tile.setFill(
                            new ImagePattern(
                                    new Image(GUI.class.getResourceAsStream(IMAGEPATH
                                            +grid[i][j]
                                            +imageChoise
                                            +".png"))));
                    bookShelfGrid.add(tile,j,i);
                }
            }
        }
    }

    private void chooseTile(Tile chosenTile){
        chosenTiles[numOfChosenTile]=chosenTile;
        chosenTilesGrid.add(chosenTile, numOfChosenTile, 0);
        numOfChosenTile++;
        boardGrid.getChildren().remove(chosenTile);
        confirmTilesChoiseBtn.setVisible(true);
        cancelTilesChoiseBtn.setVisible(true);
        if(numOfChosenTile==3)
            disablePickTiles();
    }

    public void cancelTilesChoise(){
        restoreChosenTilesToBoard();
        allowPickTiles();
        confirmTilesChoiseBtn.setVisible(false);
        cancelTilesChoiseBtn.setVisible(false);
    }

    private void restoreChosenTilesToBoard(){
        Tile tileToRemove;
        for (int i=0;i<MAX_CHOOSABLE_TILES; i++){
            if(chosenTiles[i]!=null){
                tileToRemove = chosenTiles[i];
                boardGrid.add(tileToRemove, tileToRemove.getOrdinate(), tileToRemove.getAbscissa());
                chosenTilesGrid.getChildren().remove(tileToRemove);
                chosenTiles[i] = null;
                numOfChosenTile--;
            }
        }
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
    public void allowPlaceTiles(){
        columnChosen = -1;
        numOfTilesPlaced = 0;
        int i = 0;
        for (Node n:highlightPane.getChildren()) {
            try{
                bookShelf.checkColumn(i, numOfChosenTile);
                n.setVisible(true);
            }
            catch (NotEmptyColumnException e){
                n.setVisible(false);
            }
            finally {
                i++;
            }
        }
        chosenTilesGrid.setDisable(false);
        cancelTilesChoiseBtn.setVisible(false);
        confirmTilesChoiseBtn.setVisible(false);
        for (Node n: chosenTilesGrid.getChildren()) {
            n.setOnMouseClicked(mouseEvent -> {});
            n.setOnMousePressed(mouseEvent -> tilePressed(mouseEvent, (Tile)n));
            n.setOnMouseDragged(mouseEvent -> tileDragged(mouseEvent, (Tile)n));
            n.setOnMouseReleased(mouseEvent -> tileReleased(mouseEvent,(Tile)n));
        }

    }


    private double mouseAnchorX;
    private double mouseAnchorY;
    private void tilePressed(MouseEvent mouseEvent, Tile tile){
        //bookShelfPane.getChildren().add(tile);
        //chosenTilesGrid.getChildren().remove(tile);
        mouseAnchorX = mouseEvent.getSceneX() - tile.getTranslateX();
        mouseAnchorY = mouseEvent.getSceneY() - tile.getTranslateY();
        System.out.println(" mouse press in X: " + mouseEvent.getSceneX());
        System.out.println(" mouse press in Y: " + mouseEvent.getSceneY());
    }
    private void tileDragged(MouseEvent mouseEvent, Tile tile){
        tile.setTranslateX(mouseEvent.getSceneX()-mouseAnchorX);
        tile.setTranslateY(mouseEvent.getSceneY()-mouseAnchorY);
        //System.out.println("tile: " + tile.getTranslateX() + ", " + tile.getTranslateY());
    }
    private void tileReleased(MouseEvent mouseEvent, Tile tile){
        /* CASE TILE DROPPED OUTSIDE THE BOOKSHELF*/
        if(mouseEvent.getSceneX()<bookShelfStartX || mouseEvent.getSceneX()>booShelfEndX
            || mouseEvent.getSceneY()<bookShelfStartY || mouseEvent.getSceneY()>booShelfEndY){
            System.out.println("tile dropped outside bookshelf");
        }
        else{ /* CASE TILE DROPPED INSIDE THE BOOKSHELF*/
            if(columnChosen==-1) {
                columnChosen = (int) ((mouseEvent.getSceneX() - bookShelfStartX) / dropSpace);
                for (Node n: highlightPane.getChildren())
                    n.setVisible(false);
                highlightPane.getChildren().get(columnChosen).setVisible(true);
            }
            chosenTilesGrid.getChildren().remove(tile);
            addTileToBookShelfGrid(tile, columnChosen);
            //bookShelfGrid.add(tile, columnChosen, MAXBOOKSHELFROW-1-numOfTilesPlaced);
            numOfTilesPlaced++;
            System.out.println("tile dropped");
            System.out.println(mouseEvent.getSceneY());

        }
        tile.setTranslateX(0);
        tile.setTranslateY(0);
    }
    private void addTileToBookShelfGrid(Tile tile, int columnChosen){
        for (int i = MAXBOOKSHELFROW-1; i >= 0; i--) {
            if(bookShelf.getGrid()[i][columnChosen].getTile().name().equals("BLANK")
            || bookShelf.getGrid()[i][columnChosen].getTile().name().equals("UNAVAILABLE")){
                bookShelfGrid.add(tile, columnChosen, i + numOfTilesPlaced);
                break;
            }

        }
        numOfTilesPlaced++;
    }
    public void allowPickTiles(){
        boardGrid.setDisable(false);
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
