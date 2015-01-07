package team.dhuacm.RigidJudge.config;

/**
 * Created by wujy on 15-1-7.
 */
public enum Result {
    // 0 Wait
    // 1 Queue
    // 2 Accept
    // 3 Wrong_Answer
    // 4 Time_Limit_Exceeded
    // 5 Memory_Limit_Exceeded
    // 6 Presentation_Error
    // 7 Runtime_Error
    // 8 Compile_Error
    // 9 Output_Limit_Exceeded
    // 10 Network_Error
    // 11 Judge_Error
    // 12 Other_Error
    Wait, Queue, Accept, Wrong_Answer, Time_Limit_Exceeded, Memory_Limit_Exceeded, Presentation_Error, Runtime_Error, Compile_Error, Output_Limit_Exceeded, Network_Error, Judge_Error, Other_Error;

    public static Result fromOrdinal(int id) {
        for (Result r : Result.values()) {
            if (r.ordinal() == id)
                return r;
        }
        return null;
    }
}
