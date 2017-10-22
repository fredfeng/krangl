package krangl

fun main(args: Array<String>) {


    val df3: DataFrame = dataFrameOf(
            "id", "a", "b", "c")(
            101, 1, 2, 3,
                     102, 2, 2, 3,
                     103, 3, 2, 3
    )
    var df4 = df3.groupBy("b").summarize("morpheus_mean" to { it["a"].mean() })
    var df5 = df4.innerJoin(df3)

    df4.print()
    df5.print()

    val target: DataFrame = dataFrameOf(
            "id", "mean", "a", "b", "c")(
            101, 2.000000, 1, 2, 3,
            102, 2.33333333333333, 2, 2, 3,
            103, 2.66666666666667, 3, 2, 3
    )

    println("============EXPECTED=========")
    target.print()

    println(hasSameContents(target, df5))
    println(hasSameContents(df5, target))


    var TBL_7=df3.gather("key","value", mutableListOf("a", "b", "c"), true)
    TBL_7.print()
    println(TBL_7.cols)
    var TBL_3 = TBL_7.groupBy("id").summarize("mean" to {it["value"].mean()})

    TBL_3.print()

    var mm = TBL_3.innerJoin(df3)
    mm.print()
    println(hasSameContents(mm, target))

}