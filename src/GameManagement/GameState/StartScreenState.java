package src.GameManagement.GameState;
import javax.swing.*;

import src.GameManagement.Game;

import java.awt.*;
import java.awt.event.*;

public class StartScreenState extends GameState{
    public StartScreenState(Game g){
        super(g);
    }

    // Detect if the User presses ENTER to presses START, then move to the next state
    public void update(){

    }

    public void draw(Graphics g){}
}
