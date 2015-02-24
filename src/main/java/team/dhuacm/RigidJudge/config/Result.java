package team.dhuacm.RigidJudge.config;

/**
 * Created by wujy on 15-1-7.
 */
public enum Result {
    // 0 Queue
    // 1 Accept_Answer
    // 2 Wrong_Answer
    // 3 Time_Limit_Exceeded
    // 4 Memory_Limit_Exceeded
    // 5 Presentation_Error
    // 6 Runtime_Error
    // 7 Compile_Error
    // 8 Output_Limit_Exceeded
    // 9 Network_Error
    // 10 Judge_Error
    Queue, Accept_Answer, Wrong_Answer, Time_Limit_Exceeded, Memory_Limit_Exceeded, Presentation_Error, Runtime_Error, Compile_Error, Output_Limit_Exceeded, Network_Error, Judge_Error;

    public static Result fromOrdinal(int id) {
        for (Result r : Result.values()) {
            if (r.ordinal() == id)
                return r;
        }
        return null;
    }

    public static Result parseResult(String str) {
        if (str.equalsIgnoreCase("Queue")) return Queue;
        if (str.equalsIgnoreCase("AC") || str.startsWith("Accept")) return Accept_Answer;
        if (str.equalsIgnoreCase("WA") || str.startsWith("Wrong")) return Wrong_Answer;
        if (str.equalsIgnoreCase("TLE") || str.startsWith("Time")) return Time_Limit_Exceeded;
        if (str.equalsIgnoreCase("MLT") || str.startsWith("Memory")) return Memory_Limit_Exceeded;
        if (str.equalsIgnoreCase("PE") || str.startsWith("Presentation")) return Presentation_Error;
        if (str.equalsIgnoreCase("RE") || str.startsWith("Runtime")) return Runtime_Error;
        if (str.equalsIgnoreCase("CE") || str.startsWith("Compile")) return Compile_Error;
        if (str.equalsIgnoreCase("OLE") || str.startsWith("Output")) return Output_Limit_Exceeded;
        if (str.equalsIgnoreCase("NE") || str.startsWith("Network")) return Network_Error;
        if (str.equalsIgnoreCase("JE") || str.startsWith("Judge")) return Judge_Error;
        return Queue;
    }
}
