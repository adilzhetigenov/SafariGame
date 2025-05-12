package Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoadController {

    private final String saveDirectory;

   
    public LoadController(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

   
    public List<String> getAvailableGames() {
        File dir = new File(saveDirectory); 
        List<String> games = new ArrayList<>();

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    games.add(file.getName());
                }
            }
        }
        return games;
    }
}
