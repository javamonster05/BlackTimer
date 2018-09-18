import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.NumberExpressionBase;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.util.concurrent.TimeUnit;


public class Controller {

    StringBuilder stringBuilder = new StringBuilder();
    Intervals intervals;
    Thread t;
    private boolean pause = false;
    private int seconds;

    public Label timeLabel;
    public Button startButton;
    public RadioButton $30MinRadio;
    public RadioButton $45MinRadio;
    public RadioButton $60MinRadio;

    Task<Integer> task1 = new Task<>() {
        @Override
        public Integer call() throws InterruptedException
        {
            if ($30MinRadio.isSelected()) seconds = intervals.THIRHY.getSeconds() ;
            else if ($45MinRadio.isSelected()) seconds = intervals.FOURTY_FIVE.getSeconds();
            else if($60MinRadio.isSelected()) seconds = intervals.ONE_HOUR.getSeconds();

            for(int i = 3; i>=0; i--)
            {
                if (pause){
                    synchronized (this){
                        task1.wait();
                    }
                }
                updateMessage(timeConversion(i));
                if(i == 0) updateMessage("Time is Over");
                Thread.sleep(1000);
            }
            return 0;
        }
    };

    @FXML
    private void startButtonClick(){
        if((!$30MinRadio.isSelected()
                && !$45MinRadio.isSelected()
                && !$60MinRadio.isSelected())){
            showError("Time interval error","You should pick at least one radio button in order to start");
            return;
        }
        timeLabel.textProperty().bind(task1.messageProperty());
        t = new Thread(task1);
        t.setDaemon(true);
        t.start();
    }


    public void pauseButtonClick() {
        pause = true;
    }

    public void resumeButtonClick() {
        synchronized (task1){
            task1.notify();
            pause = false;
        }
    }

    private String timeConversion(int sec) {
       int hours = sec / 3600;
       int minutes = (sec % 3600) / 60;
       int seconds = sec % 60;

       return String.format("%02d : %02d : %02d", hours, minutes, seconds);
    }


    public void stopButtonClick() {
        timeLabel.textProperty().unbind();
        timeLabel.setText("TIME");
    }

    private void showError(String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
