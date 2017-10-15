import krangl.*


fun main(args: Array<String>) {

    // Create data-frame in memory
    val df: DataFrame = dataFrameOf(
            "id", "Year", "A", "B")(
            1, 2009, 5, 10,
            1, 2008, 2, 0,
            1, 2009, 3, 50,
            2, 2007, 7, 13,
            2, 2008, 5, 17,
            2, 2009, 6, 17
    )
    print(df["A"].sum())

    df.print()

    df.groupBy("Year").summarize("sumCol" to { it["A"].count() }).print()


    val df2: DataFrame = dataFrameOf(
            "col1", "col2", "col3")(
            "A", 1, "PlaceX",
            "A", 3, "PlaceX",
            "A", 4, "PlaceX",
            "B", 2, "PlaceY",
            "B", 5, "PlaceY"
    )

    df2.groupBy("col1", "col3").summarize("sumCol" to { it["col2"].mean() }).print()

}