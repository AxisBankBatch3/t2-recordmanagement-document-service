package com.delta.document.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document



@Document(collection = "user")
class User {

    @Id
     var id: String? = null

     var fullName: String? = null

     var organization: String? = null

     var username: String? = null

     var mobile: String? = null

    var password: String? = null

    var role: String? = null

    constructor(){
    }

    constructor(
        id: String?, fullName: String?, organization: String?, username: String?, mobile: String?, password: String?,
        role: String?
    ) {

        this.id = id
        this.fullName = fullName
        this.organization = organization
        this.username = username
        this.mobile = mobile
        this.password = password
        this.role = role
    }
}