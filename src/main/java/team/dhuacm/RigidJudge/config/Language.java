package team.dhuacm.RigidJudge.config;

/**
 * Created by wujy on 15-1-7.
 */
public enum Language {
    CPP, C, JAVA, DEFAULT;

    public static Language fromOrdinal(int id) {
        for (Language l : Language.values()) {
            if (l.ordinal() == id)
                return l;
        }
        return null;
    }
}
