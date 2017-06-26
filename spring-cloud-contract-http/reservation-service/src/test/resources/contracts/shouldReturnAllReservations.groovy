import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

Contract.make {
    description "the /reservations endpoint should return all reservations"
    request {
        method GET()
        url "/reservations"
    }
    response {
        headers {
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
        }
        status 200
        body([[reservationName: "Marcin"], [reservationName: "Josh"]])
    }
}