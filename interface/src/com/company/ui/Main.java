package com.company.ui;

import java.awt.*;
import javax.swing.*;

public class Main {
    private static final Dimension FRAME_SIZE = new Dimension(500, 600);
    private static final String WINDOW_NAME = "Deposit";

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame(WINDOW_NAME);
        JPanel choosePanel = new ChooseAccountPanel();
        mainFrame.getContentPane().add(choosePanel);

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setMinimumSize(FRAME_SIZE);
        mainFrame.setVisible(true);
    }
}
