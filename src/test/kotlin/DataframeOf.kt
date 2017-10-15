/**
 * Created by yufeng on 9/26/17.
 */


import krangl.*


fun main(args: Array<String>) {

    val inDf = dataFrameOf("Var", "0", "1").invoke("c_Al", 2680, 1719, "c_D", 2402, 2136, "c_Hy", 2136.5, 2402)

    inDf.print()
    inDf.filter { it["Var"] neq "c_Al" }.print()


    val inDf2 = dataFrameOf("MORPHEUS1321", "MORPHEUS1322").invoke("0_1", 8, "1_1", 8, "1_0", 1, "0_0", 8)
    val inDf3 = dataFrameOf("vv", "MORPHEUS1322").invoke("0_1", 8, "1_1", 8, "1_0", 8, "0_0", 8)
//    val inDf3 = dataFrameOf("vs_am", "countofvalues").invoke("0_0", 2, "0_1", 2, "1_0", 3, "1_1", 1)

    inDf2.print()

    inDf3.print()

    println(hasSameContents(inDf2, inDf3))

    val v1 = mutableListOf("2","1").toHashSet()
    val v2 = mutableListOf("3","1").toHashSet()

}