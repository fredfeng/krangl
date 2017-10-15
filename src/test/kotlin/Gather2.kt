/**
 * Created by yufeng on 9/11/17.
 */

import krangl.*


fun main(args: Array<String>) {

    // Create data-frame in memory
    val df: DataFrame = dataFrameOf("id", "yr1", "yr2", "yr3", "var")(
            1, 1090, 2066, 3050, "yr2",
            2, 1092, 3066, 6050, "yr1"
    )
    df.print()

//    var TBL_3=df.gather("MORPH394","P",-"ID",-"T")
    var morpheus=df.gather("key","value",-"id",-"var")

    println("========result=====")
    morpheus.print()
    println(morpheus.cols)
}