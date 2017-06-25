import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

Contract.make {
    description "this endpoint should return all the reservations"
    request {
        method GET()
        url "/reservations"
    }
    response {
        status 200
        headers {
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
        body([[reservationName: "Marcin"], [reservationName: "Bob"]])
    }
}
