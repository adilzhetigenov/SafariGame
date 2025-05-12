package Controller;
import View.Scenes.MapPanel;

public class GameManagerController {
    private MapPanel map;
    private String font = "Comic Sans MS";
    private int fontSize = 30;
    private boolean  soundEnabled = false;
    private boolean gamePaused = false;
    public GameManagerController(MapPanel map){
        this.map = map;
    }
    public String getFont(){
        return font;
    }
    public int getFontSize(){
        return fontSize;
    }

    public void pauseGame() {
        gamePaused = true;
        // Handle game pause logic, for example, stop game logic here
        updateGameUI(); // Update the UI to reflect the pause
    }

    public void resumeGame() {
        gamePaused = false;
        // Handle game resume logic, e.g., start game logic here
        updateGameUI(); // Update the UI to reflect the game resumption
    }

    public void updateGameUI() {
        // This method would update the game UI to reflect the current game state (paused or running)
        // You can add the logic here to disable game actions while paused or enable them when resumed
        if (map != null) {
            map.repaint(); // Triggers re-rendering (you could even add a "Paused" overlay in paintComponent)
        }
    }
    public boolean isPaused() {
        return gamePaused;
    }

    public void goToMainMenu() {
        // Logic to return to the main menu screen
        // Example: maybe replace the current game panel with the main menu panel
        if (map != null) {
            // Stop the game thread
            if (map.getGameThread() != null) {
                map.getGameThread().interrupt();
                map.setGameThread(null);
            }
            
            // Reset the game state
            map.resetGameState();
            
            // Return to main menu
            map.handleGameOver();
        }
    }

    public void setFontSize(int size) {
        // Update internal font size setting
        this.fontSize = size;
    
        // Optionally trigger font size refresh on components
        // e.g., notify panels or re-render UI
    }
    
    public boolean isSoundOn() {
        // Return current sound setting (e.g., from a boolean field)
        return soundEnabled;
    }
    
    public void toggleSound(boolean enable) {
        this.soundEnabled = enable;
        // You might start/stop background music or sound effects here
    }

    // Check if the game is over (capital <= 0)
    public void checkGameOver(int currentCapital) {
        if (currentCapital <= 0) {
            if (map != null) {
                map.handleGameOver();
            }
        }
    }
}

    
    
    

