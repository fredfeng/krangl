import krangl.*


fun main(args: Array<String>) {

    // Create data-frame in memory
//    val df: DataFrame = dataFrameOf(
//            "id", "Year", "A", "B")(
//            1, 2009, 5, 10,
//            1, 2008, 2, 0,
//            1, 2009, 3, 50,
//            2, 2007, 7, 13,
//            2, 2008, 5, 17,
//            2, 2009, 6, 17
//    )
//    print(df["A"].sum())
//
//    df.print()
//
//    df.groupBy("Year").summarize("sumCol" to { it["B"].sum() }).print()
//
//
//    val df2: DataFrame = dataFrameOf(
//            "col1", "col2", "col3")(
//            "A", 1, "PlaceX",
//            "A", 3, "PlaceX",
//            "A", 4, "PlaceX",
//            "B", 2, "PlaceY",
//            "B", 5, "PlaceY"
//    )
//
//    df2.groupBy("col1", "col3").summarize("meanCol" to { it["col2"].mean() }).print()


    val df3: DataFrame = dataFrameOf(
            "x", "z")(
            "a", 93.7,
            "b", 115.9,
            "c", 101.2,
            "a", 63.7,
            "b", 15.9,
            "c", 10.2
    )
    var df4 = df3.groupBy("x").summarize("morpheus_sum" to { it["z"].sum() })

    var groupDf = df3.groupBy("x")

    groupDf.print()

    println(groupDf.rows.iterator().next())

    df4.print()

}