package ru.marathontracker.gpd.authorization.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String
)