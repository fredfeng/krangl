/**
 * Created by yufeng on 9/11/17.
 */

import krangl.*


fun main(args: Array<String>) {

    // Create data-frame in memory
    val df: DataFrame = dataFrameOf(
            "mpg", "cyl", "vs", "am")(
             21.0 ,  6,  0,  1,
     21.0,   6,  0,  1,
     22.8,   4,  1,  1,
     21.4,   6,  1,  0,
     18.7,   8,  0,  0,
     18.1,   6,  1,  0,
     14.3,   8,  0,  0,
     24.4,   4,  1,  0
    )
    var dfUnite = df.unite("vs_am", mutableListOf("am", "vs"))
    dfUnite.print()

    var TBL_1=dfUnite.groupBy("vs_am")
//    TBL_1.print()

    var morpheus=TBL_1.summarize("haha" to {it["vs_am"].count()})
    morpheus.print()

}