import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

Contract.make {
    description "the details endpoint should return a specific user for a given ID"
    request {
        method GET()
        url($(consumer(regex("/reservations/[0-9]+")), producer('/reservations/1234')))
    }
    response {
        status 200
        headers {
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        }
        body([reservationName: "Leroy"])
    }
}