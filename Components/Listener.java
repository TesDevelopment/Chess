package Components;
import java.awt.Color;
import java.util.ArrayList;

public class Listener implements DrawListener {
    public final static double SQUARE_SIZE = 0.125;
    private Draw canvas;

    private final String[][] startingBoard = new String[][]{
        {"br", "bn", "bb", "bq", "bk", "bb", "bn", "br"},
        {"bp", "bp", "bp", "bp", "bp", "bp", "bp", "bp"},
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
        {"", "", "", "", "", "", "", ""},
        {"wp", "wp", "wp", "wp", "wp", "wp", "wp", "wp"},
        {"wr", "wn", "wb", "wq", "wk", "wb", "wn", "wr"}
    };

    public String[][] board = cloneStartingBoard();

    private final Color BEIGE = new Color(235, 236, 208);
    private final Color GREEN = new Color(119, 149, 86);

    private final Color SPOT = new Color(0, 0, 0, 50);

    private String winner = "";
    public String turn = "w";

    private String draggedPiece = "";
    private Vector2D originialDraggedPiecePos = Vector2D.ZERO;
    private ArrayList<Vector2D> draggedPieceLegalMoves = new ArrayList<>();

    public Listener(Draw canvas){
        this.canvas = canvas;
    }

    private String[][] cloneStartingBoard(){
        String[][] out = new String[8][8];

        for(int i = 0; i < 8; i++){
            out[i] = startingBoard[i].clone();
        }

        return out;
    }

    @Override
    public void update(){
        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8; col++){

                double posX = (row * SQUARE_SIZE) + SQUARE_SIZE/2;
                double posY = (col * SQUARE_SIZE) + SQUARE_SIZE/2;

                canvas.setPenColor((row+col)%2==0 ? BEIGE : GREEN);
                canvas.filledSquare(posX, posY, SQUARE_SIZE/2);

                if(!board[col][row].equals("")){
                    canvas.picture(posX, posY, "Assets/" + board[col][row] + ".png", SQUARE_SIZE, SQUARE_SIZE);

                    canvas.setPenColor(Color.BLUE);
                    //canvas.text(posX, posY, "" + col + ", " + row);                    
                }
            } 
            
        }

        if(!winner.equals("")){
            canvas.setPenColor(winner.equals("White") ? Color.WHITE : Color.BLACK);

            canvas.text(0.5, 0.5, winner + " wins!");
            canvas.text(0.5, 0.4, "Press [M1] to play again"); 

            canvas.show();
            return;
        }

        if(!draggedPiece.equals("")){   
            canvas.picture(canvas.mouseX(), canvas.mouseY(), "Assets/" + draggedPiece + ".png", SQUARE_SIZE, SQUARE_SIZE);

            canvas.setPenColor(SPOT);
            for(Vector2D move : draggedPieceLegalMoves){
                double posX = (move.y * SQUARE_SIZE) + SQUARE_SIZE/2;
                double posY = (move.x * SQUARE_SIZE) + SQUARE_SIZE/2;

                canvas.filledCircle(posX, posY, SQUARE_SIZE / 6);
            }

            
        }
        canvas.show();
    }

    private Vector2D findClosestBox(double x, double y) {
        int closestRow = (int) (y / SQUARE_SIZE);
        int closestCol = (int) (x / SQUARE_SIZE);

        return new Vector2D(closestRow, closestCol);
    }

    private boolean isOccupied(Vector2D position){
        
        if(position.x < 0 || position.x > 7 || position.y < 0 || position.y > 7){
            return false;
        }

        return !board[position.x][position.y].equals("");
    }

    private boolean isOccupied(Vector2D position, String pieceType){
        if(position.x < 0 || position.x > 7 || position.y < 0 || position.y > 7){
            return false;
        }

        return !board[position.x][position.y].equals("") && !board[position.x][position.y].contains(pieceType.substring(0, 1));
    }

    private ArrayList<Vector2D> getLegalMoves(String pieceType, Vector2D startingVector, boolean... f){
        ArrayList<Vector2D> moves = new ArrayList<>();

        String color = pieceType.substring(0, 1);
        boolean checkSafe = (f.length >= 1) ? f[0] : false;

        if(!checkSafe && !pieceType.contains("k") && !isSafe(color, findPiece(color + "k"))) return moves;

        switch (pieceType) {
            case "bp": {
                if(startingVector.x != 0 && startingVector.x != 7 && !isOccupied(startingVector.add(new Vector2D(1, 0)), pieceType)){
                    moves.add(new Vector2D(startingVector.x + 1, startingVector.y));
                }

                if(isOccupied(startingVector.add(new Vector2D(1, 1)), pieceType)){
                    moves.add(startingVector.add(new Vector2D(1, 1)));
                }

                if(isOccupied(startingVector.add(new Vector2D(1, -1)), pieceType)){
                    moves.add(startingVector.add(new Vector2D(1, -1)));
                }

                if(startingVector.x == 1 && !isOccupied(startingVector.add(new Vector2D(2, 0)))){
                    moves.add(startingVector.add(new Vector2D(2, 0)));
                }

                break;
            }

            case "wp": {
                if(startingVector.x != 0 && startingVector.x != 7 && !isOccupied(startingVector.subtract(new Vector2D(1, 0)), pieceType)){
                    moves.add(startingVector.subtract(new Vector2D(1, 0)));
                }

                if(isOccupied(startingVector.subtract(new Vector2D(1, 1)), pieceType)){
                    moves.add(startingVector.subtract(new Vector2D(1, 1)));
                }

                if(isOccupied(startingVector.subtract(new Vector2D(1, -1)), pieceType)){
                    moves.add(startingVector.subtract(new Vector2D(1, -1)));
                }

                if(startingVector.x == 6 && !isOccupied(startingVector.subtract(new Vector2D(2, 0)))){
                    moves.add(startingVector.subtract(new Vector2D(2, 0)));
                }

                break;
            }

            case "wr":
            case "br": {
                
                Vector2D[] checks = new Vector2D[]{
                    new Vector2D(-1, 0),
                    new Vector2D(-2, 0),
                    new Vector2D(0, -1),
                    new Vector2D(0, -2)
                };
                moves.addAll(genericMultiPositionCheck(startingVector, pieceType, checks));

                break;
            }

            case "wn":
            case "bn": {
                Vector2D[] checks = new Vector2D[]{
                    new Vector2D(-1, -2),
                    new Vector2D(-2, -1),
                    new Vector2D(-2, 1),
                    new Vector2D(-1, 2),
                    new Vector2D(1, -2),
                    new Vector2D(2, -1),
                    new Vector2D(2, 1),
                    new Vector2D(1, 2)
                };

                for(Vector2D move : checks){
                    Vector2D checker = startingVector.add(move);

                    if(checker.x < 0 || checker.x > 7 || checker.y < 0 || checker.y > 7) continue;

                    if(isOccupied(checker)){
                        if(board[checker.x][checker.y].contains(pieceType.substring(0, 1))) continue;

                        moves.add(checker);
                    } else {
                        moves.add(checker);
                    }
                }

                break;
            }

            case "wb":
            case "bb": {

                Vector2D[] checks = new Vector2D[]{
                    new Vector2D(-1, -1),
                    new Vector2D(-2, -2),
                    new Vector2D(-1, -2),
                    new Vector2D(-2, -1)
                };

                moves.addAll(genericMultiPositionCheck(startingVector, pieceType, checks));

                break;
            }

            case "wq":
            case "bq": {

                Vector2D[] checks = new Vector2D[]{
                    new Vector2D(-1, 0),
                    new Vector2D(-2, 0),
                    new Vector2D(0, -1),
                    new Vector2D(0, -2),
                    new Vector2D(-1, -1),
                    new Vector2D(-2, -2),
                    new Vector2D(-1, -2),
                    new Vector2D(-2, -1)
                };

                moves.addAll(genericMultiPositionCheck(startingVector, pieceType, checks));

                break;
            }

            case  "wk":
            case "bk": {
                Vector2D[] checks = new Vector2D[]{
                    new Vector2D(1, 0),
                    new Vector2D(-1, 0),
                    new Vector2D(0, 1),
                    new Vector2D(0, -1),
                    new Vector2D(1, 1),
                    new Vector2D(1, -1),
                    new Vector2D(-1, 1),
                    new Vector2D(-1, -1)
                };

                for(Vector2D move : checks){
                    Vector2D checker = startingVector.add(move);

                    if(checker.x < 0 || checker.x > 7 || checker.y < 0 || checker.y > 7) continue;
                    if(!checkSafe && !isSafe(color, checker)) continue;

                    if(isOccupied(checker)){
                        if(board[checker.x][checker.y].contains(pieceType.substring(0, 1)) || !isSafe(pieceType.substring(0, 1), move)) continue;

                        moves.add(checker);
                    } else {
                        moves.add(checker);
                    }
                }

                break;
            }
        }

        return moves;
    }

    private boolean isSafe(String color, Vector2D moveTo) {
        String oldPeice = board[moveTo.x][moveTo.y];
        board[moveTo.x][moveTo.y] = "";

        String unsafeColor = color.equals("w") ? "b" : "w";
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) { // Loop through every piece on the board
                if (board[col][row].contains(unsafeColor) && isLegalMove(board[col][row], new Vector2D(col, row), moveTo, true)) {
                    if (board[col][row].contains("p")) {
                        Vector2D pawnPosition = new Vector2D(col, row);
                        
                        if(moveTo.equals(pawnPosition.add(new Vector2D(2, 0))) || moveTo.equals(pawnPosition.add(new Vector2D(-2, 0)))) continue;
                    }

                    board[moveTo.x][moveTo.y] = oldPeice;
                    return false;
                }
            }
        }

        board[moveTo.x][moveTo.y] = oldPeice;
        return true;
    }

    private Vector2D findPiece(String pieceType){
        for(int row = 0; row < 8; row++){
            for(int col = 0; col < 8; col++){
                if(board[col][row].equals(pieceType)){
                    return new Vector2D(col, row);
                }
            }
        }

        return Vector2D.ZERO;
    }

    private ArrayList<Vector2D> genericMultiPositionCheck(Vector2D startingVector,String pieceType, Vector2D[] checks){

        ArrayList<Vector2D> moves = new ArrayList<>();

        for(Vector2D moveData: checks){
            for(int i = 1; i < 8; i++){
                Vector2D checker = startingVector.add(new Vector2D(moveData.x == -1 ? i : moveData.x == -2 ? -i : moveData.x, moveData.y == -1 ? i : moveData.y == -2 ? -i : moveData.y));
    
                if(checker.x < 0 || checker.x > 7 || checker.y < 0 || checker.y > 7) break;
    
                if(isOccupied(checker)){
                    if(board[checker.x][checker.y].contains(pieceType.substring(0, 1))) break;
    
                    moves.add(checker);
                    break;
                } else {
                    moves.add(checker);
                }
            }
        }

        return moves;
    }

    private boolean isLegalMove(String pieceType, Vector2D startingVector, Vector2D endingVector, boolean... checkSafe){
        return getLegalMoves(pieceType, startingVector, checkSafe).contains(endingVector);
    }

    private boolean isCheckMate(String color, Vector2D kingPosition){

        if(kingPosition.equals(Vector2D.ZERO)) return true;
        
        if(isSafe(color, kingPosition)) return false;
        ArrayList<Vector2D> legalMoves = getLegalMoves(color + "k", kingPosition);

        for(Vector2D move : legalMoves){
            if(isSafe(color, move)) return false;
        }

        return true;
    }

    @Override
    public void mousePressed(double x, double y) {
        if(!winner.equals("")){
            board = cloneStartingBoard();
            winner = "";
            return;
        }

        Vector2D closestBox = findClosestBox(x, y);

        if(!board[closestBox.x][closestBox.y].contains(turn)) return;

        if(draggedPiece.equals("")){
            originialDraggedPiecePos = closestBox;

            draggedPiece = board[closestBox.x][closestBox.y];
            draggedPieceLegalMoves = getLegalMoves(draggedPiece, closestBox);

            board[closestBox.x][closestBox.y] = "";
        }
    }

    @Override
    public void mouseReleased(double x, double y){
        if(!winner.equals("")) return;
        Vector2D closestBox = findClosestBox(x, y);

        if(draggedPiece.equals("")) return;

        if(isLegalMove(draggedPiece, originialDraggedPiecePos, closestBox)) {

            if(draggedPiece.contains("p") && (closestBox.x == 0 || closestBox.x == 7)){
                draggedPiece = draggedPiece.substring(0, 1) + "q";
            }

            board[closestBox.x][closestBox.y] = draggedPiece;
        } else {
            board[originialDraggedPiecePos.x][originialDraggedPiecePos.y] = draggedPiece;
            draggedPiece = "";
            return;
        }

        draggedPiece = "";
        if(!board[originialDraggedPiecePos.x][originialDraggedPiecePos.y].equals("")) return;

        
        if(isCheckMate("w", findPiece("wk"))){
            winner = "Black";
            return;
        }

        if(isCheckMate("b", findPiece("bk"))) winner = "White";

        turn = turn.equals("w") ? "b" : "w";
    }
}
