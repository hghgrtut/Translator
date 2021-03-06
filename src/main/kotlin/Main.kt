import java.io.File
import java.io.FileInputStream
import org.apache.poi.ss.usermodel.WorkbookFactory

private const val SHEET_NAME = "Депозит"
private const val ROWS_COUNT = 500
private const val BY_TRANSLATION_COLUMN = 11
private const val ENG_TRANSLATION_COLUMN = 8
private const val RUS_TRANSLATION_COLUMN = 4
private const val EXCEL_FILE_NAME = "./Переводы Insync.xlsx"
private const val INPUT_FILE_NAME = "./in.txt"
private const val BY_FILE_NAME = "./by.txt"
private const val ENG_FILE_NAME = "./en.txt"

fun main() {
    File(BY_FILE_NAME).delete()
    File(ENG_FILE_NAME).delete()


    val excelSheet = WorkbookFactory.create(FileInputStream(EXCEL_FILE_NAME)).getSheet(SHEET_NAME)
    val map = mutableMapOf<String, Pair<String?, String?>>()
    for (rowIndex in 0..ROWS_COUNT) {
        val row = excelSheet.getRow(rowIndex)
        val rusTranslation = row?.getCell(RUS_TRANSLATION_COLUMN).toString()
        if (rusTranslation != "null") map[rusTranslation] = row.getCell(BY_TRANSLATION_COLUMN)?.toString() to
                    row.getCell(ENG_TRANSLATION_COLUMN)?.toString()?.replace(Regex("[\'`]"), "\\\'")
    }
    File(INPUT_FILE_NAME).forEachLine {
        val key = it.substringAfter(">").substringBefore("<")
        val translation = map[key]
        writeTranslationToFile(BY_FILE_NAME, translation?.first, it)
        writeTranslationToFile(ENG_FILE_NAME, translation?.second, it)
    }
}

private fun writeTranslationToFile(fileName: String, defaultTranslation: String?, defaultText: String) =
    File(fileName).run {
        appendText(
            if (defaultTranslation != null)
                defaultText.substringBefore(">") + ">$defaultTranslation<" + defaultText.substringAfterLast("<")
            else
                defaultText
        )
        appendText("\n")
    }