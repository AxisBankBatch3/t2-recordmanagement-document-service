package com.delta.document.controller


import com.delta.document.model.PolicyHolder
import com.delta.document.request.PolicyHolderRequest
import com.delta.document.service.PolicyService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.websocket.server.PathParam



@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/policy")

class PolicyController(
    @Autowired
    val policyService: PolicyService
) {



    @PostMapping("/addpolicy")
    @Throws(Exception::class)
    fun submit(@PathParam("partnerId") partnerId:String,
               @PathParam("clientName") clientName : String,
               @PathParam("clientAddress") clientAddress : String,
               @PathParam("pinCode") pinCode : String,
               @PathParam("emailId") emailId : String,
               @PathParam("mobile") mobile : String,
               @PathParam("aadharNumber") aadharNumber: String,
               @PathParam("aadharDoc") aadharDoc: MultipartFile,
               @PathParam("panCardNumber") panCardNumber: String,
               @PathParam("panCardDoc") panCardDoc : MultipartFile,
               @PathParam("insuranceType") insuranceType:String,
               @PathParam("policyNumber") policyNumber : String,
               @PathParam("policyQuote") policyQuote: MultipartFile,
               @PathParam("specificdocument") specificdocument :MultipartFile,



    ): String? {
        return policyService.submitDocuments(partnerId, clientName, clientAddress, pinCode,
                                            emailId, mobile, aadharNumber, aadharDoc, panCardNumber,
                                            panCardDoc, insuranceType, policyNumber, policyQuote,
                                            specificdocument)
    }

   // @ApiOperation(value = "Fetch all policy holders and documents submitted", tags = arrayOf("policyholders"))
    @GetMapping("/allpolicyholders")
    fun getallclients(): MutableList<PolicyHolder?> {
        return policyService.getAllDetails();
    }


   // @ApiOperation(value = "Fetch policy holders by id and documents submitted", tags = arrayOf("policyholders/{id}"))
    @GetMapping("/policyholder/{_id}")
    fun getPolicyHolderUsingPan(@PathVariable _id : String?): PolicyHolderRequest? {
        return policyService.getDerailsByPanCardNumber(_id!!)
    }


   // @ApiOperation(value = "doenload the documents which is submitted priviously", tags = arrayOf("download/proof/{fileName}"))
    @GetMapping("/download/proof/{fileName}")
    fun downloadFile(
        @PathVariable fileName: String,
        @RequestParam folder: String?
    ): ResponseEntity<ByteArrayResource> {
        val data: ByteArray? = policyService.downloadProofFile(folder, fileName)
        val resource = data?.let { ByteArrayResource(it) }
        return if (data != null) {
            ResponseEntity
                .ok()
                .contentLength(data.size.toLong())
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition","attatchment;filename=\""+fileName+"\"")
                .body(resource)
        } else{
            ResponseEntity.noContent().build()
        }
    }

}