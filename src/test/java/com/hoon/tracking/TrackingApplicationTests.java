package com.hoon.tracking;

import com.hoon.tracking.history.repository.BoxTrackingHistory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TrackingApplicationTests {

	@Autowired
	private WebTestClient testClient;

	@Test
	public void findByInvoiceNumber() {
		testClient.get().uri("tracking/505712344406")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(BoxTrackingHistory.class).hasSize(2).returnResult();
	}

	@Test
	public void findByInvoiceNumber2() {
		EntityExchangeResult<List<BoxTrackingHistory>> result = testClient.get().uri("tracking/10182951047441")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(BoxTrackingHistory.class).returnResult();

		List<BoxTrackingHistory> list = result.getResponseBody();
		list.stream().forEach(history -> log.info("h >> {} >> {}", history.getDeliveryMessage(), history));
	}

	@Test
	public void insert() {
		BoxTrackingHistory history = BoxTrackingHistory.builder()
				.invoiceNumber("505712344406")
				.shippingCompanyCode("D000004")
				.deliveryCode("10")
				.deliveryMessage("운송장 등록")
				.reasonMessage("")
				.branchName("인천")
				.branchPhoneNumber("02-1111-1111")
				.staffName("김인천")
				.staffMobileNumber("010-1111-2222")
				.scannedAt(LocalDateTime.now())
				.createdAt(LocalDateTime.now()).build();

		testClient.post().uri("tracking")
				.contentType(MediaType.APPLICATION_JSON)
				.syncBody(history)
				.exchange()
				.expectStatus().isOk();
	}

	@Test
	public void count() {
		EntityExchangeResult<Long> result =
				testClient.get().uri("count/tracking")
						.exchange()
						.expectStatus().isOk()
						.expectHeader().contentType(MediaType.APPLICATION_JSON)
						.expectBody(Long.class).returnResult();

		log.info("result count : {}", result.getResponseBody());
	}

}
