package team.dhuacm.RigidJudge.config;

/**
 * Created by wujy on 15-1-7.
 */
public enum Result {
    // 0 Queue
    // 1 Accept
    // 2 Wrong_Answer
    // 3 Time_Limit_Exceeded
    // 4 Memory_Limit_Exceeded
    // 5 Presentation_Error
    // 6 Runtime_Error
    // 7 Compile_Error
    // 8 Output_Limit_Exceeded
    // 9 Network_Error
    // 10 Judge_Error
    Queue, Accept, Wrong_Answer, Time_Limit_Exceeded, Memory_Limit_Exceeded, Presentation_Error, Runtime_Error, Compile_Error, Output_Limit_Exceeded, Network_Error, Judge_Error;

    public static Result fromOrdinal(int id) {
        for (Result r : Result.values()) {
            if (r.ordinal() == id)
                return r;
        }
        return null;
    }
}
