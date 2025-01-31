package sheep.features.files;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;

/**
 * A feature for loading a spreadsheet from a file.
 */
public class FileLoading implements Feature, Tick {
    private final Sheet sheet;

    /**
     * Constructs a FileLoading feature.
     *
     * @param sheet The sheet to load the spreadsheet into.
     */
    public FileLoading(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * Registers the FileLoading feature with the UI.
     *
     * @param ui The user interface to register the feature with.
     */
    @Override
    public void register(UI ui) {
        ui.addFeature("load-file", "Load File", this::activate);
        ui.onTick(this); // Registering this class for ticks
    }

    /**
     * Activates the file loading feature.
     *
     * @param i      Unused parameter.
     * @param i1     Unused parameter.
     * @param prompt The prompt for user interaction.
     */
    private void activate(int i, int i1, Prompt prompt) {
        Optional<String> filePathOptional = prompt.ask("Enter the file path:");
        if (filePathOptional.isPresent()) {
            String filePath = filePathOptional.get();
            loadFromFile(filePath, prompt);
        } else {
            prompt.message("No file path entered. Operation cancelled.");
        }
    }

    /**
     * Loads a spreadsheet from the specified file path and updates the dimensions of the sheet.
     *
     * @param filePath The path of the file to load the spreadsheet from.
     * @param prompt   The prompt for user interaction.
     */
    private void loadFromFile(String filePath, Prompt prompt) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Read file contents into memory
            StringBuilder fileContents = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append("\n");
            }

            // Process the file contents
            String[] lines = fileContents.toString().split("\n");
            int rows = lines.length;
            int cols = 0;
            for (String fileLine : lines) {
                String[] cellValues = fileLine.split("\\|");
                cols = Math.max(cols, cellValues.length);
            }

            // Clear the existing sheet
            sheet.clear();

            // Load data into the existing sheet
            for (int row = 0; row < rows; row++) {
                String[] cellValues = lines[row].split("\\|");
                for (int col = 0; col < cellValues.length; col++) {
                    sheet.update(row, col, cellValues[col].trim());
                }
            }

            // Update dimensions of the existing sheet
            sheet.updateDimensions(rows, cols);



        } catch (IOException e) {
            prompt.message("Error loading spreadsheet: " + e.getMessage());
        }
    }

    /**
     * The onTick method is called whenever a tick occurs in the user interface.
     *
     * @param prompt Provide a mechanism to interact with the user interface
     *               after a tick occurs, if required.
     * @return True iff the spreadsheet needs to be re-rendered
     * (i.e. if any cells changed).
     */
    @Override
    public boolean onTick(Prompt prompt) {
        // No tick logic needed for file loading, so returning false
        return false;
    }
}
