package Debug.Serialization;

import java.util.*;

import MathUtils.*;
import Motion.DriveToPoint.*;
import Motion.PurePursuit.PurePursuit;
import Motion.PurePursuit.PurePursuitBuilder;
import Motion.PurePursuitOptimized.PurePursuitOptimized;
import Motion.PurePursuitOptimized.PurePursuitOptimizedBuilder;
import State.*;

public class GCodeDeserializer {
    private HashMap<Integer, String> commandMap;
    private String fullCommandString;
    private StateMachine stateMachine;
    private Vector3 position;

    public GCodeDeserializer(String fullCommandString, StateMachine stateMachine, Vector3 position){
        this.fullCommandString = fullCommandString;
        commandMap = new HashMap<>();
        ArrayList<String> arr = new ArrayList<>();
        arr.addAll(Arrays.asList(fullCommandString.split("M")));
        for(String str : arr){
            commandMap.put(Integer.parseInt(str.split("\n")[0]), str.substring(str.indexOf("\n")+1));
        }
        this.stateMachine = stateMachine;
        this.position = position;
    }

    public DriveState getDriveState(int index){
        String str = commandMap.get(index);
        int type = 0;
        for(String s : str.split("\n")){
            if(s.contains("G")){
                type = Integer.parseInt(s.replace("G", ""));
            }
        }
        if(type == 0){
            return getDriveToPoint(str);
        }else if(type == 1){
            return getPurePursuit(str);
        }else if(type == 2){
            return getOptimizedPurePursuit(str);
        }
        return null;
    }

    private DriveToPoint getDriveToPoint(String str){
        DriveToPointBuilder builder = new DriveToPointBuilder(stateMachine, position);
        for(String s : str.split("\n")){
            if(s.startsWith("A")){
                builder.setRot(Double.parseDouble(s.replace("A", "")));
            }
            if(s.startsWith("S")){
                builder.setSpeed(Double.parseDouble(s.replace("S", "")));
            }
            if(s.startsWith("P")){
                String[] split = s.split(" ");
                Vector2 target = Vector2.ZERO();
                for(String st : split){
                    if(st.startsWith("X")){
                        target.setA(Double.parseDouble(st.replace("X", "")));
                    }
                    if(st.startsWith("Y")){
                        target.setB(Double.parseDouble(st.replace("Y", "")));
                    }
                }
                builder.setTarget(target);
            }
        }
        return builder.complete();
    }

    private PurePursuit getPurePursuit(String str){
        PurePursuitBuilder builder = new PurePursuitBuilder(stateMachine, position);
        for(String s : str.split("\n")){
            if(s.startsWith("A")){
                builder.setAngle(Double.parseDouble(s.replace("A", "")));
            }
            if(s.startsWith("S")){
                builder.setSpeed(Double.parseDouble(s.replace("S", "")));
            }
            if(s.startsWith("R")){
                builder.setRadius(Double.parseDouble(s.replace("R", "")));
            }
            if(s.startsWith("P")){
                String[] split = s.split(" ");
                Vector2 target = Vector2.ZERO();
                for(String st : split){
                    if(st.startsWith("X")){
                        target.setA(Double.parseDouble(st.replace("X", "")));
                    }
                    if(st.startsWith("Y")){
                        target.setB(Double.parseDouble(st.replace("Y", "")));
                    }
                }
                builder.addTarget(target);
            }
        }
        return builder.complete();
    }

    private PurePursuitOptimized getOptimizedPurePursuit(String str){
        PurePursuitOptimizedBuilder builder = new PurePursuitOptimizedBuilder(stateMachine, position);
        for(String s : str.split("\n")){
            if(s.startsWith("A")){
                builder.setApproachAngle(Double.parseDouble(s.replace("A", "")));
            }
            if(s.startsWith("S")){
                builder.setSpeed(Double.parseDouble(s.replace("S", "")));
            }
            if(s.startsWith("R")){
                builder.setRadius(Double.parseDouble(s.replace("R", "")));
            }
            if(s.startsWith("P")){
                String[] split = s.split(" ");
                Vector2 target = Vector2.ZERO();
                for(String st : split){
                    if(st.startsWith("X")){
                        target.setA(Double.parseDouble(st.replace("X", "")));
                    }
                    if(st.startsWith("Y")){
                        target.setB(Double.parseDouble(st.replace("Y", "")));
                    }
                }
                builder.addTarget(target);
            }
        }
        return builder.complete();
    }
}
