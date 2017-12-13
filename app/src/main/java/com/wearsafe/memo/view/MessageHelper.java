package com.wearsafe.memo.view;

/**
 * Created by Ali on 09-Dec-17.
 */

/**
 * Interface that helps the viewModel to communicate with the view (Activity) in order to display
 * messages
 */
public interface MessageHelper {
    void showMessage(String message);
    void showMessage(int messageId);
}
