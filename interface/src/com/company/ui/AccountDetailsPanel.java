package com.company.ui;

import com.company.remote.AccountInfo;
import com.company.remote.IDepositServer;
import com.company.remote.NoSuchAccountException;
import com.company.remote.NotEnoughFundsException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;


public class AccountDetailsPanel extends JPanel {
    private final Integer accountId;
    private final JPanel infoPanel;
    private final JPanel modificationPanel;

    public AccountDetailsPanel(final Integer accountId) {
        this.accountId = accountId;
        setLayout(new GridLayout(2, 1));
        infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 1));
        modificationPanel = new JPanel();
        modificationPanel.setLayout(new BoxLayout(modificationPanel, BoxLayout.Y_AXIS));

        add(infoPanel);
        add(modificationPanel);

        updateInfo();
        updateModificationPanel();
    }

    private void updateInfo() {
        SwingWorker worker = new SwingWorker<AccountInfo, Void>() {
            @Override
            protected AccountInfo doInBackground() throws Exception {
                IDepositServer remote = DepositServiceFactory.getDepositService();
                return remote == null ? null : remote.getAccountInfo(accountId);
            }

            @Override
            protected void done() {
                try {
                    AccountInfo info = get();
                    infoPanel.removeAll();

                    JLabel balanceInfo = new JLabel("Balance: " + info.getBalance());
                    String[] operations = new String[info.getOperationInfoList().size()];
                    for (int i = 0; i < info.getOperationInfoList().size(); i++) {
                        operations[i] = info.getOperationInfoList().get(i).getInfo();
                    }
                    JList<String> list = new JList<>(operations);

                    balanceInfo.setMaximumSize(balanceInfo.getPreferredSize());
                    balanceInfo.setHorizontalAlignment(JLabel.CENTER);
                    JScrollPane pane = new JScrollPane(list);
                    infoPanel.add(balanceInfo);
                    infoPanel.add(pane);
                    infoPanel.revalidate();
                } catch (ExecutionException | InterruptedException exception) {
                    String errorMessage;
                    if (exception.getCause() instanceof NoSuchAccountException) {
                        errorMessage = "Account " + accountId + " not found";
                    } else {
                        errorMessage = "Unknown connection error";
                    }

                    infoPanel.removeAll();
                    JLabel error = new JLabel(errorMessage);
                    error.setHorizontalAlignment(JLabel.CENTER);
                    infoPanel.add(error);
                    modificationPanel.setVisible(false);

                    infoPanel.revalidate();
                }
            }
        };
        worker.execute();
    }

    private void updateModificationPanel() {
        modificationPanel.removeAll();

        JPanel infoMessagePanel = new JPanel();
        infoMessagePanel.setLayout(new BoxLayout(infoMessagePanel, BoxLayout.X_AXIS));

        JLabel infoMessage = new JLabel("");
        infoMessagePanel.add(infoMessage);

        JPanel firstPanel = new JPanel();
        firstPanel.setLayout(new BoxLayout(firstPanel, BoxLayout.X_AXIS));

        JLabel labelAmount = new JLabel("Transfer funds: ");
        JTextField amountField = new JTextField();
        amountField.setMaximumSize(new Dimension(100, 20));
        firstPanel.add(labelAmount);
        firstPanel.add(amountField);

        JPanel secondPanel = new JPanel();
        secondPanel.setLayout(new BoxLayout(secondPanel, BoxLayout.X_AXIS));

        JLabel labelTo = new JLabel("to account: ");
        JTextField destinationField = new JTextField();
        destinationField.setMaximumSize(new Dimension(100, 20));
        secondPanel.add(labelTo);
        secondPanel.add(destinationField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        JButton transferButton = new JButton("Transfer");
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    final Integer amount = Integer.parseInt(amountField.getText());
                    final Integer destinationId = Integer.parseInt(destinationField.getText());

                    if (amount < 0 || accountId.equals(destinationId)) {
                        infoMessage.setText("Wrong parameters");
                        modificationPanel.revalidate();
                    }

                    infoMessage.setText("");
                    modificationPanel.revalidate();

                    SwingWorker worker = new SwingWorker() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            IDepositServer remote = DepositServiceFactory.getDepositService();
                            remote.transfer(accountId, destinationId, amount);

                            return null;
                        }

                        @Override
                        protected void done() {
                            try {
                                get();
                                updateInfo();
                            } catch (ExecutionException|InterruptedException exception) {
                                if (exception.getCause() instanceof NotEnoughFundsException) {
                                    infoMessage.setText("not enough funds");
                                    modificationPanel.revalidate();
                                } else if (exception.getCause() instanceof NoSuchAccountException) {
                                    infoMessage.setText("Destination account not exists");
                                    modificationPanel.revalidate();
                                } else {
                                    infoMessage.setText("Unknown error");
                                    modificationPanel.revalidate();
                                }
                            }
                        }
                    };
                    worker.execute();

                } catch (NumberFormatException exception) {
                    infoMessage.setText("Wrong transfer parameters");
                    modificationPanel.revalidate();
                }
            }
        });

        buttonPanel.add(transferButton);

        modificationPanel.add(infoMessagePanel);
        modificationPanel.add(firstPanel);
        modificationPanel.add(secondPanel);
        modificationPanel.add(buttonPanel);
        modificationPanel.revalidate();
    }
}
