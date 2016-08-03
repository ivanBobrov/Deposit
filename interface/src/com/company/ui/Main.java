package com.company.ui;

import java.awt.*;
import javax.swing.*;

public class Main {
    private static final Dimension frameSize = new Dimension(500, 600);

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("Deposit");
        JPanel choosePanel = new ChooseAccountPanel();
        mainFrame.getContentPane().add(choosePanel);

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setMinimumSize(frameSize);
        mainFrame.setVisible(true);
    }
}
