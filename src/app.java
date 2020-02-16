import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.time.LocalDate;

public class app extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        BorderPane borderPane = new BorderPane();
        //Top of GUI
        ToggleButton roundTrip = new ToggleButton("Round Trip");
        ToggleButton oneWay = new ToggleButton("One Way");
        ToggleButton multiCity = new ToggleButton("Multi-City");
        ToggleGroup flightType = new ToggleGroup();
        roundTrip.setToggleGroup(flightType);
        oneWay.setToggleGroup(flightType);
        multiCity.setToggleGroup(flightType);
        HBox topRoot = new HBox();
        topRoot.getChildren().addAll(roundTrip, oneWay, multiCity);
        borderPane.setTop(topRoot);
        topRoot.setSpacing(10);


        //Center of GUI
        GridPane gridCenter = new GridPane();
        ObservableList<String> cities=
                FXCollections.observableArrayList(
                        "Aguadilla, Puerto Rico (BQN)", "Akron"
                );

        //City Selection Dropdowns
        class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

            private ComboBox comboBox;
            private StringBuilder sb;
            private ObservableList<T> data;
            private boolean moveCaretToPos = false;
            private int caretPos;

            public AutoCompleteComboBoxListener(final ComboBox comboBox) {
                this.comboBox = comboBox;
                sb = new StringBuilder();
                data = comboBox.getItems();

                this.comboBox.setEditable(true);
                this.comboBox.setOnKeyPressed(new EventHandler<KeyEvent>() {

                    @Override
                    public void handle(KeyEvent t) {
                        comboBox.hide();
                    }
                });
                this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
            }

            @Override
            public void handle(KeyEvent event) {

                if(event.getCode() == KeyCode.UP) {
                    caretPos = -1;
                    moveCaret(comboBox.getEditor().getText().length());
                    return;
                } else if(event.getCode() == KeyCode.DOWN) {
                    if(!comboBox.isShowing()) {
                        comboBox.show();
                    }
                    caretPos = -1;
                    moveCaret(comboBox.getEditor().getText().length());
                    return;
                } else if(event.getCode() == KeyCode.BACK_SPACE) {
                    moveCaretToPos = true;
                    caretPos = comboBox.getEditor().getCaretPosition();
                } else if(event.getCode() == KeyCode.DELETE) {
                    moveCaretToPos = true;
                    caretPos = comboBox.getEditor().getCaretPosition();
                }

                if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
                        || event.isControlDown() || event.getCode() == KeyCode.HOME
                        || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
                    return;
                }

                ObservableList list = FXCollections.observableArrayList();
                for (int i=0; i<data.size(); i++) {
                    if(data.get(i).toString().toLowerCase().startsWith(
                            AutoCompleteComboBoxListener.this.comboBox
                                    .getEditor().getText().toLowerCase())) {
                        list.add(data.get(i));
                    }
                }
                String t = comboBox.getEditor().getText();

                comboBox.setItems(list);
                comboBox.getEditor().setText(t);
                if(!moveCaretToPos) {
                    caretPos = -1;
                }
                moveCaret(t.length());
                if(!list.isEmpty()) {
                    comboBox.show();
                }
            }

            private void moveCaret(int textLength) {
                if(caretPos == -1) {
                    comboBox.getEditor().positionCaret(textLength);
                } else {
                    comboBox.getEditor().positionCaret(caretPos);
                }
                moveCaretToPos = false;
            }

        }




        ComboBox departureCities = new ComboBox(cities);
        departureCities.setPromptText("City or Airport Code");

        ComboBox arrivalCities = new ComboBox(cities);
        arrivalCities.setPromptText("City or Airport Code");
        AutoCompleteComboBoxListener d = new AutoCompleteComboBoxListener<>(departureCities);
        AutoCompleteComboBoxListener a = new AutoCompleteComboBoxListener<>(arrivalCities);
        Label originLabel = new Label("Origin: ");
        originLabel.setTextFill(Color.web("#000000"));
        originLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));

        Label destinationLabel = new Label("Destination: ");
        destinationLabel.setTextFill(Color.web("#000000"));
        destinationLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));

        gridCenter.add(originLabel, 0,0);
        gridCenter.add(destinationLabel, 0,1);
        gridCenter.add(departureCities, 1,0);
        gridCenter.add(arrivalCities, 1,1);
        gridCenter.setPadding(new Insets(10,10,10,10));

        //Date Selection
       Label departDateLabel = new Label("Departure Date");
        departDateLabel.setTextFill(Color.web("#000000"));
        departDateLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
       Label returnDateLabel = new Label("Return Date");
        returnDateLabel.setTextFill(Color.web("#000000"));
        returnDateLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
       gridCenter.add(departDateLabel,0,2);
       gridCenter.add(returnDateLabel,1,2);
       gridCenter.setPadding( new Insets(10,10,10,10));


       //Date Picker
        DatePicker departDatePicker = new DatePicker(LocalDate.now());
        departDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
        DatePicker returnDatePicker = new DatePicker(LocalDate.now());
        returnDatePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate departDatePickerValue = departDatePicker.getValue();

                setDisable(empty || date.compareTo(departDatePickerValue) < 0 );
            }
        });


        HBox departDateHBox = new HBox(departDatePicker);
        HBox returnDateHBox = new HBox(returnDatePicker);
        gridCenter.add(departDateHBox,0,3);
        gridCenter.add(returnDateHBox,1,3);

        returnDateHBox.managedProperty().bind(returnDateHBox.visibleProperty());
        oneWay.setOnAction(disable -> {
            returnDateHBox.setDisable(true);

            returnDateLabel.setDisable(true);
        });

        roundTrip.setOnAction(disable -> {
            returnDateHBox.setDisable(false);
            returnDateLabel.setDisable(false);
        });


       //BOTTOM

       //Bags Selection
      GridPane gridBottom = new GridPane();
      Spinner spinnerCarryon = new Spinner(0, 1, 0, 1);
      Spinner spinnerChecked = new Spinner(0, 5, 0, 1);
      Label carryonBagLabel = new Label("Carry-On Bags: ");
      carryonBagLabel.setTextFill(Color.web("#000000"));
      carryonBagLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
      Label checkedBagLabel = new Label("Checked Bags: ");
      checkedBagLabel.setTextFill(Color.web("#000000"));
      checkedBagLabel.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
      gridBottom.add(carryonBagLabel, 0, 0);
      gridBottom.add(checkedBagLabel, 0, 1);
      gridBottom.add(spinnerCarryon, 1,0);
      gridBottom.add(spinnerChecked, 1,1);

      //# of Desired PNRs
      Label countPNR = new Label("Number of PNRs:");
        countPNR.setTextFill(Color.web("#000000"));
        countPNR.setFont(Font.font("Helvetica", FontWeight.BOLD, 12));
      TextField numberOfPNR = new TextField("0");
      numberOfPNR.lengthProperty().addListener(new ChangeListener<Number>(){
          @Override
          public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
              if (newValue.intValue() > oldValue.intValue()) {
                  char ch = numberOfPNR.getText().charAt(oldValue.intValue());
                  // Check if the new character is the number or other's
                  if (!(ch >= '0' && ch <= '9' )) {
                      // if it's not number then just setText to previous one
                      numberOfPNR.setText(numberOfPNR.getText().substring(0,numberOfPNR.getText().length()-1));
                  }
              }
          }

      });
      gridBottom.add(countPNR,3,0);
      gridBottom.add(numberOfPNR, 3,1);



      borderPane.setBottom(gridBottom);
      gridBottom.setPadding(new Insets(10, 10,10,10));
      borderPane.setCenter(gridCenter);
        Button button1 = new Button("Generate PNR");
        Scene scene1 = new Scene(borderPane);

        //Stage Properties
        stage.setResizable(false);
        stage.setTitle("PNR GENERATOR");
        stage.setHeight(250);
        stage.setWidth(450);

        borderPane.setPadding(new Insets(10, 10, 10, 10));


        stage.setScene(scene1);




        stage.show();
    }
    public void stop() throws Exception {
        System.out.println("After!!!");
    }

}
