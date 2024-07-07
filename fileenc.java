import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class FileEncryptionApp extends JFrame {

    private JPanel loginPanel;
    private JPanel actionPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    private JButton encryptButton;
    private JButton decryptButton;
    private JButton logoutButton;

    private Map<String, String> userCredentials = new HashMap<>();
    private Map<String, String> userSecretKeys = new HashMap<>();
    private String currentUser;

    public FileEncryptionApp() {
        setTitle("File Encryption App");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loginPanel = new JPanel(new GridBagLayout());
        actionPanel = new JPanel(new BorderLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx++;
        loginPanel.add(usernameField, gbc);
        gbc.gridy++;
        gbc.gridx--;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx++;
        loginPanel.add(passwordField, gbc);
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        loginPanel.add(loginButton, gbc);
        gbc.gridy++;
        loginPanel.add(registerButton, gbc);

        add(loginPanel, BorderLayout.NORTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcButton = new GridBagConstraints();
        gbcButton.gridx = 0;
        gbcButton.gridy = 0;
        gbcButton.insets = new Insets(10, 10, 10, 10);
        encryptButton = new JButton("Encrypt File");
        buttonPanel.add(encryptButton, gbcButton);

        gbcButton.gridy++;
        decryptButton = new JButton("Decrypt File");
        buttonPanel.add(decryptButton, gbcButton);

        gbcButton.gridy++;
        logoutButton = new JButton("Logout");
        buttonPanel.add(logoutButton, gbcButton);

        actionPanel.add(buttonPanel, BorderLayout.CENTER);

        add(actionPanel, BorderLayout.CENTER);
        actionPanel.setVisible(false);

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEncrypt();
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDecrypt();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
            JOptionPane.showMessageDialog(this, "Login Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            loginPanel.setVisible(false);
            actionPanel.setVisible(true);
            currentUser = username;
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (userCredentials.containsKey(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            String secretKey = generateRandomKey();
            userCredentials.put(username, password);
            userSecretKeys.put(username, secretKey);
            JOptionPane.showMessageDialog(this, "User " + username + " registered successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String generateRandomKey() {
        byte[] key = new byte[16];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    private void handleEncrypt() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File inputFile = fileChooser.getSelectedFile();
            File outputFile = new File(inputFile.getAbsolutePath() + ".enc");
            String secretKey = userSecretKeys.get(currentUser);
            try {
                encryptFile(inputFile, outputFile, secretKey);
                JOptionPane.showMessageDialog(this, "File encrypted successfully", "Encryption Success", JOptionPane.INFORMATION_MESSAGE);
                inputFile.delete();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Encryption failed: " + e.getMessage(), "Encryption Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDecrypt() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File inputFile = fileChooser.getSelectedFile();
            File outputFile = new File(inputFile.getAbsolutePath().replace(".enc", ""));
            String secretKey = userSecretKeys.get(currentUser);
            try {
                decryptFile(inputFile, outputFile, secretKey);
                JOptionPane.showMessageDialog(this, "File decrypted successfully", "Decryption Success", JOptionPane.INFORMATION_MESSAGE);
                inputFile.delete();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Decryption failed: This encrypted file doesn't belong to you." , "Decryption Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleLogout() {
        actionPanel.setVisible(false);
        loginPanel.setVisible(true);
        usernameField.setText("");
        passwordField.setText("");
        currentUser = null;
    }

    private void encryptFile(File inputFile, File outputFile, String secretKey) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKey), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             FileOutputStream outputStream = new FileOutputStream(outputFile);
             CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = inputStream.read(buffer)) > 0) {
                cipherOutputStream.write(buffer, 0, count);
            }
        }
    }

    private void decryptFile(File inputFile, File outputFile, String secretKey) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKey), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        try (FileInputStream inputStream = new FileInputStream(inputFile);
             CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = cipherInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new FileEncryptionApp().setVisible(true);
            }
        });
    }
}


