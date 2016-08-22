package com.example.dell.myapplication;

/**
 * Created by johan on 13.07.2016.
 */
public class OurMsg {


    public int pullups;
    public long hangtime;
    public int grabbed_left;
    public int grabbed_right;

    public OurMsg(int pullups, long hangtime, int grabbed_left, int grabbed_right) {
        this.pullups = pullups;
        this.hangtime = hangtime;
        this.grabbed_left = grabbed_left;
        this.grabbed_right = grabbed_right;
    }



    public void generateMessage(){
        return;
    }

    public String getMsg() {
        return this.toString();
    }

    public String toString(){
        String s="("+pullups+","+hangtime+","+grabbed_left+","+grabbed_right+")";
        return s;
    }


    public int getPullups() {
        return pullups;
    }

    public void setPullups(int pullups) {
        this.pullups = pullups;
    }

    public long getHangtime() {
        return hangtime;
    }

    public void setHangtime(long hangtime) {
        this.hangtime = hangtime;
    }

    public int getGrabbed_left() {
        return grabbed_left;
    }

    public void setGrabbed_left(int grabbed_left) {
        this.grabbed_left = grabbed_left;
    }

    public int getGrabbed_right() {
        return grabbed_right;
    }

    public void setGrabbed_right(int grabbed_right) {
        this.grabbed_right = grabbed_right;
    }
}
