/**
 * Created by yufeng on 9/11/17.
 */

import krangl.*


fun main(args: Array<String>) {

    // Create data-frame in memory
    val df: DataFrame = dataFrameOf(
            "id", "Year", "A", "B")(
            1, 2007, 5, 10,
            1, 2008, 2, 0,
            1, 2009, 3, 50,
            2, 2007, 7, 13,
            2, 2008, 5, 17,
            2, 2009, 6, 17
    )
    var dfUnite = df.unite("newCol", mutableListOf("A", "Year"))
    dfUnite.print()
    val dd: DataFrame = SimpleDataFrame()
    println(dd.ncol)

}