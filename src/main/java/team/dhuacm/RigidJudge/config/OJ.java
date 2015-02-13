package team.dhuacm.RigidJudge.config;

/**
 * Created by wujy on 15-1-7.
 */
public enum OJ {
    // local 0
    // uva 1
    // poj 2
    // hdu 3
    // zoj 4
    // uvalive 5
    // sgu 6
    // spoj 7
    // aizu 8
    // ural 9
    // codeforces 10
    // cf_gym 11
    LOCAL, UVA, POJ, HDU, ZOJ, UVALIVE, SGU, SPOJ, AIZU, URAL, CODEFORCES, CF_GYM;

    public static OJ fromOrdinal(int id) {
        for (OJ oj : OJ.values()) {
            if (oj.ordinal() == id)
                return oj;
        }
        return null;
    }
}
