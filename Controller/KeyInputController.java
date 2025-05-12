package Controller;

import View.Scenes.MapPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInputController implements KeyListener {

    private boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
    private MapPanel map;
    public KeyInputController(MapPanel map) {
        this.map = map;
    }

    @Override
    public void keyTyped(KeyEvent e) {
      

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
    
        if (code == KeyEvent.VK_W) {
            upPressed = true;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = true;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if (code == KeyEvent.VK_E) {
            enterPressed = true;
        }
    
        // P key for pausing/resuming the game
        if (code == KeyEvent.VK_ESCAPE) {
            if (!map.getGameManagerController().isPaused()) {
                map.getGameManagerController().pauseGame();
                map.getPauseMenu().showMenu();
            } else {
                map.getGameManagerController().resumeGame();
                map.getPauseMenu().hideMenu();
            }
        }
        
        // M key for toggling the minimap
        if (code == KeyEvent.VK_M) {
            map.getMiniMapController().toggleMiniMap();
        }
    }
    

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
    
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }
        if (code == KeyEvent.VK_E) {
            enterPressed = false;
        }
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isEnterPressed() {
        return enterPressed;
    }
}
