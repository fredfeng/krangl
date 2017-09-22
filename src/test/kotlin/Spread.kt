/**
 * Created by yufeng on 9/11/17.
 */
import krangl.*
import java.util.*


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

    val target: DataFrame = dataFrameOf(
            "id", "A_2007", "A_2008", "A_2009", "B_2007", "B_2008", "B_2009")(
            1, 5, 2, 3, 10, 0, 50,
            2, 7, 5, 6, 13, 17, 17
    )

    var dfGather = df.gather("MORPH132", "MORPH131", mutableListOf("A", "B"))
    dfGather.print()
    println(dfGather.cols)

    var TBL_1 = dfGather.unite("MORPH139", mutableListOf("MORPH132", "Year"))
    TBL_1.print()

    var morpheus = TBL_1.spread("MORPH139", "MORPH131")
    println(morpheus.cols)

    morpheus.print()

    target.print()
    println(hasSameContents(target, morpheus))

}