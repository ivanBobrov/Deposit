package com.company.ui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChooseAccountPanel extends JPanel implements ActionListener {
    private static final int TEXT_EDIT_WIDTH = 10;

    private final JTextField accountIdField;
    private JPanel detailsPanel = null;

    public ChooseAccountPanel() {
        setLayout(new BorderLayout());

        final JPanel dialogPanel = new JPanel();
        final JLabel label = new JLabel("Choose account: ");
        accountIdField = new JTextField();
        accountIdField.setColumns(TEXT_EDIT_WIDTH);
        final JButton chooseButton = new JButton("Find");
        chooseButton.addActionListener(this);

        dialogPanel.add(label);
        dialogPanel.add(accountIdField);
        dialogPanel.add(chooseButton);

        add(dialogPanel, BorderLayout.PAGE_START);
    }

    public void actionPerformed(ActionEvent actionEvent) {
        String input = accountIdField.getText();
        Integer accountId;
        try {
            accountId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            accountId = null;
        }

        if (accountId == null || accountId <= 0) {
            JOptionPane.showMessageDialog(new JFrame(), "Wrong account id", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (detailsPanel != null) {
            remove(detailsPanel);
            detailsPanel = null;
        }

        detailsPanel = new AccountDetailsPanel(accountId);
        add(detailsPanel);
        revalidate();
    }
}
