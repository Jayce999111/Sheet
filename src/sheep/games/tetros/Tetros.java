package sheep.games.tetros;

import sheep.expression.TypeError;
import sheep.expression.basic.Constant;
import sheep.expression.basic.Nothing;
import sheep.features.Feature;
import sheep.games.random.RandomTile;
import sheep.sheets.CellLocation;
import sheep.sheets.Sheet;
import sheep.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The Tetris game class.
 */
public class Tetros implements Tick, Feature {

    // Fields
    private final Sheet sheet;
    private boolean started = false;
    private int fallingType = 1;
    private List<CellLocation> contents = new ArrayList<>();
    private final RandomTile randomTile;

    /**
     * Constructs a Tetros game instance with the sheet and random tile.
     *
     * @param sheet      The sheet to use for rendering Tetris pieces.
     * @param randomTile The random tile generator.
     */
    public Tetros(Sheet sheet, RandomTile randomTile) {
        this.sheet = sheet;
        this.randomTile = randomTile;
    }

    /**
     * Registers the Tetris game with the UI.
     *
     * @param ui The user interface to register with.
     */
    @Override
    public void register(UI ui) {
        ui.onTick(this);
        ui.addFeature("tetros", "Start Tetros", new GameStart());
        ui.onKey("a", "Move Left", new Action(ActionType.MOVE, -1));
        ui.onKey("d", "Move Right", new Action(ActionType.MOVE, 1));
        ui.onKey("q", "Rotate Left", new Action(ActionType.ROTATE, -1));
        ui.onKey("e", "Rotate Right", new Action(ActionType.ROTATE, 1));
        ui.onKey("s", "Drop", new Action(ActionType.MOVE, 0));
    }


    /**
     * Starts the Tetris game.
     */
    public class GameStart implements Perform {
        /**
         * Performs the action to start the game.
         *
         * @param row    The row where the action is performed.
         * @param column The column where the action is performed.
         * @param prompt The prompt object.
         */
        @Override
        public void perform(int row, int column, Prompt prompt) {
            started = true;
            drop();
        }
    }


    /**
     * Drops the Tetris piece.
     */
    public boolean dropTile() {
        List<CellLocation> newContents = new ArrayList<>();
        for (CellLocation tile : contents) {
            newContents.add(new CellLocation(tile.getRow() + 1, tile.getColumn()));
        }
        unrender();
        boolean stopped = false;
        for (CellLocation newLoc : newContents) {
            if (!isInBounds(newLoc) || isStopper(newLoc)) {
                stopped = true;
                break;
            }
        }
        // If the tile cannot drop further, render the new position of the Tetris piece
        if (stopped) {
            ununrender(contents);
            return true;
        } else {
            ununrender(newContents);
            this.contents = newContents;
            return false;
        }
    }

    /**
     * Performs a full drop of the Tetris piece.
     */
    public void fullDrop() {
        while (!dropTile()) {
            // Keep dropping the tile until it cannot drop further
        }
    }


    /**
     * Checks if the specified location is a stopper (i.e., prevents further movement).
     *
     * @param location The cell location to check.
     * @return True if the location is a stopper, false otherwise.
     */
    private boolean isStopper(CellLocation location) {
        return location.getRow() >= sheet.getRows()
                || location.getColumn() >= sheet.getColumns()
                || !sheet.valueAt(location.getRow(), location.getColumn()).getContent().isEmpty();
    }

    /**
     * Checks if the Tetris piece is in bounds of the sheet.
     *
     * @param locations The list of cell locations representing the Tetris piece.
     * @return True if the Tetris piece is in bounds, false otherwise.
     */
    public boolean inBounds(List<CellLocation> locations) {
        for (CellLocation location : locations) {
            if (!sheet.contains(location)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a given cell location is in bounds of the sheet.
     *
     * @param location The cell location to check.
     * @return True if the cell location is in bounds, false otherwise.
     */
    public boolean isInBounds(CellLocation location) {
        int row = location.getRow();
        int col = location.getColumn();
        return row >= 0 && row < sheet.getRows() && col >= 0 && col < sheet.getColumns();
    }


    /**
     * Renders the Tetris piece on the sheet.
     */
    public void unrender() {
        for (CellLocation cell : contents) {
            try {
                sheet.update(cell, new Nothing());
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Unrenders the Tetris piece from the sheet.
     */
    public void ununrender(List<CellLocation> items) {
        for (CellLocation cell : items) {
            try {
                sheet.update(cell, new Constant(fallingType));
            } catch (TypeError e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Drops a new Tetris piece.
     */
    private boolean drop() {
        contents = new ArrayList<>();
        newPiece();
        for (CellLocation location : contents) {
            if (!sheet.valueAt(location).render().isEmpty()) {
                return true;
            }
        }
        ununrender(contents);
        return false;
    }

    /**
     * Creates a new Tetris piece.
     */
    private void newPiece() {
        int value = randomTile.pick();
        switch (value) {
            case 1 -> {
                contents.add(new CellLocation(0, 0));  // Square
                contents.add(new CellLocation(1, 0));  // Square
                contents.add(new CellLocation(2, 0));  // Square
                contents.add(new CellLocation(2, 1));  // Square
                fallingType = 7;  // Square
            }
            case 2 -> {
                contents.add(new CellLocation(0, 1));  // LShape
                contents.add(new CellLocation(1, 1));  // LShape
                contents.add(new CellLocation(2, 1));  // LShape
                contents.add(new CellLocation(2, 0));  // LShape
                fallingType = 5;  // LShape
            }
            case 3 -> {
                contents.add(new CellLocation(0, 0));  // ReverseL
                contents.add(new CellLocation(0, 1));  // ReverseL
                contents.add(new CellLocation(0, 2));  // ReverseL
                contents.add(new CellLocation(1, 1));  // ReverseL
                fallingType = 8;  // ReverseL
            }
            case 4 -> {
                contents.add(new CellLocation(0, 0));  // Line
                contents.add(new CellLocation(0, 1));  // Line
                contents.add(new CellLocation(1, 0));  // Line
                contents.add(new CellLocation(1, 1));  // Line
                fallingType = 3;  // Line
            }
            case 5 -> {
                contents.add(new CellLocation(0, 0));  // TShape
                contents.add(new CellLocation(1, 0));  // TShape
                contents.add(new CellLocation(2, 0));  // TShape
                contents.add(new CellLocation(3, 0));  // TShape
                fallingType = 6;  // TShape
            }
            case 6 -> {
                contents.add(new CellLocation(0, 1));  // ZShape
                contents.add(new CellLocation(0, 2));  // ZShape
                contents.add(new CellLocation(1, 1));  // ZShape
                contents.add(new CellLocation(0, 1));  // ZShape
                fallingType = 2;  // ZShape
            }
            case 0 -> {
                contents.add(new CellLocation(0, 0));  // ReverseZ
                contents.add(new CellLocation(0, 1));  // ReverseZ
                contents.add(new CellLocation(1, 1));  // ReverseZ
                contents.add(new CellLocation(1, 2));  // ReverseZ
                fallingType = 4;  // ReverseZ
            }
        }
    }

    /**
     * Flips the Tetris piece.
     */
    private void flip(int direction) {
        int x = 0;
        int y = 0;
        for (CellLocation cellLocation : contents) {
            x += cellLocation.getColumn();
            y += cellLocation.getRow();
        }
        x /= contents.size();
        y /= contents.size();
        List<CellLocation> newCells = new ArrayList<>();
        for (CellLocation location : contents) {
            int lx = x + ((y - location.getRow()) * direction);
            int ly = y + ((x - location.getColumn()) * direction);
            CellLocation replacement = new CellLocation(ly, lx);
            newCells.add(replacement);
        }
        if (!inBounds(newCells)) {
            return;
        }
        unrender();
        contents = newCells;
        ununrender(newCells);
    }

    /**
     * Moves the Tetris piece by the specified amount.
     *
     * @param x The amount to move (negative for left, positive for right, 0 for drop).
     */
    public void shift(int x) {
        if (x == 0) {
            fullDrop(); // Perform full drop if x is 0
            return;
        }

        List<CellLocation> newContents = new ArrayList<>();
        for (CellLocation tile : contents) {
            int newColumn = tile.getColumn() + x;
            if (newColumn < 0) {
                // If new column position is less than 0, do nothing
                return;
            }
            newContents.add(new CellLocation(tile.getRow(), newColumn));
        }

        if (inBounds(newContents)) {
            unrender();
            ununrender(newContents);
            this.contents = newContents;
        }
    }


    /**
     * Handles the tick event.
     *
     * @param prompt The prompt object.
     * @return True if the tick event is handled successfully, false otherwise.
     */
    @Override
    public boolean onTick(Prompt prompt) {
        if (!started) {
            return false;
        }

        if (dropTile()) {
            if (drop()) {
                prompt.message("Game Over!");
                started = false;
            }
        }
        clearCompletedRows();
        return true;
    }

    /**
     * Clears completed rows from the sheet when it is full.
     */
    private void clearCompletedRows() {
        for (int row = sheet.getRows() - 1; row >= 0; row--) {
            boolean full = true;
            for (int col = 0; col < sheet.getColumns(); col++) {
                if (sheet.valueAt(row, col).getContent().isEmpty()) {
                    full = false;
                    break;
                }
            }
            if (full) {
                moveRowsDown(row);
                row++; // Recheck the current row after moving rows down
            }
        }
    }

    /**
     * Moves rows down starting from the given row index.
     *
     * @param startRow The index of the row to start moving from.
     */
    private void moveRowsDown(int startRow) {
        for (int row = startRow; row > 0; row--) {
            for (int col = 0; col < sheet.getColumns(); col++) {
                try {
                    if (!contents.contains(new CellLocation(row - 1, col))) {
                        sheet.update(new CellLocation(row, col),
                                sheet.valueAt(new CellLocation(row - 1, col)));
                    }
                } catch (TypeError e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }



    /**
     * Represents an action performed in the Tetris game, such as moving or rotating the Tetris piece.
     */
    private class Action implements Perform {
        private final ActionType type;
        private final int direction;

        /**
         * Creates an action with the specified type and direction.
         *
         * @param type      The type of action.
         * @param direction The direction of the action (-1 for left, 1 for right, 0 for drop for move,
         * -1 for left, 1 for right for rotate).
         */
        public Action(ActionType type, int direction) {
            this.type = type;
            this.direction = direction;
        }

        /**
         * Performs the action based on its type.
         *
         * @param row    The row where the action is performed.
         * @param column The column where the action is performed.
         * @param prompt The prompt object.
         */
        @Override
        public void perform(int row, int column, Prompt prompt) {
            if (!started) {
                return;
            }

            switch (type) {
                case MOVE:
                    shift(direction);
                    break;
                case ROTATE:
                    flip(direction);
                    break;
            }
        }
    }

    /**
     * Enum representing the type of action.
     */
    private enum ActionType {
        MOVE,
        ROTATE
    }
}