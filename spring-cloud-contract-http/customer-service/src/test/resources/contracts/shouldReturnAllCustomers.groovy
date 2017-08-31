import org.springframework.cloud.contract.spec.Contract
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

Contract.make {

    description "return all the customers"

    request {
        method GET()
        headers {
            header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
        }
        url "/customers"
    }

    response {
        status 200
        headers {
            header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE)
        }
        body([[id: 1L, name: "Foo"], [id: 2L, name: "Bar"]])
    }

}