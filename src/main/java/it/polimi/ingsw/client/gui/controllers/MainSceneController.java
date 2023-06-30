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

/**
 * The MainSceneController class is responsible for controlling the main scene of the application.
 * It implements the GUIController interface.
 *
 * @author Younes Bamhaoud
 */
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
    private Label points;
    @FXML
    private ImageView endGameToken;
    @FXML
    private ImageView userIcon;
    @FXML
    private GridPane tokenGrid;

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
    private double mouseAnchorX;
    private double mouseAnchorY;

    /**
     * Sets the GUI manager for the controller.
     *
     * @param guiManager The GUI manager to be set.
     */
    @Override
    public void setGuiManger(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    /**
     * Retrieves the GUI manager associated with the controller.
     *
     * @return The GUI manager.
     */
    public GUIManager getGuiManager(){return this.guiManager;}

    /**
     * Prints the game board on the GUI.
     */
    public void printBoard(){
        boardGrid.getChildren().clear();
        imageChoise = guiManager.getModelView().getGame().getNumOfPlayers()-1;
        board = guiManager.getModelView().getGame().getBoard().getBoardforGUI();
        for (int i = 0; i < MAXBOARDDIM; i++) {
            for (int j = 0; j < MAXBOARDDIM; j++) {
                if(!(board[i][j].equals("BLANK")||board[i][j].equals("UNAVAILABLE"))){
                    Tile tile = new Tile(i,j);
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


    /**
     * Prints the bookshelf on the GUI.
     */
    public void printBookShelf(){
        bookShelfGrid.getChildren().clear();
        bookShelf = guiManager.getModelView().getGame().getPlayerByID(guiManager.getPlayerID()).getBookShelf();
        imageChoise = guiManager.getModelView().getGame().getNumOfPlayers()-1;
        Cell[][] grid = bookShelf.getGrid();
        for (int i = 0; i < MAXBOOKSHELFROW; i++) {
            for (int j = 0; j < MAXBOOKSHELFCOL; j++) {
                if(!(grid[i][j].getTile().name().equals("BLANK")||grid[i][j].getTile().name().equals("UNAVAILABLE"))){
                    Tile tile = new Tile(i,j);
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

    /**
     * Chooses a tile from the board.
     *
     * @param chosenTile The tile chosen by the player.
     */
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

    /**
     * Cancels the tile choice and restores the chosen tiles to the board.
     */
    public void cancelTilesChoise(){
        restoreChosenTilesToBoard();
        allowPickTiles();
        confirmTilesChoiseBtn.setVisible(false);
        cancelTilesChoiseBtn.setVisible(false);
    }

    /**
     * Restores the chosen tiles to the board from the chosen tiles grid.
     */
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

    /**
     * Confirms the tile choice and sends the chosen tiles to the server.
     */
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

    /**
     * Allows the player to place tiles on the bookshelf.
     */
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

    /**
     * Handles the mouse press event on a tile.
     *
     * @param mouseEvent The mouse event.
     * @param tile The tile being pressed.
     */
    private void tilePressed(MouseEvent mouseEvent, Tile tile){
        mouseAnchorX = mouseEvent.getSceneX() - tile.getTranslateX();
        mouseAnchorY = mouseEvent.getSceneY() - tile.getTranslateY();
    }

    /**
     * Handles the mouse drag event on a tile.
     *
     * @param mouseEvent The mouse event.
     * @param tile The tile being dragged.
     */
    private void tileDragged(MouseEvent mouseEvent, Tile tile){
        tile.setTranslateX(mouseEvent.getSceneX()-mouseAnchorX);
        tile.setTranslateY(mouseEvent.getSceneY()-mouseAnchorY);
        //System.out.println("tile: " + tile.getTranslateX() + ", " + tile.getTranslateY());
    }

    /**
     * Handles the mouse release event on a tile.
     *
     * @param mouseEvent The mouse event.
     * @param tile The tile being released.
     */
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

    /**
     * Adds a tile to the bookshelf grid.
     *
     * @param tile The tile to add.
     * @param columnChosen The column chosen for placement.
     */
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

    /**
     * Confirms the tile placement and sends the placement information to the server.
     */
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

    /**
     * Cancels the tile placement and restores the tiles to the chosen tiles grid.
     */
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

    /**
     * Sets the image of a token in the token grid.
     *
     * @param tokenID The ID of the token.
     * @param path The path to the image file.
     */
    public void setTokens(int tokenID, String path){
        ((ImageView)tokenGrid.getChildren().get(tokenID)).setImage(new Image(GUI.class.getResourceAsStream(path)));
        tokenGrid.getChildren().get(tokenID).setVisible(true);
    }

    /**
     * Displays the goal cards popup window.
     */
    public void printGoalCards(){
        guiManager.popupStage(GOALS,"Goal Cards");
        guiManager.printGoalCards();
    }

    /**
     * Shows the end game token.
     */
    public void showEndGameToken(){
        tokenGrid.getChildren().get(0).setVisible(true);
    }

    /**
     * Hides the end game token.
     */
    public void hideEndGameToken(){
        endGameToken.setVisible(false);
    }

    /**
     * Prints the bookshelves of other players.
     */
    public void printBookShelfs(){
        guiManager.printBookShelfs();
    }

    /**
     * Updates the points display.
     *
     * @param points The new points value.
     */
    public void updatePoints(String points){
        this.points.setText(points);
    }

    /**
     * Enables the picking of tiles from the board.
     */
    public void allowPickTiles(){
        boardGrid.setDisable(false);
    }

    /**
     * Disables the picking of tiles from the board.
     */
    public void disablePickTiles(){
        boardGrid.setDisable(true);
    }

    /**
     * Updates the current turn display.
     *
     * @param turn The new turn value.
     */
    public void updateTurn(String turn){
        this.turn.setText(turn);
    }

    /**
     * Displays the chair and hides the user icon.
     */
    public void showChair(){
        chair.setVisible(true);
        userIcon.setVisible(false);
    }

    /**
     * Displays a help dialog with game instructions.
     */
    public void help(){
        Alert help = new Alert(Alert.AlertType.INFORMATION);
        help.setTitle("HELP");
        help.setHeaderText("Here are some info to play!");
        help.setContentText("- PICK TILES: \n" +
                "\tClick on a tile to choose it.\n" +
                "\tClick on it again to un-choose it. \n" +
                "\tClick on the confirm button to send it to the server.\n" +
                "\tClick on the cancel button to abort the tile choice.\n\n" +
                "- PLACE TILES:\n" +
                "\tDrag and drop a tile on the bookshelf to place it.\n" +
                "\tClick on the confirm button to send the placement information to the server.\n" +
                "\tClick on the cancel button to abort the tile placement.\n\n" +
                "- GET GOAL CARD:\n" +
                "\tClick on the goal cards button to see your goal cards.\n\n" +
                "- END TURN:\n" +
                "\tClick on the end turn button to pass the turn to the next player.\n\n" +
                "ENJOY THE GAME!");
        help.showAndWait();
    }

    /**
     * Sets username of player.
     * @param username of type String
     */
    public void setUsername(String username){
        this.username.setText(username);
    }
}
