package ru.marathontracker.gpd.authorization.security.hashing

import io.ktor.util.*
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

internal class Sha256HashingService: HashingService {
    override fun generateSaltedHash(value: String, saltLength: Int): SaltedHash {
        val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength).run { hex(this) }
        val hash = DigestUtils.sha256Hex("$salt$value")
        return SaltedHash(hash, salt)
    }

    override fun verify(value: String, saltedHash: SaltedHash): Boolean {
        return DigestUtils.sha256Hex("${saltedHash.salt}$value") == saltedHash.hash
    }
}