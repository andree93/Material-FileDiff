package com.andrea.materialfilediff;

public interface CommunicationInterface {
    void updateProgress(int progress);
    void updateProgress();
    void enableProgressBar();
    void disableProgressBar();
    void sendTestData(String s);
    void notifyCompletion();

}
