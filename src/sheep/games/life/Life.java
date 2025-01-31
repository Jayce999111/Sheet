package sheep.games.life;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

/**
 * Conway's Game of Life feature.
 */
public class Life implements Feature, Tick {
    private final Sheet sheet;
    private boolean running;

    /**
     * Constructs a Life feature.
     *
     * @param sheet The sheet to simulate the game of life on.
     */
    public Life(Sheet sheet) {
        this.sheet = sheet;
        this.running = false;
    }

    /**
     * Registers the Life feature with the UI.
     *
     * @param ui The user interface to register the feature with.
     */
    @Override
    public void register(UI ui) {
        ui.addFeature("gol-start", "Start Game of Life", this::startGame);
        ui.addFeature("gol-end", "End Game of Life", this::endGame);
        ui.onTick(this);
    }

    /**
     * Starts the Game of Life simulation.
     *
     * @param i      Unused parameter.
     * @param i1     Unused parameter.
     * @param prompt The prompt for user interaction.
     */
    private void startGame(int i, int i1, Prompt prompt) {
        running = true;
    }

    /**
     * Stops the Game of Life simulation.
     *
     * @param i      Unused parameter.
     * @param i1     Unused parameter.
     * @param prompt The prompt for user interaction.
     */
    private void endGame(int i, int i1, Prompt prompt) {
        running = false;
    }

    /**
     * The tick method called on every tick of the UI.
     *
     * @param prompt Provide a mechanism to interact with the user interface after a tick occurs, if required.
     * @return True iff the spreadsheet needs to be re-rendered (i.e. if any cells changed).
     */
    @Override
    public boolean onTick(Prompt prompt) {
        if (!running) {
            return false;
        }

        int rows = sheet.getRows();
        int cols = sheet.getColumns();
        String[][] nextGeneration = new String[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int liveNeighbors = countLiveNeighbors(row, col);
                if (isCellAlive(row, col)) {
                    if (liveNeighbors < 2 || liveNeighbors > 3) {
                        nextGeneration[row][col] = "";
                    } else {
                        nextGeneration[row][col] = "1";
                    }
                } else {
                    if (liveNeighbors == 3) {
                        nextGeneration[row][col] = "1";
                    } else {
                        nextGeneration[row][col] = "";
                    }
                }
            }
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                sheet.update(row, col, nextGeneration[row][col]);
            }
        }

        return true;
    }

    /**
     * Checks if a cell at given row and column is alive.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return True if the cell is alive, false otherwise.
     */
    private boolean isCellAlive(int row, int col) {
        return sheet.valueAt(row, col).getContent().equals("1");
    }

    /**
     * Counts the number of live neighbors around a cell.
     *
     * @param row The row of the cell.
     * @param col The column of the cell.
     * @return The number of live neighbors.
     */
    private int countLiveNeighbors(int row, int col) {
        int count = 0;
        int[][] directions = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (newRow >= 0 && newRow < sheet.getRows()
                    && newCol >= 0 && newCol < sheet.getColumns()) {
                if (isCellAlive(newRow, newCol)) {
                    count++;
                }
            }
        }

        return count;
    }
}
