package sheep.features.files;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import sheep.features.Feature;
import sheep.sheets.Sheet;
import sheep.ui.Prompt;
import sheep.ui.Tick;
import sheep.ui.UI;

/**
 * A feature for saving a spreadsheet to a file.
 */
public class FileSaving implements Feature, Tick {
    private final Sheet sheet;

    /**
     * Constructs a FileSaving feature.
     *
     * @param sheet The sheet to save to the spreadsheet from.
     */
    public FileSaving(Sheet sheet) {
        this.sheet = sheet;
    }

    /**
     * Registers the FileSaving feature with the UI.
     *
     * @param ui The user interface to register the feature with.
     */
    @Override
    public void register(UI ui) {
        ui.addFeature("save-file", "Save File", this::activate);
        ui.onTick(this); // Registering this class for ticks
    }

    private void activate(int i, int i1, Prompt prompt) {
        Optional<String> fileNameOptional = prompt.ask("Enter the file name:");
        if (fileNameOptional.isPresent()) {
            String fileName = fileNameOptional.get();
            String filePath = fileName.isEmpty() ? "spreadsheet.txt" : fileName;
            saveToFile(filePath, prompt);
        } else {
            prompt.message("No file name entered. Operation cancelled.");
        }
    }

    /**
     * Saves the spreadsheet to the specified file path.
     *
     * @param filePath The path of the file to save the spreadsheet to.
     * @param prompt   The prompt for user interaction.
     */
    private void saveToFile(String filePath, Prompt prompt) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int row = 0; row < sheet.getRows(); row++) {
                StringBuilder rowBuilder = new StringBuilder();
                for (int col = 0; col < sheet.getColumns(); col++) {
                    String cellValue = sheet.valueAt(row, col).getContent();
                    rowBuilder.append(cellValue.isEmpty() ? " " : cellValue);
                    if (col < sheet.getColumns() - 1) {
                        rowBuilder.append("|");
                    }
                }
                writer.write(rowBuilder.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            prompt.message("Error saving spreadsheet: " + e.getMessage());
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
        // No tick logic needed for file saving, so returning false
        return false;
    }
}
