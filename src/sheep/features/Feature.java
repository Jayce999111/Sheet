package sheep.features;

import sheep.ui.Prompt;
import sheep.ui.UI;

public interface Feature {
    void register(UI ui);

    boolean onTick(Prompt prompt);
}
