package krangl

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import java.io.*
import java.util.zip.GZIPInputStream

/**
Methods to read and write tables into/from DataFrames
 * see also https://commons.apache.org/proper/commons-csv/ for other implementations
 * https://github.com/databricks/spark-csv
 * https://examples.javacodegeeks.com/core-java/apache/commons/csv-commons/writeread-csv-files-with-apache-commons-csv-example/

 */


fun DataFrame.Companion.fromCSV(file: String) = fromCSV(File(file))

fun DataFrame.Companion.fromTSV(file: String) = fromCSV(File(file), format = CSVFormat.TDF)

// http://stackoverflow.com/questions/9648811/specific-difference-between-bufferedreader-and-filereader
fun DataFrame.Companion.fromCSV(file: File,
                                format: CSVFormat = CSVFormat.DEFAULT,
                                isCompressed: Boolean = file.name.endsWith(".gz")): DataFrame {

    val bufReader = if (isCompressed) {
        // http://stackoverflow.com/questions/1080381/gzipinputstream-reading-line-by-line
        val gzip = GZIPInputStream(FileInputStream(file));
        BufferedReader(InputStreamReader(gzip));
    } else {
        BufferedReader(FileReader(file))
    }

    return fromCSV(bufReader, format)
}

//http://stackoverflow.com/questions/5200187/convert-inputstream-to-bufferedreader
fun DataFrame.Companion.fromCSV(inStream: InputStream, format: CSVFormat = CSVFormat.DEFAULT, isCompressed: Boolean = false) =
        if (isCompressed) {
            InputStreamReader(GZIPInputStream(inStream))
        } else {
            BufferedReader(InputStreamReader(inStream, "UTF-8"))
        }.run {
            fromCSV(this, format)
        }


internal fun DataFrame.Companion.fromCSV(reader: Reader, format: CSVFormat = CSVFormat.DEFAULT): DataFrame {
    val csvParser = format.parse(reader)

    val records = csvParser.records

    val cols = mutableListOf<DataCol>()

//    csvParser.headerMap.keys.pmap{colName ->
    for (colName in csvParser.headerMap.keys) {
        val firstElements = peekCol(colName, records)

        when {
        // see https://github.com/holgerbrandl/krangl/issues/10
            isIntCol(firstElements) -> try {
                IntCol(colName, records.map { it[colName].naAsNull()?.toInt() })
            } catch (e: NumberFormatException) {
                StringCol(colName, records.map { it[colName].naAsNull() })
            }
            isDoubleCol(firstElements) -> DoubleCol(colName, records.map { it[colName].naAsNull()?.toDouble() })
            isBoolCol(firstElements) -> BooleanCol(colName, records.map { it[colName].naAsNull()?.cellValueAsBoolean() })
            else -> StringCol(colName, records.map { it[colName].naAsNull() })
        }.let { cols.add(it) }

    }

    return SimpleDataFrame(cols)
}


// NA aware conversions
internal fun String.naAsNull(): String? = if (this == "NA") null else this

internal fun String?.nullAsNA(): String = this ?: "NA"

internal fun String?.cellValueAsBoolean(): Boolean? {
    if (this == null) return null

    var cellValue: String? = toUpperCase()

    cellValue = if (cellValue == "NA") null else cellValue
    cellValue = if (cellValue == "F") "false" else cellValue
    cellValue = if (cellValue == "T") "true" else cellValue

    if (!listOf("true", "false", null).contains(cellValue)) throw NumberFormatException("invalid boolean cell value")

    return cellValue?.toBoolean()
}


// TODO add missing value support with user defined string (e.g. NA here) here

internal fun isDoubleCol(firstElements: List<String?>): Boolean = try {
    firstElements.map { it?.toDouble() }; true
} catch(e: NumberFormatException) {
    false
}

internal fun isIntCol(firstElements: List<String?>): Boolean = try {
    firstElements.map { it?.toInt() }; true
} catch(e: NumberFormatException) {
    false
}

internal fun isBoolCol(firstElements: List<String?>): Boolean = try {
    firstElements.map { it?.cellValueAsBoolean() }; true
} catch(e: NumberFormatException) {
    false
}


// todo keep peeking until we hit the first/N non NA value
internal fun peekCol(colName: String?, records: List<CSVRecord>, peekSize: Int = 5) = records.take(peekSize).mapIndexed { rowIndex, csvRecord -> records[rowIndex][colName].naAsNull() }


//TODO add support for compressed writing

fun DataFrame.writeCSV(file: String, format: CSVFormat = CSVFormat.DEFAULT, colNames: Boolean = true) = writeCSV(File(file), format, colNames)

fun DataFrame.writeCSV(file: File, format: CSVFormat = CSVFormat.DEFAULT, colNames: Boolean = true) {
    //initialize FileWriter object
    val fileWriter = FileWriter(file)

    //initialize CSVPrinter object
    val csvFilePrinter = CSVPrinter(fileWriter, format)

    //Create CSV file header
    if (colNames) csvFilePrinter.printRecord(names)

    // write records
    for (record in rawRows) {
        csvFilePrinter.printRecord(record)
    }

    fileWriter.flush()
    fileWriter.close()
    csvFilePrinter.close()
}

