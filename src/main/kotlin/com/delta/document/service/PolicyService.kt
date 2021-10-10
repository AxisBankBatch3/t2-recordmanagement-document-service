package com.delta.document.service



import com.delta.document.model.PolicyHolder
import com.delta.document.request.PolicyHolderRequest
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

interface PolicyService {


    @Throws(Exception::class)
    fun downloadProofFile(folder: String?, fileName: String?): ByteArray?

    @Transactional
    @Throws(Exception::class)
    fun submitDocuments(
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
        relatedDocuments :MultipartFile,

                        ) : String?

    fun getAllDetails() : MutableList<PolicyHolder?>

    fun getDerailsByPanCardNumber(_id : String?) : PolicyHolderRequest

}