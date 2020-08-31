package org.firstinspires.ftc.robotcontroller.internal.WebInterface;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcontroller.internal.WebInterface.JsonPackets.*;
import org.firstinspires.ftc.robotcontroller.internal.WebInterface.VSD.VirtualSD;
import org.firstinspires.ftc.robotcore.internal.collections.SimpleGson;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeManagerImpl;
import org.firstinspires.ftc.robotcore.internal.opmode.OpModeMeta;
import org.firstinspires.ftc.robotcore.internal.opmode.RegisteredOpModes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class InterfaceHandler implements Runnable{
    static final int WEBSOCKET_PORT = 9000;

    private static InterfaceHandler instance = new InterfaceHandler();
    private EventLoop eventLoop;
    private OpModeManagerImpl opModeManager;
    private ArrayList<String> opmodes, consoleCommands, telemetryPacket, telemetryStrings;
    private String ping;
    private Thread mainThread;
    private JavaHTTPServer javaHTTPServer;
    private ArrayList<String> packets;
    private HashMap<String, String> telemetry, editableValues;
    private volatile boolean running, sendTelemetry;
    public static InterfaceHandler getInstance(){
        return instance;
    }
    private Context context;
    private long telemetryTimer;
    private String position;
    private Gamepad vGamepad;
    private String mousePos;

    public InterfaceHandler(){
        opmodes = new ArrayList<>();
        packets = new ArrayList<>();
        telemetry = new HashMap<>();
        editableValues = new HashMap<>();
        consoleCommands = new ArrayList<>();
        telemetryStrings = new ArrayList<>();
        packets.add("");
        consoleCommands.add("");
        //running = true;
        sendTelemetry = false;
        telemetryPacket = new ArrayList<>();
        running = true;
        position = "0, 0, 0";
        mousePos = "0, 0";
        telemetryTimer = 0;
        vGamepad = new Gamepad();
        new Thread(this).start();
    }

    public void start(){
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                JavaHTTPServer.start();
            }
        });
        serverThread.start();
    }

    public void addContext(Context context){
        this.context = context;
        VirtualSD.getInstance().addContext(context);
    }

    public static void attachEventLoop(EventLoop eventLoop){
        instance.internalAttachLoop(eventLoop);
    }

    public void internalAttachLoop(EventLoop eventLoop){
        this.eventLoop = eventLoop;
        this.opModeManager = eventLoop.getOpModeManager();
        new Thread(new SyncOpmodes()).start();
    }

    @Override
    public void run(){
        WebsocketHandler handler = new WebsocketHandler(WEBSOCKET_PORT);
        handler.start();
        RobotLog.i("Dashboard Started");
        Gson gson = SimpleGson.getInstance();
        long timer = 0;
        while(running){
            timer = System.currentTimeMillis() + 50;
            HashMap<String, String> newMessages = handler.getNewMessages();
            for(String s : newMessages.keySet()){
                if(Objects.equals(newMessages.get(s), "OPMODES")){
                    OpmodePacket packet = new OpmodePacket();
                    packet.opmodes = opmodes.toArray(new String[0]);
                    handler.send(s, gson.toJson(packet));
                }
                if(Objects.equals(newMessages.get(s), "FILEINDEX")) {
                    FileIndexPacket packet = new FileIndexPacket();
                    ArrayList<String> fileIndex = VirtualSD.getInstance().indexFiles();
                    packet.files = fileIndex.toArray(new String[0]);
                    handler.send(s, gson.toJson(packet));
                }
                if(Objects.equals(newMessages.get(s), "FOLDERSINDEX")) {
                    FolderIndexPacket packet = new FolderIndexPacket();
                    ArrayList<String> folderIndex = VirtualSD.getInstance().indexFolders();
                    packet.folders = folderIndex.toArray(new String[0]);
                    handler.send(s, gson.toJson(packet));
                }
                if(newMessages.get(s).startsWith("GET")) {
                    FilePacket packet = new FilePacket();
                    try {
                        packet.fileText = VirtualSD.getInstance().getFile(newMessages.get(s).replace("GET ", "")).read();
                    } catch (IOException e) {
                        packet.fileText = "";
                    }
                    handler.send(s, gson.toJson(packet));
                }
                if(newMessages.get(s).startsWith("EDIT")) {
                    FileEditPacket packet = gson.fromJson(newMessages.get(s).replace("EDIT ", ""), FileEditPacket.class);
                    try {
                        VirtualSD.getInstance().getFile(packet.file).clear();
                        VirtualSD.getInstance().getFile(packet.file).write(packet.text);
                    } catch (IOException ignored) {
                    }
                }
                if(newMessages.get(s).startsWith("CREATEFOLDER")){
                    try {
                        VirtualSD.getInstance().addFolder(newMessages.get(s).replace("CREATEFOLDER ", ""));
                    } catch (IOException ignored) {
                    }
                }
                if(newMessages.get(s).startsWith("DELETEFILE")){
                    try {
                        VirtualSD.getInstance().getFile(newMessages.get(s).replace("DELETEFILE ", "")).deleteFile();
                    } catch (IOException ignored) {
                    }
                }
                if(newMessages.get(s).startsWith("DELETEFOLDER")){
                    VirtualSD.getInstance().deleteFolder(newMessages.get(s).replace("DELETEFOLDER ", ""));
                }
                if(newMessages.get(s).startsWith("INIT")){
                    opModeManager.initActiveOpMode(newMessages.get(s).replace("INIT ", ""));
                }
                if(newMessages.get(s).equals("START")){
                    opModeManager.startActiveOpMode();
                }
                if(newMessages.get(s).equals("STOP")){
                    OpMode opName = opModeManager.getActiveOpMode();
                    opModeManager.requestOpModeStop(opName);
                }
                if(newMessages.get(s).startsWith("GAMEPAD")){
                    setGamepad(newMessages.get(s).replace("GAMEPAD ", ""));
                }
                if(newMessages.get(s).startsWith("MOUSECOORDS")){
                    setMousePos(newMessages.get(s).replace("MOUSECOORDS ", ""));
                }
            }
            if(sendTelemetry){
                TelemetryPacket packet = new TelemetryPacket();
                packet.timestamp = System.currentTimeMillis();
                synchronized (telemetry) {
                    for (String s : telemetry.keySet()) {
                        telemetryPacket.add(s + " : " + telemetry.get(s));
                    }
                }
                telemetryPacket.addAll(telemetryStrings);
                String telemetryStr = "";
                for(String s : telemetryPacket){
                    telemetryStr += s + "<br>";
                }
                packet.telemetry = telemetryStr;
                sendTelemetry = false;
                packet.position = position;
                telemetryPacket.clear();
                synchronized (telemetry) {
                    telemetry.clear();
                }
                if(!telemetryStr.equals("")) {
                    handler.sendAll(gson.toJson(packet));
                }
            }
            while(System.currentTimeMillis() < timer); //Limit to 50 ms per cycle so we don't accidentally DOS the client
        }
    }

    public void run2()  {
        while(running) {
            if(sendTelemetry) {
                for (String s : telemetry.keySet()) {
                    telemetryPacket.add(s + " : " + telemetry.get(s));
                }
                telemetryPacket.addAll(telemetryStrings);
                sendTelemetry = false;
            }
            if(System.currentTimeMillis() >= telemetryTimer){
                telemetryPacket.clear();
                telemetry.clear();
            }
            if (packets.size() > 1) { //Gives us a buffer to prevent weird things from happening
                String packet = packets.get(0);
                if (!packet.equals("")) {
                    String[] vals = packet.split("&");
                    consoleCommands.addAll(Arrays.asList(vals));
                }
                packets.remove(0);
            }
            ping = String.valueOf(System.currentTimeMillis());
            for (String s : consoleCommands) {
                if(s.split("=")[0].equals("console")) {
                    String origin = s;
                    s = s.split("=")[1];
                    if (s.contains("init")) {
                        opModeManager.initActiveOpMode(s.split(" ")[1]);
                        consoleCommands.remove(origin);
                    }
                    if (s.contains("start")) {
                        opModeManager.startActiveOpMode();
                        consoleCommands.remove(origin);
                    }
                    if (s.contains("stop")) {
                        OpMode opName = opModeManager.getActiveOpMode();
                        opModeManager.requestOpModeStop(opName);
                        consoleCommands.remove(origin);
                    }
                }
                if(s.split("=")[0].equals("gamepad")){
                    setGamepad(s.split("=")[1]);
                    consoleCommands.remove(s);
                }
                if(s.split("=")[0].equals("coords")){
                    setMousePos(s.split("=")[1]);
                    consoleCommands.remove(s);
                }
            }
        }
    }

    private class SyncOpmodes implements Runnable{
        @Override
        public void run() {
            RegisteredOpModes.getInstance().waitOpModesRegistered();
            for(OpModeMeta opModeMeta : RegisteredOpModes.getInstance().getOpModes()){
                opmodes.add(opModeMeta.name);
            }
            Set<String> removeDupes = new LinkedHashSet<String>(opmodes);
            opmodes.clear();
            opmodes.addAll(removeDupes);
            Collections.sort(opmodes);
        }
    }

    public void internalAddTelemetry(String header, Object data){
        synchronized (telemetry) {
            telemetry.put(header, data.toString());
        }
    }

    public void internalAddLine(String line){
        telemetryStrings.add(line);
    }

    public ArrayList<String> getTelemetry(){
        return telemetryPacket;
    }

    public ArrayList<String> getOpmodes(){
        return opmodes;
    }

    public String getConsoleCommand(){
        if(!consoleCommands.get(0).equals("")){
            String s = consoleCommands.get(0);
            consoleCommands.remove(0);
            return s;
        }
        return "";
    }

    public InputStream getInputStream(String name){
        String s = name.replaceFirst("/", "");
        if(name.contains("robot") || name.contains("field")){
            Log.i("Dashboard", name);
        }
        try {
            return context.getAssets().open(s);
        } catch (IOException e) {
            try {
                return context.getAssets().open("index.html");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public void addPacket(String s){
        packets.add(s);
    }

    public void internalUpdateTelemetry(){
        sendTelemetry = true;
        telemetryTimer = System.currentTimeMillis() + 1000;
    }

    public void internalSetPosition(String pos){
        this.position = pos;
    }

    public String getPosition(){
        return this.position;
    }

    public void setGamepad(String s){
        String[] arr = s.split(",");
        if(arr.length >= 15) {
            vGamepad.a = arr[0].equals("1");
            vGamepad.b = arr[1].equals("1");
            vGamepad.x = arr[2].equals("1");
            vGamepad.y = arr[3].equals("1");
            vGamepad.left_bumper = arr[4].equals("1");
            vGamepad.right_bumper = arr[5].equals("1");
            vGamepad.left_trigger = Float.parseFloat(arr[6]);
            vGamepad.right_trigger = Float.parseFloat(arr[7]);
            vGamepad.back = arr[8].equals("1");
            vGamepad.start = arr[9].equals("1");
            vGamepad.left_stick_button = arr[10].equals("1");
            vGamepad.right_stick_button = arr[11].equals("1");
            vGamepad.dpad_up = arr[12].equals("1");
            vGamepad.dpad_down = arr[13].equals("1");
            vGamepad.dpad_left = arr[14].equals("1");
            vGamepad.dpad_right = arr[15].equals("1");
            vGamepad.left_stick_x = Float.parseFloat(arr[17]);
            vGamepad.left_stick_y = Float.parseFloat(arr[18]);
            vGamepad.right_stick_x = Float.parseFloat(arr[19]);
            vGamepad.right_stick_y = Float.parseFloat(arr[20]);
        }
    }

    public Gamepad getVirtualGamepad(){
        return vGamepad;
    }

    public void setMousePos(String s){
        this.mousePos = s;
    }

    public String getMousePos(){
        return mousePos;
    }

    public void stop(){
        running = false;
    }

}