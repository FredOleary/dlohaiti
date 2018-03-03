package main.java.com.dlohaiti.dlokiosk;

public interface StatusView {
    void showProgressBar();

    void dismissProgressBar();

    void refreshStatus(Boolean result);
}
