package Controller;

import View.GameFrame;
import View.Scenes.MapPanel;

public class MainMenuController {
    private GameFrame frame;
    private MapPanel map;

    public MainMenuController(GameFrame frame,MapPanel map) {
        this.frame = frame;
        this.map = map;
    }
    public void changePanelToMap() {
        frame.changePanel("Map");
        map.StartGameThread();

    }
    public void changePanelToMainMenu() {
        frame.changePanel("MainMenu");

    }
    public void changePanelToSettings(){
        frame.changePanel("Settings");
    }
    public void changePanelToLoadGame(){
        frame.changePanel("Load");
    }
}
