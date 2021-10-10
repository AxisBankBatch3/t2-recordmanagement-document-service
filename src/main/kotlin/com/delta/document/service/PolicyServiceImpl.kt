package com.delta.document.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.util.IOUtils
import com.delta.document.component.Aes
import com.delta.document.model.PolicyHolder
import com.delta.document.repository.PolicyHolderRepository
import com.delta.document.request.PolicyHolderRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey


@Service
class PolicyServiceImpl(
    @Autowired
    val s3Client :AmazonS3,
    @Autowired
    val policyHolderRepository : PolicyHolderRepository,
    @Autowired
    val aes : Aes
    ) : PolicyService {

    private val logger: Logger = LoggerFactory.getLogger(PolicyServiceImpl::class.java)

    var bucketName: String? = "deltainsurance"

    val secKey: SecretKey? = aes.getSecretEncryptionKey()


    @Throws(Exception::class)
    override fun downloadProofFile(folder: String?, fileName: String?): ByteArray? {

        val s3Object =
            s3Client.getObject("deltainsurance",folder.toString() + "/" + fileName.toString())

        val inputStream = s3Object.objectContent
        return IOUtils.toByteArray(inputStream)
    }

    @Transactional
    override fun submitDocuments(
        partnerId:String,
        clientName : String,
        clientAddress : String,
        pinCode : String,
        emailId : String,
        mobile : String,
        aadharNumber: String,
        aadharDoc: MultipartFile,
        panCardNumber: String,
        panCardDoc : MultipartFile,
        insuranceType:String,
        policyNumber : String,
        policyQuote: MultipartFile,
        specificdocument :MultipartFile,

    ): String? {

        val aadharFile : File? = aadharDoc?.let{convertMultipartFileToFile(it)}
        val panFile : File?= convertMultipartFileToFile(panCardDoc)
        val quoteFile : File?= convertMultipartFileToFile(policyQuote)
        val relatedFile: File? = convertMultipartFileToFile(specificdocument)
        val date =Date()

//        logger.info("Getting original file names")
        val addharFileName : String? = aadharDoc?.originalFilename.toString()
        val panFileName: String? = panCardDoc?.originalFilename
        val quoteFileName: String? = policyQuote?.originalFilename
        val relatedFileName: String? = specificdocument?.originalFilename

            try {


                logger.info("Encrypting data")
                val aadharNumberCipherText: ByteArray? = aes.encryptText(aadharNumber, secKey)
                val panNumberCipherText: ByteArray? = aes.encryptText(panCardNumber, secKey)
                logger.info("data Encrypted")



                val data = PolicyHolder(partnerId,
                    clientName,
                    clientAddress ,
                    pinCode ,
                    emailId ,
                    mobile ,
                    aadharNumberCipherText,
                    addharFileName,
                    panNumberCipherText,
                    panFileName,
                    insuranceType,
                    policyNumber,
                    quoteFileName,
                    relatedFileName,
                    date
                    )

                policyHolderRepository.insert(data)


                try {

                    s3Client.putObject(PutObjectRequest("$bucketName/${policyNumber}", "$addharFileName",aadharFile))
                    logger.info("aadhar file is stored in s3")

                    s3Client.putObject(PutObjectRequest("$bucketName/${policyNumber}", panFileName, panFile))
                    logger.info("pan card is stored in s3")

                    s3Client.putObject(PutObjectRequest("$bucketName/${policyNumber}", quoteFileName, quoteFile))
                    logger.info("quote is stored in s3")

                    s3Client.putObject(PutObjectRequest("$bucketName/${policyNumber}", relatedFileName, relatedFile))
                    logger.info("specific documnet is stored in s3")


                    return "policy added successfully"

                }catch(e:Exception){
                    logger.error("error while storing the  in files in s3",e)


                    return e.printStackTrace().toString()

                }
            }catch (e:Exception){
                logger.error("error while storing the data in database",e)
                e.printStackTrace()
                return e.printStackTrace().toString()
            }


    }

    override fun getAllDetails(): MutableList<PolicyHolder?> {
        logger.info("get all policy details")
       return policyHolderRepository.findAll()
    }

    override fun getDerailsByPanCardNumber(_id: String?): PolicyHolderRequest {

        val encryptedData : Optional<PolicyHolder?> = policyHolderRepository.findById(_id!!)

        val decAadhar : String? = aes.decryptText(encryptedData.get().aadharNumber,secKey)
        val decPan : String? =  aes.decryptText(encryptedData.get().panCardNumber,secKey)

        var decreptedData = PolicyHolderRequest(
            encryptedData.get().id,
            encryptedData.get().partnerId,
            encryptedData.get().clientName,
            encryptedData.get().clientAddress,
            encryptedData.get().pinCode,
            encryptedData.get().emailId,
            encryptedData.get().mobile,
            decAadhar,
            encryptedData.get().aadharDoc,
            decPan,
            encryptedData.get().panCardDoc,
            encryptedData.get().insuranceType,
            encryptedData.get().policyNumber,
            encryptedData.get().policyQuote,
            encryptedData.get().specificdocument,
            encryptedData.get().submissionDate



        )

        return decreptedData
    }


    open fun convertMultipartFileToFile(file: MultipartFile): File {
        val convertedFile = File(file.originalFilename)
        try {

            FileOutputStream(convertedFile).use { fos -> fos.write(file.bytes)}
            logger.info("Multipart file converted to file")
        } catch (e: IOException) {

            logger.error("Error converting multipart file to file",e);
            e.printStackTrace()
        }
        return convertedFile
    }
}




