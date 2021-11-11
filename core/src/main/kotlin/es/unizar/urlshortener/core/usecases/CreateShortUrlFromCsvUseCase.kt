package es.unizar.urlshortener.core.usecases

import es.unizar.urlshortener.core.*
import org.springframework.web.multipart.MultipartFile
import java.io.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.springframework.core.io.InputStreamResource
import java.util.Date

/**
 * Given an url returns the key that is used to create a short URL.
 * When the url is created optional data may be added.
 *
 * **Note**: This is an example of functionality.
 */
interface CreateShortUrlFromCsvUseCase {
    fun create(file : MultipartFile, data: ShortUrlProperties): CsvProfile
}

/**
 * Implementation of [CreateShortUrlFromCsvUseCase].
 */
class CreateShortUrlFromCsvUseCaseImpl(
    private val shortUrlRepository: ShortUrlRepositoryService,
    private val validatorService: ValidatorService,
    private val hashService: HashService
) : CreateShortUrlFromCsvUseCase {
    override fun create(file : MultipartFile, data: ShortUrlProperties): CsvProfile {
        val reader = BufferedReader(InputStreamReader(file.inputStream))
        val csvParser = CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(',') )
        val byteArrayOutputStream = ByteArrayOutputStream()
        val writer = BufferedWriter(OutputStreamWriter(byteArrayOutputStream))
        val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT.withDelimiter(',') )
        var firstUri =""
        for (line in csvParser ){
            val url = line.get(0)
            if (validatorService.isValid(url)) {
                val id: String = hashService.hasUrl(url)
                val su = ShortUrl(
                    hash = id,
                    redirection = Redirection(target = url),
                    properties = ShortUrlProperties(
                        safe = data.safe,
                        ip = data.ip,
                        sponsor = data.sponsor
                    )
                )
                val uri=shortUrlRepository.save(su)
                csvPrinter.printRecord(url, uri.hash,)
                if (firstUri.equals("")){
                    firstUri=uri.hash
                }
            } else {
                throw InvalidUrlException(url)
            }


        }
        csvPrinter.flush()
        csvPrinter.close()


        return CsvProfile(
            firstUri,
            InputStreamResource(ByteArrayInputStream(byteArrayOutputStream.toByteArray()))

        )
    }


}
