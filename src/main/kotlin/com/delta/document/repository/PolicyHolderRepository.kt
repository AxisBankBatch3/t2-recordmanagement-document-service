package com.delta.document.repository

import com.delta.document.model.PolicyHolder
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PolicyHolderRepository : MongoRepository<PolicyHolder?,String?> {


}