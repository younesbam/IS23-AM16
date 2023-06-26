package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GUI;
import it.polimi.ingsw.client.gui.tiles.Tile;
import it.polimi.ingsw.common.exceptions.NotEmptyColumnException;
import it.polimi.ingsw.model.BookShelf;
import it.polimi.ingsw.model.Cell;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.control.Label;

import static it.polimi.ingsw.Const.*;

public class MainSceneController implements GUIController{
    private GUIManager guiManager;
    private static final String IMAGEPATH = "/graphics/item_tiles/";
    private static final int MAX_CHOOSABLE_TILES = 3;
    private Tile[] chosenTiles = new Tile[MAX_CHOOSABLE_TILES];
    private static final String GOALS = "goalCardScene.fxml";
    private  int numOfChosenTiles = 0;

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
    @FXML
    private Button confirmPlacementBtn;
    @FXML
    private Button cancelPlacementBtn;
    @FXML
    private Button goalCardsBtn;

    @FXML
    private Label points;
    @FXML
    private ImageView endGameToken;

    private final double bookShelfStartX = 560.0;
    private final double booShelfEndX = 900.0;
    private final double bookShelfStartY = 120.0;
    private final double booShelfEndY = 550;

    private final double dropSpace = 70.0;
    private int imageChoise;
    private BookShelf bookShelf;
    private int columnChosen;
    private int numOfTilesPlaced;
    private String[][] board;
    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    public GUIManager getGuiManager(){return this.guiManager;}
    public void printBoard(){
        boardGrid.getChildren().clear();
        imageChoise = guiManager.getModelView().getGame().getNumOfPlayers()-1;
        board = guiManager.getModelView().getGame().getBoard().getBoardforGUI();
        //Random random = new Random();
        for (int i = 0; i < MAXBOARDDIM; i++) {
            for (int j = 0; j < MAXBOARDDIM; j++) {
                if(!(board[i][j].equals("BLANK")||board[i][j].equals("UNAVAILABLE"))){
                    Tile tile = new Tile(i,j);
                    //imageChoise = (i*j)%3+1;
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
        //bookShelf = guiManager.getModelView().getGame().getCurrentPlayer().getBookShelf();
        bookShelfGrid.getChildren().clear();
        bookShelf = guiManager.getModelView().getGame().getPlayerByID(guiManager.getPlayerID()).getBookShelf();
        imageChoise = guiManager.getModelView().getGame().getNumOfPlayers()-1;
        Cell[][] grid = bookShelf.getGrid();
        for (int i = 0; i < MAXBOOKSHELFROW; i++) {
            for (int j = 0; j < MAXBOOKSHELFCOL; j++) {
                if(!(grid[i][j].getTile().name().equals("BLANK")||grid[i][j].getTile().name().equals("UNAVAILABLE"))){
                    Tile tile = new Tile(i,j);
                    //imageChoise = (i*j)%3+1;
                    tile.setFill(
                            new ImagePattern(
                                    new Image(GUI.class.getResourceAsStream(IMAGEPATH
                                            +grid[i][j].getTile().name()
                                            +imageChoise
                                            +".png"))));
                    bookShelfGrid.add(tile,j,i);
                }
            }
        }
    }

    private void chooseTile(Tile chosenTile){
        chosenTiles[numOfChosenTiles]=chosenTile;
        chosenTilesGrid.add(chosenTile, numOfChosenTiles, 0);
        numOfChosenTiles++;
        boardGrid.getChildren().remove(chosenTile);
        confirmTilesChoiseBtn.setVisible(true);
        cancelTilesChoiseBtn.setVisible(true);
        if(numOfChosenTiles==3)
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
                numOfChosenTiles--;
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
            this.getGuiManager().firePC("action", null, cmd.toString());
        }
        disablePickTiles();

    }
    public void allowPlaceTiles(){
        columnChosen = -1;
        numOfTilesPlaced = 0;
        int i = 0;
        for (Node n:highlightPane.getChildren()) {
            try{
                bookShelf.checkColumn(i, numOfChosenTiles);
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
        confirmPlacementBtn.setVisible(false);
        cancelPlacementBtn.setVisible(false);
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
        }
        else{ /* CASE TILE DROPPED INSIDE THE BOOKSHELF*/
            int tryColumn = (int) ((mouseEvent.getSceneX() - bookShelfStartX) / dropSpace);
            if(columnChosen==-1 && highlightPane.getChildren().get(tryColumn).isVisible()) {
                columnChosen = tryColumn;
                for (Node n: highlightPane.getChildren())
                    n.setVisible(false);
                highlightPane.getChildren().get(columnChosen).setVisible(true);
            }
            if(columnChosen!=-1 && highlightPane.getChildren().get(tryColumn).isVisible()){
                chosenTilesGrid.getChildren().remove(tile);
                addTileToBookShelfGrid(tile, columnChosen);
                //bookShelfGrid.add(tile, columnChosen, MAXBOOKSHELFROW-1-numOfTilesPlaced);
                numOfTilesPlaced++;
            }
        }
        tile.setTranslateX(0);
        tile.setTranslateY(0);
    }
    private void addTileToBookShelfGrid(Tile tile, int columnChosen){
        for (int i = MAXBOOKSHELFROW-1; i >= 0; i--) {
            if(bookShelf.getGrid()[i][columnChosen].getTile().name().equals("BLANK")
            || bookShelf.getGrid()[i][columnChosen].getTile().name().equals("UNAVAILABLE")){
                bookShelfGrid.add(tile, columnChosen, i - numOfTilesPlaced);
                /* ORDER chosenTiles based on column order */
                for (int j = 0; j < numOfChosenTiles; j++) {
                    if(chosenTiles[j].equals(tile))
                        chosenTiles[j] = chosenTiles[numOfTilesPlaced];
                }
                chosenTiles[numOfTilesPlaced] = tile;
                confirmPlacementBtn.setVisible(true);
                cancelPlacementBtn.setVisible(true);
                break;
            }

        }
    }
    public void confirmPlacement(){
        if(numOfTilesPlaced == numOfChosenTiles){
            StringBuilder cmd = new StringBuilder("PLACETILES ");
            for (int i = 0; i < numOfChosenTiles; i++) {
                cmd.append(board[chosenTiles[i].getAbscissa()][chosenTiles[i].getOrdinate()]+" ");
                chosenTiles[i]=null;
            }
            cmd.append(columnChosen);
            guiManager.firePC("action", null, cmd.toString());
            highlightPane.getChildren().get(columnChosen).setVisible(false);
            confirmPlacementBtn.setVisible(false);
            cancelPlacementBtn.setVisible(false);
            chosenTilesGrid.getChildren().clear();
            numOfChosenTiles = 0;
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Not allowed");
            alert.setContentText("You have to place all chosen tiles in the bookshelf!");
            alert.showAndWait();
        }

    }

    public void cancelPlacement(){
        chosenTilesGrid.getChildren().clear();
        int i;
        for (i = 0; i < numOfTilesPlaced; i++) {
            bookShelfGrid.getChildren().remove(chosenTiles[i]);
            chosenTilesGrid.add(chosenTiles[i],i,0);
        }
        for(;i<numOfChosenTiles;i++){
            chosenTilesGrid.add(chosenTiles[i],i,0);
        }
        allowPlaceTiles();
    }

    public void printGoalCards(){
        guiManager.popupStage(GOALS,"Goal Cards");
        guiManager.printGoalCards();
    }
    public void showEndGameToken(){
        endGameToken.setVisible(true);
    }
    public void printBookShelfs(){
        guiManager.printBookShelfs();
    }
    public void updatePoints(String points){
        this.points.setText(points);
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
