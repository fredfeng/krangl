/**
 * Created by yufeng on 9/11/17.
 */

import krangl.*


fun main(args: Array<String>) {

    // Create data-frame in memory
    val df: DataFrame = dataFrameOf(
            "id", "Year", "A", "B")(
            1, "a|b", 5, 10,
            1, "c|d", 2, 0,
            1, "e|f", 3, 50,
            2, "g|h", 7, 13,
            2, "i|j", 5, 17,
            2, "k|l", 6, 17
    )

    var dfUnite = df.separate("Year", mutableListOf("Part1", "Part2"), "\\.|-|_|\\|")

    dfUnite.print()
    val dd: DataFrame = SimpleDataFrame()
    println(dd.ncol)
    var str = "a|b|c"
    println(str.split("\\.|_|\\|".toRegex()).toList())

}