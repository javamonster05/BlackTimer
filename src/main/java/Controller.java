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
    Thread t;
    Intervals intervals;
    private boolean pause = false;
    private boolean startAfterStop = false;
    boolean a = false;
    private int seconds;

    public Label timeLabel;
    public Button startButton;
    public RadioButton $30MinRadio;
    public RadioButton $45MinRadio;
    public RadioButton $60MinRadio;

    private void setSeconds(){
        if ($30MinRadio.isSelected()) seconds = intervals.THIRHY.getSeconds() ;
        else if ($45MinRadio.isSelected()) seconds = intervals.FOURTY_FIVE.getSeconds();
        else if($60MinRadio.isSelected()) seconds = intervals.ONE_HOUR.getSeconds();
    }

    Task<Integer> timerTask = new Task<>() {
        @Override
        public Integer call() throws InterruptedException {
            setSeconds();

            for(int i = 3; i>=0; i--)
            {
                System.out.println(i);
                if (startAfterStop){
                    synchronized (this){
                setSeconds();
                        i = seconds;
                        a = true;
                        timerTask.wait();

 }
                }
                if (pause){
                    synchronized (this){
                        timerTask.wait();
                    }
                }
                updateMessage(timeConversion(i));
                if(i == 0) updateMessage("Time is Over");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    if (isCancelled()){
                        setSeconds();
                       // timeLabel.textProperty().unbind();
                        break;
                    }
                }
            }
            return 0;
        }
    };

    public void stopButtonClick() {
        // timeLabel.textProperty().unbind();
        //timerTask.cancel();
        startAfterStop =  true;
       // timeLabel.textProperty().unbind();
       // t.interrupt();
       // timeLabel.setText(timeConversion(seconds));
        //timeLabel.setText("TIME");
    }

    private void newTimerThread(){
        t = new Thread(timerTask);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void startButtonClick(){
        timeLabel.textProperty().bind(timerTask.messageProperty());
        if(startAfterStop){
          //  timeLabel.setText(timeConversion(seconds));
            synchronized (timerTask){
                startAfterStop=false;
                System.out.println(7);
                timerTask.notify();
            }
        }
        if((!$30MinRadio.isSelected()
                && !$45MinRadio.isSelected()
                && !$60MinRadio.isSelected())){
            showError("Time interval error","You should pick at least one radio button in order to start");
            return;
        }
           newTimerThread();
    }


    public void pauseButtonClick() {
        pause = true;
    }

    public void resumeButtonClick() {
        synchronized (timerTask){
            timerTask.notify();
            pause = false;
        }
    }

    private String timeConversion(int sec) {
       int hours = sec / 3600;
       int minutes = (sec % 3600) / 60;
       int seconds = sec % 60;

       return String.format("%02d : %02d : %02d", hours, minutes, seconds);
    }


    private void showError(String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    public void initialize() {
        timeLabel.textProperty().bind(timerTask.messageProperty());
    }
}
