package com.lyonbtouch.sms;

public interface SmsSender {

    void send(String phone, String message);
}
