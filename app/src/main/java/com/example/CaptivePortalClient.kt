package com.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Kültür ve Turizm Bakanlığı (KTB) kütüphane Wi-Fi captive portalı ile
 * HTTP üzerinden haberleşen OkHttp istemcisi.
 */
class CaptivePortalClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()
) {

    companion object {
        private const val BASE_URL = "http://captive.kultur.gov.tr"
        private const val CREATE_SMS_TOKEN_URL = "$BASE_URL/users/createSmsToken"
        private const val VALIDATE_SMS_TOKEN_URL = "$BASE_URL/users/validateSmsToken"
        private const val CONNECTIVITY_CHECK_URL = "http://connectivitycheck.gstatic.com/generate_204"

        private const val HEADER_ORIGIN = "http://captive.kultur.gov.tr"
        private const val HEADER_REFERER = "http://captive.kultur.gov.tr/"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    data class PortalResult(
        val isSuccess: Boolean,
        val message: String,
        val rawResponse: String? = null,
        val httpStatusCode: Int = 0
    )

    /**
     * 1. ADIM: SMS Doğrulama Kodu İsteği
     *
     * Method: POST
     * URL: http://captive.kultur.gov.tr/users/createSmsToken
     * Headers:
     *   - Content-Type: application/json
     *   - Origin: http://captive.kultur.gov.tr
     *   - Referer: http://captive.kultur.gov.tr/
     * Body: {"phone_number": "905xxxxxxxxx"}
     */
    suspend fun createSmsToken(phoneNumber: String): PortalResult = withContext(Dispatchers.IO) {
        val jsonBody = JSONObject().apply {
            put("phone_number", phoneNumber)
        }.toString()

        val requestBody = jsonBody.toRequestBody(JSON_MEDIA_TYPE)

        val request = Request.Builder()
            .url(CREATE_SMS_TOKEN_URL)
            .addHeader("Content-Type", "application/json")
            .addHeader("Origin", HEADER_ORIGIN)
            .addHeader("Referer", HEADER_REFERER)
            .addHeader("User-Agent", "Mozilla/5.0 (Android; Mobile; KTB-WiFi-Assistant)")
            .post(requestBody)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string().orEmpty()
                val statusCode = response.code

                if (!response.isSuccessful && statusCode != 200) {
                    return@withContext PortalResult(
                        isSuccess = false,
                        message = "Sunucu yanıt kodu: $statusCode",
                        rawResponse = bodyString,
                        httpStatusCode = statusCode
                    )
                }

                // JSON yanıtını incele: {"isSuccess": true, ...}
                parsePortalResponse(bodyString, statusCode, "SMS doğrulama kodu başarıyla istendi.")
            }
        } catch (e: IOException) {
            PortalResult(
                isSuccess = false,
                message = "Bağlantı hatası: ${e.localizedMessage ?: "Captive portala ulaşılamadı. Lütfen kütüphane Wi-Fi ağına bağlı olduğunuzdan emin olun."}",
                httpStatusCode = -1
            )
        } catch (e: Exception) {
            PortalResult(
                isSuccess = false,
                message = "Beklenmeyen hata: ${e.localizedMessage}",
                httpStatusCode = -1
            )
        }
    }

    /**
     * 2. ADIM: Doğrulama ve Giriş İsteği
     *
     * Method: POST
     * URL: http://captive.kultur.gov.tr/users/validateSmsToken
     * Headers: 1. adımla aynı
     * Body: {"phone_number": "905xxxxxxxxx", "verification_code": "XXXXXX"}
     */
    suspend fun validateSmsToken(phoneNumber: String, verificationCode: String): PortalResult =
        withContext(Dispatchers.IO) {
            val jsonBody = JSONObject().apply {
                put("phone_number", phoneNumber)
                put("verification_code", verificationCode.trim())
            }.toString()

            val requestBody = jsonBody.toRequestBody(JSON_MEDIA_TYPE)

            val request = Request.Builder()
                .url(VALIDATE_SMS_TOKEN_URL)
                .addHeader("Content-Type", "application/json")
                .addHeader("Origin", HEADER_ORIGIN)
                .addHeader("Referer", HEADER_REFERER)
                .addHeader("User-Agent", "Mozilla/5.0 (Android; Mobile; KTB-WiFi-Assistant)")
                .post(requestBody)
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    val bodyString = response.body?.string().orEmpty()
                    val statusCode = response.code

                    if (!response.isSuccessful && statusCode != 200) {
                        return@withContext PortalResult(
                            isSuccess = false,
                            message = "Giriş başarısız (HTTP $statusCode)",
                            rawResponse = bodyString,
                            httpStatusCode = statusCode
                        )
                    }

                    parsePortalResponse(bodyString, statusCode, "İnternet Bağlantısı Başarılı!")
                }
            } catch (e: IOException) {
                PortalResult(
                    isSuccess = false,
                    message = "Bağlantı hatası: ${e.localizedMessage}",
                    httpStatusCode = -1
                )
            } catch (e: Exception) {
                PortalResult(
                    isSuccess = false,
                    message = "Doğrulama hatası: ${e.localizedMessage}",
                    httpStatusCode = -1
                )
            }
        }

    /**
     * İnternet erişiminin aktif olup olmadığını denetler (Captive portal kalktı mı?)
     */
    suspend fun checkInternetConnectivity(): Boolean = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(CONNECTIVITY_CHECK_URL)
            .get()
            .build()
        try {
            client.newCall(request).execute().use { response ->
                response.code == 204 || response.isSuccessful
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun parsePortalResponse(
        rawBody: String,
        statusCode: Int,
        defaultSuccessMessage: String
    ): PortalResult {
        return try {
            val json = JSONObject(rawBody)
            val isSuccess = json.optBoolean("isSuccess", false)
            val message = when {
                json.has("message") -> json.optString("message")
                json.has("msg") -> json.optString("msg")
                json.has("error") -> json.optString("error")
                isSuccess -> defaultSuccessMessage
                else -> "İşlem captive portal tarafından onaylanmadı."
            }

            PortalResult(
                isSuccess = isSuccess,
                message = if (message.isNotBlank()) message else defaultSuccessMessage,
                rawResponse = rawBody,
                httpStatusCode = statusCode
            )
        } catch (_: Exception) {
            // Yanıt JSON formatında değilse (örneğin HTML hata sayfası döndüyse)
            val lower = rawBody.lowercase()
            val seemsSuccess = lower.contains("\"issuccess\": true") || lower.contains("\"issuccess\":true")
            PortalResult(
                isSuccess = seemsSuccess,
                message = if (seemsSuccess) defaultSuccessMessage else "Sunucudan beklenmeyen yanıt formatı alındı.",
                rawResponse = rawBody.take(500),
                httpStatusCode = statusCode
            )
        }
    }
}
