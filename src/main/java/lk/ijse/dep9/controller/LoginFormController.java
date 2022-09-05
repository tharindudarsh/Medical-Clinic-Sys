package lk.ijse.dep9.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.dep9.clinic.security.CryptoUtil;
import lk.ijse.dep9.clinic.security.SecurityContextHolder;
import lk.ijse.dep9.clinic.security.User;
import lk.ijse.dep9.clinic.security.UserRole;

import java.io.IOException;
import java.sql.*;

public class LoginFormController {

    public TextField txtUsername;
    public TextField txtPassword;
    public Button btnLogin;

    public void initialize() {
        btnLogin.setDefaultButton(true);
    }

    public void btnLoginOnAction(ActionEvent actionEvent) throws ClassNotFoundException {
        String userName = txtUsername.getText();
        //   String passwordText = CryptoUtil.getSha265Hex(txtPassword.getText());
        String passwordText = txtPassword.getText();

        if (userName.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Username cannot be empty").showAndWait();
            txtUsername.requestFocus();
            txtUsername.selectAll();
            return;
        } else if (passwordText.isBlank()) {
            new Alert(Alert.AlertType.ERROR, "Password cannot be empty").showAndWait();
            txtPassword.requestFocus();
            txtPassword.selectAll();
            return;
        } else if (!txtUsername.getText().matches("^[a-zA-Z0-9]+$")) {
            new Alert(Alert.AlertType.ERROR, "invalid credentials").showAndWait();
            txtPassword.requestFocus();
            txtPassword.selectAll();
            return;
        }

        Class.forName("com.mysql.cj.jdbc.Driver");
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/MedicalClinicSys", "root", "mysqladmin")) {
//            String sql = "SELECT role FROM LoginUsers WHERE userName='%s' AND password='%s'";
//            sql = String.format(sql,userName,passwordText);
//            Statement stm = connection.createStatement();
//            ResultSet resultSet = stm.executeQuery();

            String sql = "SELECT role,password FROM LoginUsers WHERE userName=? ";
            PreparedStatement sta = connection.prepareStatement(sql);
            sta.setString(1, userName);
            // sta.setString(2,passwordText);
            ResultSet resultSet = sta.executeQuery();

            if (resultSet.next()) {
                String cipher = resultSet.getString("password");
                if (!CryptoUtil.getSha265Hex(passwordText).equals(cipher)) {
                    new Alert(Alert.AlertType.ERROR, "invaild login ").show();
                    txtUsername.requestFocus();
                    txtUsername.selectAll();
                    return;
                }
                String role = resultSet.getString("role");
                SecurityContextHolder.setPrinciple(new User(userName, UserRole.valueOf(role)));
                Scene scene = null;
                switch (role) {
                    case "ADMIN":
                        scene = new Scene(FXMLLoader.load(this.getClass().getResource("/view/AdminDashBoard.fxml")));
                        break;
                    case "DOCTOR":
                        scene = new Scene(FXMLLoader.load(this.getClass().getResource("/view/DocterDashBoard.fxml")));
                        break;
                    default:
                        scene = new Scene(FXMLLoader.load(this.getClass().getResource("/view/ReceptionistDashBoard.fxml")));
                }
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();


            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid logic credentials").show();
                txtUsername.requestFocus();
                txtUsername.selectAll();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to connect with the Database, Try Again").show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
