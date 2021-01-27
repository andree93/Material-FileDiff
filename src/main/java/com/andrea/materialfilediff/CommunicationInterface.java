package com.andrea.materialfilediff;

public interface CommunicationInterface {
    void updateProgress(int progress);
    void enableProgressBar();
    void disableProgressBar();
    void sendTestData(String s);

}
