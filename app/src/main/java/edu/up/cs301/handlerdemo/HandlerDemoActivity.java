package edu.up.cs301.handlerdemo;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Demonstration of using a Handler to allow non-GUI threads to modify GUI objects.
 * The GUI's editable text field gets random letters inserted into it.
 * The GUI's buttons have their colors randomly changed.
 * When a user presses button, its color changes to black.
 *
 * @author Steven R. Vegdahl
 * @version 13 November 2016
 */
public class HandlerDemoActivity extends Activity implements OnClickListener {

    // controls whether insertion is done in the GUI thread
    private static boolean DO_INSERTION_RIGHT = true;

    // controls whether button-coloring is done in the GUI thread
    private static boolean DO_COLORING_RIGHT = true;

    // the handler object
    private Handler myHandler;

    // out the editiable text field in the GUI
    private EditText myText;

    // the indicies GUI-IDs for our all of our buttons
    private static int[] buttonIndices = {
            R.id.button11, R.id.button12, R.id.button13, R.id.button14, R.id.button15,
            R.id.button21, R.id.button22, R.id.button23, R.id.button24, R.id.button25,
            R.id.button31, R.id.button32, R.id.button33, R.id.button34, R.id.button35,
            R.id.button41, R.id.button42, R.id.button43, R.id.button44, R.id.button45,
            R.id.button51, R.id.button52, R.id.button53, R.id.button54, R.id.button55,
    };

    /**
     * The standard 'onCreate' method for our activityi
     *
     * @param savedInstanceState the activity's saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // perform superclass behavior
        super.onCreate(savedInstanceState);

        // load the layout
        setContentView(R.layout.activity_handler_demo);

        // create a handler for this thread (the main/GUI thread)
        myHandler = new Handler();

        // initialize the instance variable that contains our GUI's editable text fiele
        myText = (EditText)findViewById(R.id.textToEdit);

        // set listeners for all of the buttons
        setButtonListeners();

        // start a thread that randomly inserts a letter into the editable text field
        Runnable r1 = new EditTextChanger();
        new Thread(r1).start();

        // start a thread that randomly changes button colors
        Runnable r2 = new ButtonChanger();
        new Thread(r2).start();
    }

    /**
     * Helper method that sets the button's listeners.
     */
    private void setButtonListeners() {
        // go through each button-index; set the activity as the listener for
        // each
        for (int i : buttonIndices) {
            // get the button from the GUI; set the listener
            Button b = (Button)findViewById(i);
            b.setOnClickListener(this);
        }
    }

    /**
     * standard onClick method--called when a button is pressed
     *
     * @param v the view (which is the button)
     */
    public void onClick(View v) {
        // turn the clicked button black
        v.setBackgroundColor(Color.BLACK);
    }

    /**
     * helper method to sleep for a certain number of milliseconds, handling the
     * exception.
     *
     * @param millis the number of milliseconds to sleep
     */
    public static void sleep(int millis) {
        // perform the sleep, handling the exception
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException ix) {
        }
    }

    /**
     * runnable class that randomly inserts letters into the editable text field
     */
    private class EditTextChanger implements Runnable {

        /**
         * the method to run when the thread starts
         */
        @Override
        public void run() {

            // continually perform the following:
            // - wait for a random time interval between 0 and 10 seconds
            // - insert a random character into a random location of the
            for (; ; ) {
                sleep((int) (Math.random() * 10000));
                if (DO_INSERTION_RIGHT) {
                    // run the insertion in the GUI's thread
                    myHandler.post(new Runnable() {
                        public void run() {
                            insertRandomLetter();
                        }
                    });
                } else {
                    // run the insertion in the current thread
                    insertRandomLetter();
                }
            }
        }

        /**
         * helper-method to insert a letter into the editable text field
         */
        private void insertRandomLetter() {
            // get the text that is currently in the field
            String str = myText.getText() + "";

            // selectx a random position in the text
            int pos = (int) (Math.random() * (str.length() + 1));

            // select a random letter to insert
            char ch = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".
                    charAt((int) (Math.random() * 52));

            // create the string with the inserted character
            str = str.substring(0, pos) + ch + str.substring(pos);

            // set the text and  selection position of the editable text field
            myText.setText(str);
            myText.setSelection(pos);
        }
    }

    /**
     * runnable class that randomly changes the colors of a buttons
     */
    private class ButtonChanger implements Runnable {

        /**
         * the method to run when the thread starts
         */
        @Override
        public void run() {
            for (;;) {

                // sleep for a quarter-second
                sleep(250);

                if (DO_COLORING_RIGHT) {
                    // change the color of a random button to a random color in the GUI thread
                    myHandler.post(new Runnable() {
                        public void run() {
                            changeButtonColorRandomly();
                        }
                    });
                }
                else {
                    // change the color of a random button to a random color in the current thread
                    changeButtonColorRandomly();
                }
            }
        }

        /**
         * helper method to change a random button to a random color
         */
        private void changeButtonColorRandomly() {
            // select a random button
            int index = buttonIndices[(int)(Math.random()*25)];
            Button myButton = (Button)findViewById(index);

            // change the button to a random color
            int color = 0xff000000+(int)(Math.random()*0x1000000);
            myButton.setBackgroundColor(color);
        }
    }
}
