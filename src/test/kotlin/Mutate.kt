/**
 * Created by yufeng on 9/11/17.
 */

import krangl.*


fun main(args: Array<String>) {

    // Create data-frame in memory
//    val df: DataFrame = dataFrameOf(
//            "V1", "V2", "High", "Low")(
//            "a",  1, -0.6264538,  0.32950777
//    )

    val df: DataFrame = dataFrameOf(
            "V1", "V2", "High", "Low")(
            "a",  1, 2,  7
    )
    var dfUnite = df.mutate("result", { it["Low"] / it["High"]})
    dfUnite.print()
}